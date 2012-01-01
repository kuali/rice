/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.service;

import org.kuali.rice.krad.inquiry.Inquirable;

/**
 * Provides methods to query the dictionary meta-data for view entries and their
 * corresponding component entries
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewDictionaryService {

    /**
     * Queries the dictionary to find the <code>InquiryView</code> configured
     * for the data object class and returns the configured Inquirable for the
     * view. If more than one inquiry view exists for the data object class, the
     * one that matches the given viewName, or the default if viewName is blank
     * is used
     *
     * @param dataObjectClass - class for the inquiry data object
     * @param viewName - name of the inquiry view, can be blank in which case the
     * 'default' name will be used
     * @return Inquirable<?> configured inquirable for the view, or null if view
     *         is not found
     */
    public Inquirable getInquirable(Class<?> dataObjectClass, String viewName);

    /**
     * Indicates whether the given data object class has an associated
     * <code>InquiryView</code> configured and thus can have inquiry links built
     *
     * @param dataObjectClass - object class to get inquiry view for
     * @return boolean true if the class has an inquiry view, false if no
     *         inquiry view exists for the class
     */
    public boolean isInquirable(Class<?> dataObjectClass);

    /**
     * Indicates whether the given data object class has an associated
     * <code>LookupView</code> configured and thus can have quickfinders
     * associated with the class
     *
     * @param dataObjectClass - object class to get lookup view for
     * @return boolean true if the class has an lookup view, false if no lookup
     *         view exists for the class
     */
    public boolean isLookupable(Class<?> dataObjectClass);

    /**
     * Indicates whether the given data object class has an associated
     * <code>MaintenanceView</code> configured
     *
     * @param dataObjectClass - object class to get maintenance view for
     * @return boolean true if the class has an maintenance view, false if no
     *         maintenance view exists for the class
     */
    public boolean isMaintainable(Class<?> dataObjectClass);

    /**
     * Attempts to find an associated <code>LookupView</code> for the
     * given data object class and if found returns the configured result
     * set limit, if multiple lookup views are found the default is used
     *
     * @param dataObjectClass - object class to get lookup view for
     * @return Integer configured result set limit for lookup, or null if not found (note
     *         property could also be null on the view itself)
     */
    public Integer getResultSetLimitForLookup(Class<?> dataObjectClass);
}
