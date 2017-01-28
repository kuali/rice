/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_NM_MT")
public class PersonDocumentName extends PersonDocumentBoDefaultBase {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_NM_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_NM_ID_S")
    @Id
    @Column(name = "ENTITY_NM_ID")
    protected String entityNameId;

    //@Column(name = "ENTITY_ID")                       
    @Transient
    protected String entityId;

    @Column(name = "NM_TYP_CD")
    protected String nameCode;

    @Column(name = "FIRST_NM")
    protected String firstName;

    @Column(name = "MIDDLE_NM")
    protected String middleName;

    @Column(name = "LAST_NM")
    protected String lastName;

    @Column(name = "PREFIX_NM")
    protected String namePrefix;

    @Column(name = "TITLE_NM")
    protected String nameTitle;

    @Column(name = "SUFFIX_NM")
    protected String nameSuffix;

    @Column(name = "NOTE_MSG")
    protected String noteMessage;

    @Column(name = "NM_CHNG_DT")
    protected Timestamp nameChangedDate;

    @JoinFetch(value= JoinFetchType.OUTER)
    @ManyToOne(targetEntity = EntityNameTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "NM_TYP_CD", referencedColumnName = "ENT_NM_TYP_CD", insertable = false, updatable = false)
    protected EntityNameTypeBo entityNameType;

    public PersonDocumentName() {
        this.active = true;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getEntityNameId()
	 */
    public String getEntityNameId() {
        return entityNameId;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getFirstName()
	 */
    public String getFirstName() {
        return firstName;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getLastName()
	 */
    public String getLastName() {
        return lastName;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getMiddleName()
	 */
    public String getMiddleName() {
        return middleName;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getNameTypeCode()
	 */
    public String getNameCode() {
        return nameCode;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getNameSuffix()
	 */
    public String getNameSuffix() {
        return nameSuffix;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getNamePrefix()
	 */
    public String getNamePrefix() {
        return namePrefix;
    }

    public String getNameTitle() {
        return nameTitle;
    }

    public String getNoteMessage() {
        return noteMessage;
    }

    public Timestamp getNameChangedDate() {
        return nameChangedDate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setNameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }

    public void setNoteMessage(String noteMessage) {
        this.noteMessage = noteMessage;
    }

    public void setNameChangedDate(Timestamp timestamp) {
        this.nameChangedDate = timestamp;
    }

    /**
	 * This default implementation formats the name as LAST, FIRST MIDDLE.
	 * 
	 * @see org.kuali.rice.kim.api.identity.name.EntityNameContract#getCompositeName()
	 */
    public String getCompositeName() {
        return getLastName() + ", " + getFirstName() + " " + getMiddleName();
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public EntityNameTypeBo getEntityNameType() {
        return this.entityNameType;
    }

    public void setEntityNameType(EntityNameTypeBo entityNameType) {
        this.entityNameType = entityNameType;
    }

    public void setEntityNameId(String entityNameId) {
        this.entityNameId = entityNameId;
    }
}
