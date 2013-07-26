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
package org.kuali.rice.krad.data.metadata;

import java.io.Serializable;

import org.kuali.rice.krad.data.metadata.impl.security.DataObjectAttributeMaskFormatter;

/**
 * Defines a set of restrictions that are possible on an attribute.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectAttributeSecurity extends Serializable {

	boolean isReadOnly();
	boolean isHide();
	boolean isMask();
	boolean isPartialMask();

	DataObjectAttributeMaskFormatter getMaskFormatter();
	DataObjectAttributeMaskFormatter getPartialMaskFormatter();

	/**
	 * Returns whether any of the restrictions defined in this class are true.
	 */
	boolean hasAnyRestriction();

}