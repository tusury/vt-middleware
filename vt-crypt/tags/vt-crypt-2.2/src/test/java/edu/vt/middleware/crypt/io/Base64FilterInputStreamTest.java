/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import edu.vt.middleware.crypt.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Base64FilterInputStream}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class Base64FilterInputStreamTest
{

  /** Classpath location of text data file. */
  private static final String TEXT_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        {0},
        {Base64FilterInputStream.LINE_LENGTH_64},
        {Base64FilterInputStream.LINE_LENGTH_76},
      };
  }


  /**
   * @param  charsPerLine  Number of characters per line in encoded data file.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"functest", "io", "decodeBase64"},
    dataProvider = "testdata"
  )
  public void testDecodeBase64(final Integer charsPerLine)
    throws Exception
  {
    logger.info(
      "Decoding base64-encoded file with {} characters per line.",
      charsPerLine);

    final String outPath = "target/test-output/decoded-base64-" + charsPerLine +
      ".txt";
    new File(outPath).getParentFile().mkdir();

    final InputStream in = new Base64FilterInputStream(
      getClass().getResourceAsStream(
        "/edu/vt/middleware/crypt/io/base64-" + charsPerLine + ".txt"),
      charsPerLine);
    final OutputStream out = new FileOutputStream(new File(outPath));
    InputStream inRef = null;
    InputStream inTest = null;
    try {
      int count;
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
