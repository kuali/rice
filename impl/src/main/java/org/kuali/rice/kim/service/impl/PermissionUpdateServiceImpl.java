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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.PermissionAttributeDataImpl;
import org.kuali.rice.kim.service.PermissionUpdateService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.KRADServiceLocator;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(endpointInterface = KIMWebServiceConstants.PermissionUpdateService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.PermissionUpdateService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.PermissionUpdateService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class PermissionUpdateServiceImpl extends PermissionServiceBase implements PermissionUpdateService {
	private static final Logger LOG = Logger.getLogger( PermissionUpdateServiceImpl.class );
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.PermissionUpdateService#savePermission(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, org.kuali.rice.core.util.Map<String, String>)
	 */
	public void savePermission(String permissionId, String permissionTemplateId,
			String namespaceCode, String name, String description, boolean active,
			Map<String, String> permissionDetails) {
    	// look for an existing permission of the given type
    	try {
	    	KimPermissionImpl perm = getBusinessObjectService().findBySinglePrimaryKey(KimPermissionImpl.class, permissionId);
	    	if ( perm == null ) {
	    		perm = new KimPermissionImpl();
	    		perm.setPermissionId(permissionId);
	    	}
	    	perm.setTemplateId(permissionTemplateId);
	    	perm.refreshReferenceObject( "template" );
	    	perm.setNamespaceCode(namespaceCode);
	    	perm.setName(name);
	    	perm.setDescription(description);
	    	perm.setActive(active);
	    	Map<String, String> attributesToAdd = new HashMap<String, String>( permissionDetails );
	    	List<PermissionAttributeDataImpl> details = perm.getDetailObjects();
	    	Iterator<PermissionAttributeDataImpl> detailIter = details.iterator();
	    	while ( detailIter.hasNext() ) {
	    		PermissionAttributeDataImpl detail = detailIter.next();
	    		String attrName = detail.getKimAttribute().getAttributeName();
	    		String attrValue = attributesToAdd.get(attrName);
	    		// if not present in the list or is blank, remove from the list
	    		if ( StringUtils.isBlank(attrValue) ) {
	    			detailIter.remove();
	    		} else {
	    			detail.setAttributeValue(attrValue);
	    		}
	    		// remove from detail map - used to add new ones later
	    		attributesToAdd.remove(attrName);
	    	}
	    	for ( String attrName : attributesToAdd.keySet() ) {
                KimType type = KimApiServiceLocator.getKimTypeInfoService().getKimType(perm.getTemplate().getKimTypeId());
	    		KimTypeAttribute attr = type.getAttributeDefinitionByName(attrName);
	    		if ( attr != null ) {
		    		PermissionAttributeDataImpl newDetail = new PermissionAttributeDataImpl();
		    		newDetail.setId(getNewAttributeDataId());
		    		newDetail.setKimAttributeId(attr.getId());
		    		newDetail.setKimTypeId(perm.getTemplate().getKimTypeId());
		    		newDetail.setAssignedToId(permissionId);
		    		newDetail.setAttributeValue(attributesToAdd.get(attrName));
		    		details.add(newDetail);
	    		} else {
	    			LOG.error( "Unknown attribute name saving permission: '" + attrName + "'" );
	    		}
	    	}
	    	getBusinessObjectService().save(perm);
	    	//KimApiServiceLocator.getIdentityManagementService().flushPermissionCaches();
	    	flushPermissionImplCache();
    	} catch ( RuntimeException ex ) {
    		LOG.error( "Exception in savePermission: ", ex );
    		throw ex;
    	}
	}
	
	public String getNextAvailablePermissionId() throws UnsupportedOperationException {
        Long nextSeq = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_PERM_ID_S, KimPermissionImpl.class);

        if (nextSeq == null) {
            LOG.error("Unable to get new permission id from sequence " + KimConstants.SequenceNames.KRIM_PERM_ID_S);
            throw new RuntimeException("Unable to get new permission id from sequence " + KimConstants.SequenceNames.KRIM_PERM_ID_S);
        }

        return nextSeq.toString();
    }

}
