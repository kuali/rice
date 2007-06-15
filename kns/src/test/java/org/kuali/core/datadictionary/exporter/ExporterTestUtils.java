/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.exporter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang.StringUtils;

/**
 * This class is used to test a dot-separated key-path.
 * 
 * 
 */
public class ExporterTestUtils {
    /**
     * Foreach (dot-separated key-path, expected value) in the given String [], traverses the given Map using that path and asserts
     * that the expectedValue matches the actualValue. If the expectedValue begins with "*", the value (stripped of that leading *)
     * is matched as a regex against the actualValue.
     * 
     * @param map
     * @param expectedKeyValues String[][], each row of which which contains a dot-separated key-path (a.b.c) and an expected value
     */
    public static void comparePropertyStrings(Map map, String[][] expectedKeyValues) {
        for (int i = 0; i < expectedKeyValues.length; ++i) {
            String expectedPath = expectedKeyValues[i][0];
            String expectedValue = expectedKeyValues[i][1];

            boolean matchRegex = false;
            if (expectedPath.startsWith("*")) {
                expectedPath = StringUtils.substringAfter(expectedPath, "*");
                matchRegex = true;
            }

            String actualValue = (String) traverseMap(map, expectedPath);

            if (matchRegex) {
                assertTrue(expectedPath, actualValue.matches(expectedValue));
            }
            else {
                assertEquals(expectedPath, expectedValue, actualValue);
            }
        }
    }


    /**
     * @param map
     * @param path
     * @return Object found by traversing the given Map of Maps using the dot-separated keys in the given path.
     */
    public static Object traverseMap(Map map, String path) {
        if (map == null) {
            throw new IllegalArgumentException("invalid (null) composite");
        }

        Object result = null;
        if (!StringUtils.isBlank(path)) {
            String[] splitPath = path.split("\\.");

            StringBuffer currentPath = new StringBuffer();
            Object nextValue = map;
            for (int i = 0; i < splitPath.length; ++i) {
                String nextKey = splitPath[i];
                if (i > 0) {
                    currentPath.append(".");
                }

                if (nextValue == null) {
                    throw new NullPointerException("can't navigate past a null pointer at path '" + currentPath.toString() + "'");
                }

                if (nextValue instanceof Map) {
                    Map currentLevel = (Map) nextValue;
                    nextValue = currentLevel.get(nextKey);
                }
                else {
                    throw new ClassCastException("don't know how to navigate '" + nextValue.getClass().getName() + "' instances at path '" + currentPath.toString() + "'");
                }

                currentPath.append(nextKey);
            }
            result = nextValue;
        }
        else {
            result = map;
        }

        return result;
    }

    private static Object get(Collection c, int getIndex) {
        if (c == null) {
            throw new IllegalArgumentException("invalid (null) collection");
        }
        Object result = null;

        int currentIndex = 0;
        Object currentValue = null;
        for (Iterator i = c.iterator(); i.hasNext();) {
            currentValue = i.next();
            if (currentIndex++ == getIndex) {
                result = currentValue;
                break;
            }
        }

        return result;
    }


    private static void assertTrue(String expectedPath, boolean received) {
        if (!received) {
            throw new AssertionFailedError("assertion failed at path '" + expectedPath + "'");
        }
    }

    private static void assertEquals(String expectedPath, String expected, String received) {
        if (!StringUtils.equals(expected, received)) {
            throw new AssertionFailedError("assertion failed at path '" + expectedPath + "': expected '" + expected + "', received '" + received + "'");
        }
    }
}