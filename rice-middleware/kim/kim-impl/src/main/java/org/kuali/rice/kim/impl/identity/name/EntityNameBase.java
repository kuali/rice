/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.impl.identity.name;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.name.EntityNameContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@MappedSuperclass
public abstract class EntityNameBase extends DataObjectBase implements EntityNameContract {
    private static final long serialVersionUID = 7102034794623577359L;

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

    @Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @Convert(converter=BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;

    @Column(name = "NOTE_MSG")
    private String noteMessage;

    @Column(name = "NM_CHNG_DT")
    private Timestamp nameChangedDate;

    @Transient
    private boolean suppressName;


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

    public Timestamp getNameChangedTimestamp() {
        return nameChangedDate;
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

    public void setNameChangedDate(DateTime nameChangedDate) {
        if ( nameChangedDate != null ) {
            this.nameChangedDate = new Timestamp( nameChangedDate.getMillis() );
        } else {
            this.nameChangedDate = null;
        }
    }

    public void setNameChangedTimestamp(Timestamp nameChangedDate) {
        this.nameChangedDate = nameChangedDate;
    }

    public boolean getSuppressName() {
        return suppressName;
    }

    public void setSuppressName(boolean suppressName) {
        this.suppressName = suppressName;
    }

}
