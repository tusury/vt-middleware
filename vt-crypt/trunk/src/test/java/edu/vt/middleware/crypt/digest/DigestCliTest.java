/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.digest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import edu.vt.middleware.crypt.CliHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;

/**
 * Unit test for {@link DigestCli} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DigestCliTest
{

  /** Classpath location of file to be hashed. */
  private static final String TEST_PLAINTEXT =
    "src/test/resources/edu/vt/middleware/crypt/plaintext-127.txt";

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(this.getClass());


  /**
   * @return  Test data.
   *
   * @throws  Exception  On test data generation failure.
   *
   * @testng.data-provider  name="testdata"
   */
  public Object[][] createTestData()
    throws Exception
  {
    return
      new Object[][] {
        {
          "-alg MD4",
          null,
        },
        {
          "-alg MD5 -outform base64 -salt D3ADF00D",
          null,
        },
        {
          "-alg MD5 -outform base64",
          "jkdiaM9v/g6hNQSr5hnmgQ==",
        },
        {
          "-alg RIPEMD128 -outform hex",
          null,
        },
        {
          "-alg RIPEMD160 -outform hex",
          null,
        },
        {
          "-alg RIPEMD256 -outform hex",
          null,
        },
        {
          "-alg RIPEMD320 -outform hex",
          null,
        },
        {
          "-alg SHA1 -outform hex",
          "fa46b18852c72fe87b8954b987b897074c211131",
        },
        {
          "-alg SHA-1 -outform base64",
          "+kaxiFLHL+h7iVS5h7iXB0whETE=",
        },
        {
          "-alg SHA256 -outform hex",
          "949365318ff52c4d326ff3dd806cb29bd620e458c6987635f3ba0312868d2649",
        },
        {
          "-alg SHA-256 -outform base64",
          "lJNlMY/1LE0yb/PdgGyym9Yg5FjGmHY187oDEoaNJkk=",
        },
        {
          "-alg SHA384 -outform hex",
          "3ed9a131a23adb1db8837d925a04f16044df637891baf9fa" +
            "178daee1d9216a84231750e9d69a37ac462cdc7abf1cc032",
        },
        {
          "-alg SHA-384 -outform base64",
          "PtmhMaI62x24g32SWgTxYETfY3iRuvn6F42u4dkhaoQjF1Dp1po3rEYs3Hq/HMAy",
        },
        {
          "-alg SHA512 -outform hex",
          "4d0b1e5dccd9e8998bf08a9d0ea8b7c139643ae5a5b937219eaa47dd8237b987" +
            "b4dc947b2e9001b3a5f2887a62933d4e6d79865663e6e7e5bd140f4e6079f76f",
        },
        {
          "-alg SHA-512 -outform base64",
          "TQseXczZ6JmL8IqdDqi3wTlkOuWluTchnqpH3YI3uYe03JR7LpABs6XyiHpikz1O" +
            "bXmGVmPm5+W9FA9OYHn3bw==",
        },
        {
          "-alg Tiger -outform hex",
          "ff140eab46ba272631f5b02e6de52aaec6b9a2b836a5dc13",
        },
        {
          "-alg Whirlpool -outform hex",
          "b7a85efb1e0dce3805f30c95bb5e4595d2602bf5adc8743d39a293f726d851f9" +
            "fd31c59dfd7d2175018c5f4b38747d9131c2a825206aa6d29dbb5cc0be2f0581",
        },
      };
  }

  /**
   * @param  partialLine  Partial command line.
   * @param  expected  Expected result of digest operation.
   *
   * @throws  Exception  On test failure.
   *
   * @testng.test  groups = "cli digest" dataProvider = "testdata"
   */
  public void testDigestCli(final String partialLine, final String expected)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      final String fullLine = partialLine + " -in " + TEST_PLAINTEXT;
      logger.info("Testing digest CLI with command line:\n\t" + fullLine);
      DigestCli.main(CliHelper.splitArgs(fullLine));

      final String result = outStream.toString();
      AssertJUnit.assertTrue(result.length() > 0);
      if (expected != null) {
        AssertJUnit.assertEquals(expected, result.trim());
      }
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
