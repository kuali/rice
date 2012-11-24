/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.sampleu.travel.approval.dataobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import java.util.LinkedHashMap;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="TRVL_PER_DIEM_T")
public class PrimaryDestination extends PersistableBusinessObjectBase {

    @Id
    @GeneratedValue(generator="TEM_PER_DIEM_ID_SEQ")
    @SequenceGenerator(name="TEM_PER_DIEM_ID_SEQ",sequenceName="TEM_PER_DIEM_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="trip_typ_cd")
    private TripType tripType;

    @Column(name="trip_typ_cd",length=3,nullable=false)
    private String tripTypeCode;

    @Column(name="COUNTRY",length=100, nullable=false)
    private String countryState;

    @Column(name="COUNTRY_NM",length=100, nullable=false)
    private String countryStateName;

    @Column(name="COUNTY_CD",length=100, nullable=false)
    private String county;

    @Column(name="PRI_DEST",length=100, nullable=false)
    private String primaryDestinationName;

    @Column(name="ACTV_IND",nullable=false,length=1)
    private Boolean active = Boolean.TRUE;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    public String getTripTypeCode() {
        return tripTypeCode;
    }

    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    public String getCountryState() {
        return countryState;
    }

    public void setCountryState(String countryState) {
        this.countryState = countryState;
    }

    public String getCountryStateName() {
        return countryStateName;
    }

    public void setCountryStateName(String countryStateName) {
        this.countryStateName = countryStateName;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPrimaryDestinationName() {
        return primaryDestinationName;
    }

    public void setPrimaryDestinationName(String primaryDestinationName) {
        this.primaryDestinationName = primaryDestinationName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("tripType", this.tripTypeCode);
        map.put("countryState", this.countryState);
        map.put("county", this.county);
        map.put("primaryDestinationName", this.primaryDestinationName);

        return map;
    }
}
