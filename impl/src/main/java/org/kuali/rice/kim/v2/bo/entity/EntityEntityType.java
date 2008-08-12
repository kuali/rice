/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.v2.bo.entity;

//import java.util.List;

import org.kuali.rice.kim.v2.bo.reference.EntityType;

/**
 * Represents the entity type associated with a particular entity. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface EntityEntityType {

	String getEntityId();
	String getEntityTypeCode();
	
	EntityType getEntityType();
	
//	List<EntityAddress> getEntityAddresses();
//	List<EntityName> getEntityNames();
//	List<EntityEmail> getEntityEmailAddresses();
//	List<EntityPhone> getEntityPhoneNumbers();
}
