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
package org.kuali.core.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.kuali.rice.testharness.KNSTestCase;

/**
 * This class tests the GUID (Globally Unique Id) methods.
 */
public class GuidTest extends KNSTestCase {

    private final static int count = 100000; // This passed at 1 million

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Guid.class);

    @Test public void testGuidGeneration() {
        Guid guid = new Guid();
        assertNotNull(guid);
        assertNotNull(guid.toString());
    }


    @Test public void testGuidUniqueness() {
        Set<String> seen = new HashSet();

        for (int i = 0; i < count; i++) {
            Guid guid = new Guid();
            String nextGuid = guid.toString();
            assertFalse("guids should probably be unique: " + i + "," + nextGuid, seen.contains(nextGuid));
            seen.add(nextGuid);
        }
    }


    @Test public void testGuidHexConversion() throws Exception {
        assertTrue("FF".equals(Guid.toHex((byte) 255)));
        assertEquals("83", Guid.toHex((byte) 0x83));
        assertEquals("83", Guid.toHex((byte) -125));

    }


}
