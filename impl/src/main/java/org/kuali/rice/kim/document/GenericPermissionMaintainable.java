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
package org.kuali.rice.kim.document;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.impl.GenericPermission;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GenericPermissionMaintainable extends KualiMaintainableImpl {

	private static final Logger LOG = Logger.getLogger( GenericPermissionMaintainable.class );	
	private static final long serialVersionUID = -8102504656976243468L;

	/**
	 * Saves the responsibility via the responsibility update service
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
		try {
			if ( LOG.isInfoEnabled() ) {
				LOG.info( "Attempting to save GenericPermission BO via PermissionUpdateService: " + getBusinessObject() );
			}
			GenericPermission perm = (GenericPermission)getBusinessObject();
			
			KIMServiceLocatorInternal.getPermissionUpdateService().savePermission( perm.getPermissionId(),
					perm.getTemplateId(),
					perm.getNamespaceCode(), 
					perm.getName(), 
					perm.getDescription(), 
					perm.isActive(), 
					perm.getDetails() );
		} catch ( RuntimeException ex ) {
			LOG.error( "Exception in saveBusinessObject()", ex );
			throw ex;
		}
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getBoClass()
	 */
	@Override
	public Class<? extends PersistableBusinessObject> getBoClass() {
		return GenericPermission.class;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isExternalBusinessObject()
	 */
	@Override
	public boolean isExternalBusinessObject() {
		return true;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareBusinessObject(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		try {
			if ( businessObject == null ) {
				throw new RuntimeException( "Configuration ERROR: GenericPermissionMaintainable.prepareBusinessObject passed a null object." );
			}
			if ( businessObject instanceof PermissionImpl ) {
				KimPermissionImpl perm = getBusinessObjectService().findBySinglePrimaryKey(KimPermissionImpl.class, ((PermissionImpl)businessObject).getPermissionId() );
				businessObject = new GenericPermission( perm );
			} else if ( businessObject instanceof GenericPermission ) {
				// lookup the KimResponsibilityImpl and convert to a ReviewResponsibility
				KimPermissionImpl perm = getBusinessObjectService().findBySinglePrimaryKey(KimPermissionImpl.class, ((GenericPermission)businessObject).getPermissionId() );		
				((GenericPermission)businessObject).loadFromKimPermission(perm);
			} else {
				throw new RuntimeException( "Configuration ERROR: GenericPermissionMaintainable passed an unsupported object type: " + businessObject.getClass() );
			}
			if ( businessObject instanceof PersistableBusinessObject ) {
				setBusinessObject( (PersistableBusinessObject)businessObject );
			}
			super.prepareBusinessObject(businessObject);
		} catch ( RuntimeException ex ) {
			LOG.error( "Exception in prepareBusinessObject()", ex );
			throw ex;
		}
	}
	
}
