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

import java.util.HashSet;
import java.util.Iterator;
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

    public final void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths, MaintenanceDocument document, Person user) {
    	String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
    	
    	MaintenanceDocumentEntry objectEntry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(documentType);
    	Map<String, FieldAttributeSecurity> restrictionFields = DocumentAttributeSecurityUtils.getRestrictionMaintainableFields(objectEntry);
    	
    	Set<String> keys = restrictionFields.keySet();    
    	Iterator<String> keyIter = keys.iterator();
    	
    	AttributeSet basePermissionDetails = new AttributeSet();
    	populatePermissionDetails(document, basePermissionDetails);
    	
    	AttributeSet baseRoleQualification = new AttributeSet();
    	populateRoleQualification(document, baseRoleQualification);
    	
        while (keyIter.hasNext()) { 
           String fullFieldName = keyIter.next(); 
           FieldAttributeSecurity fieldAttributeSecurity = restrictionFields.get(fullFieldName);
           String fieldName = fieldAttributeSecurity.getAttributeName();
           
           //TODO:Should use ParameterService.getDetailType to get the componentName
           String componentName = fieldAttributeSecurity.getBusinessObjectClass().getSimpleName();     
           String nameSpaceCode = KNSConstants.KNS_NAMESPACE;
           
           AttributeSecurity maintainableFieldAttributeSecurity = (AttributeSecurity) fieldAttributeSecurity.getMaintainableFieldAttributeSecurity();
           AttributeSecurity  businessObjectAttributeSecurity = (AttributeSecurity) fieldAttributeSecurity.getBusinessObjectAttributeSecurity();
           
           AttributeSet permissionDetails = new AttributeSet(basePermissionDetails);
    	   permissionDetails.put(KimAttributes.PROPERTY_NAME, fieldName);
    	   
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isReadOnly()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_EDIT_PROPERTY, permissionDetails, baseRoleQualification)){
    			   auths.addReadonlyAuthField(fullFieldName);
    		   }
    	   }
    	   
    	   if(maintainableFieldAttributeSecurity != null && maintainableFieldAttributeSecurity.isReadOnly()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_EDIT_PROPERTY, permissionDetails, baseRoleQualification)){
    			   auths.addReadonlyAuthField(fullFieldName);
    		   }
    	   }
    	   
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isPartialMask()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_PARTIALLY_UNMASK_PROPERTY, permissionDetails, baseRoleQualification)){
    			   MaskFormatter partialMaskFormatter = businessObjectAttributeSecurity.getPartialMaskFormatter();
    			   auths.addPartiallyMaskedAuthField(fullFieldName, partialMaskFormatter);
    		   }
    	   }
    	   
    	   if(maintainableFieldAttributeSecurity != null  && maintainableFieldAttributeSecurity.isPartialMask()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
			   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_PARTIALLY_UNMASK_PROPERTY, permissionDetails, baseRoleQualification)){
				   MaskFormatter partialMaskFormatter = maintainableFieldAttributeSecurity.getPartialMaskFormatter();
				   auths.addPartiallyMaskedAuthField(fullFieldName, partialMaskFormatter);
			   }
		   }
    	   
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isMask()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_UNMASK_PROPERTY, permissionDetails, baseRoleQualification)){
    		       MaskFormatter maskFormatter = businessObjectAttributeSecurity.getMaskFormatter();
    			   auths.addMaskedAuthField(fullFieldName, maskFormatter);
    		   }
    	   }
    	   
    	   if(maintainableFieldAttributeSecurity != null  && maintainableFieldAttributeSecurity.isMask()){  
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
			   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_UNMASK_PROPERTY, permissionDetails, baseRoleQualification)){
				   MaskFormatter maskFormatter = maintainableFieldAttributeSecurity.getMaskFormatter();
				   auths.addMaskedAuthField(fullFieldName, maskFormatter);
			   }
		   }
    	
    	   if(businessObjectAttributeSecurity != null && businessObjectAttributeSecurity.isHide()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, componentName);
    		   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_VIEW_PROPERTY, permissionDetails, baseRoleQualification)){
    			   auths.addHiddenAuthField(fullFieldName);	  
    		   }
    	   }   

    	   if(maintainableFieldAttributeSecurity != null  && maintainableFieldAttributeSecurity.isHide()){
    		   permissionDetails.put(KimAttributes.COMPONENT_NAME, documentType);
			   if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_VIEW_PROPERTY, permissionDetails, baseRoleQualification)){
				   auths.addHiddenAuthField(fullFieldName);	  
			   }
		   }
        }
        
        Person person = GlobalVariables.getUserSession().getPerson();
        
        Set<String> hiddenSectionIds = getSecurePotentiallyHiddenSectionIds();
        for (String hiddenSectionId : hiddenSectionIds) {
            // TODO check the permission template to see if they really are hidden here
        	auths.addHiddenSectionId(hiddenSectionId);
        }
        Set<String> readOnlySectionIds = getSecurePotentiallyHiddenSectionIds();
        for (String readOnlySectionId : readOnlySectionIds) {
            // TODO check the permission template to see if they really are read only here
        	auths.addReadOnlySectionId(readOnlySectionId);
        }
    }


    /**
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActions) {
        Set docActions = super.getDocumentActions(document, user, documentActions);
        MaintenanceDocument maintDoc = (MaintenanceDocument) document;
        MaintenanceDocumentAuthorizations docAuths = KNSServiceLocator.getMaintenanceDocumentAuthorizationService().generateMaintenanceDocumentAuthorizations(maintDoc, GlobalVariables.getUserSession().getPerson());
        if (docActions.contains(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE)&& docAuths.hasAnyFieldRestrictions()) {
            docActions.remove(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }
        
        return docActions;
    }
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer#canCreate(java.lang.Class, org.kuali.rice.kim.bo.Person)
     */
    public final boolean canCreate(Class boClass, Person user){
    	//TODO: implement check of create or maintain permission template
    	// we'll need to populate the permission details and role qualifications here ourselves and directly call kim, since the populate methods will blow if there's no document
    	return true;    	
    }
    
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer#canMaintain(java.lang.Class, java.util.Map, org.kuali.rice.kim.bo.Person)
     */
    public final boolean canMaintain(Class boClass, Map primaryKeys, Person user){
    	//TODO: implement check of create or maintain permission template
    	// we'll need to populate the permission details and role qualifications here ourselves and directly call kim, since the populate methods will blow if there's no document
    	return true;
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer#canCreateOrMaintain(java.lang.Class, java.util.Map, org.kuali.rice.kim.bo.Person)
     */
    public final boolean canCreateOrMaintain(MaintenanceDocument maintenanceDocument, Person user) {
    	//TODO: implement check of create or maintain permission template
    	// on this one use the isAuthed methods on the super so the details and template are populated normally
    	return true;
    }

	/**
	 * This method should indicate which sections of the document may need to be hidden based on the user.
	 * The framework will use this list to perform the permission checks.
	 * 
	 * @param document
	 * @return Set of section ids that can be used to identify Sections that may need to be hidden based on user permissions
	 */
    protected Set<String> getSecurePotentiallyHiddenSectionIds() {
    	return new HashSet<String>();
    }
    
    protected Set<String> getSecurePotentiallyReadOnlySectionIds() {
    	return new HashSet<String>();
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