/**
 * Copyright 2005-2014 The Kuali Foundation
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.permission.GenericPermissionBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.permission.PermissionTemplateBo;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GenericPermissionMaintainable extends KualiMaintainableImpl {

	private static final Logger LOG = Logger.getLogger( GenericPermissionMaintainable.class );	
	private static final long serialVersionUID = -8102504656976243468L;

    protected static final String DETAIL_OBJECTS_ATTRIBUTE_NAME = "attributeDetails.kimAttribute.attributeName";
    protected static final String DETAIL_OBJECTS_ATTRIBUTE_VALUE = "attributeDetails.attributeValue";

    /**
     * Saves the responsibility via the responsibility update service
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveDataObject() {
        if (getDataObject() instanceof GenericPermissionBo) {
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
                KimApiServiceLocator.getPermissionService().createPermission(PermissionBo.to(perm));
            }
        } else {
            throw new RuntimeException(
                    "Cannot save object of type: " + getDataObjectClass() + " with permission service");
        }
    }

    /**
     * Pre-populates the ID field of the new PermissionBo to be created.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document,parameters);

        GenericPermissionBo permissionBo = (GenericPermissionBo) document.getNewMaintainableObject().getDataObject();
        initializePermissionId(permissionBo);
    }
	
    /**
     * Pre-populates the ID field of the new PermissionBo to be created.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document,parameters);

        GenericPermissionBo permissionBo = (GenericPermissionBo) document.getNewMaintainableObject().getDataObject();
        initializePermissionId(permissionBo);
        permissionBo.setVersionNumber(null);
    }

    private void initializePermissionId(Object dataObject) {
        if (dataObject instanceof GenericPermissionBo) {
            GenericPermissionBo permissionBo = (GenericPermissionBo) dataObject;

            if (StringUtils.isBlank(permissionBo.getId())) {
                DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(
                        KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_PERM_ID_S);
                permissionBo.setId(incrementer.nextStringValue());
            }
        }
    }

	@SuppressWarnings("rawtypes")
    @Override
	public Class getBoClass() {
		return GenericPermissionBo.class;
	}
	
	@Override
	public boolean isExternalBusinessObject() {
		return true;
	}
	
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		try {
			if ( businessObject == null ) {
				throw new RuntimeException( "Configuration ERROR: GenericPermissionMaintainable.prepareBusinessObject passed a null object." );
			}
			if ( businessObject instanceof PermissionBo ) {
			    PermissionBo perm = KradDataServiceLocator.getDataObjectService().find(PermissionBo.class, ((PermissionBo)businessObject).getId());
				businessObject = new GenericPermissionBo(perm);
			} else if ( businessObject instanceof GenericPermissionBo ) {
				// lookup the PermissionBo and convert to a GenericPermissionBo
                PermissionBo perm = KradDataServiceLocator.getDataObjectService().find(PermissionBo.class, ((GenericPermissionBo)businessObject).getId());
				((GenericPermissionBo)businessObject).loadFromPermission(perm);
			} else {
				throw new RuntimeException( "Configuration ERROR: GenericPermissionMaintainable passed an unsupported object type: " + businessObject.getClass() );
			}
			setDataObject( businessObject );
			super.prepareBusinessObject(businessObject);
		} catch ( RuntimeException ex ) {
			LOG.error( "Exception in prepareBusinessObject()", ex );
			throw ex;
		}
	}

    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        String attrName = "";
        String attrValue = "";

        GenericPermissionBo permissionBo = (GenericPermissionBo) document.getNewMaintainableObject().getDataObject();
        initializePermissionId(permissionBo);
        permissionBo.setActive(true);

        for (String paramName : parameters.keySet()) {
            String[] parameterValues = parameters.get(paramName);

            if (paramName.equals(DETAIL_OBJECTS_ATTRIBUTE_NAME)) {
                if (parameterValues.length > 0) {
                    attrName = parameterValues[0];
                }
            }

            if (paramName.equals(DETAIL_OBJECTS_ATTRIBUTE_VALUE)) {
                if (parameterValues.length > 0) {
                    attrValue = parameterValues[0];
                }
            }
        }

        if (StringUtils.isNotEmpty(attrName) && StringUtils.isNotEmpty(attrValue)) {
            Map<String, String> details = new HashMap<String, String>();
            details.put(attrName, attrValue);
            permissionBo.setDetails(details);
        }

        document.getNewMaintainableObject().setDataObject(permissionBo);
    }
}
