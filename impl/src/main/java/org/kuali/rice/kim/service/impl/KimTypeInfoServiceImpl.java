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
package org.kuali.rice.kim.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeInfoServiceImpl implements KimTypeInfoService {

	private BusinessObjectService businessObjectService;
	
	protected Map<String,KimTypeInfo> infoCache = new HashMap<String, KimTypeInfo>();
	protected Map<String,ExternalIdentifierTypeInfo> extIdTypeCache = new HashMap<String, ExternalIdentifierTypeInfo>();
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.KimTypeInfoService#getKimType(java.lang.String)
	 */
	public KimTypeInfo getKimType(String kimTypeId) {
		if ( !infoCache.containsKey(kimTypeId) ) {
			Map<String,String> pk = new HashMap<String, String>(1);
			pk.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
			KimTypeImpl impl = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pk);
			if ( impl != null ) {
				infoCache.put(kimTypeId, impl.toInfo());
			}
		}
		return infoCache.get(kimTypeId);
	}

	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
	public ExternalIdentifierTypeInfo getExternalIdentifierType( String externalIdentifierTypeCode ) {
		if ( !extIdTypeCache.containsKey(externalIdentifierTypeCode) ) {
			Map<String,String> pk = new HashMap<String, String>(1);
			pk.put("code", externalIdentifierTypeCode);
			ExternalIdentifierTypeImpl impl = (ExternalIdentifierTypeImpl)getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, pk);
			if ( impl != null ) {
				extIdTypeCache.put(externalIdentifierTypeCode, impl.toInfo());
			}
		}
		return extIdTypeCache.get(externalIdentifierTypeCode);
	}
}
