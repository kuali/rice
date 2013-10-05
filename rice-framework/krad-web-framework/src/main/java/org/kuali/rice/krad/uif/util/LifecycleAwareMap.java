/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map implementation for internal use by a lifecycle element.
 * 
 * <p>Mutability of the map will follow the semantics for the lifecycle element.</p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LifecycleAwareMap<K, V> implements Map<K, V>, UifCloneable {
    
    /**
     * The lifecycle element this map is related to.
     */
    private final LifecycleElement lifecycleElement;

    /**
     * Delegating map implementation.
     */
    private Map<K, V> delegate;

    /**
     * Create a new map instance for use with a lifecycle element.
     * 
     * @param lifecycleElement The lifecycle element to use for mutability checks.
     */
    public LifecycleAwareMap(LifecycleElement lifecycleElement) {
        this.lifecycleElement = lifecycleElement;
        this.delegate = Collections.emptyMap();
    }

    /**
     * Create a new list instance, based on another list.
     * 
     * @param lifecycleElement The lifecycle element to use for mutability checks.
     * @param delegate The list to wrap.
     */
    public LifecycleAwareMap(LifecycleElement lifecycleElement, Map<K, V> delegate) {
        this.lifecycleElement = lifecycleElement;
        this.delegate = delegate;
    }

    /**
     * Ensure that the delegate list can be modified.
     */
    private void ensureMutable() {
        lifecycleElement.checkMutable(true);
        
        if (delegate == Collections.EMPTY_MAP) {
            delegate = new HashMap<K, V>();
        }
    }

    public int size() {
        return this.delegate.size();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    public V get(Object key) {
        return this.delegate.get(key);
    }

    public V put(K key, V value) {
        ensureMutable();
        return this.delegate.put(key, value);
    }

    public V remove(Object key) {
        lifecycleElement.checkMutable(true);
        return delegate == Collections.EMPTY_MAP ? null : this.delegate.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        ensureMutable();
        this.delegate.putAll(m);
    }

    public void clear() {
        if (delegate != Collections.EMPTY_MAP) {
            this.delegate.clear();
        }
    }

    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    public Collection<V> values() {
        return this.delegate.values();
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        // TODO: Return entrySet wrapper
        return this.delegate.entrySet();
    }

    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
