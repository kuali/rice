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
package org.kuali.rice.krad.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the values for a multi-valued key. This is intended primarily for use on
 * {@link DataObjectService#find(Class, Object)} in situations where you have a data object which has a compound
 * primary key represented by more than one field. In such cases the keys in the map you construction this class with
 * should be the field names of the primary key fields, and the values in the maps should be the values by which you
 * want to perform the find.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class CompoundKey implements Serializable {

	private static final long serialVersionUID = 1L;

    private final Map<String, ?> keys;

    /**
     * Construct a new instance of a CompoundKey from the given key values map.
     *
     * @param keys map of field name to value for the compound key, must be non-null and non-empty
     *
     * @throws IllegalArgumentException if the given Map is null or empty
     */
    public CompoundKey(Map<String, ?> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("Compound key map should be non-null as well as having at least one"
                    + "value.");
        }
        this.keys = new HashMap<String, Object>(keys);
    }

    /**
     * Returns an unmodifable Map of the key values on this CompoundKey
     *
     * @return unmodifiable map of the key values on this CompoundKey
     */
    public Map<String, ?> getKeys() {
        return Collections.unmodifiableMap(keys);
    }

	/**
	 * Returns true if any of the fields in this compound key have null values, since that usually indicates an
	 * incomplete and unsaved object.
	 * 
	 * @return
	 */
	public boolean hasNullKeyValues() {
		for (Object value : keys.values()) {
			if (value == null) {
				return true;
			}
		}
		return false;
	}

}
