/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.useroptions;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link UserOptionsId}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserOptionsIdTest {

    @Test
    public void testEquals() throws Exception {
        UserOptionsId options1 = new UserOptionsId("one", "two");
        UserOptionsId options2 = new UserOptionsId("two", "one");
        UserOptionsId options3 = new UserOptionsId("one", "two");
        UserOptionsId options4 = new UserOptionsId();

        assertFalse(options1.equals(null));
        assertFalse(options1.equals(options2));
        assertFalse(options1.equals(options4));
        assertTrue(options1.equals(options3));
        assertTrue(options1.equals(options1));

    }

    @Test
    public void testHashCode() throws Exception {
        UserOptionsId options1 = new UserOptionsId("one", "two");
        UserOptionsId options2 = new UserOptionsId("two", "one");
        UserOptionsId options3 = new UserOptionsId("one", "two");
        UserOptionsId options4 = new UserOptionsId();

        assertNotEquals(options1.hashCode(), options2.hashCode());
        assertEquals(options1.hashCode(), options3.hashCode());
        assertEquals(options4.hashCode(), options4.hashCode());

    }
}
