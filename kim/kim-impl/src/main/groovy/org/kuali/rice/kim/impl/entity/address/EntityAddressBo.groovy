package org.kuali.rice.kim.impl.entity.address

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Transient
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.KimConstants
import org.kuali.rice.kim.api.entity.address.EntityAddress
import org.kuali.rice.kim.api.entity.address.EntityAddressContract
import org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferences
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_ADDR_T")
public class EntityAddressBo extends PersistableBusinessObjectBase implements EntityAddressContract {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ADDR_ID")
	String id

	@Column(name = "ENTITY_ID")
	String entityId

	@Column(name = "ADDR_TYP_CD")
	String addressTypeCode

	@Column(name = "ENT_TYP_CD")
	String entityTypeCode

	@Column(name = "CITY_NM")
	String cityName

	@Column(name = "POSTAL_STATE_CD")
	String stateCode

	@Column(name = "POSTAL_CD")
	String postalCode

	@Column(name = "POSTAL_CNTRY_CD")
	String countryCode

	@Column(name = "ADDR_LINE_1")
	String line1

	@Column(name = "ADDR_LINE_2")
	String line2

	@Column(name = "ADDR_LINE_3")
	String line3

    @Type(type="yes_no")
	@Column(name="DFLT_IND")
	boolean defaultValue;

    @Type(type="yes_no")
	@Column(name="ACTV_IND")
    boolean active;


	@ManyToOne(targetEntity=EntityAddressTypeBo.class, fetch=FetchType.EAGER, cascade=[])
	@JoinColumn(name = "ADDR_TYP_CD", insertable = false, updatable = false)
	EntityAddressTypeBo addressType;

	@Transient
    boolean suppressAddress;

  /*
   * Converts a mutable EntityAddressBo to an immutable EntityAddress representation.
   * @param bo
   * @return an immutable EntityAddress
   */
  static EntityAddress to(EntityAddressBo bo) {
    if (bo == null) { return null }
    return EntityAddress.Builder.create(bo).build()
  }

  /**
   * Creates a EntityAddressBo business object from an immutable representation of a EntityAddress.
   * @param an immutable EntityAddress
   * @return a EntityAddressBo
   */
  static EntityAddressBo from(EntityAddress immutable) {
    if (immutable == null) {return null}

    EntityAddressBo bo = new EntityAddressBo()
    bo.active = immutable.active
    bo.entityTypeCode = immutable.entityTypeCode
    if (immutable.addressType != null) {
    	bo.addressTypeCode = immutable.addressType.code
  	}
    bo.addressType = EntityAddressTypeBo.from(immutable.addressType)
    bo.defaultValue = immutable.defaultValue
    bo.line1 = immutable.line1Unmasked
    bo.line2 = immutable.line2Unmasked
    bo.line3 = immutable.line3Unmasked
    bo.cityName = immutable.cityNameUnmasked
    bo.stateCode = immutable.stateCodeUnmasked
    bo.countryCode = immutable.countryCodeUnmasked
    bo.postalCode = immutable.postalCodeUnmasked
    bo.id = immutable.id
    bo.entityId = immutable.entityId
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber

    return bo;
  }


    @Override
    EntityAddressTypeBo getAddressType() {
        return addressType
    }

    public void setAddressType(EntityAddressTypeBo addressType) {
        this.addressType = addressType
    }

    @Override
    boolean isSuppressAddress() {
        if (this.suppressAddress == null) {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId())
            if (privacy != null) {
               this.suppressAddress = privacy.isSuppressAddress()
            } else {
                this.suppressAddress = false
            }
        }

        return suppressAddress;
    }

    @Override
    String getLine1() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }
        return this.line1;

    }

    @Override
    String getLine2() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }
        return this.line2;
    }

    @Override
    String getLine3() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }
        return this.line3;
    }

    @Override
    String getCityName() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }
        return this.cityName;
    }

    @Override
    String getStateCode() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }
        return this.stateCode;
    }

    @Override
    String getPostalCode() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK_ZIP;
        }
        return this.postalCode;
    }

    @Override
    String getCountryCode() {
        if (isSuppressAddress()) {
            return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }
        return this.countryCode;
    }

    @Override
    String getLine1Unmasked() {
        return line1
    }
    @Override
    String getLine2Unmasked() {
        return line2
    }
    @Override
    String getLine3Unmasked() {
        return line3
    }
    @Override
    String getCityNameUnmasked() {
        return cityName
    }
    @Override
    String getStateCodeUnmasked() {
        return stateCode
    }
    @Override
    String getPostalCodeUnmasked() {
        return postalCode
    }
    @Override
    String getCountryCodeUnmasked() {
        return countryCode
    }
}
