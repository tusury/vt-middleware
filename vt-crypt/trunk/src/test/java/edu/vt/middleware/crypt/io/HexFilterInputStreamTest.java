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

import edu.vt.middleware.crypt.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link HexFilterInputStream} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class HexFilterInputStreamTest
{

  /** Classpath location of text data file. */
  private static final String TEXT_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  /** @throws  Exception  On test failure. */
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
