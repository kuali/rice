/**
 * Copyright 2005-2016 The Kuali Foundation
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

import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.GloballyUnique;


/**
 * CopyOption is used when calling the {@link DataObjectService#copyInstance(Object)} method to adjust the behavior of
 * the method.
 * 
 * See the constants defined within the class for the available options and descriptions.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CopyOption implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
	 * Specify that the PK fields on the object must be cleared as part of the copy operation.
	 */
	public static CopyOption RESET_PK_FIELDS = new CopyOption("org.kuali.rice.krad.data.RESET_PK_FIELDS");

	/**
	 * Specify that the {@link Version} annotated field should be cleared if present on the copied object.
	 */
	public static CopyOption RESET_VERSION_NUMBER = new CopyOption("org.kuali.rice.krad.data.RESET_VERSION_NUMBER");

	/**
	 * Specify that the {@literal <tt>objectId</tt>} field (see {@link GloballyUnique}) should be cleared on the copied
	 * object and all children.
	 */
	public static CopyOption RESET_OBJECT_ID = new CopyOption("org.kuali.rice.krad.data.RESET_OBJECT_ID");

    private final String optionId;

	/**
	 * Sets the option Id
	 * 
	 * @param optionId
	 *            cannot be null or blank.
	 */
    public CopyOption(String optionId) {
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
