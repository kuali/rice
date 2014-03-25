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
package org.kuali.rice.krad.data.jpa;

import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.provider.MetadataProvider;

/**
 * Subclass of the {@link MetadataProvider} which contains the additional methods needed.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface JpaMetadataProvider extends MetadataProvider {

	/**
	 * Given the parameters, injects into the JPA repository a 1:1 relationship between the parent entity and the
     * extension entity via the given property name (which must exist on the entityClass).
	 * 
	 * @param entityClass
	 *            The parent (owning) class which must be already known to the JPA persistence unit. This one's metadata
	 *            will be modified within the internals of the JPA metadata.
	 * @param extensionPropertyName
	 *            The property on the parent class which will hold the extensionEntity. This property must be of the
	 *            type of the extension entity or a superclass. (Object will work.)
	 * @param extensionEntity
	 *            The child/extension class which needs to be linked. It must also already be known to JPA.
     * @return A 1:1 relationship between the parent entry and the extension entity via the given property name.
	 */
	DataObjectRelationship addExtensionRelationship(Class<?> entityClass, String extensionPropertyName, Class<?> extensionEntity);

}
