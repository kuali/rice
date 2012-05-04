/*
 * Copyright 2006-2012 The Kuali Foundation
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

package org.kuali.rice.krad.uif.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *  ScriptUtilsTest tests {@link ScriptUtils}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ScriptUtilsTest {

    @Test
    /**
     * tests {@link ScriptUtils#escapeHtml(String)}
     */
    public void testEscapeHtml() throws Exception {
        assertEquals("wasn&apos;t", ScriptUtils.escapeHtml("wasn't"));
        assertEquals("&quot;wasn&apos;t&quot;", ScriptUtils.escapeHtml("\"wasn't\""));
    }

    @Test
    /**
     * tests {@link ScriptUtils#escapeHtml(java.util.List)}
     */
    public void testEscapeHtmlStringList() {
        String[] escaped = {"wasn&apos;t", "&quot;wasn&apos;t&quot;"};
        String[] unEscaped = {"wasn't", "\"wasn't\""};
        assertEquals(Arrays.asList(escaped), ScriptUtils.escapeHtml(Arrays.asList(unEscaped)));

        List<String> nullList = null;
        assertNull(ScriptUtils.escapeHtml(nullList));

        List<String> emptyList = Collections.emptyList();
        assertEquals(emptyList, ScriptUtils.escapeHtml(emptyList));
    }
}
