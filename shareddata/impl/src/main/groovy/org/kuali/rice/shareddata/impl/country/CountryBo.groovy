package org.kuali.rice.shareddata.impl.country

import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.shareddata.api.country.CountryContract
import org.kuali.rice.shareddata.api.country.Country

import javax.persistence.Id
import javax.persistence.Column
import org.hibernate.annotations.Type

class CountryBo extends PersistableBusinessObjectBase implements Inactivateable, CountryContract, ExternalizableBusinessObject {

  @Id
  @Column(name = "POSTAL_CNTRY_CD")
  def String postalCountryCode;

  @Column(name = "ALT_POSTAL_CNTRY_CD")
  def String alternatePostalCountryCode;

  @Column(name = "POSTAL_CNTRY_NM")
  def String postalCountryName;

  @Type(type = "yes_no")
  @Column(name = "PSTL_CNTRY_RSTRC_IND")
  def boolean postalCountryRestricted;

  @Type(type = "yes_no")
  @Column(name = "ACTV_IND")
  def boolean active;

  /**
   * Converts a mutable CountryBo to an immutable Country representation.
   * @param bo
   * @return an immutable Country
   */
  static Country to(CountryBo bo) {
    if (bo == null) { return null }
    return Country.Builder.create(bo).build()
  }

  /**
   * Creates a CountryBo business object from an immutable representation of a Country.
   * @param an immutable Country
   * @return a CountryBo
   */
  static CountryBo from(Country immutable) {
    if (immutable == null) {return null}

    CountryBo bo = new CountryBo()
    bo.postalCountryCode = immutable.postalCountryCode
    bo.alternatePostalCountryCode = immutable.alternatePostalCountryCode
    bo.postalCountryName = immutable.postalCountryName
    bo.postalCountryRestricted = immutable.postalCountryRestricted
    bo.active = immutable.active

    return bo;
  }
}
