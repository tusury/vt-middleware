/*
  $Id: HexFilterInputStreamTest.java 84 2009-03-26 14:23:35Z marvin.addison $

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
import edu.vt.middleware.crypt.FileHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link HexFilterInputStream} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class HexFilterInputStreamTest
{

  /** Classpath location of text data file. */
  private static final String TEXT_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"functest", "io", "decodeHex"})
  public void testDecodeHex()
    throws Exception
  {
    logger.info("Decoding hex file.");

    final String outPath = "target/test-output/decoded-hex.txt";
    new File(outPath).getParentFile().mkdir();

    final HexFilterInputStream in = new HexFilterInputStream(
      getClass().getResourceAsStream("/edu/vt/middleware/crypt/io/hex.txt"));
    final FileOutputStream out = new FileOutputStream(new File(outPath));
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
