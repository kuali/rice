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

import java.io.Serializable;

/**
* Parent and child attribute relationship.
*
* <p>
* Represents a relationship between a parent and child attribute. Used in the context of a {@link MetadataChild}
* object.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public interface DataObjectAttributeRelationship extends Serializable {

    /**
    * Gets attribute name of parent.
    *
    * <p>
    * The property on the "parent" data object.
    * </p>
    *
    * @return attribute name of parent
    */
    String getParentAttributeName();

    /**
    * Gets matching child property.
    *
    * <p>
    * The matching property on the "child", usually part of the child data object's primary key.
    * </p>
    *
    * @return matching child property
    */
    String getChildAttributeName();
}