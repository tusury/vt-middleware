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
package edu.vt.middleware.crypt.x509;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import edu.vt.middleware.crypt.x509.types.AttributeType;
import edu.vt.middleware.crypt.x509.types.AttributeTypeAndValue;
import edu.vt.middleware.crypt.x509.types.RelativeDistinguishedName;

/**
 * Utility class with convenience methods for common operations performed on
 * X.500 distinguished names.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public final class DNUtils
{

  /**
   * Private constructor of utility class.
   */
  private DNUtils() {}


  /**
   * Gets the values of the given attribute contained in the DN.
   * <p>
   * <strong>NOTE:</strong> no escaping is done on special characters in the
   * values, which could be different from what would appear in the string
   * representation of the DN.
   * </p>
   *
   * @param  dn  X.500 distinguished name.
   * @param  attribute  Attribute whose values will be retrieved.
   *
   * @return  The attribute values for the given attribute in the order
   * they appear would appear in the string representation of the DN
   * or an empty array if the given attribute does not exist.
   */
  public static String[] getAttributeValues(
    final X500Principal dn,
    final AttributeType attribute)
  {
    return getAttributeValues(dn, attribute.getOid());
  }


  /**
   * Gets the values of the given attribute contained in the DN.
   * <p>
   * <strong>NOTE:</strong> no escaping is done on special characters in the
   * values, which could be different from what would appear in the string
   * representation of the DN.
   * </p>
   *
   * @param  dn  X.500 distinguished name.
   * @param  attributeOid  OID of attribute whose values will be retrieved.
   *
   * @return  The attribute values for the given attribute in the order
   * they appear would appear in the string representation of the DN
   * or an empty array if the given attribute does not exist.
   */
  public static String[] getAttributeValues(
    final X500Principal dn,
    final String attributeOid)
  {
    final RDNSequenceIterator rdnSeqIter =
      new RDNSequenceIterator(dn.getEncoded());
    final List<String> values = new ArrayList<String>();
    for (RelativeDistinguishedName rdn : rdnSeqIter) {
      for (AttributeTypeAndValue atv : rdn.getItems()) {
        if (atv.getType().equals(attributeOid)) {
          values.add(atv.getValue());
        }
      }
    }
    return values.toArray(new String[values.size()]);
  }


  /**
   * Gets the value of the first occurrence of the given attribute in the DN.
   * <p>
   * <strong>NOTE:</strong> no escaping is done on special characters in the
   * values, which could be different from what would appear in the string
   * representation of the DN.
   * </p>
   *
   * @param  dn  X.500 distinguished name.
   * @param  attribute  Attribute whose value will be retrieved.
   *
   * @return  Value of first occurrence of given attribute or null if the
   * attribute does not exist.  The first occurrence is determined by the
   * ordering that would result from the string representation of the DN.
   */
  public static String getAttributeValue(
    final X500Principal dn,
    final AttributeType attribute)
  {
    return getAttributeValue(dn, attribute.getOid());
  }


  /**
   * Gets the value of the first occurrence of the given attribute in the DN.
   * <p>
   * <strong>NOTE:</strong> no escaping is done on special characters in the
   * values, which could be different from what would appear in the string
   * representation of the DN.
   * </p>
   *
   * @param  dn  X.500 distinguished name.
   * @param  attributeOid  OID of attribute whose value will be retrieved.
   *
   * @return  Value of first occurrence of given attribute or null if the
   * attribute does not exist.  The first occurrence is determined by the
   * ordering that would result from the string representation of the DN.
   */
  public static String getAttributeValue(
    final X500Principal dn,
    final String attributeOid)
  {
    final RDNSequenceIterator rdnSeqIter =
      new RDNSequenceIterator(dn.getEncoded());
    for (RelativeDistinguishedName rdn : rdnSeqIter) {
      for (AttributeTypeAndValue atv : rdn.getItems()) {
        if (atv.getType().equals(attributeOid)) {
          return atv.getValue();
        }
      }
    }
    return null;
  }


  /**
   * Gets the CN attribute from the given X.500 distinguished name.
   * <p>
   * <strong>NOTE:</strong> no escaping is done on special characters in the
   * CN, which could be different from what would appear in the string
   * representation of the DN.
   * </p>
   *
   * @param  dn  DN from which to extract common name attribute.
   *
   * @return  Common name or null if no CN exists.
   * If the DN contains multiple CN attributes, only the first one is returned,
   * where the ordering of attributes is the same as that of the string
   * representation of the DN.
   */
  public static String getCN(final X500Principal dn)
  {
    return getAttributeValue(dn, AttributeType.CommonName);
  }
}
