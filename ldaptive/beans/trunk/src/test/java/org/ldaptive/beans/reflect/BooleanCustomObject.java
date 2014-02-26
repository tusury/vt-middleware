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
package org.ldaptive.beans.reflect;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.ldaptive.LdapUtils;
import org.ldaptive.SortBehavior;
import org.ldaptive.beans.Attribute;
import org.ldaptive.beans.Entry;

/**
 * Class for testing bean annotations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
@Entry(
  dn = "cn=Boolean Entry,ou=people,dc=ldaptive,dc=org",
  attributes = {
    @Attribute(name = "type1", property = "type1"),
    @Attribute(name = "type2", property = "type2"),
    @Attribute(name = "booleanthree", property = "type3"),
    @Attribute(
      name = "typeArray1",
      property = "typeArray1",
      sortBehavior = SortBehavior.ORDERED),
    @Attribute(
      name = "typeArray2",
      property = "typeArray2",
      sortBehavior = SortBehavior.ORDERED),
    @Attribute(name = "typeCol1", property = "typeCol1"),
    @Attribute(name = "typeCol2", property = "typeCol2"),
    @Attribute(name = "typeSet1", property = "typeSet1"),
    @Attribute(name = "typeSet2", property = "typeSet2"),
    @Attribute(name = "typeList1", property = "typeList1"),
    @Attribute(name = "typeList2", property = "typeList2")
    }
)
public class BooleanCustomObject implements CustomObject
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 51;

  // CheckStyle:JavadocVariable OFF
  private boolean type1;
  private boolean type2;
  private boolean type3;
  private boolean[] typeArray1;
  private boolean[] typeArray2;
  private Collection<Boolean> typeCol1;
  private Collection<Boolean> typeCol2;
  private Set<Boolean> typeSet1;
  private Set<Boolean> typeSet2;
  private List<Boolean> typeList1;
  private List<Boolean> typeList2;
  // CheckStyle:JavadocVariable ON


  // CheckStyle:JavadocMethod OFF
  // CheckStyle:LeftCurly OFF
  public boolean getType1() { return type1; }
  public void setType1(final boolean t) { type1 = t; }
  public void writeType2(final boolean t) { type2 = t; }
  public boolean getType3() { return type3; }
  public void setType3(final boolean t) { type3 = t; }
  public boolean[] getTypeArray1() { return typeArray1; }
  public void setTypeArray1(final boolean[] t) { typeArray1 = t; }
  public void writeTypeArray2(final boolean[] t) { typeArray2 = t; }
  public Collection<Boolean> getTypeCol1() { return typeCol1; }
  public void setTypeCol1(final Collection<Boolean> c) { typeCol1 = c; }
  public void writeTypeCol2(final Collection<Boolean> c) { typeCol2 = c; }
  public Set<Boolean> getTypeSet1() { return typeSet1; }
  public void setTypeSet1(final Set<Boolean> s) { typeSet1 = s; }
  public void writeTypeSet2(final Set<Boolean> s) { typeSet2 = s; }
  public List<Boolean> getTypeList1() { return typeList1; }
  public void setTypeList1(final List<Boolean> l) { typeList1 = l; }
  public void writeTypeList2(final List<Boolean> l) { typeList2 = l; }
  // CheckStyle:LeftCurly ON
  // CheckStyle:JavadocMethod ON


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o)
  {
    return LdapUtils.areEqual(this, o);
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtils.computeHashCode(
        HASH_CODE_SEED,
        type1,
        type2,
        type3,
        typeArray1,
        typeArray2,
        typeCol1,
        typeCol2,
        typeSet1,
        typeSet2,
        typeList1,
        typeList2);
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return String.format(
      "[%s@%d::" +
        "type1=%s, type2=%s, type3=%s, " +
        "typeArray1=%s, typeArray2=%s, " +
        "typeCol1=%s, typeCol2=%s, " +
        "typeSet1=%s, typeSet2=%s, " +
        "typeList1=%s, typeList2=%s]",
      getClass().getSimpleName(),
      hashCode(),
      type1,
      type2,
      type3,
      Arrays.toString(typeArray1),
      Arrays.toString(typeArray2),
      typeCol1,
      typeCol2,
      typeSet1,
      typeSet2,
      typeList1,
      typeList2);
  }
}
