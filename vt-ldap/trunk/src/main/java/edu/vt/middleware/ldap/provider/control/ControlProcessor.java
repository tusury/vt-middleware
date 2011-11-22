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
package edu.vt.middleware.ldap.provider.control;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.vt.middleware.ldap.control.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for invoking provider specific control processors.
 *
 * @param  <T>  type of provider specific control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ControlProcessor<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Request control handlers. */
  private Set<RequestControlHandler<T>> requestHandlers =
    new HashSet<RequestControlHandler<T>>();

  /** Response control handlers. */
  private Set<ResponseControlHandler<T>> responseHandlers =
    new HashSet<ResponseControlHandler<T>>();


  /** Default constructor. */
  public ControlProcessor() {}


  /**
   * Adds a request control handler to this control processor.
   *
   * @param  handler  to add
   */
  public void addRequestControlHandler(final RequestControlHandler<T> handler)
  {
    requestHandlers.add(handler);
  }


  /**
   * Adds a response control handler to this control processor.
   *
   * @param  handler  to add
   */
  public void addResponseControlHandler(final ResponseControlHandler<T> handler)
  {
    responseHandlers.add(handler);
  }


  /**
   * Converts the supplied request controls to a provider specific request
   * controls.
   *
   * @param  requestControls  to convert
   * @return  provider specific controls
   */
  @SuppressWarnings("unchecked")
  public T[] processRequestControls(final Control[] requestControls)
  {
    if (requestControls == null) {
      return null;
    }
    final List<T> providerCtls = new ArrayList<T>(requestControls.length);
    for (Control c : requestControls) {
      final T providerCtl = processRequest(c);
      if (providerCtl != null) {
        providerCtls.add(providerCtl);
      }
    }
    return !providerCtls.isEmpty() ?
      providerCtls.toArray(
        (T[]) Array.newInstance(
          providerCtls.iterator().next().getClass(), providerCtls.size())) :
      null;
  }


  /**
   * Converts the supplied control to a provider control.
   *
   * @param  ctl  to convert
   * @return  provider control
   */
  protected T processRequest(final Control ctl)
  {
    if (ctl == null) {
      return null;
    }
    T providerCtl = null;
    for (RequestControlHandler<T> ch : requestHandlers) {
      providerCtl = ch.processRequest(ctl);
      if (providerCtl != null) {
        break;
      }
    }
    if (providerCtl == null) {
      throw new UnsupportedOperationException(
        "Request Control not supported: " + ctl);
    }
    return providerCtl;
  }


  /**
   * Converts the supplied provider controls to a response controls. The
   * supplied request controls were used to produce the response.
   *
   * @param  requestControls  that produced the response
   * @param  responseControls  to convert
   * @return  controls
   */
  public Control[] processResponseControls(
    final Control[] requestControls, final T[] responseControls)
  {
    if (responseControls == null) {
      return null;
    }
    final List<Control> ctls = new ArrayList<Control>(responseControls.length);
    for (T c : responseControls) {
      final Control ctl = processResponse(requestControls, c);
      if (ctl != null) {
        ctls.add(ctl);
      }
    }
    return ctls.toArray(new Control[ctls.size()]);
  }


  /**
   * Converts the supplied provider control to a control.
   *
   * @param  requestControls  that produced the response controls
   * @param  providerCtl  to convert
   * @return  control
   */
  protected Control processResponse(
    final Control[] requestControls, final T providerCtl)
  {
    if (providerCtl == null) {
      return null;
    }
    Control ctl = null;
    for (ResponseControlHandler<T> ch : responseHandlers) {
      ctl = ch.processResponse(
        findControl(requestControls, ch.getOID()), providerCtl);
      if (ctl != null) {
        break;
      }
    }
    if (ctl == null) {
      throw new UnsupportedOperationException(
        "Response Control not supported: " + providerCtl);
    }
    return ctl;
  }


  /**
   * Searches the supplied array for a control that matches the supplied OID.
   *
   * @param  controls  to search
   * @param  oid  to search for
   * @return  control that matches the oid
   */
  public static Control findControl(
    final Control[] controls, final String oid)
  {
    if (controls == null) {
      return null;
    }
    Control match = null;
    for (Control c : controls) {
      if (c.getOID().equals(oid)) {
        match = c;
        break;
      }
    }
    return match;
  }
}