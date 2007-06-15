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
package org.kuali.core.datadictionary.validation.fieldlevel;

import org.junit.Test;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;


@WithTestSpringContext
public class JavaClassValidationPatternTest extends KualiTestBase {
    JavaClassValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new JavaClassValidationPattern();
    }

    @Test public final void testMatches_actualClass() {
        assertTrue(pattern.matches(String.class.getName()));
    }

    @Test public final void testMatches_fictitiousClass() {
        assertTrue(pattern.matches("something.wicked.this.way.Comes"));
    }

    @Test public final void testMatches_unqualifiedClass() {
        assertTrue(pattern.matches("String"));
    }

    @Test public final void testMatches_invalidClassname1() {
        assertFalse(pattern.matches("23Tests"));
    }

    @Test public final void testMatches_invalidClassname2() {
        assertFalse(pattern.matches("more tests"));
    }

    @Test public final void testMatches_invalidClassname3() {
        assertFalse(pattern.matches("more.and.more:tests"));
    }

    @Test public final void testMatches_invalidClassname4() {
        assertFalse(pattern.matches("still.more.tests."));
    }
}
