/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.web.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.ParamConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link ParametersEditor} class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@RunWith(Parameterized.class)
public class ParametersEditorTest
{
  /** String representation of property value */
  private String stringValue;
 
  /** Normalized string value resulting from conversion to string */
  private String normalizedStringValue;
 
  /** Property value */
  private Set<? extends ParamConfig> paramValue;


  /**
   * Creates a new test instance with given parameters.
   * @param stringValue Expected string representation.
   * @param normalizedValue A canonical string representation.
   * @param paramValue The expected parsed value.
   */
  public ParametersEditorTest(
      final String stringValue,
      final String normalizedValue,
      final Set<? extends ParamConfig> paramValue)
  {
    this.stringValue = stringValue;
    this.normalizedStringValue = normalizedValue;
    this.paramValue = paramValue;
  }


  /**
   * Gets the unit test parameters.
   *
   * @return  Test parameter data.
   * 
   * @throws  Exception on parameter setup errors.
   */
  @Parameters
  public static Collection<Object[]> getTestParameters() throws Exception
  {
    final Collection<Object[]> params = new ArrayList<Object[]>();
    
    // Test parameter #1 -- pretty typical property values
    final String stringValue1 =
      "file=/apps/logs/file.log\n" +
      "maxBackupIndex=1\n" +
      "maxFileSize=10000\n";
    final Set<AppenderParamConfig> appenderParams1 =
      new LinkedHashSet<AppenderParamConfig>();
    appenderParams1.add(new AppenderParamConfig("file", "/apps/logs/file.log"));
    appenderParams1.add(new AppenderParamConfig("maxBackupIndex", "1"));
    appenderParams1.add(new AppenderParamConfig("maxFileSize", "10000"));
    params.add(new Object[] {stringValue1, stringValue1, appenderParams1});
    
    // Test parameter #2 -- empty string handling
    params.add(new Object[] {"", "", Collections.emptySet()});
    
    // Test parameter #3 -- newline handling
    final Set<AppenderParamConfig> appenderParams3 =
      new LinkedHashSet<AppenderParamConfig>();
    appenderParams3.add(new AppenderParamConfig("a", "b"));
    params.add(new Object[] {"   \na=b", "a=b\n", appenderParams3});
    
    // Test parameter #4 -- values containing '=' character
    final String stringValue4 = "name=some=crazy==value\n";
    final Set<AppenderParamConfig> appenderParams4 =
      new LinkedHashSet<AppenderParamConfig>();
    appenderParams4.add(new AppenderParamConfig("name", "some=crazy==value"));
    params.add(new Object[] {stringValue4, stringValue4, appenderParams4});

    return params;
  }

  
  /**
   * Test method for {@link edu.vt.middleware.gator.web.support.ParametersEditor#getAsText()}.
   */
  @Test
  public void testGetAsText()
  {
    final ParametersEditor<AppenderParamConfig> editor =
      new ParametersEditor<AppenderParamConfig>(AppenderParamConfig.class);
    editor.setValue(paramValue);
    Assert.assertEquals(normalizedStringValue, editor.getAsText());
  }

  /**
   * Test method for {@link edu.vt.middleware.gator.web.support.ParametersEditor#getValue()}.
   */
  @Test
  public void testGetValue()
  {
    final ParametersEditor<AppenderParamConfig> editor =
      new ParametersEditor<AppenderParamConfig>(AppenderParamConfig.class);
    editor.setAsText(stringValue);
    Assert.assertEquals(paramValue, editor.getValue());
  }

}
