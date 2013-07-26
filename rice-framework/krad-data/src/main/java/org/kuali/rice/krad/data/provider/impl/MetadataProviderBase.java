package org.kuali.rice.krad.data.provider.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.MetadataProvider;

/**
 * Superclass for all metadata providers which contain the basic operations and data structure. All each subclass needs
 * to implement is the initializeMetadata method.
 */
public abstract class MetadataProviderBase implements MetadataProvider {

	protected ConcurrentHashMap<Class<?>, DataObjectMetadata> masterMetadataMap =
            new ConcurrentHashMap<Class<?>, DataObjectMetadata>();

	/**
	 * Performs the initialization of the provider with the given set of types.
	 * 
	 * If the list is null or empty, the provider is expected to discover the types via other means, or do nothing if
     * the types cannot be discovered.
	 */
	protected abstract void initializeMetadata(Collection<Class<?>> types);

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

	@Override
	public Collection<Class<?>> getSupportedTypes() {
		if (masterMetadataMap.isEmpty()) {
			initializeMetadata(null);
		}
		return masterMetadataMap.keySet();
	}

	@Override
	public Map<Class<?>, DataObjectMetadata> provideMetadata() {
		return provideMetadataForTypes(null);
	}

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

    protected boolean isClassPersistable(Class<?> clazz) {
        if (masterMetadataMap.isEmpty()) {
			initializeMetadata(null);
        }
        return masterMetadataMap.containsKey(clazz);
    }

	/**
	 * By default, providers are assumed to be able to pull the list of annotated types from somewhere.
	 */
	@Override
	public boolean requiresListOfExistingTypes() {
		return false;
	}
}
