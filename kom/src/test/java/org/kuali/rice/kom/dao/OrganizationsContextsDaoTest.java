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
package org.kuali.rice.kom.dao;

import java.util.HashMap;

import org.kuali.rice.kom.bo.Organization;
import org.kuali.rice.kom.bo.OrganizationCategory;
import org.kuali.rice.kom.bo.OrganizationContext;
import org.kuali.rice.kom.bo.OrganizationsContexts;
import org.kuali.rice.kom.test.KOMDaoTestBase;
import org.kuali.rice.kom.test.util.KOMMockObjectUtil;
import org.kuali.rice.kom.util.KOMConstants;

/**
 * This is a description of what this class does - pberres don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class OrganizationsContextsDaoTest extends KOMDaoTestBase {
    OrganizationContextDaoTest orgConDaoTest_1;
    OrganizationContextDaoTest orgConDaoTest_2;
    OrganizationDaoTest orgDaoTest;
    OrganizationsContexts orgsCon_1;
    OrganizationsContexts orgsCon_2;

    /**
     * This method is responsible for testing the basic persistence of a business object.
     */

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#delete()
     */
    @Override
    protected boolean delete() {
        // retrieve fresh again
        orgsCon_1 = new OrganizationsContexts();
        orgsCon_2 = new OrganizationsContexts();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.ORGANIZATION_ID, orgDaoTest.org_1.getId());
        orgsCon_1 = (OrganizationsContexts) (bos.findMatching(OrganizationsContexts.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.ORGANIZATION_ID, orgDaoTest.org_2.getId());
        orgsCon_2 = (OrganizationsContexts) (bos.findMatching(OrganizationsContexts.class, criteria)).iterator().next();

        try {
            bos.delete(orgsCon_2);
            bos.delete(orgsCon_1);
            orgConDaoTest_1.updatedNames[0] = orgConDaoTest_1.orgCon_1.getName();
            orgConDaoTest_1.updatedNames[1] = orgConDaoTest_1.orgCon_2.getName();
            orgConDaoTest_2.updatedNames[0] = orgConDaoTest_2.orgCon_1.getName();
            orgConDaoTest_2.updatedNames[1] = orgConDaoTest_2.orgCon_2.getName();
            orgConDaoTest_1.delete();
            orgConDaoTest_2.delete();
            orgDaoTest.updatedNames[0] = orgDaoTest.org_1.getName();
            orgDaoTest.updatedNames[1] = orgDaoTest.org_2.getName();
            orgDaoTest.delete();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#insert()
     */
    @Override
    protected boolean insert() {
        try {
            orgsCon_1.setContextId(orgConDaoTest_1.orgCon_1.getId());
            orgsCon_2.setContextId(orgConDaoTest_1.orgCon_2.getId());
            orgsCon_1.setOrganizationId(orgDaoTest.org_1.getId());
            orgsCon_2.setOrganizationId(orgDaoTest.org_2.getId());
            bos.save(orgsCon_1);
            bos.save(orgsCon_2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#retrieve()
     */
    @Override
    protected boolean retrieve() {
        OrganizationsContexts mock_1 = KOMMockObjectUtil.getTestOrganizationsContexts_1(orgConDaoTest_1.orgCon_1.getId(), orgDaoTest.org_1.getId());
        OrganizationsContexts mock_2 = KOMMockObjectUtil.getTestOrganizationsContexts_2(orgConDaoTest_1.orgCon_2.getId(), orgDaoTest.org_2.getId());

        orgsCon_1 = new OrganizationsContexts();
        orgsCon_2 = new OrganizationsContexts();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.CONTEXT_ID, mock_1.getContextId());
        orgsCon_1 = (OrganizationsContexts) (bos.findMatching(OrganizationsContexts.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.CONTEXT_ID, mock_2.getContextId());
        orgsCon_2 = (OrganizationsContexts) (bos.findMatching(OrganizationsContexts.class, criteria)).iterator().next();

        boolean success = true;

        success &= orgsCon_1 != null;
        success &= orgsCon_1.getId() != null;
        success &= orgsCon_1.getContextId().longValue() == mock_1.getContextId().longValue();


        success &= orgsCon_2 != null;
        success &= orgsCon_2.getId() != null;
        success &= orgsCon_2.getContextId().longValue() == mock_2.getContextId().longValue();

        return success;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#setupReferences()
     */
    @Override
    protected void setupReferences() {
        try {
            orgConDaoTest_1 = new OrganizationContextDaoTest();
            orgConDaoTest_2 = new OrganizationContextDaoTest();
            orgConDaoTest_1.setUp();
            orgConDaoTest_2.setUp();
            orgConDaoTest_2.orgCon_1.setName("Test 3 - updated name");
            orgConDaoTest_2.orgCon_2.setName("Test 4 - updated name");
            orgConDaoTest_1.insert();
            orgConDaoTest_2.insert();
            orgDaoTest = new OrganizationDaoTest();
            orgDaoTest.setUp();
            orgDaoTest.setupReferences();
            orgDaoTest.insert();
            orgsCon_1 = KOMMockObjectUtil.getTestOrganizationsContexts_1(orgConDaoTest_1.orgCon_1.getId(), orgDaoTest.org_1.getId());
            orgsCon_2 = KOMMockObjectUtil.getTestOrganizationsContexts_1(orgConDaoTest_1.orgCon_2.getId(), orgDaoTest.org_2.getId());
        } catch (Exception e) {}
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#update()
     */
    @Override
    protected boolean update() {
        Long contextId_1 = orgConDaoTest_2.orgCon_1.getId();
        Long contextId_2 = orgConDaoTest_2.orgCon_2.getId();
        long x1 = contextId_1.longValue();
        long x2 = contextId_2.longValue();
        orgsCon_1.setContextId(contextId_1);
        orgsCon_2.setContextId(contextId_2);

        try {
            bos.save(orgsCon_1);
            bos.save(orgsCon_2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#validateDelete()
     */
    @Override
    protected boolean validateDelete() {
        // retrieve fresh again
        orgsCon_1 = new OrganizationsContexts();
        orgsCon_2 = new OrganizationsContexts();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.CONTEXT_ID, orgConDaoTest_2.orgCon_1.getId());
        criteria.put(KOMConstants.BO_PROPERTY_NAMES.ORGANIZATION_ID, orgDaoTest.org_1.getId());
        boolean success = bos.findMatching(OrganizationsContexts.class, criteria).isEmpty();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.CONTEXT_ID, orgConDaoTest_2.orgCon_2.getId());
        criteria.put(KOMConstants.BO_PROPERTY_NAMES.ORGANIZATION_ID, orgDaoTest.org_2.getId());

        success &= bos.findMatching(OrganizationsContexts.class, criteria).isEmpty();

        return success;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#validateUpdateChanges()
     */
    @Override
    protected boolean validateUpdateChanges() {
        orgsCon_1 = new OrganizationsContexts();
        orgsCon_2 = new OrganizationsContexts();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.CONTEXT_ID, orgConDaoTest_2.orgCon_1.getId());
        criteria.put(KOMConstants.BO_PROPERTY_NAMES.ORGANIZATION_ID, orgDaoTest.org_1.getId());
        orgsCon_1 = (OrganizationsContexts) (bos.findMatching(OrganizationsContexts.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.CONTEXT_ID, orgConDaoTest_2.orgCon_2.getId());
        criteria.put(KOMConstants.BO_PROPERTY_NAMES.ORGANIZATION_ID, orgDaoTest.org_2.getId());
        orgsCon_2 = (OrganizationsContexts) (bos.findMatching(OrganizationsContexts.class, criteria)).iterator().next();

        boolean success = true;

        success &= orgsCon_1 != null;
        success &= orgsCon_1.getId() != null;
        success &= orgsCon_1.getContextId().equals(orgConDaoTest_2.orgCon_1.getId());
        success &= orgsCon_1.getOrganizationId().equals(orgDaoTest.org_1.getId());

        success &= orgsCon_2 != null;
        success &= orgsCon_2.getId() != null;
        success &= orgsCon_2.getContextId().equals(orgConDaoTest_2.orgCon_2.getId());
        success &= orgsCon_2.getOrganizationId().equals(orgDaoTest.org_2.getId());

        return success;
    }

}
