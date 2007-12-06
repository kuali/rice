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
import org.kuali.rice.kom.bo.Organization;
import org.kuali.rice.kom.bo.OrganizationContext;
import org.kuali.rice.kom.bo.OrganizationsContexts;

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
    /**
     * This method is a helper to build an OrganizationCategory instance.
     * @param name
     * @return OrganizationCategory
     */
    public static final Organization buildTestOrganization(String name, OrganizationCategory oc) {
        Organization org = new Organization();
        org.setShortName(name);
        org.setName(name);
        org.setCategoryId(oc.getId());
        org.setActive(true);
        org.setVersionNumber(new Long(1));
        return org;
    }       
    
    /**
     * This method returns back a specific test mock object.
     * @return OrganizationCategory
     */
    public static final Organization getTestOrganization_1() {
        OrganizationCategory oc = buildTestOrganizationCategory("Test Organization Category 1");
        return buildTestOrganization("Test Organization 1", oc);
    }

    /**
     * This method returns back a specific test mock object.
     * @return OrganizationCategory
     */
    public static final Organization getTestOrganization_2() {
        OrganizationCategory oc = buildTestOrganizationCategory("Test Organization Category 2");
        return buildTestOrganization("Test Organization 2", oc);
    }
    /**
     * This method is a helper to build an OrganizationContext instance.
     * @param name
     * @return OrganizationContext
     */
    public static final OrganizationContext buildTestOrganizationContext(String name) {
        OrganizationContext oc = new OrganizationContext();
        oc.setName(name);
        oc.setDescription("Description for " + name);
        oc.setVersionNumber(new Long(1));
        return oc;
    }       
    
    /**
     * This method returns back a specific test mock object.
     * @return OrganizationContext
     */
    public static final OrganizationContext getTestOrganizationContext_1() {
        return buildTestOrganizationContext("Test Organization Context 1");
    }

    /**
     * This method returns back a specific test mock object.
     * @return OrganizationContext
     */
    public static final OrganizationContext getTestOrganizationContext_2() {
        return buildTestOrganizationContext("Test Organization Context 2");
    }
    /**
     * This method is a helper to build an OrganizationsContexts instance.
     * @param name
     * @return OrganizationsContexts
     */
    public static final OrganizationsContexts buildTestOrganizationsContexts(Long contextId, Long organizationId) {
        OrganizationsContexts orgsCon = new OrganizationsContexts();
        orgsCon.setContextId(contextId);
        orgsCon.setOrganizationId(contextId);
        orgsCon.setActive(true);
        return orgsCon;
    }       
    
    /**
     * This method returns back a specific test mock object.
     * @return OrganizationsContexts
     */
    public static final OrganizationsContexts getTestOrganizationsContexts_1(Long contextId, Long organizationId) {
        return buildTestOrganizationsContexts(contextId, organizationId);
    }

    /**
     * This method returns back a specific test mock object.
     * @return OrganizationsContexts
     */
    public static final OrganizationsContexts getTestOrganizationsContexts_2(Long contextId, Long organizationId) {
        return buildTestOrganizationsContexts(contextId, organizationId);
    }
}
