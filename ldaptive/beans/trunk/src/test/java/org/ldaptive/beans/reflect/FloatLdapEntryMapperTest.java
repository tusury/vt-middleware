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
public class FloatLdapEntryMapperTest
  extends AbstractDefaultLdapEntryMapperTest
{


  /**
   * Test data for float based object.
   *
   * @return  custom objects
   */
  @DataProvider(name = "objects")
  public Object[][] createCustomObjects()
  {
    final Set<Float> s1 = new HashSet<Float>();
    s1.add(601.6f);
    s1.add(602.6f);

    final FloatCustomObject o1 = new FloatCustomObject();
    o1.setFloatDn("cn=Float Entry,ou=people,dc=ldaptive,dc=org");
    o1.setType1(100.1f);
    o1.writeType2(200.2f);
    o1.setType3(300.3f);
    o1.setTypeArray1(new Float[]{301.1f, 302.2f});
    o1.writeTypeArray2(new Float[]{301.1f, 302.2f});
    o1.setTypeCol1(Arrays.asList(501.5f, 502.5f));
    o1.writeTypeCol2(Arrays.asList(501.5f, 502.5f));
    o1.setTypeSet1(s1);
    o1.writeTypeSet2(s1);
    o1.setTypeList1(Arrays.asList(701.7f, 702.7f));
    o1.writeTypeList2(Arrays.asList(701.7f, 702.7f));

    return new Object[][] {
      new Object[] {o1, createFloatLdapEntry(), },
    };
  }
}
