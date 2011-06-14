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
import org.kuali.rice.krad.exception.DuplicateKeyException;
import org.kuali.rice.krad.exception.PropertiesException;
import org.kuali.test.KRADTestCase;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * This class tests the PropertyHolder methods.
 */
public class PropertyHolderTest extends KRADTestCase {
    private static final String KNOWN_KEY1 = "key1";
    private static final String KNOWN_VALUE1 = "value1";

    private static final String KNOWN_KEY2 = "key 2";
    private static final String KNOWN_VALUE2 = "value 2";

    private static final String KNOWN_KEY3 = "";
    private static final String KNOWN_VALUE3 = "";

    @Test public void testIsEmpty_emptyHolder() {
        PropertyHolder propertyHolder = new PropertyHolder();

        assertTrue(propertyHolder.isEmpty());
    }

    @Test public void testIsEmpty_notEmptyHolder() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(!propertyHolder.isEmpty());
    }

    @Test public void testContainsKey_invalidKey() {
        PropertyHolder propertyHolder = new PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.containsKey(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testContainsKey_emptyHolder() {
        PropertyHolder propertyHolder = new PropertyHolder();

        assertFalse(propertyHolder.containsKey(KNOWN_KEY1));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY2));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY3));
    }

    @Test public void testContainsKey_notContains() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertFalse(propertyHolder.containsKey(KNOWN_KEY1 + "foo"));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY2 + "foo"));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY3 + "foo"));
    }

    @Test public void testContainsKey_contains() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY2));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY3));
    }

    @Test public void testGetProperty_invalidKey() {
        PropertyHolder propertyHolder = new PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.getProperty(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testGetProperty_emptyHolder() {
        PropertyHolder propertyHolder = new PropertyHolder();

        assertNull(propertyHolder.getProperty(KNOWN_KEY1));
        assertNull(propertyHolder.getProperty(KNOWN_KEY2));
        assertNull(propertyHolder.getProperty(KNOWN_KEY3));
    }

    @Test public void testGetProperty_notContains() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertNull(propertyHolder.getProperty(KNOWN_KEY1 + "foo"));
        assertNull(propertyHolder.getProperty(KNOWN_KEY2 + "foo"));
        assertNull(propertyHolder.getProperty(KNOWN_KEY3 + "foo"));
    }

    @Test public void testGetProperty_contains() {
        PropertyHolder propertyHolder = buildNonEmpty();

        String value = propertyHolder.getProperty(KNOWN_KEY1);
        assertEquals(KNOWN_VALUE1, value);
        value = propertyHolder.getProperty(KNOWN_KEY2);
        assertEquals(KNOWN_VALUE2, value);
        value = propertyHolder.getProperty(KNOWN_KEY3);
        assertEquals(KNOWN_VALUE3, value);
    }

    @Test public void testSetProperty_invalidKey() {
        PropertyHolder propertyHolder = new PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.setProperty(null, KNOWN_VALUE1);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testSetProperty_invalidValue() {
        PropertyHolder propertyHolder = new PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.setProperty(KNOWN_KEY1, null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testSetProperty_uniqueKey() {
        PropertyHolder propertyHolder = new PropertyHolder();

        propertyHolder.setProperty(KNOWN_KEY1, KNOWN_VALUE1);
        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        assertEquals(KNOWN_VALUE1, propertyHolder.getProperty(KNOWN_KEY1));
    }

    @Test public void testSetProperty_duplicateKey() {
        PropertyHolder propertyHolder = buildNonEmpty();

        boolean failedAsExpected = false;
        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        try {
            propertyHolder.setProperty(KNOWN_KEY1, KNOWN_VALUE1);
        }
        catch (DuplicateKeyException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testClearProperty_invalidKey() {
        PropertyHolder propertyHolder = buildNonEmpty();

        boolean failedAsExpected = false;
        try {
            propertyHolder.clearProperty(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testClearProperty_unknownKey() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        propertyHolder.clearProperty(KNOWN_KEY1 + "foo");
        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
    }

    @Test public void testClearProperty_knownKey() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY2));
        propertyHolder.clearProperty(KNOWN_KEY1);
        assertFalse(propertyHolder.containsKey(KNOWN_KEY1));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY2));
    }

    @Test public void testClearProperties_empty() {
        PropertyHolder propertyHolder = new PropertyHolder();

        assertTrue(propertyHolder.isEmpty());
        propertyHolder.clearProperties();
        assertTrue(propertyHolder.isEmpty());
    }

    @Test public void testClearProperties_nonEmpty() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertFalse(propertyHolder.isEmpty());
        propertyHolder.clearProperties();
        assertTrue(propertyHolder.isEmpty());
    }

    @Test public void testGetKeys_empty() {
        PropertyHolder propertyHolder = new PropertyHolder();

        assertTrue(propertyHolder.isEmpty());
        Iterator i = propertyHolder.getKeys();
        assertFalse(i.hasNext());
    }

    @Test public void testGetKeys_nonEmpty() {
        PropertyHolder propertyHolder = buildNonEmpty();

        assertFalse(propertyHolder.isEmpty());
        Iterator i = propertyHolder.getKeys();
        assertTrue(i.hasNext());

        for (; i.hasNext();) {
            String key = (String) i.next();
            assertTrue(propertyHolder.containsKey(key));
        }
    }

    @Test public void testLoadProperties_nullPropertySource() {
        PropertyHolder propertyHolder = new PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.loadProperties(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testLoadProperties_invalidPropertySource() {
        PropertyHolder propertyHolder = new PropertyHolder();
        FilePropertySource fps = new FilePropertySource();

        boolean failedAsExpected = false;
        try {
            propertyHolder.loadProperties(fps);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testLoadProperties_unknownPropertySource() {
        PropertyHolder propertyHolder = new PropertyHolder();
        FilePropertySource fps = new FilePropertySource();
        fps.setFileName("foo");

        boolean failedAsExpected = false;
        try {
            propertyHolder.loadProperties(fps);
        }
        catch (PropertiesException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    private final PropertyHolder buildNonEmpty() {
        PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.setProperty(KNOWN_KEY1, KNOWN_VALUE1);
        propertyHolder.setProperty(KNOWN_KEY2, KNOWN_VALUE2);
        propertyHolder.setProperty(KNOWN_KEY3, KNOWN_VALUE3);

        return propertyHolder;
    }
}
