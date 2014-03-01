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
package org.kuali.rice.krad.data.util;

import junit.framework.TestCase;
import org.kuali.rice.krad.data.util.ReferenceLinker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Unit test for {@link org.kuali.rice.krad.data.util.ReferenceLinker}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReferenceLinkerTest extends TestCase {

    private ReferenceLinker referenceLinker = new ReferenceLinker();

    public void testDecomposePropertyPaths() throws Exception {
        Set<String> changes = new HashSet<String>();
        changes.add("a.b.c.e");
        changes.add("a.b2.c.e");
        changes.add("b.1.2.3");
        changes.add("c[0].a");
        changes.add("c[1].b");
        changes.add("c['2'].c");
        changes.add("c[\"a.b.c\"].b.a");

        Map<String, Set<String>> decomposed = referenceLinker.decomposePropertyPaths(changes);

        // first let's check 'a'
        assertTrue(decomposed.containsKey("a"));
        Set<String> paths = decomposed.get("a");
        assertTrue(paths.contains("b.c.e"));
        assertTrue(paths.contains("b2.c.e"));
        assertEquals(2, paths.size());

        // now let's check 'b'
        assertTrue(decomposed.containsKey("b"));
        paths = decomposed.get("b");
        assertTrue(paths.contains("1.2.3"));
        assertEquals(1, paths.size());

        // now let's check for the 'c's
        assertTrue(decomposed.containsKey("c[0]"));
        assertTrue(decomposed.containsKey("c[1]"));
        assertTrue(decomposed.containsKey("c['2']"));
        assertTrue(decomposed.containsKey("c[\"a.b.c\"]"));

        // check c[0]
        paths = decomposed.get("c[0]");
        assertTrue(paths.contains("a"));
        assertEquals(1, paths.size());

        // check c[1]
        paths = decomposed.get("c[1]");
        assertTrue(paths.contains("b"));
        assertEquals(1, paths.size());

        // check c['2']
        paths = decomposed.get("c['2']");
        assertTrue(paths.contains("c"));
        assertEquals(1, paths.size());

        // check c["a.b.c"]
        paths = decomposed.get("c[\"a.b.c\"]");
        assertTrue(paths.contains("b.a"));
        assertEquals(1, paths.size());

        // all told, there should have been 6 keys
        assertEquals(6, decomposed.size());

    }

}
