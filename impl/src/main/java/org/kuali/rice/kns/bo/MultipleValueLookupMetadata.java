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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.util.KNSConstants;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class MultipleValueLookupMetadata extends PersistableBusinessObjectBase {
    @Id
    @Column(name="LOOKUP_RSLT_ID")
    private String lookupResultsSequenceNumber;
    @Column(name="PRNCPL_ID")
    private String lookupPersonId;
    /**
     * the time the lookup data was persisted, used by a batch purge job
     */
    @Transient
    private Timestamp lookupDate;
    
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(String lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(KNSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, getLookupResultsSequenceNumber());
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

