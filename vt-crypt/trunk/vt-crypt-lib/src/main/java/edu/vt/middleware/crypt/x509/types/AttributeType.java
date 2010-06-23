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
package edu.vt.middleware.crypt.x509.types;

/**
 * Describes the registered values of AttributeType that may appear in a
 * RelativeDistinguishedName as defined in section 2 of RFC 2253. The values
 * here were obtained from http://www.iana.org/assignments/ldap-parameters and
 * only contain the attribute types most likely to appear in an RDN.
 *
 * @author  Middleware Services
 * @version  $Revision: 578 $
 */
public enum AttributeType {

  /** CN attribute type. */
  CommonName("2.5.4.3", "CN"),

  /** C attribute type. */
  CountryName("2.5.4.6", "C"),

  /** DNQUALIFIER attribute type. */
  DnQualifier("2.5.4.46", "DNQUALIFIER"),

  /** DC attribute type. */
  DomainComponent("0.9.2342.19200300.100.1.25", "DC"),

  /** GIVENNAME attribute type. */
  GivenName("2.5.4.42", "GIVENNAME"),

  /** INITIALS attribute type. */
  Initials("2.5.4.43", "INITIALS"),

  /** L attribute type. */
  LocalityName("2.5.4.7", "L"),

  /** MAIL attribute type. */
  Mail("0.9.2342.19200300.100.1.3", "MAIL"),

  /** NAME attribute type. */
  Name("2.5.4.41", "NAME"),

  /** O attribute type. */
  OrganizationName("2.5.4.10", "O"),

  /** OU attribute type. */
  OrganizationalUnitName("2.5.4.11", "OU"),

  /** POSTALADDRESS attribute type. */
  PostalAddress("2.5.4.16", "POSTALADDRESS"),

  /** POSTALCODE attribute type. */
  PostalCode("2.5.4.17", "POSTALCODE"),

  /** POSTOFFICEBOX attribute type. */
  PostOfficeBox("2.5.4.18", "POSTOFFICEBOX"),

  /** SERIALNUMBER attribute type. */
  SerialNumber("2.5.4.5", "SERIALNUMBER"),

  /** ST attribute type. */
  StateOrProvinceName("2.5.4.8", "ST"),

  /** STREET attribute type. */
  StreetAddress("2.5.4.9", "STREET"),

  /** SN attribute type. */
  Surname("2.5.4.4", "STREET"),

  /** TITLE attribute type. */
  Title("2.5.4.12", "TITLE"),

  /** UNIQUEIDENTIFIER attribute type. */
  UniqueIdentifier("0.9.2342.19200300.100.1.44", "UNIQUEIDENTIFIER"),

  /** UID attribute type. */
  UserId("0.9.2342.19200300.100.1.1", "UID");


  /** OID of RDN attribute type. */
  private String oid;

  /** Display string of the type in an RDN. */
  private String name;


  /**
   * Creates a new type for the given OID.
   *
   * @param  attributeTypeOid  OID of attribute type.
   * @param  shortName  Registered short name for the attribute type.
   */
  AttributeType(final String attributeTypeOid, final String shortName)
  {
    oid = attributeTypeOid;
    name = shortName;
  }


  /** @return  OID of attribute type. */
  public String getOid()
  {
    return oid;
  }


  /** @return  Registered short name of attribute type. */
  public String getName()
  {
    return name;
  }


  /**
   * Gets the attribute type whose OID is the given string.
   *
   * @param  oid  OID of attribute type to get.
   *
   * @return  Attribute type whose OID matches given value or null if there is
   * no registered attribute type with the given OID.
   */
  public static AttributeType fromOid(final String oid)
  {
    for (AttributeType t : AttributeType.values()) {
      if (t.getOid().equals(oid)) {
        return t;
      }
    }
    return null;
  }
}
