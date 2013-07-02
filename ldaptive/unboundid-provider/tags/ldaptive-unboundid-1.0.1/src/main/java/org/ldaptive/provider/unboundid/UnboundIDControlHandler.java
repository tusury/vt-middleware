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
package org.ldaptive.provider.unboundid;

import com.unboundid.asn1.ASN1Exception;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import org.ldaptive.control.ControlFactory;
import org.ldaptive.control.RequestControl;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlHandler;

/**
 * Unbound ID control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIDControlHandler
  implements ControlHandler<com.unboundid.ldap.sdk.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID(final com.unboundid.ldap.sdk.Control control)
  {
    return control.getOID();
  }


  /** {@inheritDoc} */
  @Override
  public com.unboundid.ldap.sdk.Control handleRequest(
    final RequestControl requestControl)
  {
    try {
      final byte[] value = requestControl.encode();
      if (value == null) {
        return
          new com.unboundid.ldap.sdk.Control(
            requestControl.getOID(),
            requestControl.getCriticality());
      } else {
        return
          new com.unboundid.ldap.sdk.Control(
            requestControl.getOID(),
            requestControl.getCriticality(),
            new ASN1OctetString(ASN1Sequence.decodeAsSequence(value).encode()));
      }
    } catch (ASN1Exception e) {
      throw new IllegalArgumentException(e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl handleResponse(
    final com.unboundid.ldap.sdk.Control responseControl)
  {
    return
      ControlFactory.createResponseControl(
        responseControl.getOID(),
        responseControl.isCritical(),
        responseControl.getValue().getValue());
  }
}
