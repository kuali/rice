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