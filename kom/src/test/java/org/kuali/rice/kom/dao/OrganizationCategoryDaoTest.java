/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kom.dao;

import java.util.HashMap;

import org.kuali.rice.kom.bo.OrganizationCategory;
import org.kuali.rice.kom.test.KOMDaoTestBase;
import org.kuali.rice.kom.test.util.KOMMockObjectUtil;
import org.kuali.rice.kom.util.KOMConstants;

/**
 * This class tests the OrganizationCategory BO and DAO for persistence.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class OrganizationCategoryDaoTest extends KOMDaoTestBase {
	OrganizationCategory oc_1 = KOMMockObjectUtil.getTestOrganizationCategory_1();
	OrganizationCategory oc_2 = KOMMockObjectUtil.getTestOrganizationCategory_2();

	String[] updatedNames = {"Test 1 - updated name", "Test 2 - updated name"};

	/**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#setupReferences()
     */
	@Override
	protected void setupReferences() {
		// Note no business object references to set up for OrganizationCategory
	}

	/**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#insert()
     */
	@Override
	protected boolean insert() {
		try {
			bos.save(oc_1);
			bos.save(oc_2);
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
		OrganizationCategory mock_1 = KOMMockObjectUtil.getTestOrganizationCategory_1();
		OrganizationCategory mock_2 = KOMMockObjectUtil.getTestOrganizationCategory_2();

		oc_1 = new OrganizationCategory();
		oc_2 = new OrganizationCategory();

		HashMap criteria = new HashMap();

		criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, mock_1.getName());
		oc_1 = (OrganizationCategory) (bos.findMatching(OrganizationCategory.class, criteria)).iterator().next();

		criteria.clear();

		criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, mock_2.getName());
		oc_2 = (OrganizationCategory) (bos.findMatching(OrganizationCategory.class, criteria)).iterator().next();

		boolean success = true;

		success &= oc_1 != null;
		success &= oc_1.getId() != null;
		success &= oc_1.getName().equals(mock_1.getName());

		success &= oc_2 != null;
		success &= oc_2.getId() != null;
		success &= oc_2.getName().equals(mock_2.getName());

		return success;
	}

	/**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#update()
     */
	@Override
	protected boolean update() {
		oc_1.setName(updatedNames[0]);

		oc_2.setName(updatedNames[1]);

		try {
			bos.save(oc_1);
			bos.save(oc_2);
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
        oc_1 = new OrganizationCategory();
        oc_2 = new OrganizationCategory();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        oc_1 = (OrganizationCategory) (bos.findMatching(OrganizationCategory.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        oc_2 = (OrganizationCategory) (bos.findMatching(OrganizationCategory.class, criteria)).iterator().next();

        boolean success = true;

        success &= oc_1 != null;
        success &= oc_1.getId() != null;
        success &= oc_1.getName().equals(updatedNames[0]);

        success &= oc_2 != null;
        success &= oc_2.getId() != null;
        success &= oc_2.getName().equals(updatedNames[1]);

        return success;
	}

	/**
     * @see org.kuali.rice.kim.test.KOMDaoTestBase#delete()
     */
	@Override
	protected boolean delete() {
		// retrieve fresh again
        oc_1 = new OrganizationCategory();
        oc_2 = new OrganizationCategory();

        HashMap criteria = new HashMap();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
        oc_1 = (OrganizationCategory) (bos.findMatching(OrganizationCategory.class, criteria)).iterator().next();

        criteria.clear();

        criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
        oc_2 = (OrganizationCategory) (bos.findMatching(OrganizationCategory.class, criteria)).iterator().next();

		try {
			bos.delete(oc_1);
			bos.delete(oc_2);
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
		oc_1 = new OrganizationCategory();
		oc_2 = new OrganizationCategory();

		HashMap criteria = new HashMap();

		criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[0]);
		boolean success = bos.findMatching(OrganizationCategory.class, criteria).isEmpty();

		criteria.clear();

		criteria.put(KOMConstants.BO_PROPERTY_NAMES.NAME, updatedNames[1]);
		success &= bos.findMatching(OrganizationCategory.class, criteria).isEmpty();

		return success;
	}
}