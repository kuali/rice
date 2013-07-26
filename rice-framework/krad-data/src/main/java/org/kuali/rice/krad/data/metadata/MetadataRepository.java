package org.kuali.rice.krad.data.metadata;

import java.util.Map;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;

/**
 * Public (read-only) interface to MetadataProvider stack.
 * Implementations must be thread safe.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MetadataRepository {

    /**
     * Obtain metadata for specified data object type
     *
     * @param type the data object type for which to return metadata
     *
     * @return DataObjectMetadata for the type or null if none is known
     */
	DataObjectMetadata getMetadata(Class<?> type);

    /**
     * Returns all metadata for all known data object types
     *
     * @return all metadata for all known data object types
     */
	Map<Class<?>, DataObjectMetadata> getAllMetadata();

    /**
     * Indicates whether or not the repository contains metadata for the given type.
     *
     * @param type the type to check metadata for
     *
     * @return true if the repository contains metadata for the given type, false otherwise
     */
    boolean contains(Class<?> type);

}