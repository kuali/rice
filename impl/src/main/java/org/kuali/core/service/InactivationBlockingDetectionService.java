/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.core.service;

import java.util.Collection;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.InactivationBlockingMetadata;

/**
 * This service detects whether there are any records that block the inactivation of a particular record 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface InactivationBlockingDetectionService {
    /**
     * Determines whether there is ANY record in the relationship defined by the inactivationBlockingMetadata that prevents inactivation of blockedBo
     * 
     * @param blockedBo a BO that is potentially inactivation blocked
     * @param inactivationBlockingMetadata
     * @return true iff there was a record that blocks the blockedBo using the metadata in inactivationBlockingMetadata
     */
    public boolean hasABlockingRecord(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata);

    /**
     * Lists all records in the relationship defined by the inactivationBlockingMetadata that prevents inactivation of blockedBo
     * 
     * @param blockedBo a BO that is potentially inactivation blocked
     * @param inactivationBlockingMetadata
     * @return true iff there was a record that blocks the blockedBo using the metadata in inactivationBlockingMetadata
     */
    public Collection<BusinessObject> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata);
}
