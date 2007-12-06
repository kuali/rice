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

import org.junit.Test;
import org.kuali.rice.kom.bo.Organization;
import org.kuali.rice.kom.bo.OrganizationCategory;
import org.kuali.rice.kom.test.KOMDaoTestBase;
import org.kuali.rice.kom.test.util.KOMMockObjectUtil;
import org.kuali.rice.kom.util.KOMConstants;

/**
 * This is a description of what this class does - pberres don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class OrganizationDaoTest extends KOMDaoTestBase {
    OrganizationCategoryDaoTest orgCatDaoTest;
    Organization org_1 = KOMMockObjectUtil.getTestOrganization_1();
    Organization org_2 = KOMMockObjectUtil.getTestOrganization_2();

    String[] updatedNames = {"Test 1 - updated name", "Test 2 - updated name"};
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
        org_1 = new Organization();
        org_2 = new Organization();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        org_1 = (Organization) (bos.findMatching(Organization.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        org_2 = (Organization) (bos.findMatching(Organization.class, criteria)).iterator().next();

        try {
            bos.delete(org_2);
            bos.delete(org_1);
            orgCatDaoTest.updatedNames[0] = orgCatDaoTest.oc_1.getName();
            orgCatDaoTest.updatedNames[1] = orgCatDaoTest.oc_2.getName();            
            orgCatDaoTest.delete();
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
            org_1.setCategoryId(orgCatDaoTest.oc_1.getId());
            org_2.setCategoryId(orgCatDaoTest.oc_2.getId());
            bos.save(org_1);
            org_2.setParentOrganizationId(org_1.getId());
            bos.save(org_2);
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
        Organization mock_1 = KOMMockObjectUtil.getTestOrganization_1();
        Organization mock_2 = KOMMockObjectUtil.getTestOrganization_2();

        org_1 = new Organization();
        org_2 = new Organization();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, mock_1.getName());
        org_1 = (Organization) (bos.findMatching(Organization.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, mock_2.getName());
        org_2 = (Organization) (bos.findMatching(Organization.class, criteria)).iterator().next();

        boolean success = true;

        success &= org_1 != null;
        success &= org_1.getId() != null;
//        success &= org_1.getShortName().equals(mock_1.getShortName());
        success &= org_1.getName().equals(mock_1.getName());
//        success &= org_1.getActive() == mock_1.getActive();

        success &= org_2 != null;
        success &= org_2.getId() != null;
//        success &= org_2.getShortName().equals(mock_2.getShortName());
        success &= org_2.getName().equals(mock_2.getName());
//        success &= org_2.getActive() == mock_2.getActive();

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
            orgCatDaoTest = new OrganizationCategoryDaoTest();
            orgCatDaoTest.setUp();
            orgCatDaoTest.insert();
        } catch (Exception e) {}
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#update()
     */
    @Override
    protected boolean update() {
        org_1.setName(updatedNames[0]);

        org_2.setName(updatedNames[1]);

        try {
            bos.save(org_1);
            bos.save(org_2);
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
        org_1 = new Organization();
        org_2 = new Organization();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        boolean success = bos.findMatching(Organization.class, criteria).isEmpty();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        success &= bos.findMatching(Organization.class, criteria).isEmpty();

        return success;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kom.test.KOMDaoTestBase#validateUpdateChanges()
     */
    @Override
    protected boolean validateUpdateChanges() {
        org_1 = new Organization();
        org_2 = new Organization();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        org_1 = (Organization) (bos.findMatching(Organization.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        org_2 = (Organization) (bos.findMatching(Organization.class, criteria)).iterator().next();

        boolean success = true;

        success &= org_1 != null;
        success &= org_1.getId() != null;
        success &= org_1.getName().equals(updatedNames[0]);

        success &= org_2 != null;
        success &= org_2.getId() != null;
        success &= org_2.getName().equals(updatedNames[1]);

        return success;
    }

}
