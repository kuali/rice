/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.lookup.valuefinder;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.lookup.keyvalues.KeyValuesBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionTemplateValuesFinder extends KeyValuesBase {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.lookup.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	@Override
	public List<KeyValue> getKeyValues() {
		List<KimPermissionTemplateInfo> templates = KimApiServiceLocator.getPermissionService().getAllTemplates();
		List<KeyValue> result = new ArrayList<KeyValue>( templates.size() ); 
		for ( KimPermissionTemplateInfo template : templates ) {
			result.add( new ConcreteKeyValue( template.getPermissionTemplateId(), template.getNamespaceCode() + " : " + template.getName() ) );
		}
		return result;
	}

}
