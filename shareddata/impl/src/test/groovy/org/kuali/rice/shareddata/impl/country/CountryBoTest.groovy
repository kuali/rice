package org.kuali.rice.shareddata.impl.country

import org.junit.Test
import org.kuali.rice.shareddata.api.country.Country
import junit.framework.Assert


class CountryBoTest {
  @Test
  public void testNotEqualsWithCountry() {
    Country immutable = Country.Builder.create("US", null, "United States", false, true).build()
    CountryBo bo = CountryBo.from(immutable )
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
  }
}
