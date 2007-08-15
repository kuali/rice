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
// Created on Aug 29, 2006

package org.kuali.rice.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

public class SimpleNodeSettingStoreTest extends TestCase {
    
	@Test public void test() throws Exception {
        SimpleNodeSettingsStore s = new SimpleNodeSettingsStore();
        File f = File.createTempFile("simplenodesettingstoretest", "unittest");
        f.deleteOnExit();
        s.setPropertiesPath(f.getAbsolutePath());
        s.afterPropertiesSet();
        assertEquals(0, s.getSettings().size());
        s.setSetting("foo", "bar");

        assertSetting(f.getAbsolutePath(), "foo", "bar");

        s.removeSetting("foo");

        assertSetting(f.getAbsolutePath(), "foo", null);
    }

    protected void assertSetting(String path, String name, String value) throws IOException {
        Properties p = new Properties();
        FileInputStream fis = new FileInputStream(path);
        try {
            p.load(fis);
        } finally {
            fis.close();
        }
        assertEquals(value, p.getProperty(name));
    }
}