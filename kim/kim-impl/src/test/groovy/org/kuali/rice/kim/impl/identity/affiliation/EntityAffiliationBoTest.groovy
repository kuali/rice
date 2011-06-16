package org.kuali.rice.kim.impl.identity.affiliation

import org.junit.Test
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation
import org.junit.Assert


class EntityAffiliationBoTest {
  @Test
  public void testNotEqualsWithAffiliation() {
      EntityAffiliation.Builder builder = EntityAffiliation.Builder.create();
      builder.setActive(true)
      builder.setCampusCode("ISU")
      builder.setDefaultValue(true)
      builder.setEntityId("10101")
      EntityAffiliation immutable = builder.build()
      EntityAffiliationBo bo = EntityAffiliationBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityAffiliationBo.to(bo))
  }
}
