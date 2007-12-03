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
package org.kuali.rice.util;

import java.util.Set;

import org.apache.ojb.broker.OptimisticLockException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Tests the DataAccessUtils.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DataAccessUtilsTest extends Assert {

    /**
     * Tests the standard optimistic lock exception types (currently just OJB and Spring)
     */
    @Test public void testStandardOptimisticLockExceptions() throws Exception {
	// ojb
	OptimisticLockException ojbOptimisticLock = new OptimisticLockException();
	assertTrue("Should be an optimistic lock.", DataAccessUtils.isOptimisticLockFailure(ojbOptimisticLock));

	// spring
	OptimisticLockingFailureException springOptimisticLock = new OptimisticLockingFailureException("You've been locked!");
	assertTrue("Should be an optimistic lock.", DataAccessUtils.isOptimisticLockFailure(springOptimisticLock));

	// some bad exceptions
	assertFalse("Should not be an optimistic lock.", DataAccessUtils.isOptimisticLockFailure(new Exception()));
	assertFalse("Should not be an optimistic lock.", DataAccessUtils.isOptimisticLockFailure(new RuntimeException()));
    }

    @Test public void testOptimisticLockExceptionHierarchy() throws Exception {
	assertTrue("Should be an optimistic lock.", DataAccessUtils.isOptimisticLockFailure(new OptimisticLockExceptionSubClass()));
	assertTrue("Should be an optimistic lock.", DataAccessUtils.isOptimisticLockFailure(new OptimisticLockExceptionSubSubClass()));
    }

    @Test public void testAddingNewOptimisticLockException() throws Exception {
	Set<Class<?>> classes = DataAccessUtils.getOptimisticLockExceptionClasses();
	int numClasses = classes.size();

	// first check that our new exception isn't an optimistic lock
	assertFalse(DataAccessUtils.isOptimisticLockFailure(new NewOptimisticLockException()));

	// now add it to the set
	DataAccessUtils.addOptimisticLockExceptionClass(NewOptimisticLockException.class);
	assertEquals("1 classes should have been added", numClasses+1, DataAccessUtils.getOptimisticLockExceptionClasses().size());

	// now check that our new exception is a valid optimistic lock
	assertTrue(DataAccessUtils.isOptimisticLockFailure(new NewOptimisticLockException()));
    }

    private class OptimisticLockExceptionSubClass extends OptimisticLockingFailureException {
	private static final long serialVersionUID = 250096545213130485L;
	public OptimisticLockExceptionSubClass() {
	    super("");
	}
    }

    private class OptimisticLockExceptionSubSubClass extends OptimisticLockExceptionSubClass {
	private static final long serialVersionUID = -5203289703393837390L;
    }

    private class NewOptimisticLockException extends Exception {
	private static final long serialVersionUID = -3559800499152004714L;
    }

}
