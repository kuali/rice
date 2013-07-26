package org.kuali.rice.kim.impl.identity.name;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.name.EntityNameContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.sql.Timestamp;

public class EntityNameBo extends PersistableBusinessObjectBase implements EntityNameContract {
    @Id
    @Column(name = "ENTITY_NM_ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "NM_TYP_CD")
    private String nameCode;
    @Column(name = "FIRST_NM")
    private String firstName;
    @Column(name = "MIDDLE_NM")
    private String middleName;
    @Column(name = "LAST_NM")
    private String lastName;
    @Column(name = "PREFIX_NM")
    private String namePrefix;
    @Column(name = "TITLE_NM")
    private String nameTitle;
    @Column(name = "SUFFIX_NM")
    private String nameSuffix;
    @ManyToOne(targetEntity = EntityNameTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "NM_TYP_CD", insertable = false, updatable = false)
    private EntityNameTypeBo nameType;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;
    @Column(name = "NOTE_MSG")
    private String noteMessage;
    @Column(name = "NM_CHNG_DT")
    private Timestamp nameChangedDate;
    @Transient
    private boolean suppressName;

    public static EntityName to(EntityNameBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityName.Builder.create(bo).build();
    }

    /**
     * Creates a EntityNameBo business object from an immutable representation of a EntityName.
     *
     * @param an immutable EntityName
     * @return a EntityNameBo
     */
    public static EntityNameBo from(EntityName immutable) {
        if (immutable == null) {
            return null;
        }

        EntityNameBo bo = new EntityNameBo();
        bo.id = immutable.getId();
        bo.active = immutable.isActive();

        bo.entityId = immutable.getEntityId();
        if (immutable.getNameType() != null) {
            bo.nameCode = immutable.getNameType().getCode();
        }

        bo.firstName = immutable.getFirstNameUnmasked();
        bo.lastName = immutable.getLastNameUnmasked();
        bo.middleName = immutable.getMiddleNameUnmasked();
        bo.namePrefix = immutable.getNamePrefixUnmasked();
        bo.nameTitle = immutable.getNameTitleUnmasked();
        bo.nameSuffix = immutable.getNameSuffixUnmasked();
        bo.noteMessage = immutable.getNoteMessage();
        if (immutable.getNameChangedDate() != null) {
            bo.nameChangedDate = new Timestamp(immutable.getNameChangedDate().getMillis());
        }

        bo.defaultValue = immutable.isDefaultValue();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public EntityNameTypeBo getNameType() {
        return this.nameType;
    }

    @Override
    public String getFirstName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.firstName;
    }

    @Override
    public String getMiddleName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.middleName;
    }

    @Override
    public String getLastName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.lastName;
    }

    @Override
    public String getNamePrefix() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.namePrefix;
    }

    @Override
    public String getNameTitle() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.nameTitle;
    }

    @Override
    public String getFirstNameUnmasked() {
        return this.firstName;
    }

    @Override
    public String getMiddleNameUnmasked() {
        return this.middleName;
    }

    @Override
    public String getLastNameUnmasked() {
        return this.lastName;
    }

    @Override
    public String getNamePrefixUnmasked() {
        return this.namePrefix;
    }

    @Override
    public String getNameTitleUnmasked() {
        return this.nameTitle;
    }

    @Override
    public String getNameSuffixUnmasked() {
        return this.nameSuffix;
    }

    @Override
    public String getCompositeName() {
        if (isSuppressName()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return getCompositeNameUnmasked();
    }

    @Override
    public String getCompositeNameUnmasked() {
        return getLastName() + ", " + getFirstName() + (getMiddleName() == null ? "" : " " + getMiddleName());
    }

    @Override
    public DateTime getNameChangedDate() {
        return nameChangedDate != null ? new DateTime(nameChangedDate.getTime()) : null;
    }

    @Override
    public boolean isSuppressName() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressName = privacy.isSuppressName();
            } else {
                this.suppressName = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }

        return this.suppressName;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setNameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }

    @Override
    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public void setNameType(EntityNameTypeBo nameType) {
        this.nameType = nameType;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getNoteMessage() {
        return noteMessage;
    }

    public void setNoteMessage(String noteMessage) {
        this.noteMessage = noteMessage;
    }

    public void setNameChangedDate(Timestamp nameChangedDate) {
        this.nameChangedDate = nameChangedDate;
    }

    public boolean getSuppressName() {
        return suppressName;
    }

    public void setSuppressName(boolean suppressName) {
        this.suppressName = suppressName;
    }

}
