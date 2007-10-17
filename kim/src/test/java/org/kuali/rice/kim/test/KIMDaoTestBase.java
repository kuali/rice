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
package org.kuali.rice.kim.test;

import org.junit.Test;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.rice.KNSServiceLocator;

/**
 * This base class should be used for all KIM Unit Tests that will be testing 
 * the DAO layer. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class KIMDaoTestBase extends KIMTestCase {
    protected BusinessObjectService bos = null;
    
    /**
     * This gets an instance of the businessObjectService to use for testing.
     * 
     * @see org.kuali.rice.kim.test.KIMTestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
	// call set up first so the transaction lifecycle starts up
	super.setUp();
	
	// now get a handle on the service
	bos = KNSServiceLocator.getBusinessObjectService();
    }
    
    /**
     * This method is responsible for testing the basic persistence of a business object.
     */
    @Test public void testBasicPersistence() throws Exception {
	setupReferences();
	assertTrue(insert());
	assertTrue(retrieve());
	assertTrue(update());
	assertTrue(validateUpdateChanges());
	assertTrue(delete());
	assertTrue(validateDelete());
    }
    
    /**
     * This method should be overridden and implemented to perform a setup of any dependent objects that a
     * business object may need to reference.
     */
    protected abstract void setupReferences();
    
    /**
     * This method must be implemented to return true if a record 
     * was properly inserted into the database.
     * @return boolean
     */
    protected abstract boolean insert();

    /**
     * This method must be implemented to return true if a record 
     * was properly retrieved from the database.
     * @return boolean
     */
    protected abstract boolean retrieve();
    
    /**
     * This method must be implemented to return true if a record 
     * was properly updated in the database.
     * @return boolean
     */
    protected abstract boolean update();
    
    /**
     * This method should be implemented to retrieve the objects that were just updated, and validate 
     * that their changes took effect.
     * @return boolean
     */
    protected abstract boolean validateUpdateChanges();
    
    /**
     * This method must be implemented to return true if a record 
     * was properly deleted from the database.
     * @return boolean
     */
    protected abstract boolean delete();
    
    /**
     * This method must be implemented to return true if the 
     * previously deleted records cannot be found.
     * @return boolean
     */
    protected abstract boolean validateDelete();
}