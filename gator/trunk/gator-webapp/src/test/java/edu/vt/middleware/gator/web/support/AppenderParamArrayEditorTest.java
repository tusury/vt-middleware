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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.web.support.AppenderParamArrayEditor;

/**
 * Unit test for {@link AppenderParamArrayEditor} class.
 *
 * @author Marvin S. Addison
 *
 */
public class AppenderParamArrayEditorTest
{
  private static final String TEST_PARAM_STRING =
    "file=/apps/logs/file.log\n" +
    "maxBackupIndex=1\n" +
    "maxFileSize=10000\n";
  
  private static final AppenderParamConfig[] TEST_PARAMS =
    new AppenderParamConfig[3];

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUp() throws Exception
  {
    TEST_PARAMS[0] = new AppenderParamConfig();
    TEST_PARAMS[0].setName("file");
    TEST_PARAMS[0].setValue("/apps/logs/file.log");
    TEST_PARAMS[1] = new AppenderParamConfig();
    TEST_PARAMS[1].setName("maxBackupIndex");
    TEST_PARAMS[1].setValue("1");
    TEST_PARAMS[2] = new AppenderParamConfig();
    TEST_PARAMS[2].setName("maxFileSize");
    TEST_PARAMS[2].setValue("10000");
  }

  /**
   * Test method for {@link AppenderParamArrayEditor#getAsText()}.
   */
  @Test
  public void testGetAsText()
  {
    final AppenderParamArrayEditor editor = new AppenderParamArrayEditor();
    editor.setAsText(TEST_PARAM_STRING);
    Assert.assertEquals(TEST_PARAM_STRING, editor.getAsText());
  }

  /**
   * Test method for {@link AppenderParamArrayEditor#getValue()}.
   */
  @Test
  public void testGetValue() {
    final AppenderParamArrayEditor editor = new AppenderParamArrayEditor();
    editor.setValue(TEST_PARAMS);
    final AppenderParamConfig[] testValue =
      (AppenderParamConfig[]) editor.getValue();
    Assert.assertEquals(TEST_PARAMS.length, testValue.length);
    for (int i = 0; i < TEST_PARAMS.length; i++) {
      Assert.assertEquals(TEST_PARAMS[i].getName(), testValue[i].getName());
      Assert.assertEquals(TEST_PARAMS[i].getValue(), testValue[i].getValue());
    }
    Assert.assertEquals(TEST_PARAM_STRING, editor.getAsText());
  }

  /**
   * Test method for {@link AppenderParamArrayEditor#setAsText(String)}.
   */
  @Test
  public void testSetAsTextString() {
    final AppenderParamArrayEditor editor = new AppenderParamArrayEditor();
    editor.setAsText(TEST_PARAM_STRING);
    AppenderParamConfig[] testValue = (AppenderParamConfig[]) editor.getValue();
    Assert.assertEquals(TEST_PARAMS.length, testValue.length);
    for (int i = 0; i < TEST_PARAMS.length; i++) {
      Assert.assertEquals(TEST_PARAMS[i].getName(), testValue[i].getName());
      Assert.assertEquals(TEST_PARAMS[i].getValue(), testValue[i].getValue());
    }
    
    // Ensure setting empty string and whitespace string works
    editor.setAsText("");
    Assert.assertEquals(0, ((AppenderParamConfig[]) editor.getValue()).length);
    editor.setAsText(" \n");
    Assert.assertEquals(0, ((AppenderParamConfig[]) editor.getValue()).length);
    
    // Test that a name/value pair not terminated with new line works
    editor.setAsText("name=value");
    Assert.assertEquals(1, ((AppenderParamConfig[]) editor.getValue()).length);
    
    // Ensure we can handle values containing equals signs
    editor.setAsText("name=some=crazy=value");
    testValue = (AppenderParamConfig[]) editor.getValue();
    Assert.assertEquals(1, testValue.length);
    Assert.assertEquals("some=crazy=value", testValue[0].getValue());
  }

  /**
   * Test method for {@link AppenderParamArrayEditor#setValue(Object)}.
   */
  @Test
  public void testSetValueObject() {
    final AppenderParamArrayEditor editor = new AppenderParamArrayEditor();
    editor.setValue(TEST_PARAMS);
    final AppenderParamConfig[] testValue =
      (AppenderParamConfig[]) editor.getValue();
    Assert.assertEquals(TEST_PARAMS.length, testValue.length);
    for (int i = 0; i < TEST_PARAMS.length; i++) {
      Assert.assertEquals(TEST_PARAMS[i].getName(), testValue[i].getName());
      Assert.assertEquals(TEST_PARAMS[i].getValue(), testValue[i].getValue());
    }
  }

}
