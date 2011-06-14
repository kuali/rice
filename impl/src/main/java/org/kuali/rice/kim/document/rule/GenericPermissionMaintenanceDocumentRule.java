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
package org.kuali.rice.kim.document.rule;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.impl.GenericPermission;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GenericPermissionMaintenanceDocumentRule extends
		MaintenanceDocumentRuleBase {
	protected static final String DETAIL_VALUES_PROPERTY = "detailValues";
	protected static final String ERROR_MESSAGE_PREFIX = "error.document.kim.genericpermission.";
	protected static final String ERROR_MISSING_TEMPLATE = ERROR_MESSAGE_PREFIX + "missingtemplate";
	protected static final String ERROR_UNKNOWN_ATTRIBUTE = ERROR_MESSAGE_PREFIX + "unknownattribute";
	protected static final String ERROR_ATTRIBUTE_VALIDATION = ERROR_MESSAGE_PREFIX + "attributevalidation";
	
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean rulesPassed = true;
		try {
			GenericPermission perm = (GenericPermission)getNewBo();
			validateDetailValuesFormat(perm.getDetailValues());
			// detailValues
			// get the type from the template for validation
			KimPermissionTemplateInfo template = KimApiServiceLocator.getPermissionService().getPermissionTemplate( perm.getTemplateId() );
			if ( template == null ) {
				GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
				GlobalVariables.getMessageMap().putError( DETAIL_VALUES_PROPERTY, ERROR_MISSING_TEMPLATE, perm.getTemplateId() );
				GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
				rulesPassed = false;
			} else {
				KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(template.getKimTypeId());
				AttributeSet details = perm.getDetails();
				// check that add passed attributes are defined
				for ( String attributeName : details.keySet() ) {
					if ( kimType.getAttributeDefinitionByName(attributeName) == null ) {
						GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
						GlobalVariables.getMessageMap().putError( DETAIL_VALUES_PROPERTY, ERROR_UNKNOWN_ATTRIBUTE, attributeName, template.getNamespaceCode(), template.getName() );
						GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
						rulesPassed = false;
					}
				}
				// if all attributes are known, pass to the service for validation
				if ( !GlobalVariables.getMessageMap().hasErrors() ) {
					KimPermissionTypeService service = getPermissionTypeService( kimType.getServiceName() );
					if ( service != null ) {
						AttributeSet validationErrors = service.validateAttributes( kimType.getId(), details);
						if ( validationErrors != null && !validationErrors.isEmpty() ) {
							for ( String attributeName : validationErrors.keySet() ) {
								GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
								GlobalVariables.getMessageMap().putError( DETAIL_VALUES_PROPERTY, ERROR_ATTRIBUTE_VALIDATION, attributeName, validationErrors.get(attributeName) );
								GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
							}
							rulesPassed = false;
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
			String spacesPattern = "[\\s\\t]*";
			Pattern pattern = Pattern.compile(".+"+"="+".+");
			Matcher matcher;
			// ensure that all line delimiters are single linefeeds
			permissionDetailValues = permissionDetailValues.replace( "\r\n", "\n" );
			permissionDetailValues = permissionDetailValues.replace( '\r', '\n' );
			if(StringUtils.isNotBlank(permissionDetailValues)){
				String[] values = permissionDetailValues.split( "\n" );
				for(String attrib: values){
				      matcher = pattern.matcher(attrib);
				      if(!matcher.matches()){
				    	  GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+DETAIL_VALUES_PROPERTY, RiceKeyConstants.ERROR_INVALID_FORMAT, new String[]{"Detail Values", permissionDetailValues});
				    	  return false;
				      }
				}
			}
		}
		return true;
	}
	
	protected KimPermissionTypeService getPermissionTypeService( String serviceName ) {
    	if ( StringUtils.isBlank( serviceName ) ) {
    		return null;
    	}
    	try {
	    	Object service = KIMServiceLocatorInternal.getService(serviceName);
	    	// if we have a service name, it must exist
	    	if ( service == null ) {
				LOG.warn("null returned for permission type service for service name: " + serviceName);
	    	} else {
		    	// whatever we retrieved must be of the correct type
		    	if ( !(service instanceof KimPermissionTypeService)  ) {
		    		LOG.warn( "Service " + serviceName + " was not a KimPermissionTypeService.  Was: " + service.getClass().getName() );
		    		service = null;
		    	}
	    	}
	    	return (KimPermissionTypeService)service;
    	} catch( Exception ex ) {
    		LOG.error( "Error retrieving service: " + serviceName + " from the KIMServiceLocatorInternal.", ex );
    	}
    	return null;
    }

}
