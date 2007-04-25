/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.exporter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.DuplicateKeyException;


/**
 * Adds a litle strong type-checking and validation on top of the generic LinkedHashMap
 * 
 * 
 */
public class StringMap extends LinkedHashMap {
    private static final long serialVersionUID = 7364206011639131063L;

    /**
     * Associates the given String with the given Map value.
     * 
     * @param key
     * @param value
     */
    public void set(String key, Map value) {
        setUnique(key, value);
    }

    /**
     * Associates the given String with the given String value.
     * 
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        setUnique(key, value);
    }


    /**
     * Verifies that the key isn't blank, and that the value isn't null, and prevents duplicate keys from being used.
     * 
     * @param key
     * @param value
     */
    private void setUnique(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("invalid (blank) key");
        }
        if (value == null) {
            throw new IllegalArgumentException("invalid (null) value");
        }

        if (containsKey(key)) {
            throw new DuplicateKeyException("duplicate key '" + key + "'");
        }

        super.put(key, value);
    }


    /**
     * Overridden to prevent direct calls
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException("direct calls to put not supported");
    }

    /**
     * Overridden to prevent direct calls
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public void putAll(Map m) {
        throw new UnsupportedOperationException("direct calls to putAll not supported");
    }
}