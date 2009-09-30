/*
  $Id: Base64FilterInputStreamTest.java 84 2009-03-26 14:23:35Z marvin.addison $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 84 $
  Updated: $Date: 2009-03-26 10:23:35 -0400 (Thu, 26 Mar 2009) $
*/
package edu.vt.middleware.crypt.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import edu.vt.middleware.crypt.FileHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Base64FilterInputStream}.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class Base64FilterInputStreamTest
{

  /** Classpath location of text data file. */
  private static final String TEXT_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());

  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "testdata")
  public Object[][] createTestDataBase64()
    throws Exception
  {
    return
      new Object[][] {
        {new Integer(0)},
        {new Integer(Base64FilterInputStream.LINE_LENGTH_64)},
        {new Integer(Base64FilterInputStream.LINE_LENGTH_76)},
      };
  }


  /**
   * @param  charsPerLine  Number of characters per line in encoded data file.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "io", "decodeBase64"}, dataProvider = "testdata")
  public void testDecodeBase64(final Integer charsPerLine)
    throws Exception
  {
    logger.info(
      "Decoding base64-encoded file with " + charsPerLine +
      " characters per line.");

    final String outPath = "target/test-output/decoded-base64-" + charsPerLine +
      ".txt";
    new File(outPath).getParentFile().mkdir();

    final InputStream in = new Base64FilterInputStream(
      getClass().getResourceAsStream(
        "/edu/vt/middleware/crypt/io/base64-" + charsPerLine + ".txt"),
      charsPerLine.intValue());
    final OutputStream out = new FileOutputStream(new File(outPath));
    InputStream inRef = null;
    InputStream inTest = null;
    try {
      int count = 0;
      final int bufsize = 2048;
      final byte[] buffer = new byte[bufsize];
      while ((count = in.read(buffer)) > 0) {
        out.write(buffer, 0, count);
      }
      inRef = getClass().getResourceAsStream(TEXT_FILE_PATH);
      inTest = new FileInputStream(new File(outPath));
      AssertJUnit.assertTrue(FileHelper.equal(inRef, inTest));
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
      if (inRef != null) {
        inRef.close();
      }
      if (inTest != null) {
        inTest.close();
      }
    }
  }
}
