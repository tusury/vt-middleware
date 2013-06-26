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
package edu.vt.middleware.crypt.x509.types;

/**
 * Enumeration of supported OIDs for the <code>accessMethod</code> field of the
 * <code>AccessDescription</code> type described in section 4.2.2.1 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public enum AccessMethod {

  /** CA Issuers access method. */
  CAIssuers("1.3.6.1.5.5.7.48.2"),

  /** Online Certificate Status Protocol. */
  OCSP("1.3.6.1.5.5.7.48.1");


  /** Key purpose object identifier. */
  private final String oid;


  /**
   * Creates a new instance with the given OID.
   *
   * @param  objectId  Access method OID.
   */
  AccessMethod(final String objectId)
  {
    oid = objectId;
  }


  /** @return  Key purpose object identifier. */
  public String getOid()
  {
    return oid;
  }


  /**
   * Gets an access method by its OID.
   *
   * @param  oid  OID of access method to retrieve.
   *
   * @return  Access method whose OID matches given value.
   *
   * @throws  IllegalArgumentException  If there is no access method with the
   * given OID.
   */
  public static AccessMethod getByOid(final String oid)
  {
    for (AccessMethod id : values()) {
      if (id.getOid().equals(oid)) {
        return id;
      }
    }
    throw new IllegalArgumentException(
      "No access method defined with oid " + oid);
  }
}
