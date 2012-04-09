/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import java.nio.charset.Charset;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Contains functions common to all tests.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractTest
{

  /** Number of threads for threaded tests. */
  public static final int TEST_THREAD_POOL_SIZE = 2;

  /** Invocation count for threaded tests. */
  public static final int TEST_INVOCATION_COUNT = 10;

  /** Timeout for threaded tests. */
  public static final int TEST_TIME_OUT = 60000;

  /** Add the BC provider. */
  static {
    Security.addProvider(new BouncyCastleProvider());
  }


  /**
   * Creates the supplied ldap entry and confirms it exists in the ldap.
   *
   * @param  entry  to create.
   *
   * @throws  Exception  On failure.
   */
  public void createLdapEntry(final LdapEntry entry)
    throws Exception
  {
    Connection conn = TestUtils.createSetupConnection();
    try {
      conn.open();
      final AddOperation create = new AddOperation(conn);
      create.execute(new AddRequest(entry.getDn(), entry.getAttributes()));
      while (!entryExists(conn, entry)) {
        Thread.sleep(100);
      }
      if (TestControl.isActiveDirectory() &&
          entry.getAttribute("userPassword") != null) {
        final ModifyOperation modify = new ModifyOperation(conn);
        modify.execute(
          new ModifyRequest(
            entry.getDn(),
            new AttributeModification(
              AttributeModificationType.REPLACE,
              new LdapAttribute("unicodePwd", createADPassword(entry))),
            new AttributeModification(
              AttributeModificationType.REPLACE,
              new LdapAttribute("userAccountControl", "512"))));
      }
    } catch (LdapException e) {
      // ignore entry already exists
      if (ResultCode.ENTRY_ALREADY_EXISTS != e.getResultCode()) {
        throw e;
      }
    } finally {
      conn.close();
    }
  }


  /**
   * Deletes the supplied dn if it exists.
   *
   * @param  dn  to delete
   *
   * @throws  Exception  On failure.
   */
  public void deleteLdapEntry(final String dn)
    throws Exception
  {
    final Connection conn = TestUtils.createSetupConnection();
    try {
      conn.open();
      if (entryExists(conn, new LdapEntry(dn))) {
        final DeleteOperation delete = new DeleteOperation(conn);
        delete.execute(new DeleteRequest(dn));
      }
    } finally {
      conn.close();
    }
  }


  /**
   * Performs a compare on the supplied entry to determine if it exists in the
   * LDAP.
   *
   * @param  conn  to perform compare with
   * @param  entry  to perform compare on
   * @return  whether the supplied entry exists
   * @throws  Exception  On failure.
   */
  protected boolean entryExists(final Connection conn, final LdapEntry entry)
    throws Exception
  {
    final CompareOperation compare = new CompareOperation(conn);
    final LdapAttribute la = new LdapAttribute();
    la.setName(entry.getDn().split(",ou=")[0].split("=", 2)[0]);
    la.addStringValue(
      entry.getDn().split(",ou=")[0].split("=", 2)[1].replaceAll("\\\\", ""));
    try {
      return compare.execute(new CompareRequest(entry.getDn(), la)).getResult();
    } catch (LdapException e) {
      if (ResultCode.NO_SUCH_OBJECT == e.getResultCode()) {
        return false;
      }
      throw e;
    }
  }


  /**
   * Creates a password in the format required by Active Directory for the
   * supplied entry. Test data uses a password of the form 'password${uid}'.
   *
   * @param  entry  to create password for
   *
   * @return  AD password
   */
  protected byte[] createADPassword(final LdapEntry entry)
  {
    final String pwd = String.format(
      "\"password%s\"", entry.getAttribute("uid").getStringValue());
    return pwd.getBytes(Charset.forName("UTF-16LE"));
  }
}
