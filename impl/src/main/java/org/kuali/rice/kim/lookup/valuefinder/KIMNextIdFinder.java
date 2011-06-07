/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.lookup.valuefinder;

import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kns.lookup.valuefinder.ValueFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;


public abstract class KIMNextIdFinder implements ValueFinder {
    
    private String sequenceName;
    
	public KIMNextIdFinder( String sequenceName ) {
		this.sequenceName = sequenceName;
	}
    
    /**
     * Get the next sequence number value as a Long.
     */
    public Long getLongValue() {
        // no constant because this is the only place the sequence name is used
    	SequenceAccessorService sas = KNSServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber(sequenceName, KimEntityImpl.class);
    }

    /**
     * @see org.kuali.rice.kns.lookup.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }

	public final String getSequenceName() {
		return sequenceName;
	}
}
