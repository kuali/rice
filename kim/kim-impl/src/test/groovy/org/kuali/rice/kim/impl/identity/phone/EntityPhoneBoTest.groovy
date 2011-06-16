package org.kuali.rice.kim.impl.identity.phone

import org.junit.Test
import org.kuali.rice.kim.api.identity.phone.EntityPhone
import org.junit.Assert


class EntityPhoneBoTest {
  @Test
  public void testNotEqualsWithPhone() {
      EntityPhone.Builder builder = EntityPhone.Builder.create();
      builder.setActive(true)
      builder.setCountryCode("1")
      builder.setPhoneNumber("439-0116")
      builder.setDefaultValue(true)
      builder.setEntityId("10101")
      builder.setEntityTypeCode("PERSON")
      EntityPhone immutable = builder.build()
      EntityPhoneBo bo = EntityPhoneBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityPhoneBo.to(bo))
  }
}
