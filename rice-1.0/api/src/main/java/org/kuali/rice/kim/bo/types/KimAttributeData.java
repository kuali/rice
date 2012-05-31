/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.types;

/**
 * This is the generic template for a holder for the
 * actual value of a property attached to a KIM object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimAttributeData {

	/*
	 * Generic getter for the data ID - this method could be implemented differently in
	 * subclasses specific to their types.
	 */
	String getAttributeDataId();

	/** The PK of the object that this data belongs to.  If this is a attached to a group
	 * then this value is the group ID.
	 */
	//String getTargetPrimaryKey();

	String getKimTypeId();
	String getKimAttributeId();

//	KimAttribute getKimAttribute();
//	KimType getKimType();

	String getAttributeValue();
}
