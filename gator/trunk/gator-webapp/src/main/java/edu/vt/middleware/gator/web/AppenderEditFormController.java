/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.util.RequestParamExtractor;
import edu.vt.middleware.gator.web.support.AppenderParamArrayEditor;
import edu.vt.middleware.gator.web.support.LayoutParamArrayEditor;

/**
 * Handles appender configuration additions/updates.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class AppenderEditFormController extends BaseFormController
{

  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception
  {
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    if (project == null) {
      throw new IllegalArgumentException("Project not found.");
    }
	  final AppenderConfig appender = project.getAppender(
	    RequestParamExtractor.getAppenderId(request));
	  AppenderWrapper wrapper = null;
	  if (appender == null) {
	    final AppenderConfig newAppender = new AppenderConfig();
	    newAppender.setProject(project);
	    wrapper = new AppenderWrapper(newAppender);
	  } else {
      wrapper = new AppenderWrapper(appender);
	  }
	  return wrapper;
  }


  /** {@inheritDoc} */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
    throws Exception
  {
    final Map<String, Object> data = new HashMap<String, Object>();
    final ProjectConfig project = configManager.findProject(
      RequestParamExtractor.getProjectName(request));
    data.put("project", project);
    return data;
  }


  /** {@inheritDoc} */
  @Override
  protected void initBinder(HttpServletRequest request,
      ServletRequestDataBinder binder) throws Exception
  {
    super.initBinder(request, binder);
    binder.registerCustomEditor(
      AppenderParamConfig[].class,
      new AppenderParamArrayEditor());
    binder.registerCustomEditor(
      LayoutParamConfig[].class,
      new LayoutParamArrayEditor());
  }


  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ModelAndView onSubmit(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object command, final BindException errors)
      throws Exception
  {
    final AppenderWrapper wrapper = (AppenderWrapper) command;
    final AppenderConfig appender = wrapper.getAppender();
    final boolean isNew = !configManager.exists(appender);
    final ProjectConfig project = appender.getProject();
    // Ensure appender name is unique within project
    if (isNew || nameChanged(appender)) {
      for (AppenderConfig a : project.getAppenders()) {
        if (a.getName().equals(appender.getName())) {
          errors.rejectValue(
              "appender.name",
              "error.appender.save",
              new Object[] {
                a.getName(),
                "Appender name must be unique.",
              },
              "Appender name must be unique."
          );
          return showForm(request, errors, getFormView());
        }
      }
    }
    if (isNew) {
      project.addAppender(appender);
    }
    mergeAppenderParams(appender, wrapper.getAppenderParams());
    mergeLayoutParams(appender, wrapper.getLayoutParams());
    configManager.save(project);

    return new ModelAndView(
        ControllerHelper.filterViewName(getSuccessView(), project));
  }


  /**
   * Merge appenders parameters from the form into the appender.
   * @param appender Target appender to merge with.
   * @param appenderParams Appender parameters to merge.
   */
  private void mergeAppenderParams(
    final AppenderConfig appender,
    final AppenderParamConfig[] appenderParams)
  {
    final Set<AppenderParamConfig> tbd = new HashSet<AppenderParamConfig>();
    tbd.addAll(appender.getAppenderParams());
    tbd.removeAll(Arrays.asList(appenderParams));
    for (AppenderParamConfig p : tbd) {
      appender.removeAppenderParam( appender.getAppenderParam(p.getName()));
    }
    for (AppenderParamConfig p : appenderParams) {
      final AppenderParamConfig param = appender.getAppenderParam(p.getName());
      if (param != null) {
        param.setValue(p.getValue());
      } else {
        appender.addAppenderParam(p);
      }
    }
  }

  
  /**
   * Merge layout parameters from the form into the appender.
   * @param appender Target appender to merge with.
   * @param layoutParams Layout parameters to merge.
   */
  private void mergeLayoutParams(
    final AppenderConfig appender,
    final LayoutParamConfig[] layoutParams)
  {
    final Set<LayoutParamConfig> tbd = new HashSet<LayoutParamConfig>();
    tbd.addAll(appender.getLayoutParams());
    tbd.removeAll(Arrays.asList(layoutParams));
    for (LayoutParamConfig p : tbd) {
      appender.removeLayoutParam(appender.getLayoutParam(p.getName()));
    }
    for (LayoutParamConfig p : layoutParams) {
      final LayoutParamConfig param = appender.getLayoutParam(p.getName());
      if (param != null) {
        param.setValue(p.getValue());
      } else {
        appender.addLayoutParam(p);
      }
    }
  }


  /**
   * Determines whether the name of the given appender has changed from
   * what is recorded in the DB.
   * @param appender Appender to evaluate.
   * @return True if name of given appender is different from that in the DB,
   * false otherwise.  Returns false if appender does not exist in DB.
   */
  private boolean nameChanged(final AppenderConfig appender)
  {
    final AppenderConfig appenderFromDb = configManager.find(
      AppenderConfig.class,
      appender.getId());
    if (appenderFromDb != null) {
      return !appenderFromDb.getName().equals(appender.getName());
    } else {
      return false;
    }
  }
  
  
  /**
   * Wrapper class for {@link AppenderConfig} that exposes additional attributes
   * needed for binding to forms.
   * @author Marvin S. Addison
   *
   */
  public class AppenderWrapper
  {
    private AppenderConfig appender;
    
    private AppenderParamConfig[] appenderParams;
    
    private LayoutParamConfig[] layoutParams;


    /**
     * Creates a new wrapper around the given appender configuration.
     * @param wrapped Appender configuration to wrap.
     */
    public AppenderWrapper(final AppenderConfig wrapped)
    {
      setAppender(wrapped);
      setAppenderParams(
        wrapped.getAppenderParams().toArray(
          new AppenderParamConfig[wrapped.getAppenderParams().size()]));
      setLayoutParams(
        wrapped.getLayoutParams().toArray(
          new LayoutParamConfig[wrapped.getLayoutParams().size()]));
    }


    /**
     * @param appender the appender to set
     */
    public void setAppender(AppenderConfig appender)
    {
      this.appender = appender;
    }


    /**
     * @return the appender
     */
    public AppenderConfig getAppender()
    {
      return appender;
    }


    /**
     * Gets the appender parameters as an array.
     * @return Array of parameter configuration objects.
     */
    public AppenderParamConfig[] getAppenderParams()
    {
      return appenderParams;
    }
    
    /**
     * Sets the appender parameters as an array.
     * @param params Array of parameter configuration objects.
     */
    public void setAppenderParams(final AppenderParamConfig[] params)
    {
      appenderParams = params;
    }
    
    /**
     * Gets the layout parameters as an array.
     * @return Array of parameter configuration objects.
     */
    public LayoutParamConfig[] getLayoutParams()
    {
      return layoutParams;
    }
    
    /**
     * Sets the layout parameters as an array.
     * @param params Array of parameter configuration objects.
     */
    public void setLayoutParams(final LayoutParamConfig[] params)
    {
      layoutParams = params;
    }
  }
}
