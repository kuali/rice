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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimTypeInfoServiceImpl implements KimTypeInfoService {

	private BusinessObjectService businessObjectService;
	
	protected Map<String,KimTypeInfo> infoCache = Collections.synchronizedMap( new HashMap<String, KimTypeInfo>() );
	protected Map<String,KimTypeInfo> infoCacheByName = Collections.synchronizedMap( new HashMap<String, KimTypeInfo>() );
	protected boolean allLoaded = false;
	
	@SuppressWarnings("unchecked")
	public Collection<KimTypeInfo> getAllTypes() {
		if ( !allLoaded ) {
			Collection<KimTypeImpl> types = getBusinessObjectService().findAll(KimTypeImpl.class);
			synchronized ( this ) {
				for ( KimTypeImpl typ : types ) {
					if ( typ.isActive() ) {
						KimTypeInfo info = typ.toInfo();
						infoCache.put(typ.getKimTypeId(), info);
						infoCacheByName.put(typ.getNamespaceCode()+typ.getName(), info);
					}
				}
			}
			allLoaded = true;
		}
		return new ArrayList<KimTypeInfo>( infoCache.values() );
	}
	
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

	public KimTypeInfo getKimTypeByName( String namespaceCode, String typeName ) {
		if ( !infoCacheByName.containsKey(namespaceCode+typeName) ) {
			Map<String,String> pk = new HashMap<String, String>(2);
			pk.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
			pk.put("name", typeName);
			KimTypeImpl impl = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pk);
			if ( impl != null ) {
				infoCacheByName.put(namespaceCode+typeName, impl.toInfo());
			}
		}
		return infoCacheByName.get(namespaceCode+typeName);
	}
	
	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
}
