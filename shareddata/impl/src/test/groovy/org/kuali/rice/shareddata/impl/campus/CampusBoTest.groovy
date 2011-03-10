package org.kuali.rice.shareddata.impl.campus

import org.kuali.rice.shareddata.api.campus.Campus
import junit.framework.Assert
import org.junit.Test


class CampusBoTest {
  @Test
  public void testNotEqualsWithCampus() {
    Campus immutable = Campus.Builder.create(Campus.Builder.create("AMES")).build();
    CampusBo bo = CampusBo.from(immutable)
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
  }
}
