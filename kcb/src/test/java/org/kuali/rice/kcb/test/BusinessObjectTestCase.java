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
package org.kuali.rice.kcb.test;


/**
 * This base class for testing CRUD operations on BOs
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class BusinessObjectTestCase extends KCBTestCase {
	/**
	 * This method should be overridden to test creation
	 */
    public abstract void testCreate();
    /**
     * This method should be overridden to test invalid creation
     */
    public void testInvalidCreate() {}
    /**
     * This method should be overridden to test duplicate creation
     */
    public void testDuplicateCreate() {}
    /**
     * This method should be overridden to test retrieval by id
     */
    public abstract void testReadById();
    /**
     * This method should be overridden to test retrieval
     */
    public void testReadByQuery() {}
    /**
     * This method should be overridden to test an invalid retrieval
     */
    public void testInvalidRead() {}
    /**
     * This method should be overridden to test updating
     */
    public abstract void testUpdate();
    /**
     * This method should be overridden to test an invalid update
     */
    public void testInvalidUpdate() {}
    /**
     * This method should be overridden to test delete
     */
    public abstract void testDelete();
    /**
     * This method should be overridden to test an invalid delete
     */
    public void testInvalidDelete() {}
}