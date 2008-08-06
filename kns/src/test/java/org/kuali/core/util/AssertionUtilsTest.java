/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.IllegalFormatException;

import org.junit.Test;
import org.kuali.test.KNSTestCase;

/**
 * This class tests {@link AssertionUtils}.
 */
public class AssertionUtilsTest extends KNSTestCase {
    private static final String BAD_FORMAT = "%q";

    @Test public void testBooleanUnboxing() {
        AssertionUtils.assertThat(Boolean.TRUE);
        try {
            AssertionUtils.assertThat(Boolean.FALSE);
            fail("didn't throw AssertionError");
        }
        catch (AssertionError e) {
            // good
        }
    }

    @Test public void testBoxingMessage() {
        try {
            AssertionUtils.assertThat(false, 42);
            fail("didn't throw AssertionError");
        }
        catch (AssertionError e) {
            // good
            assertEquals("42", e.getMessage());
        }
    }

    @Test public void testFormatting() {
        try {
            AssertionUtils.assertThat(false, "and %s", "foo");
            fail("didn't throw AssertionError");
        }
        catch (AssertionError e) {
            // good
            assertEquals("and foo", e.getMessage());
        }
    }

    @Test public void testBadFormatting() {
        try {
            AssertionUtils.assertThat(false, BAD_FORMAT, "foo");
            fail("didn't throw IllegalFormatException");
        }
        catch (IllegalFormatException e) {
            // good
        }
    }

    @Test public void testNoArgsNoFormatting() {
        try {
            AssertionUtils.assertThat(false, BAD_FORMAT);
            fail("didn't throw AssertionError");
        }
        catch (AssertionError e) {
            // good
            assertEquals(BAD_FORMAT, e.getMessage());
        }
    }

    @Test public void testVarArgsFormatting() {
        try {
            AssertionUtils.assertThat(false, "and %s %s", "foo", "bar");
            fail("didn't throw AssertionError");
        }
        catch (AssertionError e) {
            // good
            assertEquals("and foo bar", e.getMessage());
        }
    }

    @Test public void testNullDetail() {
        try {
            AssertionUtils.assertThat(false, null);
            fail("didn't throw AssertionError");
        }
        catch (AssertionError e) {
            // good
            assertEquals("null", e.getMessage());
        }
    }

    @Test public void testNullDetailPrintf() {
        try {
            AssertionUtils.assertThat(false, null, "foo");
            fail("didn't throw NullPointerException");
        }
        catch (NullPointerException e) {
            // good
        }
    }
}
