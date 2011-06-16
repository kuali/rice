package org.kuali.rice.kim.impl.identity.email

import org.junit.Test
import org.kuali.rice.kim.api.identity.email.EntityEmail
import org.junit.Assert


class EntityEmailBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityEmail.Builder builder = EntityEmail.Builder.create();
      builder.setActive(true)
      builder.setEmailAddress("test@testkuali.org")
      builder.setDefaultValue(true)
      builder.setEntityId("10101")
      builder.setEntityTypeCode("PERSON")
      EntityEmail immutable = builder.build()
      EntityEmailBo bo = EntityEmailBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityEmailBo.to(bo))
  }
}
