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
package org.kuali.rice.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * This is just a unit test that verifies that the ImmutableProperties class works as expected.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ImmutablePropertiesTest extends Assert {

	private Properties properties;
	
	@Before
	public void setup() {
		Properties defaultProperties = new Properties();
		defaultProperties.put("20", "default");
		Properties properties = new Properties(defaultProperties);
		properties.put("1", "A");
		properties.put("2", "B");
		properties.put("3", "C");
		this.properties = new ImmutableProperties(properties);
	}

	@Test public void setProperty() {
		UnsupportedOperationException exception = null;
		try {
			properties.setProperty("4", "D");
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(properties.get("4"));
	}
	
	@Test public void loadReader() throws IOException {
		UnsupportedOperationException exception = null;
		try {
			StringReader reader = new StringReader("4 = D");
			properties.load(reader);
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(properties.get("4"));
	}
	
	@Test public void loadInputStream() throws IOException {
		UnsupportedOperationException exception = null;
		try {
			InputStream input = new ByteArrayInputStream("4 = D".getBytes());
			properties.load(input);
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(properties.get("4"));
	}
	
	@Test public void loadFromXML() throws IOException {
		UnsupportedOperationException exception = null;
		try {
			InputStream input = new ByteArrayInputStream("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\"><properties><entry key=\"4\">D</entry></properties>".getBytes());
			properties.load(input);
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(properties.get("4"));
	}
	
	@Test public void getProperty() throws IOException {
		String value1 = properties.getProperty("1");
		assertEquals("A", value1);
		String value2 = properties.getProperty("2");
		assertEquals("B", value2);
		String value3 = properties.getProperty("3");
		assertEquals("C", value3);
		String defaultValue = properties.getProperty("20");
		assertEquals("default", defaultValue);
	}
	
	@Test public void propertyNames() throws IOException {
		Enumeration en = properties.propertyNames();
		Set<String> set = new HashSet<String>();
		while (en.hasMoreElements()) {
			set.add((String)en.nextElement());
		}
		assertTrue(set.contains("1"));
		assertTrue(set.contains("2"));
		assertTrue(set.contains("3"));
		assertTrue(set.contains("20"));
		assertFalse(set.contains("30"));
	}
	
	@Test public void stringPropertyNames() throws IOException {

	    Enumeration propertyNames = properties.propertyNames();
	    Set<String> set = new HashSet<String>();
	    while (propertyNames.hasMoreElements()) {
	        set.add((String)propertyNames.nextElement());
	    }
	        
		assertTrue(set.contains("1"));
		assertTrue(set.contains("2"));
		assertTrue(set.contains("3"));
		assertTrue(set.contains("20"));
		assertFalse(set.contains("30"));
	}
	
	@Test public void clear() {
		UnsupportedOperationException exception = null;
		try {
			properties.clear();
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertEquals("A", properties.get("1"));
	}
	
	@Test public void put() {
		UnsupportedOperationException exception = null;
		try {
			properties.put("4", "D");
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(properties.get("4"));
	}
	
	@Test public void putAll() {
		UnsupportedOperationException exception = null;
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("4", "D");
			properties.putAll(map);
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(properties.get("4"));
	}
	
	@Test public void remove() {
		UnsupportedOperationException exception = null;
		try {
			properties.remove("1");
		} catch (UnsupportedOperationException e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNotNull(properties.get("1"));
	}
	
}
