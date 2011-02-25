package org.kuali.rice.shareddata.framework.country

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.shareddata.api.country.Country
import org.kuali.rice.shareddata.api.country.CountryContract

class CountryEbo implements Inactivateable, CountryContract, ExternalizableBusinessObject {
  def String postalCountryCode;
  def String alternatePostalCountryCode;
  def String postalCountryName;
  def boolean postalCountryRestricted;
  def boolean active;

  /**
   * Converts a mutable CountryEbo to an immutable Country representation.
   * @param bo
   * @return an immutable Country
   */
  static Country to(CountryEbo bo) {
    if (bo == null) { return null }
    return Country.Builder.create(bo).build()
  }

  /**
   * Creates a CountryEbo business object from an immutable representation of a Country.
   * @param an immutable Country
   * @return a CountryEbo
   */
  static CountryEbo from(Country immutable) {
    if (immutable == null) {return null}

    CountryEbo bo = new CountryEbo()
    bo.postalCountryCode = immutable.postalCountryCode
    bo.alternatePostalCountryCode = immutable.alternatePostalCountryCode
    bo.postalCountryName = immutable.postalCountryName
    bo.postalCountryRestricted = immutable.postalCountryRestricted
    bo.active = immutable.active

    return bo;
  }

    void refresh() { }
}
