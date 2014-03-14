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
package org.ldaptive.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.LdapUtils;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.SearchExecutor;
import org.ldaptive.SearchResult;
import org.ldaptive.SearchScope;
import org.ldaptive.io.LdifReader;
import org.ldaptive.schema.io.AttributeTypeValueTranscoder;
import org.ldaptive.schema.io.DITContentRuleValueTranscoder;
import org.ldaptive.schema.io.DITStructureRuleValueTranscoder;
import org.ldaptive.schema.io.MatchingRuleUseValueTranscoder;
import org.ldaptive.schema.io.MatchingRuleValueTranscoder;
import org.ldaptive.schema.io.NameFormValueTranscoder;
import org.ldaptive.schema.io.ObjectClassValueTranscoder;
import org.ldaptive.schema.io.SyntaxValueTranscoder;

/**
 * Bean that contains the schema definitions in RFC 4512.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Schema
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 1181;

  /**
   * Attribute on the root DSE indicating the location of the subschema entry.
   */
  private static final String SUBSCHEMA_SUBENTRY_ATTR_NAME =
    "subschemaSubentry";

  /** Attribute types attribute name on the subschema entry. */
  private static final String ATTRIBUTE_TYPES_ATTR_NAME = "attributeTypes";

  /** DIT content rules attribute name on the subschema entry. */
  private static final String DIT_CONTENT_RULES_ATTR_NAME = "dITContentRules";

  /** DIT structure rules attribute name on the subschema entry. */
  private static final String DIT_STRUCTURE_RULES_ATTR_NAME =
    "dITStructureRules";

  /** LDAP syntaxes attribute name on the subschema entry. */
  private static final String LDAP_SYNTAXES_ATTR_NAME = "ldapSyntaxes";

  /** Matching rules attribute name on the subschema entry. */
  private static final String MATCHING_RULES_ATTR_NAME = "matchingRules";

  /** Matching rule use attribute name on the subschema entry. */
  private static final String MATCHING_RULE_USE_ATTR_NAME = "matchingRuleUse";

  /** Name forms attribute name on the subschema entry. */
  private static final String NAME_FORMS_ATTR_NAME = "nameForms";

  /** Object classes attribute name on the subschema entry. */
  private static final String OBJECT_CLASS_ATTR_NAME = "objectClasses";

  /** Attribute types. */
  private Collection<AttributeType> attributeTypes;

  /** DIT content rules. */
  private Collection<DITContentRule> ditContentRules;

  /** DIT structure rules. */
  private Collection<DITStructureRule> ditStructureRules;

  /** Syntaxes. */
  private Collection<Syntax> syntaxes;

  /** Matching rules. */
  private Collection<MatchingRule> matchingRules;

  /** Matching rule uses. */
  private Collection<MatchingRuleUse> matchingRuleUses;

  /** Name forms. */
  private Collection<NameForm> nameForms;

  /** Object classes.*/
  private Collection<ObjectClass> objectClasses;


  /**
   * Creates a new schema. The input stream should contain the LDIF for the
   * subschema entry.
   *
   * @param  is  containing the schema ldif
   *
   * @throws  IOException  if an error occurs reading the input stream
   */
  public Schema(final InputStream is)
    throws IOException
  {
    final LdifReader reader = new LdifReader(new InputStreamReader(is));
    initialize(reader.read().getEntry());
  }


  /**
   * Creates a new schema. The subschema subentry is searched for on the root
   * DSE, followed by searching for the subschema entry itself.
   *
   * @param  factory  to obtain an LDAP connection from
   *
   * @throws  LdapException  if the search fails
   */
  public Schema(final ConnectionFactory factory)
    throws LdapException
  {
    final LdapEntry rootDSE = getLdapEntry(
      factory,
      "",
      "(objectClass=*)",
      new String[]{SUBSCHEMA_SUBENTRY_ATTR_NAME});
    final String entryDn = rootDSE.getAttribute(
      SUBSCHEMA_SUBENTRY_ATTR_NAME).getStringValue();
    initialize(
      getLdapEntry(
        factory,
        entryDn,
        "(objectClass=subSchema)",
        ReturnAttributes.ALL.value()));
  }


  /**
   * Creates a new schema. The entryDn is searched to obtain the schema.
   *
   * @param  factory  to obtain an LDAP connection from
   * @param  entryDn  the subschema entry
   *
   * @throws  LdapException  if the search fails
   */
  public Schema(final ConnectionFactory factory, final String entryDn)
    throws LdapException
  {
    initialize(
      getLdapEntry(
        factory,
        entryDn,
        "(objectClass=subSchema)",
        ReturnAttributes.ALL.value()));
  }


  /**
   * Creates a new schema.
   *
   * @param  schemaEntry  containing the schema
   */
  public Schema(final LdapEntry schemaEntry)
  {
    initialize(schemaEntry);
  }


  /**
   * Searches for the supplied dn and returns its ldap entry.
   *
   * @param  factory  to obtain an LDAP connection from
   * @param  dn  to search for
   * @param  filter  search filter
   * @param  retAttrs  attributes to return
   *
   * @return  ldap entry
   *
   * @throws  LdapException  if the search fails
   */
  protected LdapEntry getLdapEntry(
    final ConnectionFactory factory,
    final String dn,
    final String filter,
    final String[] retAttrs)
    throws LdapException
  {
    final SearchExecutor executor = new SearchExecutor();
    executor.setBaseDn(dn);
    executor.setSearchScope(SearchScope.OBJECT);
    executor.setReturnAttributes(retAttrs);
    final SearchResult result = executor.search(factory, filter).getResult();
    return result.getEntry();
  }


  /**
   * Initializes the underlying collections in this bean with the attribute
   * values contained in the supplied schema entry.
   *
   * @param  schemaEntry  to read schema attributes from
   */
  protected void initialize(final LdapEntry schemaEntry)
  {
    if (schemaEntry == null) {
      throw new IllegalArgumentException("Schema entry cannot be null");
    }
    final LdapAttribute atAttr = schemaEntry.getAttribute(
      ATTRIBUTE_TYPES_ATTR_NAME);
    if (atAttr != null) {
      attributeTypes = atAttr.getValues(new AttributeTypeValueTranscoder());
    } else {
      attributeTypes = Collections.emptySet();
    }

    final LdapAttribute dcrAttr = schemaEntry.getAttribute(
      DIT_CONTENT_RULES_ATTR_NAME);
    if (dcrAttr != null) {
      ditContentRules = dcrAttr.getValues(new DITContentRuleValueTranscoder());
    } else {
      ditContentRules = Collections.emptySet();
    }

    final LdapAttribute dsrAttr = schemaEntry.getAttribute(
      DIT_STRUCTURE_RULES_ATTR_NAME);
    if (dsrAttr != null) {
      ditStructureRules = dsrAttr.getValues(
        new DITStructureRuleValueTranscoder());
    } else {
      ditStructureRules = Collections.emptySet();
    }

    final LdapAttribute sAttr = schemaEntry.getAttribute(
      LDAP_SYNTAXES_ATTR_NAME);
    if (sAttr != null) {
      syntaxes = sAttr.getValues(new SyntaxValueTranscoder());
    } else {
      syntaxes = Collections.emptySet();
    }

    final LdapAttribute mrAttr = schemaEntry.getAttribute(
      MATCHING_RULES_ATTR_NAME);
    if (mrAttr != null) {
      matchingRules = mrAttr.getValues(new MatchingRuleValueTranscoder());
    } else {
      matchingRules = Collections.emptySet();
    }

    final LdapAttribute mruAttr = schemaEntry.getAttribute(
      MATCHING_RULE_USE_ATTR_NAME);
    if (mruAttr != null) {
      matchingRuleUses = mruAttr.getValues(
        new MatchingRuleUseValueTranscoder());
    } else {
      matchingRuleUses = Collections.emptySet();
    }

    final LdapAttribute nfAttr = schemaEntry.getAttribute(
      NAME_FORMS_ATTR_NAME);
    if (nfAttr != null) {
      nameForms = nfAttr.getValues(new NameFormValueTranscoder());
    } else {
      nameForms = Collections.emptySet();
    }

    final LdapAttribute ocAttr = schemaEntry.getAttribute(
      OBJECT_CLASS_ATTR_NAME);
    if (ocAttr != null) {
      objectClasses = ocAttr.getValues(new ObjectClassValueTranscoder());
    } else {
      objectClasses = Collections.emptySet();
    }
  }


  /**
   * Returns the attribute types.
   *
   * @return  attribute types
   */
  public Collection<AttributeType> getAttributeTypes()
  {
    return attributeTypes;
  }


  /**
   * Returns the attribute type with the supplied OID or name.
   *
   * @param  name  OID or name
   *
   * @return  attribute type or null if name does not exist
   */
  public AttributeType getAttributeType(final String name)
  {
    for (AttributeType at : attributeTypes) {
      if (at.getOID().equals(name) || at.hasName(name)) {
        return at;
      }
    }
    return null;
  }


  /**
   * Returns the DIT content rules.
   *
   * @return  DIT content rules
   */
  public Collection<DITContentRule> getDitContentRules()
  {
    return ditContentRules;
  }


  /**
   * Returns the DIT content rule with the supplied OID or name.
   *
   * @param  name  OID or name
   *
   * @return  DIT content rule or null if name does not exist
   */
  public DITContentRule getDITContentRule(final String name)
  {
    for (DITContentRule rule : ditContentRules) {
      if (rule.getOID().equals(name) || rule.hasName(name)) {
        return rule;
      }
    }
    return null;
  }


  /**
   * Returns the DIT structure rules.
   *
   * @return  DIT structure rules
   */
  public Collection<DITStructureRule> getDitStructureRules()
  {
    return ditStructureRules;
  }


  /**
   * Returns the DIT structure rule with the supplied ID.
   *
   * @param  id  rule ID
   *
   * @return  DIT structure rule or null if id does not exist
   */
  public DITStructureRule getDITStructureRule(final int id)
  {
    for (DITStructureRule rule : ditStructureRules) {
      if (rule.getID() == id) {
        return rule;
      }
    }
    return null;
  }


  /**
   * Returns the DIT structure rule with the supplied name.
   *
   * @param  name  rule name
   *
   * @return  DIT structure rule or null if name does not exist
   */
  public DITStructureRule getDITStructureRule(final String name)
  {
    for (DITStructureRule rule : ditStructureRules) {
      if (rule.hasName(name)) {
        return rule;
      }
    }
    return null;
  }


  /**
   * Returns the syntaxes
   *
   * @return  syntaxes
   */
  public Collection<Syntax> getSyntaxes()
  {
    return syntaxes;
  }


  /**
   * Returns the syntax with the supplied OID.
   *
   * @param  oid  OID
   *
   * @return  syntax or null if OID does not exist
   */
  public Syntax getSyntax(final String oid)
  {
    for (Syntax syntax : syntaxes) {
      if (syntax.getOID().equals(oid)) {
        return syntax;
      }
    }
    return null;
  }


  /**
   * Returns the matching rules.
   *
   * @return  matching rules
   */
  public Collection<MatchingRule> getMatchingRules()
  {
    return matchingRules;
  }


  /**
   * Returns the matching rule with the supplied OID or name.
   *
   * @param  name  OID or name
   *
   * @return  matching rule or null if name does not exist
   */
  public MatchingRule getMatchingRule(final String name)
  {
    for (MatchingRule rule : matchingRules) {
      if (rule.getOID().equals(name) || rule.hasName(name)) {
        return rule;
      }
    }
    return null;
  }


  /**
   * Returns the matching rule uses.
   *
   * @return  matching rule ueses
   */
  public Collection<MatchingRuleUse> getMatchingRuleUses()
  {
    return matchingRuleUses;
  }


  /**
   * Returns the matching rule use with the supplied OID or name.
   *
   * @param  name  OID or name
   *
   * @return  matching rule use or null if name does not exist
   */
  public MatchingRuleUse getMatchingRuleUse(final String name)
  {
    for (MatchingRuleUse rule : matchingRuleUses) {
      if (rule.getOID().equals(name) || rule.hasName(name)) {
        return rule;
      }
    }
    return null;
  }


  /**
   * Returns the name forms.
   *
   * @return  name forms
   */
  public Collection<NameForm> getNameForms()
  {
    return nameForms;
  }


  /**
   * Returns the name form with the supplied OID or name.
   *
   * @param  name  OID or name
   *
   * @return  name form or null if name does not exist
   */
  public NameForm getNameForm(final String name)
  {
    for (NameForm form : nameForms) {
      if (form.getOID().equals(name) || form.hasName(name)) {
        return form;
      }
    }
    return null;
  }


  /**
   * Returns the object classes.
   *
   * @return  object classes
   */
  public Collection<ObjectClass> getObjectClasses()
  {
    return objectClasses;
  }


  /**
   * Returns the object class with the supplied OID or name.
   *
   * @param  name  OID or name
   *
   * @return  object class or null if name does not exist
   */
  public ObjectClass getObjectClass(final String name)
  {
    for (ObjectClass oc : objectClasses) {
      if (oc.getOID().equals(name) || oc.hasName(name)) {
        return oc;
      }
    }
    return null;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtils.computeHashCode(HASH_CODE_SEED, attributeTypes, ditContentRules,
                                ditStructureRules, syntaxes, matchingRules,
                                matchingRuleUses, nameForms, objectClasses);
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o)
  {
    return LdapUtils.areEqual(this, o);
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::attributeTypes=%s, ditContentRules=%s, " +
          "ditStructureRules=%s, syntaxes=%s, matchingRules=%s, " +
          "matchingRuleUses=%s, nameForms=%s, objectClasses=%s]",
        getClass().getName(),
        hashCode(),
        attributeTypes,
        ditContentRules,
        ditStructureRules,
        syntaxes,
        matchingRules,
        matchingRuleUses,
        nameForms,
        objectClasses);
  }
}
