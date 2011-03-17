/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.opensource.org/licenses/ecl2.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kuali.rice.shareddata.api.county;

/**
 * This is the contract for a State.
 */
public interface CountyContract {

    /**
     * This the county code for the County.  This cannot be null or a blank string.
     *
     * @return code
     */
    String getCode();

    /**
     * This the postal country code for the County.  This cannot be null or a blank string.
     *
     * @return postal country code
     */
    String getCountryCode();

    /**
     * This the postal state code for the County.  This cannot be null or a blank string.
     *
     * @return postal state code
     */
    String getStateCode();

    /**
     * This the name for the County.  This cannot be null or a blank string.
     *
     * @return name
     */
    String getName();

    /**
     * This the active flag for the County.
     *
     * @return the active flag of the County
     */
    boolean isActive();

    /**
     * This the object version number for County.
     *
     * @return the version number of the County
     */
    Long getVersionNumber();
}
