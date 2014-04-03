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
package org.kuali.rice.krad.uif.view;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultExpressionEvaluatorTest {
    @Test
    public void testServerEvaluationPattern() {
        List<String> shouldMatch = Arrays.asList(
                "(getSomething() || isSomething())",
                "(getThis())");

        for (String input: shouldMatch) {
            Matcher matcher = DefaultExpressionEvaluator.SERVER_EVALUATION_PATTERN.matcher(input);
            assertTrue("Should match server evaluation pattern. input: " + input, matcher.find());
        }

        // The first four items in the list contain the characters is or get
        List<String> shouldNotMatch = Arrays.asList(
                "(newCollectionLines['listOfItems'].id == null) or (newCollectionLines['listOfItems'].id == '')",
                "(newCollectionLines['budgetItems'].id == null) or (newCollectionLines['budgetItems'].id == '')",
                "(listOfItems.id == null)",
                "(budgetItems.id == null)",
                "(setThis())");
        for (String input: shouldNotMatch) {
            Matcher matcher = DefaultExpressionEvaluator.SERVER_EVALUATION_PATTERN.matcher(input);
            assertFalse("Should not match server evaluation pattern. input: " + input, matcher.find());
        }
    }
}

