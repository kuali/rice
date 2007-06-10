/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.dao.KualiDBPlatformDao;
import org.kuali.core.service.SequenceAccessorService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SequenceAccessorServiceImpl implements SequenceAccessorService {
    private KualiDBPlatformDao dbPlatformDao;

    /**
     * @see org.kuali.core.service.SequenceAccessorService#getNextAvailableSequenceNumber(java.lang.String)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName) {
        if (StringUtils.isBlank(sequenceName)) {
            throw new RuntimeException("Sequence name cannot be blank.");
        }
        return dbPlatformDao.getNextAvailableSequenceNumber(sequenceName);
    }

    /**
     * Sets the dbPlatformDao attribute value.
     * @param dbPlatformDao The dbPlatformDao to set.
     */
    public void setDbPlatformDao(KualiDBPlatformDao dbPlatformDao) {
        this.dbPlatformDao = dbPlatformDao;
    }
}