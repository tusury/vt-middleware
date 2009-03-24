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
package edu.vt.middleware.gator.web.support;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.vt.middleware.gator.ParamConfig;

/**
 * Converts between an array of {@link ParamConfig} items and a text
 * representation of the following form:
 * <pre>
 * name=value
 * name=value
 * name=value
 * </pre>
 * where each name/value pair is separated by a line terminator, either
 * CR or CRLF.
 *
 * @author Marvin S. Addison
 *
 */
public abstract class AbstractParamArrayEditor extends PropertyEditorSupport
{
  /** Logger instance */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Pattern used to match param lines in string representation */
  protected static final Pattern LINE_MATCH_PATTERN =
    Pattern.compile("(\\w+=.+)\n?");
  
  /** Pattern to split param line into name/value pairs */
  protected static final Pattern NAME_VALUE_SPLIT_PATTERN = Pattern.compile("=");
  
  /** Holds value of editor */
  protected List<ParamConfig> paramList = new ArrayList<ParamConfig>();


  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsText()
  {
    final StringBuilder sb = new StringBuilder();
    for (ParamConfig param : paramList) {
      sb.append(String.format("%s=%s\n", param.getName(), param.getValue()));
    }
    return sb.toString();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setAsText(final String text)
  {
    logger.debug(String.format(
        "Attempting to set value using text [[%s]].", text));
    paramList.clear();
    final Matcher matcher = LINE_MATCH_PATTERN.matcher(text);
    while (matcher.find()) {
      final String line = matcher.group(1);
      final String[] pair = NAME_VALUE_SPLIT_PATTERN.split(line.trim(), 2);
      if (pair.length != 2) {
        throw new IllegalArgumentException("Param string must be name=value.");
      }
      final ParamConfig param = newParam();
      param.setName(pair[0]);
      param.setValue(pair[1]);
      paramList.add(param);
    }
  }
 
 
  /**
   * Creates a new configuration parameter instance.
   * @return New config param.
   */
  protected abstract ParamConfig newParam();

}
