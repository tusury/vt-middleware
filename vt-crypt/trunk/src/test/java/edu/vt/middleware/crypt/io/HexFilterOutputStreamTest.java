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

import edu.vt.middleware.crypt.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.io.*;

/**
 * Unit test for {@link HexFilterOutputStream} class.
 *
 * @author  Middleware Services
 * @version  $Revision: 84 $
 */
public class HexFilterOutputStreamTest
{

  /** Classpath location of text data file. */
  private static final String TEXT_FILE_PATH =
    "/edu/vt/middleware/crypt/plaintext.txt";

  /** Logger instance. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());


  /** @throws  Exception  On test failure. */
  @Test(groups = {"functest", "io", "encodeHex"})
  public void testEncodeHex()
    throws Exception
  {
    logger.info("Writing encoded hex file");

    final String outPath = "target/test-output/encoded-hex.txt";
    new File(outPath).getParentFile().mkdir();

    final InputStream in = getClass().getResourceAsStream(TEXT_FILE_PATH);
    final OutputStream out = new HexFilterOutputStream(
      new BufferedOutputStream(new FileOutputStream(new File(outPath))));
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
      "/edu/vt/middleware/crypt/io/hex.txt");
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
