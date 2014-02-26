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
import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.DataProvider;

/**
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BooleanLdapEntryMapperTest
  extends AbstractDefaultLdapEntryMapperTest
{


  /**
   * Test data for boolean based object.
   *
   * @return  custom objects
   */
  @DataProvider(name = "objects")
  public Object[][] createCustomObjects()
  {
    final Set<Boolean> s1 = new HashSet<Boolean>();
    s1.add(true);
    s1.add(false);

    final BooleanCustomObject o1 = new BooleanCustomObject();
    o1.setType1(true);
    o1.writeType2(false);
    o1.setType3(true);
    o1.setTypeArray1(new boolean[]{false, true});
    o1.writeTypeArray2(new boolean[]{false, true});
    o1.setTypeCol1(Arrays.asList(true, false));
    o1.writeTypeCol2(Arrays.asList(false, true));
    o1.setTypeSet1(s1);
    o1.writeTypeSet2(s1);
    o1.setTypeList1(Arrays.asList(false, true));
    o1.writeTypeList2(Arrays.asList(true, false));

    return new Object[][] {
      new Object[] {o1, createBooleanLdapEntry(), },
    };
  }
}
