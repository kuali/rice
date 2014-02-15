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
package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This interface provides read-only metadata about inactivation blocking.  This metadata object is associated with a
 * data object. The source of this information often comes from the data dictionary file.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface InactivationBlockingMetadata {

    /**
     * The property name of the reference on the blocked object which points to the blocking object.
     *
     * @return the name of the blocking reference
     * @deprecated use {@link #getBlockedAttributeName()} instead
     */
    @Deprecated
    String getBlockedReferencePropertyName();

    /**
     * The name of the attribute which is blocked by the blocking object.
     *
     * @return the name of the blocked attribute
     */
    String getBlockedAttributeName();

    /**
     * The type of the object that is blocked.
     *
     * @return the type of the business object that is blocked
     *
     * @deprecated use {@link #getBlockedDataObjectClass()} instead, there is no requirement that the blocked class
     *             must implement the deprecated {@link BusinessObject} interface.
     */
    @Deprecated
    Class<? extends BusinessObject> getBlockedBusinessObjectClass();

    /**
     * Returns the type of the data object that is blocked.
     *
     * @return the type of the data object that is blocked, should never be null
     */
    Class<?> getBlockedDataObjectClass();

    /**
     * The bean name of the service that is responsible for determining whether there are any records that block
     * inactivation
     */
    String getInactivationBlockingDetectionServiceBeanName();

    /**
     * The type of the object that is blocking another record.
     *
     * @return the type of the object that is blocking
     *
     * @deprecated use {@link #getBlockingDataObjectClass()} instead, there is no requirement that the blocking class
     *             must implement the deprecated {@link BusinessObject} interface.
     */
    @Deprecated
    Class<? extends BusinessObject> getBlockingReferenceBusinessObjectClass();

    /**
     * The type of the object that is blocking another record.
     *
     * @return the type of the object that is blocking, should never be null
     */
    Class<?> getBlockingDataObjectClass();


    /**
     * Returns the human-meaningful name of the relationship
     *
     * @return relationship label
     */
    String getRelationshipLabel();
}
