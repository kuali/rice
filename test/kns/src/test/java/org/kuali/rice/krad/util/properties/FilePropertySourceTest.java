/*
 * Copyright 2005-2008 The Kuali Foundation
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

package org.kuali.rice.krad.util.properties;

import org.junit.Test;
import org.kuali.rice.krad.exception.PropertiesException;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the FilePropertySource methods.
 */
public class FilePropertySourceTest extends KNSTestCase {

    @Test public void testLoadProperties_defaultFileName() {
        FilePropertySource fps = new FilePropertySource();

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testLoadProperties_invalidFileName() {
        FilePropertySource fps = new FilePropertySource();
        fps.setFileName("      ");

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testLoadProperties_unknownFileName() {
        FilePropertySource fps = new FilePropertySource();
        fps.setFileName("unknown");

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        }
        catch (PropertiesException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testLoadProperties_knownFileName_noSuffix() {
        FilePropertySource fps = new FilePropertySource();
        fps.setFileName("configuration");

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        }
        catch (PropertiesException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }
}
