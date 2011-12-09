/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch

import org.junit.Test
import org.kuali.rice.core.api.search.SearchOperator
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

/**
 * Tests DocumentSearchInternalUtils
 */
class DocumentSearchInternalUtilsTest {
    // ensures that the range is split in only two parts and no more
    // (any "third part" should be considered a malformed bound)
    @Test void testSplitsRangeInTwo() {
        def first = "1"
        def middle = "2"
        def last = "3"

        for (SearchOperator op: [ SearchOperator.BETWEEN, SearchOperator.BETWEEN_EXCLUSIVE_UPPER ]) {
            // Maximum of 2 values!
            Range range = DocumentSearchInternalUtils.parseRange(first + op.op() + middle + op.op() + last)
            assertEquals(first, range.lowerBoundValue)
            assertEquals(middle + op.op() + last, range.upperBoundValue)
        }
    }

    // makes sure that the range is split on the entire range operator, no just on any character in the operator
    @Test void testSplitsOnWholeOperator() {
        def lower = "1.0"
        def upper = "2.0"
        for (SearchOperator op: [ SearchOperator.BETWEEN, SearchOperator.BETWEEN_EXCLUSIVE_UPPER ]) {
            // Maximum of 2 values!
            Range range = DocumentSearchInternalUtils.parseRange(lower + op.op() + upper)
            assertEquals(lower, range.lowerBoundValue)
            assertEquals(upper, range.upperBoundValue)
        }
    }
}
