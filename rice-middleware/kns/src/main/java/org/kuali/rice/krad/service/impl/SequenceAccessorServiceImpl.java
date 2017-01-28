/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.dao.SequenceAccessorDao;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Deprecated
public class SequenceAccessorServiceImpl implements SequenceAccessorService {
    private SequenceAccessorDao sequenceAccessorDao;

	public Long getNextAvailableSequenceNumber(String sequenceName, 
			Class clazz) {
    	if (StringUtils.isBlank(sequenceName)) {
    		throw new RuntimeException("Sequence name cannot be blank.");
    	}
    	return sequenceAccessorDao.getNextAvailableSequenceNumber(sequenceName, clazz);		
	}
	
    /**
     * @see org.kuali.rice.krad.service.SequenceAccessorService#getNextAvailableSequenceNumber(java.lang.String)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName) {
    	if (StringUtils.isBlank(sequenceName)) {
    		throw new RuntimeException("Sequence name cannot be blank.");
    	}
    	return sequenceAccessorDao.getNextAvailableSequenceNumber(sequenceName);
    }

    public void setSequenceAccessorDao(SequenceAccessorDao sequenceAccessorDao) {
    	this.sequenceAccessorDao = sequenceAccessorDao;
    }
}
