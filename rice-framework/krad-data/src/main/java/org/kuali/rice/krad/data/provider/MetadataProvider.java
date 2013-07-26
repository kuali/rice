/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.krad.data.provider;

import java.util.Collection;
import java.util.Map;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;

/**
 * Defines metadata SPI for data providers. These providers extract data from their sources (JPA Annotations, Custom
 * krad-data anotations, Spring configuration, Message Services) to provide comprehensive information to the application
 * and UIF layers to help with presentation of information.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MetadataProvider extends Provider {

    /**
     * Provides the metadata available from this provider for all of it's data objects.
     *
     * @return the metadata provided by this provider
     */
	Map<Class<?>, DataObjectMetadata> provideMetadata();

	/**
	 * Provides the metadata for the given types.
	 * 
	 * @param types
	 * @return a Map of the data object types to their metadata
	 */
	Map<Class<?>, DataObjectMetadata> provideMetadataForTypes(Collection<Class<?>> types);

    /**
     * Obtains the metadata for a specific data type.
     *
     * @return The metadata for the given data object or null if no metadata is available for the given type.
     * @throws IllegalArgumentException if the data object type is null.
     */
	DataObjectMetadata getMetadataForType(Class<?> dataObjectType);

	/**
	 * Indicates whether or not this provider handles metadata for the given data object type.
	 * 
	 * @param type the data object type to check
	 * 
	 * @return true if this provider will return any data for the given type, false otherwise
	 */
	boolean handles(Class<?> type);

	/**
	 * Returns a complete list of the data object types which will return data from this provider.
	 * 
	 * @return A non-null list of all the data object types supported by this provider.
	 */
	Collection<Class<?>> getSupportedTypes();

	/**
	 * Flag which allows the service to indicate that it requires knowledge of previously discovered persistable
	 * entities. That is, the service is designed only to process existing objects and not to create new ones.
	 * 
	 * @return true if this provider will fail when passed an empty list of entity types
	 */
	boolean requiresListOfExistingTypes();

}
