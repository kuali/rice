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

import org.kuali.rice.krad.data.provider.CompositeMetadataProvider;

/**
 * A component of {@link MetadataCommon} which specifies what to do when a duplicate data object, attribute, collection
 * or reference is encountered during the merging performed by the {@link CompositeMetadataProvider}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum MetadataMergeAction {
	/**
	 * The default behavior. Unset attributes will be left alone.
	 */
	MERGE,
	/**
	 * If a match is found (same data object or attribute name), the existing object will be replaced completely by this
	 * one.
	 */
	REPLACE,
	/**
	 * If a match is found (same data object or attribute name), the existing object will be removed. (Any attribute
	 * except that forming the key (type or attribute name) can be left unset.)
	 */
	REMOVE,
	/**
	 * If a match is found (same data object or attribute name), the existing object will be left alone. The metadata
	 * object will only be included if there is not already an existing object.
	 */
	NO_OVERRIDE
}
