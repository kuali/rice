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

import org.kuali.rice.kom.bo.OrganizationContext;
import org.kuali.rice.kom.test.KOMDaoTestBase;
import org.kuali.rice.kom.test.util.KOMMockObjectUtil;
import org.kuali.rice.kom.util.KOMConstants;

/**
 * This is a description of what this class does - pberres don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class OrganizationContextDaoTest extends KOMDaoTestBase {
    OrganizationContext orgCon_1 = KOMMockObjectUtil.getTestOrganizationContext_1();
    OrganizationContext orgCon_2 = KOMMockObjectUtil.getTestOrganizationContext_2();

    String[] updatedNames = {"Test 1 - updated name", "Test 2 - updated name"};

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#setupReferences()
     */
    @Override
    protected void setupReferences() {
        // Note no business object references to set up for OrganizationContext
    }

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#insert()
     */
    @Override
    protected boolean insert() {
        try {
            bos.save(orgCon_1);
            bos.save(orgCon_2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#retrieve()
     */
    @Override
    protected boolean retrieve() {
        OrganizationContext mock_1 = KOMMockObjectUtil.getTestOrganizationContext_1();
        OrganizationContext mock_2 = KOMMockObjectUtil.getTestOrganizationContext_2();

        orgCon_1 = new OrganizationContext();
        orgCon_2 = new OrganizationContext();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, mock_1.getName());
        orgCon_1 = (OrganizationContext) (bos.findMatching(OrganizationContext.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, mock_2.getName());
        orgCon_2 = (OrganizationContext) (bos.findMatching(OrganizationContext.class, criteria)).iterator().next();

        boolean success = true;

        success &= orgCon_1 != null;
        success &= orgCon_1.getId() != null;
        success &= orgCon_1.getName().equals(mock_1.getName());

        success &= orgCon_2 != null;
        success &= orgCon_2.getId() != null;
        success &= orgCon_2.getName().equals(mock_2.getName());

        return success;
    }

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#update()
     */
    @Override
    protected boolean update() {
        orgCon_1.setName(updatedNames[0]);

        orgCon_2.setName(updatedNames[1]);

        try {
            bos.save(orgCon_1);
            bos.save(orgCon_2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#validateUpdateChanges()
     */
    @Override
    protected boolean validateUpdateChanges() {
        orgCon_1 = new OrganizationContext();
        orgCon_2 = new OrganizationContext();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        orgCon_1 = (OrganizationContext) (bos.findMatching(OrganizationContext.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        orgCon_2 = (OrganizationContext) (bos.findMatching(OrganizationContext.class, criteria)).iterator().next();

        boolean success = true;

        success &= orgCon_1 != null;
        success &= orgCon_1.getId() != null;
        success &= orgCon_1.getName().equals(updatedNames[0]);

        success &= orgCon_2 != null;
        success &= orgCon_2.getId() != null;
        success &= orgCon_2.getName().equals(updatedNames[1]);

        return success;
    }

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#delete()
     */
    @Override
    protected boolean delete() {
        // retrieve fresh again
        orgCon_1 = new OrganizationContext();
        orgCon_2 = new OrganizationContext();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        orgCon_1 = (OrganizationContext) (bos.findMatching(OrganizationContext.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        orgCon_2 = (OrganizationContext) (bos.findMatching(OrganizationContext.class, criteria)).iterator().next();

        try {
            bos.delete(orgCon_1);
            bos.delete(orgCon_2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#validateDelete()
     */
    @Override
    protected boolean validateDelete() {
        // retrieve fresh again
        orgCon_1 = new OrganizationContext();
        orgCon_2 = new OrganizationContext();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        boolean success = bos.findMatching(OrganizationContext.class, criteria).isEmpty();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        success &= bos.findMatching(OrganizationContext.class, criteria).isEmpty();

        return success;
    }
}