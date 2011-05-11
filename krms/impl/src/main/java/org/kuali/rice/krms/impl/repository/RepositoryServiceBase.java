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

package org.kuali.rice.krms.impl.repository;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class RepositoryServiceBase {

    protected BusinessObjectService businessObjectService;

    private final static Map<String,String> krmsAttributeDefinitionIdCache = Collections.synchronizedMap( new HashMap<String,String>() );

	protected Map<String,String> convertAttributeKeys(Map<String,String> attributesByName, String namespace) {
		Map<String,String> attributesById = new HashMap<String,String>();
		if(attributesByName != null && CollectionUtils.isNotEmpty(attributesByName.keySet())) { 
			for(String attributeName : attributesByName.keySet()) {
				String newKey = getKrmsAttributeId(attributeName, namespace);
				if(StringUtils.isNotEmpty(newKey)) {
					attributesById.put(newKey, attributesByName.get(attributeName));
				}
			}
		}
		return attributesById;
	}
   
	protected String getKrmsAttributeId( String attributeName, String namespace) {
		String key = namespace + ":" + attributeName;
		if (krmsAttributeDefinitionIdCache.containsKey(key))
			return krmsAttributeDefinitionIdCache.get(key);		

		String result = null;
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put( "name", attributeName );
		criteria.put( "namespace", namespace );
		Collection<KrmsAttributeDefinitionBo> defs = getBusinessObjectService().findMatching( KrmsAttributeDefinitionBo.class, criteria );
		if(CollectionUtils.isNotEmpty(defs)) {
			result = defs.iterator().next().getId();
			krmsAttributeDefinitionIdCache.put(key, result);
		}
		return result;
	}
    
    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
    
}
