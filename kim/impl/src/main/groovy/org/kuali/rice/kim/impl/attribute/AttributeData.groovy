/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.attribute

import javax.persistence.Id
import javax.persistence.Column
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase


public abstract class AttributeData extends PersistableBusinessObjectBase {
    @Id
	@Column(name="ATTR_DATA_ID")
	String Id

	@Column(name="KIM_TYP_ID")
	String kimTypeId

	@Column(name="KIM_ATTR_DEFN_ID")
	String attributeId

	@Column(name="ATTR_VAL")
	String value
}
