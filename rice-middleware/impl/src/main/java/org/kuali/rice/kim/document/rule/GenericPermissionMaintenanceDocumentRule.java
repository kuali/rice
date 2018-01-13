/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kim.document.rule;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.framework.permission.PermissionTypeService;
import org.kuali.rice.kim.impl.permission.GenericPermissionBo;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GenericPermissionMaintenanceDocumentRule extends MaintenanceDocumentRuleBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericPermissionMaintenanceDocumentRule.class);    
    
	protected static final String DETAIL_VALUES_PROPERTY = "detailValues";
    protected static final String NAMESPACE_CODE_PROPERTY = "namespaceCode";
	protected static final String ERROR_MESSAGE_PREFIX = "error.document.kim.genericpermission.";
	protected static final String ERROR_MISSING_TEMPLATE = ERROR_MESSAGE_PREFIX + "missingtemplate";
	protected static final String ERROR_UNKNOWN_ATTRIBUTE = ERROR_MESSAGE_PREFIX + "unknownattribute";
	protected static final String ERROR_ATTRIBUTE_VALIDATION = ERROR_MESSAGE_PREFIX + "attributevalidation";
    protected static final String ERROR_NAMESPACE_AND_NAME_VALIDATION = ERROR_MESSAGE_PREFIX + "namespaceandnamevalidation";

	
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean rulesPassed = super.processCustomRouteDocumentBusinessRules( document );
		try {
			GenericPermissionBo newPerm = (GenericPermissionBo)getNewBo();
            GenericPermissionBo oldPerm = (GenericPermissionBo)getOldBo();
			rulesPassed &= validateDetailValuesFormat(newPerm.getDetailValues());
            if(StringUtils.isNotBlank(newPerm.getNamespaceCode()) && StringUtils.isNotBlank(newPerm.getName()) && StringUtils.isBlank(newPerm.getId())){
                rulesPassed &= validateNamespaceCodeAndName(newPerm.getNamespaceCode(), newPerm.getName());
            }
            // rule case for copy
            if(StringUtils.isNotBlank(newPerm.getNamespaceCode()) &&
               StringUtils.isNotBlank(newPerm.getName()) &&
               StringUtils.isNotBlank(oldPerm.getId()) &&
               StringUtils.isNotBlank(newPerm.getId()) &&
               !StringUtils.equals(oldPerm.getId(), newPerm.getId())){
                  rulesPassed &= validateNamespaceCodeAndName(newPerm.getNamespaceCode(), newPerm.getName());
            }
            // rule case for edit
            if(StringUtils.isNotBlank(newPerm.getNamespaceCode()) &&
                    StringUtils.isNotBlank(newPerm.getName()) &&
                    StringUtils.isNotBlank(oldPerm.getId()) &&
                    StringUtils.isNotBlank(newPerm.getId()) &&
                    StringUtils.equals(oldPerm.getId(), newPerm.getId()) &&
                    ( !StringUtils.equals(oldPerm.getNamespaceCode(), newPerm.getNamespaceCode()) ||
                      !StringUtils.equals(oldPerm.getName(), newPerm.getName())
                    )
               ) {
                rulesPassed &= validateNamespaceCodeAndName(newPerm.getNamespaceCode(), newPerm.getName());
            }
			// detailValues
			// get the type from the template for validation
            if(StringUtils.isNotBlank(newPerm.getTemplateId())){
                Template template = KimApiServiceLocator.getPermissionService().getPermissionTemplate(newPerm.getTemplateId());
                if ( template == null ) {
                    GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
                    GlobalVariables.getMessageMap().putError( DETAIL_VALUES_PROPERTY, ERROR_MISSING_TEMPLATE, newPerm.getTemplateId() );
                    GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
                    rulesPassed &= false;
                } else {
                    KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(template.getKimTypeId());
                    Map<String, String> details = newPerm.getDetails();
                    // check that add passed attributes are defined
                    for ( String attributeName : details.keySet() ) {
                        if ( kimType.getAttributeDefinitionByName(attributeName) == null ) {
                            GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
                            GlobalVariables.getMessageMap().putError( DETAIL_VALUES_PROPERTY, ERROR_UNKNOWN_ATTRIBUTE, attributeName, template.getNamespaceCode(), template.getName() );
                            GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
                            rulesPassed &= false;
                        }
                    }
                    // if all attributes are known, pass to the service for validation
                    if ( !GlobalVariables.getMessageMap().hasErrors() ) {
                        PermissionTypeService service = getPermissionTypeService( kimType.getServiceName() );
                        if ( service != null ) {
                            List<RemotableAttributeError> validationErrors = service.validateAttributes( kimType.getId(), details);
                            if ( validationErrors != null && !validationErrors.isEmpty() ) {
                                for ( RemotableAttributeError error : validationErrors ) {
                                    GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
                                    for (String errMsg : error.getErrors()) {
                                        GlobalVariables.getMessageMap().putError( DETAIL_VALUES_PROPERTY, ERROR_ATTRIBUTE_VALIDATION, error.getAttributeName(), errMsg );
                                    }
                                    GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
                                }
                                rulesPassed &= false;
                            }
                        }
                    }

                }

            }
			// check each permission name against the type
		} catch ( RuntimeException ex ) {
			LOG.error( "Error in processCustomRouteDocumentBusinessRules()", ex );
			throw ex;
		}
		return rulesPassed;
	}

	protected boolean validateDetailValuesFormat(String permissionDetailValues){
		if(permissionDetailValues != null){
			Pattern pattern = Pattern.compile(".+"+"="+".+");
			// ensure that all line delimiters are single linefeeds
			permissionDetailValues = permissionDetailValues.replace( "\r\n", "\n" );
			permissionDetailValues = permissionDetailValues.replace( '\r', '\n' );
			if(StringUtils.isNotBlank(permissionDetailValues)){
				String[] values = permissionDetailValues.split( "\n" );
				for(String attrib : values){
                    Matcher matcher = pattern.matcher(attrib);
                    if (!matcher.matches()) {
                        GlobalVariables.getMessageMap().putError(
                                MAINTAINABLE_ERROR_PATH + "." + DETAIL_VALUES_PROPERTY,
                                RiceKeyConstants.ERROR_INVALID_FORMAT,
                                new String[]{"Detail Values", permissionDetailValues});
                        return false;
                    }
				}
			}
		}
		return true;
	}
    protected boolean validateNamespaceCodeAndName(String namespaceCode,String name){
        Permission permission = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(namespaceCode,name);
        if(null != permission){
            GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+NAMESPACE_CODE_PROPERTY,ERROR_NAMESPACE_AND_NAME_VALIDATION,namespaceCode,name);
            return false;
        } else{
            return true;
        }
    }
	
	protected PermissionTypeService getPermissionTypeService( String serviceName ) {
    	if ( StringUtils.isBlank( serviceName ) ) {
    		return null;
    	}
    	try {
	    	Object service = GlobalResourceLoader.getService(QName.valueOf(serviceName));
	    	// if we have a service name, it must exist
	    	if ( service == null ) {
				LOG.warn("null returned for permission type service for service name: " + serviceName);
	    	} else {
		    	// whatever we retrieved must be of the correct type
		    	if ( !(service instanceof PermissionTypeService)  ) {
		    		LOG.warn( "Service " + serviceName + " was not a KimPermissionTypeService.  Was: " + service.getClass().getName() );
		    		service = null;
		    	}
	    	}
	    	return (PermissionTypeService)service;
    	} catch( Exception ex ) {
    		LOG.error( "Error retrieving service: " + serviceName + " from the KimImplServiceLocator.", ex );
    	}
    	return null;
    }

}
