/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
// Created on Dec 14, 2005

package edu.iu.uis.eden.applicationconstants;

import junit.framework.AssertionFailedError;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;

/**
 * Tests adding/modifying an application constant
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsTest extends KEWTestCase {
    private static final String CONSTANT_NAME = "BogusConstant";
    private static final String CONSTANT_VALUE = "test value";
    
    @Test public void testApplicationConstantsDeleteCacheInteraction() {
    	ApplicationConstantsService appConstSrv = KEWServiceLocator.getApplicationConstantsService();
    	String name = "testName";
    	String value = "testValue";
    	ApplicationConstant constant = new ApplicationConstant(name, value);
    	appConstSrv.save(constant);
    	
    	//verify that the constant is there.
    	constant = appConstSrv.findByName(name);
    	assertNotNull("Constant should have been fetched.", constant);
    	
    	//that fetch should have put the constant in the cache. verify using the cache service
    	constant = (ApplicationConstant)KEWServiceLocator.getCacheAdministrator().getFromCache(name);
    	assertNotNull("Constant should have been in cache", constant);
    	
    	//delete the constant and verify that it's not in the cache
    	appConstSrv.delete(constant);
    	constant = (ApplicationConstant)KEWServiceLocator.getCacheAdministrator().getFromCache(name);
    	assertNull("Constant should no longer be in the cache", constant);
    	
    	//verify that the service isn't giving any deleted constants
    	constant = appConstSrv.findByName(name);
    	assertNull("Constant should be deleted and not fetched.", constant);
    }
    
    @Test public void testApplicationConstantsUpdateCacheInteraction() {
    	ApplicationConstantsService appConstSrv = KEWServiceLocator.getApplicationConstantsService();
    	String name = "testName";
    	String value = "testValue";
    	ApplicationConstant constant = new ApplicationConstant(name, value);
    	appConstSrv.save(constant);
    	
    	//verify that the constant is there.
    	constant = appConstSrv.findByName(name);
    	assertNotNull("Constant should have been fetched.", constant);
    	
    	//that fetch should have put the constant in the cache. verify using the cache service
    	constant = (ApplicationConstant)KEWServiceLocator.getCacheAdministrator().getFromCache(name);
    	assertNotNull("Constant should have been in cache", constant);
    	
    	String newValue = "new Value";
    	
    	constant.setApplicationConstantValue(newValue);
    	appConstSrv.save(constant);
    	
    	constant = appConstSrv.findByName(name);
    	assertEquals("Constants value should have been updated", newValue, constant.getApplicationConstantValue());
    	
    	//check that it's in the cache correctly
    	constant = (ApplicationConstant)KEWServiceLocator.getCacheAdministrator().getFromCache(name);
    	assertEquals("Constants value should have been updated in the cache", newValue, constant.getApplicationConstantValue());
    }
    
    
    @Test public void testConstants() {
        ApplicationConstantsService acs = KEWServiceLocator.getApplicationConstantsService();
        ApplicationConstant ac = acs.findByName(CONSTANT_NAME);
        assertNull(ac);
        ac = new ApplicationConstant();
        ac.setApplicationConstantName(CONSTANT_NAME);

        try {
            acs.save(ac);
            throw new AssertionFailedError("Empty constant was saved");
        } catch (WorkflowServiceErrorException wsee) {
            // should be thrown due to empty constant value
//            log.info("WorkflowServiceErrorException due to empty constant value should follow.");
//            wsee.printStackTrace();
        }

        assertNull(acs.findByName(CONSTANT_NAME));

        ac.setApplicationConstantValue(CONSTANT_VALUE);
        acs.save(ac);
        ac = acs.findByName(CONSTANT_NAME);
        assertNotNull(ac);
        assertEquals(CONSTANT_NAME, ac.getApplicationConstantName());
        assertEquals(CONSTANT_VALUE, ac.getApplicationConstantValue());

        ac = new ApplicationConstant();
        ac.setApplicationConstantName(CONSTANT_NAME);
        ac.setApplicationConstantValue(CONSTANT_VALUE);
//        try {
            acs.save(ac);
//            fail("Exception should have been thrown from lack of version number");
//        } catch (OjbOperationException ooe) {
            // should throw an exception because lockvernbr is not set
//        }

        ac = acs.findByName(CONSTANT_NAME);
        assertNotNull(ac);
        assertEquals(CONSTANT_NAME, ac.getApplicationConstantName());
        assertEquals(CONSTANT_VALUE, ac.getApplicationConstantValue());
        ac.setApplicationConstantValue(CONSTANT_VALUE + "2");

        acs.save(ac);

        ac = acs.findByName(CONSTANT_NAME);
        assertNotNull(ac);
        assertEquals(CONSTANT_NAME, ac.getApplicationConstantName());
        assertEquals(CONSTANT_VALUE + "2", ac.getApplicationConstantValue());
    }
}