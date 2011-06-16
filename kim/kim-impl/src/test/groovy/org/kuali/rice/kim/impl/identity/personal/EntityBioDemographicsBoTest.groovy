package org.kuali.rice.kim.impl.identity.personal

import org.junit.Test
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographics
import junit.framework.Assert

class EntityBioDemographicsBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityBioDemographics immutable = EntityBioDemographics.Builder.create("1010101", "M").build()
      EntityBioDemographicsBo bo = EntityBioDemographicsBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityBioDemographicsBo.to(bo))
  }
}
