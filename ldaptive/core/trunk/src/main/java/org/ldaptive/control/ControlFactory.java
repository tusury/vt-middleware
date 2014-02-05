/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.control;

import org.ldaptive.ad.control.DirSyncControl;
import org.ldaptive.ad.control.GetStatsControl;

/**
 * Utility class for creating controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class ControlFactory
{


  /** Default constructor. */
  private ControlFactory() {}


  /**
   * Creates a response control from the supplied control data.
   *
   * @param  oid  of the control
   * @param  critical  whether the control is critical
   * @param  encoded  BER encoding of the control
   *
   * @return  response control
   */
  public static ResponseControl createResponseControl(
    final String oid,
    final boolean critical,
    final byte[] encoded)
  {
    ResponseControl ctl;
    if (SortResponseControl.OID.equals(oid)) {
      ctl = new SortResponseControl(critical);
      ctl.decode(encoded);
    } else if (PagedResultsControl.OID.equals(oid)) {
      ctl = new PagedResultsControl(critical);
      ctl.decode(encoded);
    } else if (VirtualListViewResponseControl.OID.equals(oid)) {
      ctl = new VirtualListViewResponseControl(critical);
      ctl.decode(encoded);
    } else if (PasswordPolicyControl.OID.equals(oid)) {
      ctl = new PasswordPolicyControl(critical);
      ctl.decode(encoded);
    } else if (SyncStateControl.OID.equals(oid)) {
      ctl = new SyncStateControl(critical);
      ctl.decode(encoded);
    } else if (SyncDoneControl.OID.equals(oid)) {
      ctl = new SyncDoneControl(critical);
      ctl.decode(encoded);
    } else if (DirSyncControl.OID.equals(oid)) {
      ctl = new DirSyncControl(critical);
      ctl.decode(encoded);
    } else if (EntryChangeNotificationControl.OID.equals(oid)) {
      ctl = new EntryChangeNotificationControl(critical);
      ctl.decode(encoded);
    } else if (GetStatsControl.OID.equals(oid)) {
      ctl = new GetStatsControl(critical);
      ctl.decode(encoded);
    } else if (PasswordExpiredControl.OID.equals(oid)) {
      ctl = new PasswordExpiredControl(critical);
      ctl.decode(encoded);
    } else if (PasswordExpiringControl.OID.equals(oid)) {
      ctl = new PasswordExpiringControl(critical);
      ctl.decode(encoded);
    } else {
      throw new IllegalArgumentException("Unknown OID: " + oid);
    }
    return ctl;
  }
}
