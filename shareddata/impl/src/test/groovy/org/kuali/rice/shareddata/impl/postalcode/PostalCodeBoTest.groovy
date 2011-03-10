package org.kuali.rice.shareddata.impl.postalcode

import org.kuali.rice.shareddata.api.postalcode.PostalCode
import junit.framework.Assert
import org.junit.Test


class PostalCodeBoTest {
  @Test
  public void testNotEqualsWithCampus() {
    PostalCode immutable = PostalCode.Builder.create(PostalCode.Builder.create("48848", "US")).build()
    PostalCodeBo bo = PostalCodeBo.from(immutable)
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
  }
}
