package org.kuali.rice.kim.impl.identity.external

import org.junit.Test
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier
import org.junit.Assert


class EntityExternalIdentitiferBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityExternalIdentifier.Builder builder = EntityExternalIdentifier.Builder.create();
      builder.setEntityId("10101")
      builder.setExternalId("PERSON")
      EntityExternalIdentifier immutable = builder.build()
      EntityExternalIdentifierBo bo = EntityExternalIdentifierBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityExternalIdentifierBo.to(bo))
  }
}
