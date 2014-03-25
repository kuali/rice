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

import org.apache.commons.lang.StringUtils;

/**
 * PersistenceOption is used when saving through the data object service to configure how the data will be stored.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersistenceOption implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * Used to link references and synchronize foreign keys in the data object.
     */
    public static PersistenceOption LINK_KEYS = new PersistenceOption("org.kuali.rice.krad.data.LINK_KEYS");

    /**
     * Used to synchronize the data object with the database.
     */
    public static PersistenceOption FLUSH = new PersistenceOption("org.kuali.rice.krad.data.FLUSH");

    private final String optionId;

    /**
    * Sets the option Id
    *
    * @param optionId cannot be null or blank.
    */
    public PersistenceOption(String optionId) {
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

}
