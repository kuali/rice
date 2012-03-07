/**
 * Copyright 2005-2012 The Kuali Foundation
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

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.permission.GenericPermissionBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.permission.PermissionTemplateBo;
import org.kuali.rice.kim.impl.responsibility.ReviewResponsibilityBo;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.HashMap;

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
				LOG.info( "Attempting to save Permission BO via PermissionService: " + getBusinessObject() );
			}
            GenericPermissionBo genericPermissionBo = (GenericPermissionBo)getBusinessObject();
            if (genericPermissionBo.getTemplateId() != null && genericPermissionBo.getTemplate() == null) {
                genericPermissionBo.setTemplate(
                        PermissionTemplateBo.from(
                                KimApiServiceLocator.getPermissionService().getPermissionTemplate(genericPermissionBo.getTemplateId())));
            }
			PermissionBo perm = GenericPermissionBo.toPermissionBo(genericPermissionBo);
			
			KimApiServiceLocator.getPermissionService().createPermission(PermissionBo.to(perm));
		} catch ( RuntimeException ex ) {
			LOG.error( "Exception in saveBusinessObject()", ex );
			throw ex;
		}
	}

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#saveDataObject
     */
    @Override
    public void saveDataObject() {
        if (getDataObject() instanceof PersistableBusinessObject) {
            GenericPermissionBo genericPermissionBo = (GenericPermissionBo)getDataObject();
            boolean permissionExists = false;
            if (genericPermissionBo.getId() != null) {
                permissionExists = KimApiServiceLocator.getPermissionService().getPermission(genericPermissionBo.getId()) != null;
            }

            if (genericPermissionBo.getTemplateId() != null) {
                genericPermissionBo.setTemplate(
                        PermissionTemplateBo.from(
                                KimApiServiceLocator.getPermissionService().getPermissionTemplate(genericPermissionBo.getTemplateId())));
            }
            PermissionBo perm = GenericPermissionBo.toPermissionBo(genericPermissionBo);
            if (permissionExists) {
                KimApiServiceLocator.getPermissionService().updatePermission(PermissionBo.to(perm));
            } else {
                //if its a copy the objectId should be empty and versionNumber should be null
                if(getMaintenanceAction().equals(KRADConstants.MAINTENANCE_COPY_ACTION)){
                    if(org.apache.commons.lang.StringUtils.isNotBlank(perm.getObjectId())){
                        perm.setObjectId("");
                    }
                    if(null!= perm.getVersionNumber()){
                        perm.setVersionNumber(null);
                    }
                }
                KimApiServiceLocator.getPermissionService().createPermission(PermissionBo.to(perm));
            }
            //getBusinessObjectService().linkAndSave((PersistableBusinessObject) dataObject);
        } else {
            throw new RuntimeException(
                    "Cannot save object of type: " + getDataObjectClass() + " with permission service");
        }
    }
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getBoClass()
	 */
	@Override
	public Class<? extends PersistableBusinessObject> getBoClass() {
		return GenericPermissionBo.class;
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
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
	 */
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		try {
			if ( businessObject == null ) {
				throw new RuntimeException( "Configuration ERROR: GenericPermissionMaintainable.prepareBusinessObject passed a null object." );
			}
			if ( businessObject instanceof PermissionBo ) {
				PermissionBo perm = getBusinessObjectService().findBySinglePrimaryKey(PermissionBo.class, ((PermissionBo)businessObject).getId() );
				businessObject = new GenericPermissionBo(perm);
			} else if ( businessObject instanceof GenericPermissionBo ) {
				// lookup the PermissionBo and convert to a GenericPermissionBo
				PermissionBo perm = getBusinessObjectService().findBySinglePrimaryKey(PermissionBo.class, ((GenericPermissionBo)businessObject).getId() );
				((GenericPermissionBo)businessObject).loadFromPermission(perm);
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
