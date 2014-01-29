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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This class provides the mileage rate
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@Entity
@Table(name = "TRVL_MLG_RT_T")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY, UifAutoCreateViewType.LOOKUP})
public class TravelMileageRate extends DataObjectBase implements MutableInactivatable, Serializable {

    private static final long serialVersionUID = 4525338013753227579L;

    @Id @Column(name = "MLG_RT_ID", length = 40)
    @GeneratedValue(generator = "TRVL_MLG_RT_ID_S")
    @PortableSequenceGenerator(name = "TRVL_MLG_RT_ID_S")
    @Label("Id")
   private String mileageRateId;

    @Column(name = "MLG_RT_CD", length = 40)
    private String mileageRateCd;

    @Column(name = "MLG_RT_NM", length = 40)
    private String mileageRateName;

    @Column(name = "MLG_RT", length = 10)
    private BigDecimal mileageRate;

    @Column(name = "ACTV_IND", nullable = false, length = 1)
    @javax.persistence.Convert(converter = BooleanYNConverter.class)
    boolean active = Boolean.TRUE;

    public String getMileageRateId() {
        return mileageRateId;
    }

    public void setMileageRateId(String mileageRateId) {
        this.mileageRateId = mileageRateId;
    }

    public String getMileageRateCd() {
        return mileageRateCd;
    }

    public void setMileageRateCd(String mileageRateCd) {
        this.mileageRateCd = mileageRateCd;
    }

    public String getMileageRateName() {
        return mileageRateName;
    }

    public void setMileageRateName(String mileageRateName) {
        this.mileageRateName = mileageRateName;
    }

    public BigDecimal getMileageRate() {
        return mileageRate;
    }

    public void setMileageRate(BigDecimal mileageRate) {
        this.mileageRate = mileageRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
