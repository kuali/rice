/*
 * Copyright 2007 The Kuali Foundation
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
// Created on Apr 27, 2006

package org.kuali.rice.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

public class BaseConfigTest extends TestCase {
    private static final class SimpleConfig extends BaseConfig {
        private Properties baseProps;
        private Map baseObjects;
        public SimpleConfig(List<String> fileLocs, Properties baseProps, Map baseObjects) {
            super(fileLocs);
            this.baseProps = baseProps;
            this.baseObjects = baseObjects;
        }
        public Properties getBaseProperties() {
            return this.baseProps;
        }
        public Map getBaseObjects() {
        	return this.baseObjects;
        }
    }

    /**
     * Tests the hierarchical override capabilities.
     */
    @Test public void testBaseConfig() throws IOException {
    	Properties base = new Properties();
        base.setProperty("foo", "base:foo");
        base.setProperty("boo", "base:boo");
        List<String> configs = new ArrayList<String>(1);
        configs.add("classpath:org/kuali/rice/config/config-1.xml");
        SimpleConfig sc = new SimpleConfig(configs, base, null);
        sc.parseConfig();
        assertEquals("base:boo", sc.getProperty("boo"));
        assertEquals("config-1:foo base:boo", sc.getProperty("foo"));
        assertEquals("config-2:bar config-1:baz", sc.getProperty("bar"));
        assertEquals("config-2:blah base:boo", sc.getProperty("blah"));
    }
}