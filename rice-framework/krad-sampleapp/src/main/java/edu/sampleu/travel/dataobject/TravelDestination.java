/**
 * Copyright 2005-2013 The Kuali Foundation
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

import edu.sampleu.travel.options.TripTypeKeyValuesFinder.TripType;
import org.kuali.rice.krad.bo.DataObjectBase;

import java.io.Serializable;

/**
 * This class provides travel destination record
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class TravelDestination extends DataObjectBase implements Serializable {

    private static final long serialVersionUID = 8448891916448081149L;

    private String travelDestinationId;

    private TripType tripTypeCd;

    private String travelDestinationName;

    private String countryCd;

    private String stateCd;

    private boolean active = Boolean.TRUE;

    public String getTravelDestinationId() {
        return travelDestinationId;
    }

    public void setTravelDestinationId(String travelDestinationId) {
        this.travelDestinationId = travelDestinationId;
    }

    public TripType getTripTypeCd() {
        return tripTypeCd;
    }

    public void setTripTypeCd(TripType tripTypeCd) {
        this.tripTypeCd = tripTypeCd;
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

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
