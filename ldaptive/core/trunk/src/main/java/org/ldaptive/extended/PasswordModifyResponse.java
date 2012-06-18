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

import java.nio.ByteBuffer;
import org.ldaptive.Credential;
import org.ldaptive.asn1.DERParser;
import org.ldaptive.asn1.DERPath;
import org.ldaptive.asn1.OctetStringType;
import org.ldaptive.asn1.ParseHandler;

/**
 * Contains the response from an ldap password modify operation. See RFC 3062.
 * Response is defined as:
 *
 * <pre>
 * PasswdModifyResponseValue ::= SEQUENCE {
 *   genPasswd       [0]     OCTET STRING OPTIONAL }
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordModifyResponse extends AbstractExtendedResponse<Credential>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    // RFC defines the response name as absent
    return null;
  }



  /** {@inheritDoc} */
  @Override
  public void decode(final byte[] encoded)
  {
    final PasswordModifyResponseHandler handler =
      new PasswordModifyResponseHandler(this);
    final DERParser parser = new DERParser();
    parser.registerHandler(
      PasswordModifyResponseHandler.SEQ_PATH, handler);
    parser.parse(ByteBuffer.wrap(encoded));
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d]",
        getClass().getName(),
        hashCode());
  }


  /** Parse handler implementation for the password modify response. */
  private static class PasswordModifyResponseHandler implements ParseHandler
  {

    /** DER path to generated password. */
    public static final DERPath SEQ_PATH = new DERPath("/SEQ");

    /** Generated password constant. */
    private static final byte GEN_PASS = (byte) 0x80;

    /** Password modify response to configure with this handler. */
    private final PasswordModifyResponse modifyResponse;


    /**
     * Creates a new password modify response handler.
     *
     * @param  response  to configure
     */
    public PasswordModifyResponseHandler(final PasswordModifyResponse response)
    {
      modifyResponse = response;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final DERParser parser, final ByteBuffer encoded)
    {
      if (SEQ_PATH.equals(parser.getCurrentPath())) {
        final byte tag = encoded.get();
        encoded.limit(parser.readLength(encoded) + encoded.position());
        if (tag == GEN_PASS) {
          modifyResponse.setValue(
            new Credential(OctetStringType.decode(encoded)));
        } else {
          throw new IllegalArgumentException("Unknown genPasswd tag " + tag);
        }
      }
    }
  }
}
