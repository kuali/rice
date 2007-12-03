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
package org.kuali.notification.dao;

import java.util.HashMap;

import org.kuali.notification.bo.RecipientPreference;
import org.kuali.notification.util.NotificationConstants;

/**
 * This class test basic persistence for the RecipientPreference business object.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RecipientPreferenceDaoTest extends BusinessObjectPersistenceTestCaseBase {
    RecipientPreference pref1 = new RecipientPreference();
    RecipientPreference pref2 = new RecipientPreference();
    
    private String[] recipientTypes = {"Type 1", "Type 2"};
    private String[] recipientIds = {"unit_test_recip1", "unit_test_recip2"};
    private String[] propertys = {"Property A", "Property B"};
    private String[] values = {"Value A", "Value B"};
    private String[] updatedValues = {"Value C", "Value D"};
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#delete()
     */
    @Override
    protected boolean delete() {
	try {
	    businessObjectDao.delete(pref1);
	    businessObjectDao.delete(pref2);
	} catch(Exception e) {
	    return false;
	}
	return true;
    }
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#retrieve()
     */
    @Override
    protected boolean retrieve() {
	pref1 = new RecipientPreference();
	pref2 = new RecipientPreference();
	
	HashMap criteria = new HashMap();
	
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.RECIPIENT_ID, recipientIds[0]);
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.RECIPIENT_TYPE, recipientTypes[0]);
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.PROPERTY, propertys[0]);
	pref1 = (RecipientPreference) businessObjectDao.findByUniqueKey(RecipientPreference.class, criteria);
	
	criteria.clear();
	
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.RECIPIENT_ID, recipientIds[1]);
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.RECIPIENT_TYPE, recipientTypes[1]);
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.PROPERTY, propertys[1]);
	pref2 = (RecipientPreference) businessObjectDao.findByUniqueKey(RecipientPreference.class, criteria);
	
	boolean success = true;
	
	success &= pref1 != null;
	success &= pref1.getRecipientId().equals(recipientIds[0]);
	
	success &= pref2 != null;
	success &= pref2.getRecipientId().equals(recipientIds[1]);
	
	return success;
    }
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#insert()
     */
    @Override
    protected boolean insert() {
	pref1.setRecipientId(recipientIds[0]);
	pref1.setRecipientType(recipientTypes[0]);
	pref1.setProperty(propertys[0]);
	pref1.setValue(values[0]);
	
	pref2.setRecipientId(recipientIds[1]);
	pref2.setRecipientType(recipientTypes[1]);
	pref2.setProperty(propertys[1]);
	pref2.setValue(values[1]);
	
	try {
	    businessObjectDao.save(pref1);
	    businessObjectDao.save(pref2);
	} catch(Exception e) {
	    return false;
	}
	return true;
    }
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#update()
     */
    @Override
    protected boolean update() {
	pref1.setValue(updatedValues[0]);
	
	pref2.setValue(updatedValues[1]);
	
	try {
	    businessObjectDao.save(pref1);
	    businessObjectDao.save(pref2);
	} catch(Exception e) {
	    return false;
	}
	return true;
    }
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#validateChanges()
     */
    @Override
    protected boolean validateChanges() {
	retrieve();  //retrieve fresh again
	
	boolean success = true;
	
	success &= pref1.getValue().equals(updatedValues[0]);
	success &= pref2.getValue().equals(updatedValues[1]);
	    
	return success;
    }
}