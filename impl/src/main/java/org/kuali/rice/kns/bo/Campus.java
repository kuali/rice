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

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.LinkedHashMap;


/**
 * 
 */
@Entity
@Table(name="SH_CAMPUS_T")
public class Campus extends PersistableBusinessObjectBase {

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

    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="CAMPUS_TYP_CD", insertable=false, updatable=false)
	private CampusType campusType;
    
    /**
     * Default no-arg constructor.
     */
    public Campus() {

    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     * 
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
     * Gets the campusName attribute.
     * 
     * @return Returns the campusName
     * 
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
     * Gets the campusShortName attribute.
     * 
     * @return Returns the campusShortName
     * 
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
     * Gets the campusTypeCode attribute.
     * 
     * @return Returns the campusTypeCode
     * 
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
     * Gets the campusType attribute. 
     * @return Returns the campusType.
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        return m;
    }

}

