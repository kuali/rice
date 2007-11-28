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
package org.kuali.rice.kom.test.util;

import org.kuali.rice.kom.bo.OrganizationCategory;

/**
 * This class is a utility class for easy access to re-usable mock objects that can 
 * be used in KOM unit tests. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KOMMockObjectUtil {
    /**
     * This method is a helper to build an OrganizationCategory instance.
     * @param name
     * @return OrganizationCategory
     */
    public static final OrganizationCategory buildTestOrganizationCategory(String name) {
        OrganizationCategory oc = new OrganizationCategory();
        oc.setName(name);
        oc.setVersionNumber(new Long(1));
        return oc;
    }       
    
    /**
     * This method returns back a specific test mock object.
     * @return OrganizationCategory
     */
    public static final OrganizationCategory getTestOrganizationCategory_1() {
        return buildTestOrganizationCategory("Test Organization Category 1");
    }

    /**
     * This method returns back a specific test mock object.
     * @return OrganizationCategory
     */
    public static final OrganizationCategory getTestOrganizationCategory_2() {
        return buildTestOrganizationCategory("Test Organization Category 2");
    }
}
