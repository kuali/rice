/*
 * Copyright 2006-2008 The Kuali Foundation
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

package org.kuali.rice.core.config;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class BaseConfigTest {
    /**
     * Tests the hierarchical override capabilities.
     */
    @Test public void testBaseConfig() throws IOException {
    	Properties base = new Properties();
        base.setProperty("foo", "base:foo");
        base.setProperty("boo", "base:boo");
        List<String> configs = new ArrayList<String>(1);
        configs.add("classpath:org/kuali/rice/core/config/config-1.xml");
        Config sc = new JAXBConfigImpl(configs, base);
        sc.parseConfig();
        assertEquals("base:boo", sc.getProperty("boo"));
        assertEquals("config-1:foo base:boo", sc.getProperty("foo"));
        assertEquals("config-2:bar config-1:baz", sc.getProperty("bar"));
        assertEquals("config-2:blah base:boo", sc.getProperty("blah"));
        assertEquals("config-2:quux", sc.getProperty("quux"));
        
        String r1 = sc.getProperty("random1");
        String r2 = sc.getProperty("random2");
        String r3 = sc.getProperty("random3");
        
        assertNotNull(r1);
        assertNotNull(r2);
        assertNotNull(r3);
        
        assertTrue(between(100, 199, Integer.parseInt(r1)));
        assertTrue(between(200, 299, Integer.parseInt(r2)));
        assertTrue(between(300, 399, Integer.parseInt(r3)));
    }
    
    protected boolean between(int min, int max, int val) {
        return val >= min && val <= max;
    }
}
