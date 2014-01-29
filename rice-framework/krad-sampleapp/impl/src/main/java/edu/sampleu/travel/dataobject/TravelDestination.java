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
package edu.sampleu.travel.dataobject;

import edu.sampleu.travel.options.PostalCountryCode;
import edu.sampleu.travel.options.PostalCountryCodeKeyValuesFinder;
import edu.sampleu.travel.options.PostalStateCode;
import edu.sampleu.travel.options.PostalStateCodeKeyValuesFinder;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.KeyValuesFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * This class provides travel destination record for TEM sample
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "TRVL_DEST_T")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY, UifAutoCreateViewType.LOOKUP})
public class TravelDestination extends DataObjectBase implements MutableInactivatable, Serializable {

    private static final long serialVersionUID = 8448891916448081149L;

    @Id @Column(name = "TRVL_DEST_ID", length = 40)
    @GeneratedValue(generator = "TRVL_DEST_ID_S")
    @PortableSequenceGenerator(name = "TRVL_DEST_ID_S")
    @Label("Id")
    @Description("Unique identifier for destination item")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String travelDestinationId;

    @Column(name = "DEST_NM", length = 40)
    @Label("Destination")
    @Description("Name of location")
    private String travelDestinationName;

    @Column(name = "POSTAL_CNTRY_CD")
    @UifDisplayHints({@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
                      @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    @KeyValuesFinderClass(PostalCountryCodeKeyValuesFinder.class)
    @Label("Country")
    private String countryCd;

    @Transient
    @UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA))
    @Label("Country")
    private String countryName;

    @Column(name = "POSTAL_STATE_CD")
    @UifDisplayHints({@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
                      @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    @KeyValuesFinderClass(PostalStateCodeKeyValuesFinder.class)
    @Label("State")
    private String stateCd;

    @Transient
    @UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA))
    @Label("State")
    private String stateName;


    @Column(name = "ACTV_IND", nullable = false, length = 1)
    @javax.persistence.Convert(converter = BooleanYNConverter.class)
    @Label("Active")
    @Description("Whether active or inactive")
    private boolean active = Boolean.TRUE;

    public String getTravelDestinationId() {
        return travelDestinationId;
    }

    public void setTravelDestinationId(String travelDestinationId) {
        this.travelDestinationId = travelDestinationId;
    }

    public String getTravelDestinationName() {
        return travelDestinationName;
    }

    public void setTravelDestinationName(String travelDestinationName) {
        this.travelDestinationName = travelDestinationName;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getCountryName() {
        return PostalCountryCode.valueOf(countryCd).getLabel();
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getStateName() {
        return PostalStateCode.valueOf(stateCd).getLabel();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
