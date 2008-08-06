/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.util;

import java.util.ArrayList;

import org.junit.Test;
import org.kuali.rice.kns.test.document.bo.Account;
import org.kuali.rice.kns.test.document.bo.AccountManager;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.test.KNSTestCase;

/**
 * This class tests the TypedArrayList methods.
 */
public class TypedArrayListTest extends KNSTestCase {

    /**
     * TypedArrayList should have to be initialized with a type.
     */
    @Test public void testInitialization() {
        TypedArrayList list;
        // should throw runtime
        try {
            list = new TypedArrayList(null);
            fail("TypedArrayList accepted null initialization.");
        }
        catch (RuntimeException e) {
        }

        list = new TypedArrayList(Account.class);
        assertEquals("Class not properly initialized", Account.class.getName(), list.getListObjectType().getName());
    }

    /**
     * TypedArrayList should have to be initialized with a type.
     */
    @Test public void testInitializationWithCyclic() {
        TypedArrayList list;
        
        InnerTestCyclic innerTestCyclic = new InnerTestCyclic();
        //this should time out with a stack overflow error if this is broken
        try {
            innerTestCyclic = new InnerTestCyclic();
        } catch (StackOverflowError soe) {
            fail("TypedArrayList is not handling cyclic references correctly");
        }
        
    }
    
    /**
     * TypedArrayList should only allow objects of the initialized type to be added.
     */
    @Test public void testAdd() {
        TypedArrayList list = new TypedArrayList(Account.class);
        list.add(new Account());
        assertTrue(list.get(0) instanceof Account);

        // should throw runtime
        try {
            list.add(new AccountManager());
            fail("TypedArrayList allowed add of wrong type");
        }
        catch (RuntimeException e) {
        }
    }

    /**
     * TypedArrayList will grow the list with the type if an index greater than the current size is requested.
     */
    @Test public void testGrow() {
        TypedArrayList list = new TypedArrayList(Account.class);
        list.add(new Account());
        assertTrue(list.size() == 1);

        Object object = list.get(1);
        assertTrue(object != null && object instanceof Account);
        assertTrue(list.size() == 2);

        object = list.get(5);
        assertTrue(object != null && object instanceof Account);
        object = list.get(3);
        assertTrue(object != null && object instanceof Account);
        assertTrue(list.size() == 6);
    }
  //inner class implements the Iterator pattern
    private static class InnerTestCyclic {
        public ArrayList<InnerTestCyclic> innerTestCyclic;
        public InnerTestCyclic () {
            innerTestCyclic = new TypedArrayList(InnerTestCyclic.class);
        }
    }
}
