package org.kuali.rice.shareddata.impl.county

import org.junit.Test
import org.kuali.rice.shareddata.api.county.County
import junit.framework.Assert


class CountyBoTest {
  @Test
  public void testNotEqualsWithCampus() {
    County immutable = County.Builder.create("SHA", "SHA County", "US", "MI").build();
    CountyBo bo = CountyBo.from(immutable)
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
  }
}
