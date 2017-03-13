/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;


/**
 * MaterializeOption is used when calling the
 * {@link DataObjectWrapper#materializeReferencedObjects(MaterializeOption...)} method to adjust the behavior of the
 * method.
 * 
 * See the constants defined within the class for the available options and descriptions.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaterializeOption implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
	 * Specify that only references should be materialized. Adding this disables the default of refreshing both
	 * references and collections.
	 */
	public static MaterializeOption REFERENCES = new MaterializeOption("org.kuali.rice.krad.data.REFERENCES");

	/**
	 * Specify that only collections should be materialized. Adding this disables the default of refreshing both
	 * references and collections.
	 */
	public static MaterializeOption COLLECTIONS = new MaterializeOption("org.kuali.rice.krad.data.COLLECTIONS");

	/**
	 * Specify that references and/or collections which are saved when the wrapped object is saved should also be
	 * refreshed.
	 * 
	 * <b>CAUTION:</b> This has the potential to overwrite previously updated data. This will effectively reset all
	 * child objects to their current saved state.
	 */
	public static MaterializeOption UPDATE_UPDATABLE_REFS = new MaterializeOption(
			"org.kuali.rice.krad.data.UPDATE_UPDATABLE_REFS");

    /**
	 * If this option is set, when the foreign key fields do not point to a saved object (per the persistence provider),
	 * the object reference will be nulled out.
	 * 
	 * Without this option, the object in that reference (if any) will be left alone if a valid object is not found.
	 */
	public static MaterializeOption NULL_INVALID_REFS = new MaterializeOption(
			"org.kuali.rice.krad.data.NULL_INVALID_REFS");

	/**
	 * Specify that non-lazy-loaded references should also be reloaded from the persistence provider.
	 */
	public static MaterializeOption INCLUDE_EAGER_REFS = new MaterializeOption(
			"org.kuali.rice.krad.data.INCLUDE_EAGER_REFS");

    private final String optionId;

	/**
	 * Sets the option Id
	 * 
	 * @param optionId
	 *            cannot be null or blank.
	 */
    public MaterializeOption(String optionId) {
        if (StringUtils.isBlank(optionId)) {
            throw new IllegalArgumentException("optionId must not be a null or blank value");
        }
        this.optionId = optionId;
    }

	/**
	 * Gets the option id.
	 * 
	 * @return not null or blank.
	 */
    public String getOptionId() {
        return this.optionId;
    }

	@Override
	public String toString() {
		return optionId;
	}
}
