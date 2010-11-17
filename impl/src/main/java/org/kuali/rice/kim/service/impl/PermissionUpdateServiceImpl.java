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

import java.util.Iterator;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.PermissionAttributeDataImpl;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionUpdateService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;

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
	 * @see org.kuali.rice.kim.service.PermissionUpdateService#savePermission(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, org.kuali.rice.core.xml.dto.AttributeSet)
	 */
	public void savePermission(String permissionId, String permissionTemplateId,
			String namespaceCode, String name, String description, boolean active,
			AttributeSet permissionDetails) {
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
	    	AttributeSet attributesToAdd = new AttributeSet( permissionDetails );
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
	    		KimTypeAttributeInfo attr = perm.getTemplate().getKimType().getAttributeDefinitionByName(attrName);
	    		if ( attr != null ) {
		    		PermissionAttributeDataImpl newDetail = new PermissionAttributeDataImpl();
		    		newDetail.setAttributeDataId(getNewAttributeDataId());
		    		newDetail.setKimAttributeId(attr.getKimAttributeId());
		    		newDetail.setKimTypeId(perm.getTemplate().getKimTypeId());
		    		newDetail.setPermissionId(permissionId);
		    		newDetail.setAttributeValue(attributesToAdd.get(attrName));
		    		details.add(newDetail);
	    		} else {
	    			LOG.error( "Unknown attribute name saving permission: '" + attrName + "'" );
	    		}
	    	}
	    	getBusinessObjectService().save(perm);
	    	KIMServiceLocator.getIdentityManagementService().flushPermissionCaches();
	    	flushPermissionImplCache();
    	} catch ( RuntimeException ex ) {
    		LOG.error( "Exception in savePermission: ", ex );
    		throw ex;
    	}
	}
	
	public String getNextAvailablePermissionId() throws UnsupportedOperationException {
        Long nextSeq = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_PERM_ID_S, KimPermissionImpl.class);

        if (nextSeq == null) {
            LOG.error("Unable to get new permission id from sequence " + KimConstants.SequenceNames.KRIM_PERM_ID_S);
            throw new RuntimeException("Unable to get new permission id from sequence " + KimConstants.SequenceNames.KRIM_PERM_ID_S);
        }

        return nextSeq.toString();
    }

}
