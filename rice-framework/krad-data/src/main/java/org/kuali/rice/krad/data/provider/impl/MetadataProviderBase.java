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
package org.kuali.rice.krad.data.provider.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.MetadataProvider;

/**
 * Superclass for all metadata providers which contain the basic operations and data structure.
 *
 * <p>All each subclass needs to implement is the initializeMetadata method.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MetadataProviderBase implements MetadataProvider {

    /**
     * The map of types to metadata.
     */
	protected ConcurrentHashMap<Class<?>, DataObjectMetadata> masterMetadataMap =
            new ConcurrentHashMap<Class<?>, DataObjectMetadata>();

	/**
	 * Performs the initialization of the provider with the given set of types.
	 *
     * <p>
	 * If the list is null or empty, the provider is expected to discover the types via other means, or do nothing if
     * the types cannot be discovered.
     * </p>
	 */
	protected abstract void initializeMetadata(Collection<Class<?>> types);

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean handles(Class<?> type) {
		if (type == null) {
			return false;
		}
		if (masterMetadataMap.isEmpty()) {
			initializeMetadata(null);
		}
		return masterMetadataMap.containsKey(type);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<Class<?>> getSupportedTypes() {
		if (masterMetadataMap.isEmpty()) {
			initializeMetadata(null);
		}
		return masterMetadataMap.keySet();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Map<Class<?>, DataObjectMetadata> provideMetadata() {
		return provideMetadataForTypes(null);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Map<Class<?>, DataObjectMetadata> provideMetadataForTypes(Collection<Class<?>> types) {
		if (masterMetadataMap.isEmpty()) {
			initializeMetadata(types);
			return Collections.unmodifiableMap(masterMetadataMap);
		} else if (types == null || types.isEmpty()) {
			return Collections.unmodifiableMap(masterMetadataMap);
		} else {
			HashMap<Class<?>, DataObjectMetadata> subMap = new HashMap<Class<?>, DataObjectMetadata>();

			for (Class<?> key : masterMetadataMap.keySet()) {
				if (types.contains(key)) {
					subMap.put(key, masterMetadataMap.get(key));
				}
			}
			return subMap;
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public DataObjectMetadata getMetadataForType(Class<?> dataObjectType) throws IllegalArgumentException {
		if (dataObjectType == null) {
			throw new IllegalArgumentException("getMetadataForType: NULL passed for the dataObjectType");
		}
		if (masterMetadataMap.isEmpty()) {
			initializeMetadata(null);
		}
		return masterMetadataMap.get(dataObjectType);
	}

    /**
     * Determines whether the given class can be persisted.
     *
     * @param clazz the class to check for persistability.
     * @return true if the class is persistable, false otherwise.
     */
    protected boolean isClassPersistable(Class<?> clazz) {
        if (masterMetadataMap.isEmpty()) {
			initializeMetadata(null);
        }
        return masterMetadataMap.containsKey(clazz);
    }

	/**
	 * {@inheritDoc}
     *
     * By default, providers are assumed to be able to pull the list of annotated types from somewhere.
	 */
	@Override
	public boolean requiresListOfExistingTypes() {
		return false;
	}
}
