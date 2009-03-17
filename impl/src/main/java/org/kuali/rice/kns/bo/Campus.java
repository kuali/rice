/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.bo;

import java.util.LinkedHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 
 */
@Entity
@Table(name="KRNS_CAMPUS_T")
public class Campus extends PersistableBusinessObjectBase implements CampusEBO, Inactivateable {

    private static final long serialVersionUID = 787567094298971223L;
    @Id
	@Column(name="CAMPUS_CD")
	private String campusCode;
    @Column(name="CAMPUS_NM")
	private String campusName;
    @Column(name="CAMPUS_SHRT_NM")
	private String campusShortName;
    @Column(name="CAMPUS_TYP_CD")
	private String campusTypeCode;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
    protected boolean active;

    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="CAMPUS_TYP_CD", insertable=false, updatable=false)
	private CampusType campusType;
    
    /**
     * Default no-arg constructor.
     */
    public Campus() {

    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.CampusEBO#getCampusCode()
	 */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     * 
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.CampusEBO#getCampusName()
	 */
    public String getCampusName() {
        return campusName;
    }

    /**
     * Sets the campusName attribute.
     * 
     * @param campusName The campusName to set.
     * 
     */
    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.CampusEBO#getCampusShortName()
	 */
    public String getCampusShortName() {
        return campusShortName;
    }

    /**
     * Sets the campusShortName attribute.
     * 
     * @param campusShortName The campusShortName to set.
     * 
     */
    public void setCampusShortName(String campusShortName) {
        this.campusShortName = campusShortName;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.CampusEBO#getCampusTypeCode()
	 */
    public String getCampusTypeCode() {
        return campusTypeCode;
    }

    /**
     * Sets the campusTypeCode attribute.
     * 
     * @param campusTypeCode The campusTypeCode to set.
     * 
     */
    public void setCampusTypeCode(String campusTypeCode) {
        this.campusTypeCode = campusTypeCode;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.CampusEBO#getCampusType()
	 */
    public CampusType getCampusType() {
        return campusType;
    }

    /**
     * Sets the campusType attribute value.
     * @param campusType The campusType to set.
     * @deprecated
     */
    public void setCampusType(CampusType campusType) {
        this.campusType = campusType;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.CampusEBO#isActive()
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        return m;
    }

}

