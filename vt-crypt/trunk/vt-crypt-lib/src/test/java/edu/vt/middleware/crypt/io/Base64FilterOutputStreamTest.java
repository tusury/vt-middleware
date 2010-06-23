/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.io;

import java.io.BufferedOutputStream;
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
 * Unit test for {@link Base64FilterOutputStream}.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class Base64FilterOutputStreamTest
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
  @Test(
    groups = {"functest", "io", "encodeBase64"},
    dataProvider = "testdata"
  )
  public void testEncodeBase64(final Integer charsPerLine)
    throws Exception
  {
    logger.info(
      "Writing encoded base64 file with " + charsPerLine +
      " characters per line.");

    final String outPath = "target/test-output/encoded-base64-" + charsPerLine +
      ".txt";
    new File(outPath).getParentFile().mkdir();

    final InputStream in = getClass().getResourceAsStream(TEXT_FILE_PATH);
    final OutputStream out = new Base64FilterOutputStream(
      new BufferedOutputStream(new FileOutputStream(new File(outPath))),
      charsPerLine.intValue());
    try {
      int count = 0;
      final int bufsize = 2048;
      final byte[] buffer = new byte[bufsize];
      while ((count = in.read(buffer)) > 0) {
        out.write(buffer, 0, count);
      }
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }

    final InputStream inRef = getClass().getResourceAsStream(
      "/edu/vt/middleware/crypt/io/base64-" + charsPerLine + ".txt");
    final InputStream inTest = new FileInputStream(new File(outPath));
    try {
      AssertJUnit.assertTrue(FileHelper.equal(inRef, inTest));
    } finally {
      if (inRef != null) {
        inRef.close();
      }
      if (inTest != null) {
        inTest.close();
      }
    }
  }
}
