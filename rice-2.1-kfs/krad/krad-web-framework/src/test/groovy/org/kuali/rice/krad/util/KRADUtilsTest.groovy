/**
 * Copyright 2005-2012 The Kuali Foundation
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


package org.kuali.rice.krad.util;

import org.junit.Test;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;

import static org.junit.Assert.assertTrue;

public class KRADUtilsTest {
    
    @Test
    public void testHydrateAttributeValue() throws Exception {

        // KRADUtils.hydrateAttributeValue(Class, String) attempts to coerce the given string to the type of the class.
        // it has a few simple tricks it attempts to do this, otherwise it just returns null.

        Object result = KRADUtils.hydrateAttributeValue(Boolean.class, "Yes");
        assertTrue(Boolean.TRUE.equals(result));

        result = KRADUtils.hydrateAttributeValue(Boolean.TYPE, "Yes");
        assertTrue(Boolean.TRUE.equals(result));

        result = KRADUtils.hydrateAttributeValue(String.class, "Yes");
        assertTrue("Yes".equals(result));

        result = KRADUtils.hydrateAttributeValue(Double.class, "1.2");
        assertTrue(Double.valueOf("1.2").equals(result));

        // Can't possibly turn "Yes" into a DictionaryValidationResult
        result = KRADUtils.hydrateAttributeValue(DictionaryValidationResult.class, "Yes");
        assertTrue(result == null);

        // Can't possibly turn null String into anything but null
        result = KRADUtils.hydrateAttributeValue(String.class, null);
        assertTrue(result == null);

        // null class results in null coming back
        result = KRADUtils.hydrateAttributeValue(null, "Yes");
        assertTrue(result == null);
    }
}
