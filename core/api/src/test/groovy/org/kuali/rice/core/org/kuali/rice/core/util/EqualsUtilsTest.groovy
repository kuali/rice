package org.kuali.rice.core.org.kuali.rice.core.util;/*
 * Copyright 2006-2011 The Kuali Foundation
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

import org.junit.Assert
import org.junit.Test
import org.kuali.rice.core.util.EqualsUtils

public class EqualsUtilsTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    public final static class Foo {
        String canCompare1;
        String canCompare2;
        List<String> cannotCompare;
    }

    @Test
    public void test_areObjectsEqualUsingCompareTo_shouldBeEqual() {
        Foo f1 = new Foo(canCompare1: "a", canCompare2: "b", cannotCompare: Collections.emptyList());
        Foo f2 = new Foo(canCompare1: "a", canCompare2: "b", cannotCompare: Collections.emptyList());
        Assert.assertTrue(
                EqualsUtils.areObjectsEqualUsingCompareTo(f1, f2, "canCompare1", "canCompare2")
        );
    }

    @Test
    public void test_areObjectsEqualUsingCompareTo_shouldBeUnEqual() {
        Foo f1 = new Foo(canCompare1: "a", canCompare2: "b", cannotCompare: Collections.emptyList());
        Foo f2 = new Foo(canCompare1: "a", canCompare2: "c", cannotCompare: Collections.emptyList());
        Assert.assertFalse(
                EqualsUtils.areObjectsEqualUsingCompareTo(f1, f2, "canCompare1", "canCompare2")
        );
    }

    @Test
    public void test_areObjectsEqualUsingCompareTo_sameInstance() {
        Foo f1 = new Foo(canCompare1: "a", canCompare2: "b", cannotCompare: Collections.emptyList());

        Assert.assertTrue(
                EqualsUtils.areObjectsEqualUsingCompareTo(f1, f1, "canCompare1", "canCompare2")
        );
    }

    @Test
    public void test_areObjectsEqualUsingCompareTo_nonComparableField() {
        Foo f1 = new Foo(canCompare1: "a", canCompare2: "b", cannotCompare: Collections.emptyList());
        Foo f2 = new Foo(canCompare1: "a", canCompare2: "c", cannotCompare: Collections.emptyList());
        Assert.assertFalse(
                EqualsUtils.areObjectsEqualUsingCompareTo(f1, f2, "cannotCompare", "canCompare1")
        );
    }

    @Test
    public void test_areObjectsEqualUsingCompareTo_nonExistentField() {
        Foo f1 = new Foo(canCompare1: "a", canCompare2: "b", cannotCompare: Collections.emptyList());
        Foo f2 = new Foo(canCompare1: "a", canCompare2: "c", cannotCompare: Collections.emptyList());
        shouldFail(RuntimeException) {
            EqualsUtils.areObjectsEqualUsingCompareTo(f1, f2, "scoobydoo")
        }
    }
}
