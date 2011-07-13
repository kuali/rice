package org.kuali.rice.kim.impl.identity.type

import org.junit.Test

import org.junit.Assert
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo


class EntityTypeContactInfoBoTest {
  @Test
  public void testNotEqualsWithEntityTypeData() {
    EntityTypeContactInfo immutable = EntityTypeContactInfo.Builder.create("101021", "PERSON").build()
    EntityTypeContactInfoBo bo = EntityTypeContactInfoBo.from(immutable )
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
    Assert.assertEquals(immutable, EntityTypeContactInfoBo.to(bo))
  }
}
