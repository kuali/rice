package org.kuali.rice.kim.impl.entity.privacy

import javax.persistence.Entity
import javax.persistence.Table
import org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferencesContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import javax.persistence.Column
import javax.persistence.Id
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.entity.phone.EntityPhone
import org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferences

@Entity
@Table(name = "KRIM_ENTITY_PRIV_PREF_T")
class EntityPrivacyPreferencesBo extends PersistableBusinessObjectBase implements EntityPrivacyPreferencesContract {
    	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	String entityId;

	@Type(type="yes_no")
	@Column(name="SUPPRESS_NM_IND")
	boolean suppressName;

	@Type(type="yes_no")
	@Column(name="SUPPRESS_EMAIL_IND")
	boolean suppressEmail;

	@Type(type="yes_no")
	@Column(name="SUPPRESS_ADDR_IND")
	boolean suppressAddress;

	@Type(type="yes_no")
	@Column(name="SUPPRESS_PHONE_IND")
	boolean suppressPhone;

	@Type(type="yes_no")
	@Column(name="SUPPRESS_PRSNL_IND")
	boolean suppressPersonal;

 /*
   * Converts a mutable EntityPhoneBo to an immutable EntityPhone representation.
   * @param bo
   * @return an immutable Country
   */
  static EntityPrivacyPreferences to(EntityPrivacyPreferencesBo bo) {
    if (bo == null) { return null }
    return EntityPrivacyPreferences.Builder.create(bo).build()
  }

  /**
   * Creates a CountryBo business object from an immutable representation of a Country.
   * @param an immutable Country
   * @return a CountryBo
   */
  static EntityPrivacyPreferencesBo from(EntityPrivacyPreferences immutable) {
    if (immutable == null) {return null}

    EntityPrivacyPreferencesBo bo = new EntityPrivacyPreferencesBo()

    bo.entityId = immutable.entityId
    bo.suppressAddress = immutable.suppressAddress
    bo.suppressEmail = immutable.suppressEmail
    bo.suppressName = immutable.suppressName
    bo.suppressPersonal = immutable.suppressPersonal
    bo.suppressPhone = immutable.suppressPhone
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }

}
