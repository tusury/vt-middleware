/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.util;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Helper class with static methods for date/time conversions.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DateHelper
{

  /** Protected default constructor of utility class. */
  protected DateHelper() {}


  /**
   * Converts a SQL timestamp into a {@link Calendar}.
   *
   * @param  ts  Timestamp to convert.
   *
   * @return  Equivalent calendar.
   */
  public static Calendar toCalendar(final Timestamp ts)
  {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(ts.getTime());
    return cal;
  }


  /**
   * Converts a {@link Calendar} into a SQL timestamp.
   *
   * @param  cal  Calendar to convert.
   *
   * @return  Equivalent timestamp.
   */
  public static Timestamp toTimestamp(final Calendar cal)
  {
    final Timestamp ts = new Timestamp(cal.getTimeInMillis());
    return ts;
  }
}
