/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This is a wrapper for the Properties class that prevents other objects from making
 * changes to the properties stored within it. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ImmutableProperties extends Properties {
	
	private Properties _properties;
	
	public ImmutableProperties(Properties properties){
		super();
		this._properties = properties;	
	}

	@Override
	public Object get(Object key) {
		return _properties.get(key);
	}
	
	@Override
	public String getProperty(String key) {
		return _properties.getProperty(key);
	}
	
	@Override
    public String getProperty(String key, String defaultValue) {
		return _properties.getProperty(key, defaultValue);
	}
	
	@Override
	public Enumeration<?> propertyNames() {
		return _properties.propertyNames();
	}
	
	@Override 
	public Set<String> stringPropertyNames() {
		return _properties.stringPropertyNames();
	}
	
	@Override
	public void list(PrintStream out) {
		_properties.list(out);
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException("This class is immutable");
	}
	
	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		 throw new UnsupportedOperationException("This class is immutable");
	}

	//@Override
	public synchronized void load(Reader reader) throws IOException {
		throw new UnsupportedOperationException("This class is immutable");
	}

	@Override
	public synchronized void loadFromXML(InputStream in) throws IOException,
			InvalidPropertiesFormatException {
		throw new UnsupportedOperationException("This class is immutable");
	}

	@Override
	public synchronized void save(OutputStream out, String comments) {
		throw new UnsupportedOperationException("This class is immutable");
	}

	@Override
	public synchronized Object setProperty(String key, String value) {
		throw new UnsupportedOperationException("This class is immutable");
	}

	@Override
	public synchronized Object put(Object key, Object value) {
		throw new UnsupportedOperationException("This class is immutable");
	}

	@Override
	public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
		throw new UnsupportedOperationException("This class is immutable");
	}

	@Override
	public synchronized Object remove(Object key) {
		throw new UnsupportedOperationException("This class is immutable");
	}
	
	@Override
	public Set<Object> keySet() {
		return Collections.unmodifiableSet(_properties.keySet());
	}
	
	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		return Collections.unmodifiableSet(_properties.entrySet());
	}
	
	@Override
	public Collection<Object> values() {
		return Collections.unmodifiableCollection(_properties.values());
	}

}
