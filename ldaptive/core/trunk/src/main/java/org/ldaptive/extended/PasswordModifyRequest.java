/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.extended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ldaptive.AbstractRequest;
import org.ldaptive.Credential;
import org.ldaptive.asn1.ContextType;
import org.ldaptive.asn1.DEREncoder;
import org.ldaptive.asn1.SequenceEncoder;

/**
 * Contains the data required to perform an ldap password modify operation. See
 * RFC 3062. Request is defined as:
 *
 * <pre>
   PasswdModifyRequestValue ::= SEQUENCE {
     userIdentity    [0]  OCTET STRING OPTIONAL
     oldPasswd       [1]  OCTET STRING OPTIONAL
     newPasswd       [2]  OCTET STRING OPTIONAL }
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordModifyRequest extends AbstractRequest
  implements ExtendedRequest
{

  /** OID of this extended request. */
  public static final String OID = "1.3.6.1.4.1.4203.1.11.1";

  /** DN to modify. */
  private String modifyDn;

  /** Current password. */
  private Credential oldPassword;

  /** Desired password. */
  private Credential newPassword;


  /** Default constructor. */
  public PasswordModifyRequest() {}


  /**
   * Creates a new password modify request.
   *
   * @param  dn  to create
   */
  public PasswordModifyRequest(final String dn)
  {
    setDn(dn);
  }


  /**
   * Creates a new password modify request.
   *
   * @param  dn  to create
   * @param  oldPass  current password for the dn
   * @param  newPass  desired password for the dn
   */
  public PasswordModifyRequest(
    final String dn,
    final Credential oldPass,
    final Credential newPass)
  {
    setDn(dn);
    setOldPassword(oldPass);
    setNewPassword(newPass);
  }


  /**
   * Returns the DN to modify.
   *
   * @return  DN
   */
  public String getDn()
  {
    return modifyDn;
  }


  /**
   * Sets the DN to modify.
   *
   * @param  dn  to modify
   */
  public void setDn(final String dn)
  {
    modifyDn = dn;
  }


  /**
   * Returns the old password.
   *
   * @return  old password
   */
  public Credential getOldPassword()
  {
    return oldPassword;
  }


  /**
   * Sets the old password.
   *
   * @param  oldPass  to verify
   */
  public void setOldPassword(final Credential oldPass)
  {
    oldPassword = oldPass;
  }


  /**
   * Returns the new password.
   *
   * @return  new password
   */
  public Credential getNewPassword()
  {
    return newPassword;
  }


  /**
   * Sets the new password.
   *
   * @param  newPass  to set
   */
  public void setNewPassword(final Credential newPass)
  {
    newPassword = newPass;
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    final List<DEREncoder> l = new ArrayList<DEREncoder>();
    if (getDn() != null) {
      l.add(new ContextType(0, getDn()));
    }
    if (getOldPassword() != null) {
      l.add(new ContextType(1, getOldPassword().getString()));
    }
    if (getNewPassword() != null) {
      l.add(new ContextType(2, getNewPassword().getString()));
    }

    final SequenceEncoder se = new SequenceEncoder(
      l.toArray(new DEREncoder[l.size()]));
    return se.encode();
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::modifyDn=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        modifyDn,
        Arrays.toString(getControls()));
  }
}
