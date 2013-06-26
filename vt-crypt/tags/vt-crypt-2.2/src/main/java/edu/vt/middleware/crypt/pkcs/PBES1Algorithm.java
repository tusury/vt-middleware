/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.pkcs;

import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.digest.MD2;
import edu.vt.middleware.crypt.digest.MD5;
import edu.vt.middleware.crypt.digest.SHA1;
import edu.vt.middleware.crypt.symmetric.AlgorithmSpec;

/**
 * Password-based encryption algorithms defined in PKCS#5 for PBES1 scheme.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public enum PBES1Algorithm {

  /** PBES1 encryption method with MD2 hash and DES CBC cipher. */
  PbeWithMD2AndDES_CBC(
    "1.2.840.113549.1.5.1",
    new AlgorithmSpec("DES", "CBC", "PKCS5Padding"),
    new MD2()),

  /** PBES1 encryption method with MD2 hash and RC2 CBC cipher. */
  PbeWithMD2AndRC2_CBC(
    "1.2.840.113549.1.5.4",
    new AlgorithmSpec("RC2", "CBC", "PKCS5Padding"),
    new MD2()),

  /** PBES1 encryption method with MD5 hash and DES CBC cipher. */
  PbeWithMD5AndDES_CBC(
    "1.2.840.113549.1.5.3",
    new AlgorithmSpec("DES", "CBC", "PKCS5Padding"),
    new MD5()),

  /** PBES1 encryption method with MD5 hash and RC2 CBC cipher. */
  PbeWithMD5AndRC2_CBC(
    "1.2.840.113549.1.5.6",
    new AlgorithmSpec("RC2", "CBC", "PKCS5Padding"),
    new MD5()),

  /** PBES1 encryption method with SHA1 hash and DES CBC cipher. */
  PbeWithSHA1AndDES_CBC(
    "1.2.840.113549.1.5.10",
    new AlgorithmSpec("DES", "CBC", "PKCS5Padding"),
    new SHA1()),

  /** PBES1 encryption method with SHA1 hash and RC2 CBC cipher. */
  PbeWithSHA1AndRC2_CBC(
    "1.2.840.113549.1.5.11",
    new AlgorithmSpec("RC2", "CBC", "PKCS5Padding"),
    new SHA1());


  /** Algorithm identifier OID. */
  private final String oid;

  /** Cipher algorithm specification. */
  private final AlgorithmSpec spec;

  /** Digest algorithm used for pseudo-random function. */
  private final DigestAlgorithm digest;


  /**
   * Creates a new instance with given parameters.
   *
   * @param  id  Algorithm OID.
   * @param  cipherSpec  Cipher algorithm specification.
   * @param  prf  Digest used for pseudorandom function.
   */
  PBES1Algorithm(
    final String id,
    final AlgorithmSpec cipherSpec,
    final DigestAlgorithm prf)
  {
    this.oid = id;
    this.spec = cipherSpec;
    this.digest = prf;
  }


  /**
   * Gets the PBE algorithm for the given object identifier.
   *
   * @param  oid  PBE algorithm OID.
   *
   * @return  Algorithm whose identifier equals given value.
   *
   * @throws  IllegalArgumentException  If no matching algorithm found.
   */
  public static PBES1Algorithm fromOid(final String oid)
  {
    for (PBES1Algorithm a : PBES1Algorithm.values()) {
      if (a.getOid().equals(oid)) {
        return a;
      }
    }
    throw new IllegalArgumentException("Unknown PBES1Algorithm for OID " + oid);
  }


  /** @return  the oid */
  public String getOid()
  {
    return oid;
  }


  /** @return  Cipher algorithm specification. */
  public AlgorithmSpec getSpec()
  {
    return spec;
  }


  /** @return  Digest algorithm. */
  public DigestAlgorithm getDigest()
  {
    return digest;
  }
}
