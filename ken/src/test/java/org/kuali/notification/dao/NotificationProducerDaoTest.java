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

import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationProducer;
import org.kuali.notification.test.util.MockObjectsUtil;
import org.kuali.notification.util.NotificationConstants;


/**
 * This class tests basic persistence for the NotificationProducer business object.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationProducerDaoTest extends BusinessObjectPersistenceTestCaseBase {
    NotificationChannel mockChannel1 = MockObjectsUtil.getTestChannel1();
    NotificationChannel mockChannel2 = MockObjectsUtil.getTestChannel2();
    
    NotificationProducer mockProducer1 = MockObjectsUtil.getTestProducer1();
    
    private String[] updatedDescriptions = {"Test 1 - updated description", "Test 2 - updated description"};
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#setup()
     */
    @Override
    protected void setup() {
	super.setup();
	businessObjectDao.save(mockChannel1);
	businessObjectDao.save(mockChannel2);
    }
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#delete()
     */
    @Override
    protected boolean delete() {
	HashMap criteria = new HashMap();
	
	NotificationProducer producer4 = new NotificationProducer();
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.NAME, mockProducer1.getName());
	producer4 = (NotificationProducer) businessObjectDao.findByUniqueKey(NotificationProducer.class, criteria);
	
	assertEquals(1, producer4.getChannels().size());
	
	criteria.clear();
	NotificationProducer producer5 = new NotificationProducer();
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.NAME, mockProducer1.getName());
	producer5 = (NotificationProducer) businessObjectDao.findByUniqueKey(NotificationProducer.class, criteria);
		
	try {
	    businessObjectDao.delete(producer5);
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
	NotificationProducer producer2 = new NotificationProducer();
	
	HashMap criteria = new HashMap();
	
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.NAME, mockProducer1.getName());
	producer2 = (NotificationProducer) businessObjectDao.findByUniqueKey(NotificationProducer.class, criteria);
	
	boolean success = true;
	
	success &= producer2 != null;
	success &= producer2.getDescription().equals(mockProducer1.getDescription());
	success &= producer2.getChannels().size()==2;
	
	return success;
    }
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#insert()
     */
    @Override
    protected boolean insert() {
	NotificationProducer producer1 = MockObjectsUtil.getTestProducer1();
	
	//set up the channels
	producer1.getChannels().add(mockChannel1);
	producer1.getChannels().add(mockChannel2);
	
	try {
	    businessObjectDao.save(producer1);
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
	NotificationProducer producer2 = new NotificationProducer();
	
	HashMap criteria = new HashMap();
	
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.NAME, mockProducer1.getName());
	producer2 = (NotificationProducer) businessObjectDao.findByUniqueKey(NotificationProducer.class, criteria);
	
	producer2.setDescription(updatedDescriptions[0]);
	producer2.getChannels().remove(0);
	
	try {
	    businessObjectDao.save(producer2);
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
	NotificationProducer producer2 = new NotificationProducer();
	
	HashMap criteria = new HashMap();
	
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.NAME, mockProducer1.getName());
	producer2 = (NotificationProducer) businessObjectDao.findByUniqueKey(NotificationProducer.class, criteria);
	
	boolean success = true;
	
	success &= producer2.getDescription().equals(updatedDescriptions[0]);
	success &= producer2.getChannels().size()==1;
	
	return success;
    }
}