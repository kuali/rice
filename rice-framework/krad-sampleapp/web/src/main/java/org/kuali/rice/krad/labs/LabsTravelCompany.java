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
package org.kuali.rice.krad.labs;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Labs version of {@code TravelCompany} to show {@code allowsNewOrCopy} functionality.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "TRVL_CO_T")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY, UifAutoCreateViewType.LOOKUP})
public class LabsTravelCompany extends DataObjectBase implements MutableInactivatable, Serializable {

    @Id @Column(name = "CO_ID", length = 40)
    @GeneratedValue(generator = "TRVL_CO_ID_S")
    @PortableSequenceGenerator(name = "TRVL_CO_ID_S")
    @Label("id")
    @Description("Unique identifier for company")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String travelCompanyId;

    @Column(name = "CO_NM", length = 40)
    @Label("Company name")
    @Description("Company Name")
    private String travelCompanyName;

    @Column(name = "ACTV_IND", nullable = false, length = 1)
    @javax.persistence.Convert(converter = BooleanYNConverter.class)
    @Label("Active")
    @Description("Whether active or inactive")
    private boolean active = Boolean.TRUE;

    public String getTravelCompanyId() {
        return travelCompanyId;
    }

    public void setTravelCompanyId(String travelCompanyId) {
        this.travelCompanyId = travelCompanyId;
    }

    public String getTravelCompanyName() {
        return travelCompanyName;
    }

    public void setTravelCompanyName(String travelCompanyName) {
        this.travelCompanyName = travelCompanyName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
