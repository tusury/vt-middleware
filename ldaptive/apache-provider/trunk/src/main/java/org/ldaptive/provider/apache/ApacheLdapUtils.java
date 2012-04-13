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
package org.ldaptive.provider.apache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.directory.shared.ldap.model.entry.Attribute;
import org.apache.directory.shared.ldap.model.entry.BinaryValue;
import org.apache.directory.shared.ldap.model.entry.DefaultAttribute;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.DefaultModification;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.entry.StringValue;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SortBehavior;
import org.ldaptive.control.RequestControl;
import org.ldaptive.provider.ControlProcessor;

/**
 * Provides methods for converting between Apache Ldap specific objects and
 * ldaptive specific objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapUtils
{

  /** Default binary attributes. */
  protected static final String[] DEFAULT_BINARY_ATTRS = new String[] {
    "userPassword",
    "jpegPhoto",
    "userCertificate",
  };

  /** Ldap result sort behavior. */
  private final SortBehavior sortBehavior;

  /** Attributes that should be treated as binary. */
  private List<String> binaryAttrs = Arrays.asList(DEFAULT_BINARY_ATTRS);


  /** Default constructor. */
  public ApacheLdapUtils()
  {
    sortBehavior = SortBehavior.getDefaultSortBehavior();
  }


  /**
   * Creates a new apache ldap util.
   *
   * @param  sb  sort behavior
   */
  public ApacheLdapUtils(final SortBehavior sb)
  {
    sortBehavior = sb;
  }


  /**
   * Returns the list of binary attributes.
   *
   * @return  list of binary attributes
   */
  public List<String> getBinaryAttributes()
  {
    return binaryAttrs;
  }


  /**
   * Sets the list of binary attributes.
   *
   * @param  s  binary attributes
   */
  public void setBinaryAttributes(final String[] s)
  {
    if (s != null) {
      binaryAttrs = Arrays.asList(s);
    }
  }


  /**
   * Returns an apache ldap value for the supplied object.
   *
   * @param  o  object value
   *
   * @return  apache ldap value
   */
  public static Value<?> createValue(final Object o)
  {
    if (o instanceof String) {
      return new StringValue((String) o);
    } else if (o instanceof byte[]) {
      return new BinaryValue((byte[]) o);
    } else {
      throw new IllegalArgumentException(
        "Unsupported attribute value type " + o.getClass());
    }
  }


  /**
   * Returns an apache ldap attribute that represents the values in the supplied
   * ldap attribute.
   *
   * @param  la  ldap attribute
   *
   * @return  apache ldap attribute
   */
  public Attribute fromLdapAttribute(final LdapAttribute la)
  {
    final DefaultAttribute attribute = new DefaultAttribute(la.getName());
    if (la.isBinary()) {
      for (byte[] value : la.getBinaryValues()) {
        attribute.add(createValue(value));
      }
    } else {
      for (String value : la.getStringValues()) {
        attribute.add(createValue(value));
      }
    }
    return attribute;
  }


  /**
   * Returns an ldap attribute using the supplied apache ldap attribute.
   *
   * @param  a  apache ldap attribute
   *
   * @return  ldap attribute
   */
  public LdapAttribute toLdapAttribute(final Attribute a)
  {
    boolean isBinary = false;
    if (a.getId().contains(";binary")) {
      isBinary = true;
    } else if (binaryAttrs != null && binaryAttrs.contains(a.getUpId())) {
      isBinary = true;
    } else if (!a.isHumanReadable()) {
      isBinary = true;
    }

    final LdapAttribute la = new LdapAttribute(sortBehavior, isBinary);
    la.setName(a.getUpId());
    for (Value<?> v : a) {
      if (isBinary) {
        la.addBinaryValue(v.getBytes());
      } else {
        la.addStringValue(v.getString());
      }
    }
    return la;
  }


  /**
   * Returns a list of apache ldap attribute that represents the values in the
   * supplied ldap attributes.
   *
   * @param  c  ldap attributes
   *
   * @return  apache ldap attributes
   */
  public Attribute[] fromLdapAttributes(final Collection<LdapAttribute> c)
  {
    final List<Attribute> attributes = new ArrayList<Attribute>();
    for (LdapAttribute a : c) {
      attributes.add(fromLdapAttribute(a));
    }
    return attributes.toArray(new Attribute[attributes.size()]);
  }


  /**
   * Returns an apache ldap entry that represents the supplied ldap entry.
   *
   * @param  le  ldap entry
   *
   * @return  apache ldap entry
   *
   * @throws  LdapException  if the apache object cannot be created
   */
  public Entry fromLdapEntry(final LdapEntry le)
    throws LdapException
  {
    final DefaultEntry entry = new DefaultEntry(le.getDn());
    entry.add(fromLdapAttributes(le.getAttributes()));
    return entry;
  }


  /**
   * Returns an ldap entry using the supplied apache ldap entry.
   *
   * @param  e  apache ldap entry
   *
   * @return  ldap entry
   */
  public LdapEntry toLdapEntry(final Entry e)
  {
    final LdapEntry le = new LdapEntry(sortBehavior);
    le.setDn(e.getDn().getName());
    for (Attribute a : e) {
      le.addAttribute(toLdapAttribute(a));
    }
    return le;
  }


  /**
   * Returns apache ldap modifications using the supplied attribute
   * modifications.
   *
   * @param  am  attribute modifications
   *
   * @return  apache ldap modifications
   */
  public Modification[] fromAttributeModification(
    final AttributeModification[] am)
  {
    final Modification[] mods = new Modification[am.length];
    for (int i = 0; i < am.length; i++) {
      mods[i] = new DefaultModification(
        getAttributeModification(am[i].getAttributeModificationType()),
        fromLdapAttribute(am[i].getAttribute()));
    }
    return mods;
  }


  /**
   * Returns the apache ldap modification operation for the supplied attribute
   * modification type.
   *
   * @param  am  attribute modification type
   *
   * @return  modification operation
   */
  protected static ModificationOperation getAttributeModification(
    final AttributeModificationType am)
  {
    ModificationOperation op = null;
    if (am == AttributeModificationType.ADD) {
      op = ModificationOperation.ADD_ATTRIBUTE;
    } else if (am == AttributeModificationType.REMOVE) {
      op = ModificationOperation.REMOVE_ATTRIBUTE;
    } else if (am == AttributeModificationType.REPLACE) {
      op = ModificationOperation.REPLACE_ATTRIBUTE;
    }
    return op;
  }


  /**
   * Retrieves the response controls from the supplied response.
   *
   * @param  response  to get controls from
   *
   * @return  response controls
   */
  public static Control[] getResponseControls(final Message response)
  {
    Control[] ctls = null;
    if (response != null) {
      final Map<String, Control> respControls = response.getControls();
      ctls = respControls.values().toArray(new Control[respControls.size()]);
    }
    return ctls;
  }


  /**
   * Retrieves the response controls from the supplied response and processes
   * them with the supplied control processor.
   *
   * @param  processor  control processor
   * @param  requestControls  that produced this response
   * @param  response  to get controls from
   *
   * @return  response controls
   */
  public static org.ldaptive.control.ResponseControl[]
  processResponseControls(
    final ControlProcessor<Control> processor,
    final RequestControl[] requestControls,
    final Message response)
  {
    return
      processor.processResponseControls(
        requestControls,
        getResponseControls(response));
  }
}
