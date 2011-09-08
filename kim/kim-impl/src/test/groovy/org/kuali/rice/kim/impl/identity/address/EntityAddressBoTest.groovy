package org.kuali.rice.kim.impl.identity.address

import org.junit.Test
import org.kuali.rice.kim.api.identity.address.EntityAddress
import org.junit.Assert

class EntityAddressBoTest {

  @Test
  public void testNotEqualsWithAddress() {
      EntityAddress.Builder builder = EntityAddress.Builder.create();
      builder.setActive(true)
      builder.setCity("city")
      builder.setStateProvinceCode("IA")
      builder.setCountryCode("USA")
      builder.setDefaultValue(true)
      builder.setEntityId("10101")
      builder.setEntityTypeCode("PERSON")
      EntityAddress immutable = builder.build()
      EntityAddressBo bo = EntityAddressBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityAddressBo.to(bo))
  }
}
