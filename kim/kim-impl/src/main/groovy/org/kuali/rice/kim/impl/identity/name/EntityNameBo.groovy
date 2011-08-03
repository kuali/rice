package org.kuali.rice.kim.impl.identity.name

import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import javax.persistence.Transient
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.identity.name.EntityName
import org.kuali.rice.kim.api.identity.name.EntityNameContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences
import org.kuali.rice.kim.api.services.KimApiServiceLocator

import org.kuali.rice.kim.api.KimApiConstants


class EntityNameBo extends PersistableBusinessObjectBase implements EntityNameContract {

	@Id
	@Column(name = "ENTITY_NM_ID")
	String id;

	@Column(name = "ENTITY_ID")
	String entityId;

	@Column(name = "NM_TYP_CD")
	String nameTypeCode ;

	@Column(name = "FIRST_NM")
	String firstName;

	@Column(name = "MIDDLE_NM")
	String middleName;

	@Column(name = "LAST_NM")
	String lastName;

	@Column(name = "TITLE_NM")
	String title;

	@Column(name = "SUFFIX_NM")
	String suffix;
	
	@ManyToOne(targetEntity=EntityNameTypeBo.class, fetch = FetchType.EAGER, cascade = [])
	@JoinColumn(name = "NM_TYP_CD", insertable = false, updatable = false)
	EntityNameTypeBo nameType;
    
    @Type(type="yes_no")
    @Column(name="ACTV_IND")
    boolean active;

    @Type(type="yes_no")
    @Column(name="DFLT_IND")
    boolean defaultValue;
	
	@Transient
	boolean suppressName;
    
         /*
       * Converts a mutable EntityNameBo to an immutable EntityName representation.
       * @param bo
       * @return an immutable EntityName
       */
      static EntityName to(EntityNameBo bo) {
        if (bo == null) { return null }
        return EntityName.Builder.create(bo).build()
      }

      /**
       * Creates a EntityNameBo business object from an immutable representation of a EntityName.
       * @param an immutable EntityName
       * @return a EntityNameBo
       */
      static EntityNameBo from(EntityName immutable) {
        if (immutable == null) {return null}

        EntityNameBo bo = new EntityNameBo()
        bo.id = immutable.id
        bo.active = immutable.active

        bo.entityId = immutable.entityId
        if (immutable.nameType != null) {
            bo.nameTypeCode = immutable.nameType.code
        }
        bo.firstName = immutable.firstNameUnmasked
        bo.lastName = immutable.lastNameUnmasked
        bo.middleName = immutable.middleNameUnmasked
        bo.title = immutable.titleUnmasked
        bo.suffix = immutable.suffixUnmasked

        bo.defaultValue = immutable.defaultValue
        bo.versionNumber = immutable.versionNumber
        bo.objectId = immutable.objectId

        return bo;
      }


    @Override
    EntityNameTypeBo getNameType() {
        return this.nameType
    }

    String getFirstName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK
        }
        return this.firstName
    }

    String getMiddleName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK
        }
        return this.middleName
    }

    String getLastName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK
        }
        return this.lastName
    }

    String getTitle() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK
        }
        return this.title
    }


    String getFirstNameUnmasked() {
        return this.firstName
    }

    String getMiddleNameUnmasked() {
        return this.middleName
    }

    String getLastNameUnmasked() {
        return this.lastName
    }

    String getTitleUnmasked() {
        return this.title
    }

    String getSuffixUnmasked() {
        return this.suffix
    }

    String getFormattedName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK
        }
        return getFormattedNameUnmasked()
    }

    String getFormattedNameUnmasked() {
        return getLastName() + ", " + getFirstName() + (getMiddleName()==null?"":" " + getMiddleName())
    }

    boolean isSuppressName() {
        if (this.suppressName == null) {
                EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId())
                if (privacy != null) {
                   this.suppressName = privacy.isSuppressName()
                } else {
                   this.suppressName = false
                }
            }
            return this.suppressName;
    }
}
