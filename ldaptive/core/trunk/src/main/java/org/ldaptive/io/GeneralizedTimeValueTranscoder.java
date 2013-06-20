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
package org.ldaptive.io;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import org.ldaptive.LdapUtils;

/**
 * Decodes and encodes a generalized time for use in an ldap attribute value.
 * See http://tools.ietf.org/html/rfc4517#section-3.3.13
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class GeneralizedTimeValueTranscoder implements ValueTranscoder<Calendar>
{

  /** Thread local container holding date format which is not thread safe. */
  private static final ThreadLocal<DateFormat> DATE_FORMAT =
    new ThreadLocal<DateFormat>() {


      /** {@inheritDoc} */
      @Override
      protected DateFormat initialValue()
      {
        final SimpleDateFormat format = new SimpleDateFormat(
          "yyyyMMddHHmmss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
      }
    };


  /** {@inheritDoc} */
  @Override
  public Calendar decodeStringValue(final String value)
  {
    try {
      return parseGeneralizedTime(value);
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public Calendar decodeBinaryValue(final byte[] value)
  {
    return decodeStringValue(LdapUtils.utf8Encode(value));
  }


  /** {@inheritDoc} */
  @Override
  public String encodeStringValue(final Calendar value)
  {
    final DateFormat format = DATE_FORMAT.get();
    return format.format(value.getTime());
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encodeBinaryValue(final Calendar value)
  {
    return LdapUtils.utf8Encode(encodeStringValue(value));
  }


  /** {@inheritDoc} */
  @Override
  public Class<Calendar> getType()
  {
    return Calendar.class;
  }


  /**
   * Parses the supplied value and sets a calendar with the appropriate fields.
   *
   * @param  value  of generalized time to parse
   *
   * @return  calendar initialized to the correct time
   *
   * @throws  ParseException  if the value does not contain correct generalized
   * time syntax
   */
  protected Calendar parseGeneralizedTime(final String value)
    throws ParseException
  {
    // CheckStyle:MagicNumber OFF
    if (value == null || value.length() < 11) {
      throw new ParseException(
        "Generalized time must be at least 11 characters long",
        value == null ? 0 : value.length());
    }
    // CheckStyle:MagicNumber ON
    final Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(0);
    calendar.setLenient(false);

    boolean foundTimeZone = false;
    int pos = 0;
    try {
      while (pos < value.length()) {
        final char c = value.charAt(pos);
        if (c >= '0' && c <= '9') {
          String datePart;
          // CheckStyle:MagicNumber OFF
          switch (pos) {
          case 0:
            datePart = value.substring(pos, pos + 4);
            calendar.set(Calendar.YEAR, Integer.parseInt(datePart));
            break;
          case 4:
            datePart = value.substring(pos, pos + 2);
            calendar.set(Calendar.MONTH, Integer.parseInt(datePart) - 1);
            break;
          case 6:
            datePart = value.substring(pos, pos + 2);
            calendar.set(Calendar.DATE, Integer.parseInt(datePart));
            break;
          case 8:
            datePart = value.substring(pos, pos + 2);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(datePart));
            break;
          case 10:
            datePart = value.substring(pos, pos + 2);
            calendar.set(Calendar.MINUTE, Integer.parseInt(datePart));
            break;
          case 12:
            datePart = value.substring(pos, pos + 2);
            calendar.set(Calendar.SECOND, Integer.parseInt(datePart));
            break;
          default:
            throw new ParseException(
              String.format("Error parsing generalized time %s", value), pos);
          }
          // CheckStyle:MagicNumber ON
          pos += datePart.length();
        } else if (c == '.' || c == ',') {
          final String digits = parseDigits(value.substring(pos + 1));
          setFraction(calendar, digits, pos);
          pos += digits.length() + 1;
        } else if (c == 'Z' || c == '+' || c == '-') {
          final String tz = value.substring(pos);
          try {
            setTimeZone(calendar, tz);
          } catch (ParseException e) {
            throw new ParseException(
              String.format(
                "Invalid timezone for %s at position %s", value, pos),
              pos);
          }
          foundTimeZone = true;
          pos += tz.length();
        } else {
          throw new ParseException(
            String.format(
              "Invalid character for %s at position %s", value, pos),
            pos);
        }
      }
    } catch (NumberFormatException e) {
      throw new ParseException(
        String.format("Expected integer for %s at position %s", value, pos),
        pos);
    } catch (StringIndexOutOfBoundsException e) {
      throw new ParseException(
        String.format(
          "Incorrect integer length for %s at position %s", value, pos),
        pos);
    }

    if (!foundTimeZone) {
      throw new ParseException(
        String.format("No timezone found for %s", value), pos);
    }

    // force calendar to calculate
    try {
      calendar.getTimeInMillis();
    } catch (IllegalArgumentException e) {
      throw new ParseException(e.getMessage(), 0);
    }
    calendar.setLenient(true);
    return calendar;
  }


  /**
   * Parses a fraction and set the minute, second, and millisecond fields of the
   * supplied calendar appropriately. Which fields are set is determined by the
   * supplied position.
   *
   * @param  calendar  to set fields on
   * @param  value  of the fraction to parse
   * @param  pos  of the fraction in the generalized time string
   *
   * @throws  ParseException  if the fraction cannot be read from the value
   */
  private void setFraction(
    final Calendar calendar,
    final String value,
    final int pos)
    throws ParseException
  {
    if (value.isEmpty()) {
      throw new ParseException("Fraction length cannot be zero", pos);
    }

    double fraction;
    try {
      fraction = Double.parseDouble(String.format("0.%s", value));
    } catch (NumberFormatException e) {
      throw new ParseException(
        String.format("Error parsing fraction %s at position %s", value, pos),
        pos);
    }

    // CheckStyle:MagicNumber OFF
    switch (pos) {
    case 10:
      final double minOfHour = fraction * 60;
      final double secOfHour = (minOfHour - Math.floor(minOfHour)) * 60;
      final double msecOfHour = Math.round(
        (secOfHour - Math.floor(secOfHour)) * 1000);
      calendar.set(Calendar.MINUTE, (int) minOfHour);
      calendar.set(Calendar.SECOND, (int) secOfHour);
      calendar.set(Calendar.MILLISECOND, (int) msecOfHour);
      break;
    case 12:
      final double secOfMin = fraction * 60;
      final double msecOfMin = Math.round(
        (secOfMin - Math.floor(secOfMin)) * 1000);
      calendar.set(Calendar.SECOND, (int) secOfMin);
      calendar.set(Calendar.MILLISECOND, (int) msecOfMin);
      break;
    case 14:
      final double msecOfSec = fraction * 1000;
      calendar.set(Calendar.MILLISECOND, (int) msecOfSec);
      break;
    default:
      throw new ParseException(
        String.format(
          "Error parsing generalized time fraction %s at position %s",
          value,
          pos),
        pos);
    }
    // CheckStyle:MagicNumber ON
  }


  /**
   * Parses a timezone from the supplied value and sets it on the supplied
   * calendar. A value of 'Z' corresponds to 'UTC'.
   *
   * @param  calendar  to set the timezone for
   * @param  value  to parse the timezone from
   *
   * @throws  ParseException  if a timezone cannot be read from the value
   */
  private void setTimeZone(final Calendar calendar, final String value)
    throws ParseException
  {
    // CheckStyle:MagicNumber OFF
    if ("Z".equals(value)) {
      calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    } else if (value.startsWith("-") || value.startsWith("+")) {
      if (value.length() != 3 && value.length() != 5) {
        throw new ParseException(
          String.format("Invalid timezone %s", value), 0);
      }
      final TimeZone tz = TimeZone.getTimeZone("GMT" + value);
      if (tz.getRawOffset() == 0) {
        if (value.length() == 3 && !value.endsWith("00")) {
          throw new ParseException(
            String.format("Invalid timezone %s", value), 0);
        } else if (value.length() == 5 && !value.endsWith("0000")) {
          throw new ParseException(
            String.format("Invalid timezone %s", value), 0);
        }
      }
      calendar.setTimeZone(tz);
    } else {
      throw new ParseException(
        String.format("Could not parse timezone %s", value), 0);
    }
    // CheckStyle:MagicNumber ON
  }


  /**
   * Reads all the digits from the beginning of the supplied value until a
   * non-digit is found.
   *
   * @param  value  to read digits from
   *
   * @return  digits found at the beginning of value or an empty string
   */
  private String parseDigits(final String value)
  {
    final StringBuilder sb = new StringBuilder();
    int pos = 0;
    while (pos < value.length()) {
      final char c = value.charAt(pos);
      if (c >= '0' && c <= '9') {
        sb.append(c);
      } else {
        break;
      }
      pos++;
    }
    return sb.toString();
  }
}
