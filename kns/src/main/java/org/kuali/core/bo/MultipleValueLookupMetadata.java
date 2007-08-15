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
package org.kuali.core.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.RiceConstants;

public abstract class MultipleValueLookupMetadata extends PersistableBusinessObjectBase {
    private String lookupResultsSequenceNumber;
    private String lookupUniversalUserId;
    /**
     * the time the lookup data was persisted, used by a batch purge job
     */
    private Timestamp lookupDate;
    
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getLookupUniversalUserId() {
        return lookupUniversalUserId;
    }

    public void setLookupUniversalUserId(String lookupUniversalUserId) {
        this.lookupUniversalUserId = lookupUniversalUserId;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(RiceConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, getLookupResultsSequenceNumber());
        return map;
    }

    /**
     * @return the time the lookup data was persisted, used by a batch purge job
     */
    public Timestamp getLookupDate() {
        return lookupDate;
    }

    /**
     * @param lookupDate the time the lookup data was persisted, used by a batch purge job
     */
    public void setLookupDate(Timestamp lookupDate) {
        this.lookupDate = lookupDate;
    }
}
