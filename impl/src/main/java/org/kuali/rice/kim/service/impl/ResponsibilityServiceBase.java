/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.ksb.api.bus.services.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityServiceBase {
	protected static final String RESPONSIBILITY_IMPL_CACHE_PREFIX = "ResponsibilityImpl-Template-";
	protected static final String RESPONSIBILITY_IMPL_CACHE_GROUP = "ResponsibilityImpl";
	protected static final String DEFAULT_RESPONSIBILITY_TYPE_SERVICE = "defaultResponsibilityTypeService";
	protected static final Integer DEFAULT_PRIORITY_NUMBER = Integer.valueOf(1);
	
	private SequenceAccessorService sequenceAccessorService;
	private RiceCacheAdministrator cacheAdministrator;

	private BusinessObjectService businessObjectService;
	
	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
	public void flushResponsibilityImplCache() {
    	getCacheAdministrator().flushGroup(RESPONSIBILITY_IMPL_CACHE_GROUP);
    }
	
	protected String getResponsibilityImplByTemplateNameCacheKey( String namespaceCode, String responsibilityTemplateName ) {
    	return RESPONSIBILITY_IMPL_CACHE_PREFIX + namespaceCode + "-" + responsibilityTemplateName;
    }
	
	protected String getNewAttributeDataId(){
		SequenceAccessorService sas = getSequenceAccessorService();		
		Long nextSeq = sas.getNextAvailableSequenceNumber(
				KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S, 
				RoleMemberAttributeDataImpl.class );
		return nextSeq.toString();
    }
	
	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}
	
	protected RiceCacheAdministrator getCacheAdministrator() {
		if ( cacheAdministrator == null ) {
			cacheAdministrator = KsbApiServiceLocator.getCacheAdministrator();
		}
		return cacheAdministrator;
	}
	
	
}
