package org.kuali.rice.kim.impl.identity.personal

import org.junit.Test
import org.junit.Assert
import org.kuali.rice.kim.api.identity.personal.EntityEthnicity


class EntityEthnicityBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityEthnicity.Builder builder = EntityEthnicity.Builder.create()
      builder.setId("1")
      builder.setEntityId("10101")
      builder.setEthnicityCode("LKJ")
      builder.setSubEthnicityCode("RJEK")
      EntityEthnicity immutable = builder.build()
      EntityEthnicityBo bo = EntityEthnicityBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityEthnicityBo.to(bo))
  }
}
