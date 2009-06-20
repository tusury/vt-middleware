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
package edu.vt.middleware.ldap.search;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.dsml.Dsmlv1;
import edu.vt.middleware.ldap.dsml.Dsmlv2;
import edu.vt.middleware.ldap.ldif.Ldif;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <code>PeopleSearch</code> queries an LDAP and returns the result as DSML or
 * LDIF. Each instance of <code>PeopleSearch</code> maintains it's own pool of
 * LDAP connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class PeopleSearch
{

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(PeopleSearch.class);

  /** Valid DSML versions. */
  public enum OutputFormat {

    /** DSML version 1. */
    DSMLV1,

    /** DSML version 2. */
    DSMLV2,

    /** LDIF. */
    LDIF
  }

  /** Class used to perform queries. */
  private SearchInvoker searchInvoker;

  /** Dsml version 1 object. */
  private Dsmlv1 dsmlv1 = new Dsmlv1();

  /** Dsml version 2 object. */
  private Dsmlv2 dsmlv2 = new Dsmlv2();

  /** Ldif object. */
  private Ldif ldif = new Ldif();


  /** Default constructor. */
  public PeopleSearch() {}


  /**
   * This creates a new <code>PeopleSearch</code> with the supplied search
   * invoker.
   *
   * @param  si  <code>SearchInvoker</code>
   */
  public PeopleSearch(final SearchInvoker si)
  {
    this.searchInvoker = si;
  }


  /**
   * Creates a <code>PeopleSearch</code> using a spring bean configuration
   * context at the supplied path.
   *
   * @param  path  to context on the classpath
   *
   * @return  <code>PeopleSearch</code>
   */
  public static PeopleSearch createFromSpringContext(final String path)
  {
    final ApplicationContext context = new ClassPathXmlApplicationContext(path);
    return (PeopleSearch) context.getBean("peopleSearch");
  }


  /**
   * Sets the invoker used to search the ldap.
   *
   * @param  si  used for searching
   */
  public void setSearchInvoker(final SearchInvoker si)
  {
    this.searchInvoker = si;
  }


  /**
   * Returns the invoker used to search the ldap.
   *
   * @return  <code>SearchInvoker</code>
   */
  public SearchInvoker getSearchInvoker()
  {
    return this.searchInvoker;
  }


  /**
   * This will perform a LDAP search with the supplied <code>Query</code>. The
   * results will be written to the supplied <code>OutputStream</code>.
   *
   * @param  query  <code>Query</code> to search for
   * @param  format  <code>OutputFormat</code> to return
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  PeopleSearchException  if an error occurs while searching
   */
  public void search(
    final Query query,
    final OutputFormat format,
    final OutputStream out)
    throws PeopleSearchException
  {
    if (format == OutputFormat.DSMLV2) {
      try {
        this.dsmlv2.outputDsml(this.doSearch(query), out);
      } catch (Exception e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error outputting DSML", e);
        }
        throw new PeopleSearchException(e.getMessage());
      }
    } else if (format == OutputFormat.LDIF) {
      try {
        this.ldif.outputLdif(this.doSearch(query), out);
      } catch (Exception e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error outputting LDIF", e);
        }
        throw new PeopleSearchException(e.getMessage());
      }
    } else {
      try {
        this.dsmlv1.outputDsml(this.doSearch(query), out);
      } catch (Exception e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error outputting DSML", e);
        }
        throw new PeopleSearchException(e.getMessage());
      }
    }
  }


  /**
   * This will perform a LDAP search with the supplied <code>Query</code>.
   *
   * @param  query  <code>Query</code> to search for
   * @param  format  <code>OutputFormat</code> to return
   *
   * @return  <code>String</code> of results
   *
   * @throws  PeopleSearchException  if an error occurs while searching
   */
  public String searchToString(final Query query, final OutputFormat format)
    throws PeopleSearchException
  {
    final String results;
    if (format == OutputFormat.DSMLV2) {
      results = this.dsmlv2.outputDsmlToString(this.doSearch(query));
    } else if (format == OutputFormat.LDIF) {
      results = this.ldif.createLdif(this.doSearch(query));
    } else {
      results = this.dsmlv1.outputDsmlToString(this.doSearch(query));
    }
    return results;
  }


  /**
   * This provides command line access to a <code>PeopleSearch</code>.
   *
   * @param  args  <code>String[]</code>
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    final PeopleSearch ps = createFromSpringContext(
      "/peoplesearch-context.xml");
    final Query query = new Query();
    final List<String> attrs = new ArrayList<String>();

    try {
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-query")) {
          query.setLdapQuery(args[++i]);
        } else {
          attrs.add(args[i]);
        }
      }

      if (query.getLdapQuery() == null) {
        throw new ArrayIndexOutOfBoundsException();
      }

      if (!attrs.isEmpty()) {
        query.setQueryAttributes(attrs.toArray(new String[0]));
      }
      ps.search(query, OutputFormat.LDIF, System.out);

    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println(
        "Usage: java " + PeopleSearch.class.getName() +
        " -query <query> <attributes>");
      System.exit(1);
    }
  }


  /**
   * This will perform a LDAP search with the supplied <code>Query</code>.
   *
   * @param  query  <code>Query</code> to search for
   *
   * @return  <code>Iterator</code> of search results
   *
   * @throws  PeopleSearchException  if an error occurs while searching
   */
  private Iterator<SearchResult> doSearch(final Query query)
    throws PeopleSearchException
  {
    return this.searchInvoker.find(query);
  }
}
