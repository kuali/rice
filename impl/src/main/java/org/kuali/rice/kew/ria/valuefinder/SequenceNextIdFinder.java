/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kew.ria.valuefinder;

import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * A value finder which returns the next id from a database sequence
 * via the SequenceAccessorService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SequenceNextIdFinder implements ValueFinder { 
	/**
	 * The database sequence to query
	 */
    protected String sequenceName;
    
    /**
     * Constructor which takes a mandatory sequence name
     * @param sequenceName the database sequence name
     */
    public SequenceNextIdFinder(String sequenceName) {
    	this.sequenceName = sequenceName;
    }
    
    /**
     * Get the next sequence number value as a Long.
     * @return The next sequence number.
     */
    public Long getLongValue() {
        return KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(this.sequenceName);
    }

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }
}
