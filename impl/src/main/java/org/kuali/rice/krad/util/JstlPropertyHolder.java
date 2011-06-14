/*
 * Copyright 2005-2008 The Kuali Foundation
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

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.kuali.rice.krad.util.properties.PropertyTree;

/**
 * This class implements the Map interface for a Properties instance. Exports all properties from the given Properties instance as
 * constants, usable from jstl. Implements the Map interface (by delegating everything to the PropertyTree, which really implements
 * the Map methods directly) so that jstl can translate ${Constants.a} into a call to ConfigConstants.get( "a" ).
 * <p>
 * The contents of this Map cannot be changed once it has been initialized. Any calls to any of the Map methods made before the
 * propertyTree has been initialized (i.e. before setProperties has been called) will throw an IllegalStateException.
 * <p>
 * Jstl converts ${Constants.a.b.c} into get("a").get("b").get("c"), so the properties are stored in a PropertyTree, which converts
 * the initial set( "a.b.c", "value" ) into construction of the necessary tree structure to support get("a").get("b").get("c").
 * <p>
 * Implicitly relies on the assumption that the JSP will be calling toString() on the result of the final <code>get</code>, since
 * <code>get</code> can only return one type, and that type must be the complex one so that further dereferencing will be
 * possible.
 * 
 * 
 */

public class JstlPropertyHolder implements Map {
    private PropertyTree propertyTree;

    /**
     * Default constructor
     */
    public JstlPropertyHolder() {
        propertyTree = null;
    }

    /**
     * Creates a propertyTree to store the given properties
     * 
     * @param properties
     */
    public void setProperties(Properties properties) {
        propertyTree = new PropertyTree(properties);
    }


    /**
     * Copies in the given propertyTree rather than building its own. Reasonably dangerous, since that tree might presumably be
     * modified, violating the readonlyness of this datastructure.
     * 
     * @param properties
     */
    protected void setPropertyTree(PropertyTree tree) {
        propertyTree = tree;
    }


    // delegated methods
    @Override
	public Object get(Object key) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.get(key);
    }

    @Override
	public int size() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.size();
    }

    @Override
	public void clear() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        this.propertyTree.clear();
    }

    @Override
	public boolean isEmpty() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.isEmpty();
    }

    @Override
	public boolean containsKey(Object key) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.containsKey(key);
    }

    @Override
	public boolean containsValue(Object value) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.containsValue(value);
    }

    @Override
	public Collection values() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.values();
    }

    @Override
	public void putAll(Map m) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        this.propertyTree.putAll(m);
    }

    @Override
	public Set entrySet() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.entrySet();
    }

    @Override
	public Set keySet() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.keySet();
    }

    @Override
	public Object remove(Object key) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.remove(key);
    }

    @Override
	public Object put(Object key, Object value) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.put(key, value);
    }
}
