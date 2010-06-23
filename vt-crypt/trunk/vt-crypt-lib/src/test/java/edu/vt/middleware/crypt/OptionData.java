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
package edu.vt.middleware.crypt;

/**
 * Encapsulates option and argument data for testing.
 *
 * @author  Middleware Services
 * @version  $Revision: 318 $
 */
public class OptionData
{

  /** Option name without "-" character. */
  private String option;

  /** Argument value. */
  private String argument;


  /** Creates a new instance with no data. */
  public OptionData() {}


  /**
   * Creates a new instance with the given option name. This form is for an
   * option that does not take an argument.
   *
   * @param  opt  Option name.
   * @param  arg  Argument value.
   */
  public OptionData(final String opt, final String arg)
  {
    setOption(opt);
    setArgument(arg);
  }

  /**
   * Creates a new instance with the given option name and argument value.
   *
   * @param  opt  Option name.
   */
  public OptionData(final String opt)
  {
    setOption(opt);
  }


  /** @return  the option */
  public String getOption()
  {
    return option;
  }


  /** @param  opt  the option to set */
  public void setOption(final String opt)
  {
    this.option = opt;
  }


  /** @return  the argument */
  public String getArgument()
  {
    return argument;
  }


  /**
   * Sets the command line option argument value. May be null if option does not
   * take an argument value.
   *
   * @param  arg  the argument to set
   */
  public void setArgument(final String arg)
  {
    this.argument = arg;
  }
}
