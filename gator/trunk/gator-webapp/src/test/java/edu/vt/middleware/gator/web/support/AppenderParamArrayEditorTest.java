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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.web.support.AppenderParamArrayEditor;

/**
 * Unit test for {@link AppenderParamArrayEditor} class.
 *
 * @author Marvin S. Addison
 *
 */
@RunWith(Parameterized.class)
public class AppenderParamArrayEditorTest
{
  /** String representation of property value */
  private String stringValue;
 
  /** Normalized string value resulting from conversion to string */
  private String normalizedStringValue;
 
  /** Property value */
  private AppenderParamConfig[] paramValue;


  public AppenderParamArrayEditorTest(
      final String stringValue,
      final String normalizedValue,
      final AppenderParamConfig[] paramValue)
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
    final AppenderParamConfig[] appenderParams1 = new AppenderParamConfig[] {
        new AppenderParamConfig("file", "/apps/logs/file.log"),
        new AppenderParamConfig("maxBackupIndex", "1"),
        new AppenderParamConfig("maxFileSize", "10000"),
    };
    params.add(new Object[] {stringValue1, stringValue1, appenderParams1});
    
    // Test parameter #2 -- empty string handling
    params.add(new Object[] {"", "", new AppenderParamConfig[0]});
    
    // Test parameter #3 -- newline handling
    final AppenderParamConfig[] appenderParams3 = new AppenderParamConfig[] {
        new AppenderParamConfig("a", "b"),
    };
    params.add(new Object[] {"   \na=b", "a=b\n", appenderParams3});
    
    // Test parameter #4 -- values containing '=' character
    final String stringValue4 = "name=some=crazy==value\n";
    final AppenderParamConfig[] appenderParams4 = new AppenderParamConfig[] {
        new AppenderParamConfig("name", "some=crazy==value"),
    };
    params.add(new Object[] {stringValue4, stringValue4, appenderParams4});

    return params;
  }


  /**
   * Test method for {@link AppenderParamArrayEditor#getAsText()}.
   */
  public void testGetAsText()
  {
    final AppenderParamArrayEditor editor = new AppenderParamArrayEditor();
    editor.setValue(paramValue);
    Assert.assertEquals(normalizedStringValue, editor.getAsText());
  }


  /**
   * Test method for {@link AppenderParamArrayEditor#getValue()}.
   */
  @Test
  public void testGetValue() {
    final AppenderParamArrayEditor editor = new AppenderParamArrayEditor();
    editor.setAsText(stringValue);
    final Object actual = editor.getValue();
    Assert.assertTrue(Arrays.equals(paramValue, (AppenderParamConfig[]) actual));
  }
}
