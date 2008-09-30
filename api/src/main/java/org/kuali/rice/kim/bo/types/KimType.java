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
package org.kuali.rice.kim.bo.types;

import java.util.List;

import org.kuali.rice.kns.bo.Inactivateable;

/**
 * This is the master for an Kim type.  It contains a service that can provide
 * additional logic when this type is used on a business object. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimType extends Inactivateable {

	String getKimTypeId();
	
	String getName();
	
	String getKimTypeServiceName();
	
	List<KimTypeAttribute> getAttributeDefinitions();
}
