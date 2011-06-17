package org.kuali.rice.kim.impl.identity.visa

import org.kuali.rice.kim.api.identity.visa.EntityVisa
import org.junit.Test
import org.junit.Assert


class EntityVisaBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityVisa.Builder builder = EntityVisa.Builder.create();
      builder.setEntityId("101010")
      builder.setVisaEntry("1092193894839290")
      builder.setVisaId("8483904808239048")
      builder.setVisaTypeKey("VISA")
      EntityVisa immutable = builder.build()
      EntityVisaBo bo = EntityVisaBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityVisaBo.to(bo))
  }
}
