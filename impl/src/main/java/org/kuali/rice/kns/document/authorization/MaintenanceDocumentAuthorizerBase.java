/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.FieldAttributeSecurity;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.DocumentAttributeSecurityUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.mask.MaskFormatter;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase implements MaintenanceDocumentAuthorizer {

 	private static MaintenanceDocumentDictionaryService  maintenanceDocumentDictionaryService;
 	private static PersistenceStructureService persistenceStructureService;
    /**
     * @see org.kuali.rice.kns.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.rice.kns.document.MaintenanceDocument,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, Person user) {
        
    	MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
    	String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
    	
    	MaintenanceDocumentEntry objectEntry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(documentType);
    	Map<String, FieldAttributeSecurity> restrictionFields = DocumentAttributeSecurityUtils.getRestrictionMaintainableFields(objectEntry);
    	
    	Set keys = restrictionFields.keySet();    
    	Iterator keyIter = keys.iterator();
        while (keyIter.hasNext()) { 
           String fullFieldName = (String) keyIter.next(); 
           FieldAttributeSecurity fieldAttributeSecurity = (FieldAttributeSecurity) restrictionFields.get(fullFieldName);
           String fieldName = fieldAttributeSecurity.getAttributeName();
           
           //TODO:Should use ParameterService.getDetailType to get the componentName
           String componentName = fieldAttributeSecurity.getBusinessObjectClass().getSimpleName();     
           String nameSpaceCode = KNSConstants.KNS_NAMESPACE;
           
           AttributeSecurity maintainableFieldAttributeSecurity = (AttributeSecurity) fieldAttributeSecurity.getMaintainableFieldAttributeSecurity();
           AttributeSecurity  businessObjectAttributeSecurity = (AttributeSecurity) fieldAttributeSecurity.getBusinessObjectAttributeSecurity();
           
           AttributeSet permissionDetails = new AttributeSet();
    	   permissionDetails.put(KimAttributes.PROPERTY_NAME, fieldName);
    	   
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isReadOnly()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_EDIT_PROPERTY, permissionDetails, null)){
    			   auths.addReadonlyAuthField(fullFieldName);
    		   }
    	   }
    	   
    	   if(maintainableFieldAttributeSecurity != null && maintainableFieldAttributeSecurity.isReadOnly()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_EDIT_PROPERTY, permissionDetails, null)){
    			   auths.addReadonlyAuthField(fullFieldName);
    		   }
    	   }
    	   
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isPartialMask()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_PARTIALLY_UNMASK_PROPERTY, permissionDetails, null)){
    			   MaskFormatter partialMaskFormatter = businessObjectAttributeSecurity.getPartialMaskFormatter();
    			   auths.addPartiallyMaskedAuthField(fullFieldName, partialMaskFormatter);
    		   }
    	   }
    	   
    	   if(maintainableFieldAttributeSecurity != null  && maintainableFieldAttributeSecurity.isPartialMask()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
			   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_PARTIALLY_UNMASK_PROPERTY, permissionDetails, null)){
				   MaskFormatter partialMaskFormatter = maintainableFieldAttributeSecurity.getPartialMaskFormatter();
				   auths.addPartiallyMaskedAuthField(fullFieldName, partialMaskFormatter);
			   }
		   }
    	   
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isMask()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_UNMASK_PROPERTY, permissionDetails, null)){
    		       MaskFormatter maskFormatter = businessObjectAttributeSecurity.getMaskFormatter();
    			   auths.addMaskedAuthField(fullFieldName, maskFormatter);
    		   }
    	   }
    	   
    	   if(maintainableFieldAttributeSecurity != null  && maintainableFieldAttributeSecurity.isMask()){  
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
			   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_UNMASK_PROPERTY, permissionDetails, null)){
				   MaskFormatter maskFormatter = maintainableFieldAttributeSecurity.getMaskFormatter();
				   auths.addMaskedAuthField(fullFieldName, maskFormatter);
			   }
		   }
    	
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isHide()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_VIEW_PROPERTY, permissionDetails, null)){
    			   auths.addHiddenAuthField(fullFieldName);	  
    		   }
    	   }   

    	   if(maintainableFieldAttributeSecurity != null  && maintainableFieldAttributeSecurity.isHide()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
			   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_VIEW_PROPERTY, permissionDetails, null)){
				   auths.addHiddenAuthField(fullFieldName);	  
			   }
		   }
  
        }    	
    	return auths; 
    }


    /**
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    public Set getDocumentActions(Document document, Person user, Set<String> documentActions) {

        Set docActions = super.getDocumentActions(document, user, documentActions);
        MaintenanceDocument maintDoc = (MaintenanceDocument) document;
        MaintenanceDocumentAuthorizations docAuths = getFieldAuthorizations(maintDoc, user);
        if (docActions.contains(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE)&& docAuths.hasAnyFieldRestrictions()) {
            docActions.remove(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }
        
        return docActions;
    }


    
    /**
     * This method returns whether this document is creating a new entry in the maintenible/underlying table
     * 
     * This method is useful to determine whether all the field-based edit modes should be enabled, which is 
     * useful in determining which fields are encrypted
     * 
     * This method considers that Constants.MAINTENANCE_NEWWITHEXISTING_ACTION is not a new document because 
     * there is uncertainity how documents with this action will actually be implemented
     * 
     * @param maintDoc
     * @param user
     * @return
     */
    protected boolean isDocumentForCreatingNewEntry(MaintenanceDocument maintDoc) {
        // the rule is as follows: if the maint doc represents a new record AND the user is the same user who initiated the maintenance doc
        // if the user check is not added, then it would be pointless to do any encryption since I can just pull up a document to view the encrypted values
        
        // A maint doc is new when the new maintainable maintenance flag is set to either Constants.MAINTENANCE_NEW_ACTION or Constants.MAINTENANCE_COPY_ACTION
        String maintAction = maintDoc.getNewMaintainableObject().getMaintenanceAction();
        return (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintAction) || KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintAction));
    }
    
    	
	/**
	 * @return the maintenanceDocumentDictionaryService
	 */
	public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		
		if (maintenanceDocumentDictionaryService == null ) {
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}

	protected void addPrimaryKeysToMap( BusinessObject bo, Map<String,String> attributes ) {
	    if ( bo == null ) {
	        return;
	    }
	    List<String> pkFields = getPersistenceStructureService().getPrimaryKeys(bo.getClass());
	    for ( String field : pkFields ) {
	        try {
	            Object fieldValue = ObjectUtils.getPropertyValue(bo, field);
	            attributes.put(field, (fieldValue==null)?"":fieldValue.toString() );
	        } catch ( RuntimeException ex ) {
	            // do nothing - just skip the attribute - ObjectUtils has already logged the error
	        }
	    }
	}

	/**
	 * Return the class of the maintained business object.
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getComponentClass(org.kuali.rice.kns.document.Document)
	 */
	@Override
	protected Class getComponentClass(Document document) {
        MaintenanceDocument md = (MaintenanceDocument)document;
        return md.getNewMaintainableObject().getBoClass();        
	}


    public static PersistenceStructureService getPersistenceStructureService() {
        if ( persistenceStructureService == null ) {
            persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
        }
        return persistenceStructureService;
    }
    
    
}

