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
package org.kuali.rice.kim.dao;

import java.util.HashMap;

import org.kuali.rice.kim.bo.AttributeType;
import org.kuali.rice.kim.test.KIMDaoTestBase;
import org.kuali.rice.kim.test.util.KIMMockObjectUtil;
import org.kuali.rice.kim.util.KIMConstants;

/**
 * This class tests the AttributeType BO and DAO for persistence. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AttributeTypeDaoTest extends KIMDaoTestBase {
    AttributeType at_1 = KIMMockObjectUtil.getTestAttributeType_1();
    AttributeType at_2 = KIMMockObjectUtil.getTestAttributeType_2();
    
    private String[] updatedDescriptions = {"Test 1 - updated description", "Test 2 - updated description"};

    
    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#setupReferences()
     */
    @Override
    protected void setupReferences() {
	// Note no business object references to set up for AttributeType
    }

    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#insert()
     */
    @Override
    protected boolean insert() {
	try {
	    bos.save(at_1);
	    bos.save(at_2);
	} catch(Exception e) {
	    return false;
	}
	return true;
    }
    
    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#retrieve()
     */
    @Override
    protected boolean retrieve() {
	AttributeType mock_1 = KIMMockObjectUtil.getTestAttributeType_1();
	AttributeType mock_2 = KIMMockObjectUtil.getTestAttributeType_2();
	
	at_1 = new AttributeType();
	at_2 = new AttributeType();
	
	HashMap criteria = new HashMap();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_1.getAttributeTypeName());
	at_1 = (AttributeType) (bos.findMatching(AttributeType.class, criteria)).iterator().next();
	
	criteria.clear();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_2.getAttributeTypeName());
	at_2 = (AttributeType) (bos.findMatching(AttributeType.class, criteria)).iterator().next();
	
	boolean success = true;
	
	success &= at_1 != null;
	success &= at_1.getId() != null;
	success &= at_1.getDescription().equals(mock_1.getDescription());
	
	success &= at_2 != null;
	success &= at_2.getId() != null;
	success &= at_2.getDescription().equals(mock_2.getDescription());
	
	return success;
    }
    
    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#update()
     */
    @Override
    protected boolean update() {
	at_1.setDescription(updatedDescriptions[0]);
	
	at_2.setDescription(updatedDescriptions[1]);

	try {
	    bos.save(at_1);
	    bos.save(at_2);
	} catch(Exception e) {
	    return false;
	}
	return true;
    }
    
    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#validateUpdateChanges()
     */
    @Override
    protected boolean validateUpdateChanges() {
	//retrieve fresh again
	AttributeType mock_1 = KIMMockObjectUtil.getTestAttributeType_1();
	AttributeType mock_2 = KIMMockObjectUtil.getTestAttributeType_2();
	
	at_1 = new AttributeType();
	at_2 = new AttributeType();
	
	HashMap criteria = new HashMap();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_1.getAttributeTypeName());
	at_1 = (AttributeType) (bos.findMatching(AttributeType.class, criteria)).iterator().next();
	
	criteria.clear();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_2.getAttributeTypeName());
	at_2 = (AttributeType) (bos.findMatching(AttributeType.class, criteria)).iterator().next();
	
	boolean success = true;
	
	success &= at_1.getDescription().equals(updatedDescriptions[0]);
	
	success &= at_2.getDescription().equals(updatedDescriptions[1]);
	    
	return success;
    }
    
    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#delete()
     */
    @Override
    protected boolean delete() {
	//retrieve fresh again
	AttributeType mock_1 = KIMMockObjectUtil.getTestAttributeType_1();
	AttributeType mock_2 = KIMMockObjectUtil.getTestAttributeType_2();
	
	at_1 = new AttributeType();
	at_2 = new AttributeType();
	
	HashMap criteria = new HashMap();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_1.getAttributeTypeName());
	at_1 = (AttributeType) (bos.findMatching(AttributeType.class, criteria)).iterator().next();
	
	criteria.clear();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_2.getAttributeTypeName());
	at_2 = (AttributeType) (bos.findMatching(AttributeType.class, criteria)).iterator().next();
	
	try {
	    bos.delete(at_1);
	    bos.delete(at_2);
	} catch(Exception e) {
	    return false;
	}
	return true;
    }
    
    /**
     * @see org.kuali.rice.kim.test.KIMDaoTestBase#validateDelete()
     */
    @Override
    protected boolean validateDelete() {
	//retrieve fresh again
	AttributeType mock_1 = KIMMockObjectUtil.getTestAttributeType_1();
	AttributeType mock_2 = KIMMockObjectUtil.getTestAttributeType_2();
	
	at_1 = new AttributeType();
	at_2 = new AttributeType();
	
	HashMap criteria = new HashMap();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_1.getAttributeTypeName());
	boolean success = bos.findMatching(AttributeType.class, criteria).isEmpty();
	
	criteria.clear();
	
	criteria.put(KIMConstants.BO_PROPERTY_NAMES.ATTRIBUTE_TYPE_NAME, mock_2.getAttributeTypeName());
	success &= bos.findMatching(AttributeType.class, criteria).isEmpty();
	
	return success;
    }
}