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
package org.kuali.rice.krad.uif.util;

import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.view.ViewSessionPolicy;
import org.kuali.rice.krad.lookup.LookupForm;

/**
 * Provides basic view dictionary service functionality for unit tests. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestViewDictionaryService implements ViewDictionaryService {

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#getInquirable(java.lang.Class, java.lang.String)
     */
    @Override
    public Inquirable getInquirable(Class<?> dataObjectClass, String viewName) {
        return new MockInquirable(dataObjectClass, viewName);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#isInquirable(java.lang.Class)
     */
    @Override
    public boolean isInquirable(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#isLookupable(java.lang.Class)
     */
    @Override
    public boolean isLookupable(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#isMaintainable(java.lang.Class)
     */
    @Override
    public boolean isMaintainable(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#getResultSetLimitForLookup(java.lang.Class, org.kuali.rice.krad.web.form.LookupForm)
     */
    @Override
    public Integer getResultSetLimitForLookup(Class<?> dataObjectClass, LookupForm form) {
        return 0;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#getViewSessionPolicy(java.lang.String)
     */
    @Override
    public ViewSessionPolicy getViewSessionPolicy(String viewId) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewDictionaryService#isSessionStorageEnabled(java.lang.String)
     */
    @Override
    public boolean isSessionStorageEnabled(String viewId) {
        return false;
    }

}
