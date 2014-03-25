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
     * Obtain metadata for specified data object type.
     *
     * @param type the data object type for which to return metadata
     * @return DataObjectMetadata for the type or null if none is known
     */
	DataObjectMetadata getMetadata(Class<?> type);

    /**
     * Returns all metadata for all known data object types.
     *
     * @return all metadata for all known data object types
     */
	Map<Class<?>, DataObjectMetadata> getAllMetadata();

    /**
     * Indicates whether or not the repository contains metadata for the given type.
     *
     * @param type the type to check metadata for
     * @return true if the repository contains metadata for the given type, false otherwise
     */
    boolean contains(Class<?> type);

}