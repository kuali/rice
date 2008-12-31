
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;


/**
 * DocumentAuthorizer containing common, reusable document-level authorization code.
 */
/**
 * This is a description of what this class does - zjzhou don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentAuthorizerBase implements DocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(DocumentAuthorizerBase.class);
    
    public static final String EDIT_MODE_DEFAULT_TRUE_VALUE = "TRUE";
    public static final String USER_SESSION_METHOD_TO_CALL_OBJECT_KEY = "METHOD_TO_CALL_KEYS_METHOD_OBJECT_KEY";
    public static final String USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY = "METHOD_TO_CALL_KEYS_COMPLETE_OBJECT_KEY";

    private static AuthorizationService authorizationService;
    private static KualiWorkflowInfo kualiWorkflowInfo;
    private static KualiConfigurationService kualiConfigurationService;
    private static IdentityManagementService identityManagementService;
    private static PersonService personService;
    private static KualiModuleService kualiModuleService;    
    
    /**
     * Individual document families will need to reimplement this according to their own needs; this version should be good enough
     * to be usable during initial development.
     *
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    public Set getDocumentActions(Document document, Person user, Set<String> documentActions) {
        if ( LOG.isDebugEnabled() ) {
        LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        }
        boolean canEdit = documentActions.contains(KNSConstants.KUALI_ACTION_CAN_EDIT) && canEdit(document, user);
        
        if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_EDIT) && !canEdit){
        	documentActions.remove(KNSConstants.KUALI_ACTION_CAN_EDIT);
        }
        
        if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_COPY) && !canCopy(document, user)){
        	documentActions.remove(KNSConstants.KUALI_ACTION_CAN_COPY);
        }
                
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE) && !canBlanketApprove(document, user)){
    	   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_CANCEL) && !canCancel(document, user)){
    	   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_CANCEL);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_SAVE) && !canSave(document, user)){
    	   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_SAVE);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_ROUTE)&& !canRoute(document, user)){
    	   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_ROUTE);
       }
        
       if(canAcknowledge(document, user)){
    	   documentActions.add(KNSConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
       }

       if(canClearFYI(document, user)){
    	   documentActions.add(KNSConstants.KUALI_ACTION_CAN_FYI);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE) && !canEdit){
    	   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE);
       }
        
       if(canApprove(document, user)){
    	   documentActions.add(KNSConstants.KUALI_ACTION_CAN_APPROVE);
       }
       else {
    	   if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_DISAPPROVE)) {
    		   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_DISAPPROVE);
    	   }
       }
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_ANNOTATE) && !canAnnotate(document, user)){
    	   documentActions.remove(KNSConstants.KUALI_ACTION_CAN_ANNOTATE);
       }
        return documentActions;
    }
    
    /**
     * DocumentTypeAuthorizationException can be extended to customize the initiate error message
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    public boolean canInitiate(String documentTypeName, Person user) {
    	String nameSpaceCode = KNSConstants.KUALI_RICE_SYSTEM_NAMESPACE;
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_CODE, documentTypeName);    
        return getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_INITIATE_DOCUMENT, permissionDetails, null);
    }
    
	public final boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_AD_HOC_REVIEW_DOCUMENT, user.getPrincipalId());
	 }

    private boolean canOpen(Document document, Person user){    	
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_OPEN_DOCUMENT, user.getPrincipalId());
	 }
	    
	private boolean canEdit(Document document, Person user){
	    return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_EDIT_DOCUMENT, user.getPrincipalId());
	 }

	private boolean canCopy(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_COPY_DOCUMENT, user.getPrincipalId());
	 }

	private boolean canCancel(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_CANCEL_DOCUMENT, user.getPrincipalId());
	 }

	private boolean canRoute(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_ROUTE_DOCUMENT, user.getPrincipalId());
	 }

	private boolean canSave(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_SAVE_DOCUMENT, user.getPrincipalId());
	  	   	   	
	 }

	private boolean canBlanketApprove(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_BLANKET_APPROVE_DOCUMENT, user.getPrincipalId());
	 }
	 	 
	private boolean canApprove(Document document, Person user){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
	 }

	private boolean canClearFYI(Document document, Person user){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_FYI_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
	}
	
	private boolean canAcknowledge(Document document, Person user){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
	 }
	
	private boolean canAnnotate(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_ADD_NOTE, user.getPrincipalId());
	        	
    }

    private boolean canTakeRequestedAction(Document document, Person user){
		return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_TAKE_REQUESTED_ACTION, user.getPrincipalId());
	}

   private ThreadLocal<AttributeSet> roleQualification = new ThreadLocal<AttributeSet>();
   private ThreadLocal<AttributeSet> permissionDetails = new ThreadLocal<AttributeSet>();
    
    /**
     * Returns the namespace for the given class by consulting the KualiModuleService.
     * 
     * This method should not need to be overridden but may be if special namespace handling is required.
     */
    protected String getNamespaceForClass( Class clazz ) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if ( moduleService == null ) {
            return "KUALI";
        }
        return moduleService.getModuleConfiguration().getNamespaceCode();
    }

    /**
     * Return the component class to be used for the document.  This base implemnentation simply returns
     * the class of the document object.
     * 
     * Subclasses may override this if necessary.
     */
    protected Class getComponentClass( Document document ) {
        return document.getClass();
    }

    private void populateStandardAttributes( Document document, Map<String,String> attributes ) {
        KualiWorkflowDocument wd = document.getDocumentHeader().getWorkflowDocument();
        attributes.put(KimAttributes.DOCUMENT_NUMBER, document.getDocumentNumber() );
        attributes.put(KimAttributes.DOCUMENT_TYPE_CODE, wd.getDocumentType());
        if ( wd.stateIsInitiated() || wd.stateIsSaved() ) {
        	attributes.put(KimAttributes.ROUTE_NODE_NAME, KimConstants.PRE_ROUTING_ROUT_NAME );
        } else {
        	attributes.put(KimAttributes.ROUTE_NODE_NAME, wd.getCurrentRouteNodeNames() );
        }
        attributes.put(KimAttributes.ROUTE_STATUS_CODE, wd.getRouteHeader().getDocRouteStatus());
        
        
        if ( wd.isCompletionRequested() ){
            attributes.put(KimAttributes.ACTION_REQUEST_CD, KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
        } else if ( wd.isApprovalRequested() ) {
            attributes.put(KimAttributes.ACTION_REQUEST_CD, KEWConstants.ACTION_REQUEST_APPROVE_REQ );  
        } else if ( wd.isAcknowledgeRequested() ) {
            attributes.put(KimAttributes.ACTION_REQUEST_CD, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ );
        } else if ( wd.isFYIRequested() ) {
            attributes.put(KimAttributes.ACTION_REQUEST_CD, KEWConstants.ACTION_REQUEST_FYI_REQ );
        }
        attributes.put(KimAttributes.NAMESPACE_CODE, getNamespaceForClass(getComponentClass(document)) );
        attributes.put(KimAttributes.COMPONENT_NAME, getComponentClass(document).getName() );
    }    
    /**
     * Override this method to populate the role qualifier attributes from the document
     * for the given document.  This will only be called once per request.
     * 
     * Each subclass implementing this method should first call the <b>super</b> version of the method
     * and then add their own, more specific values to the map in addition.
     */
    protected void populateRoleQualification( Document document, Map<String,String> attributes ) {
        populateStandardAttributes(document, attributes);
    }

    /**
     * Override this method to populate the role qualifier attributes from the document
     * for the given document.  This will only be called once per request.
     */
    protected void populatePermissionDetails( Document document, Map<String,String> attributes ) {
        populateStandardAttributes(document, attributes);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getRoleQualification(org.kuali.rice.kns.document.Document)
     */
    private AttributeSet getRoleQualification(Document document) {
        if ( roleQualification.get() == null ) {
            AttributeSet attributes = new AttributeSet();
            populateRoleQualification( document, attributes );
            attributes.put(KimConstants.KIM_ATTRIB_PRINCIPAL_ID, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            roleQualification.set( attributes );
        }
        return roleQualification.get();
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getPermissionDetailValues(org.kuali.rice.kns.document.Document)
     */
    private AttributeSet getPermissionDetailValues(Document document) {
        if ( permissionDetails.get() == null ) {
            AttributeSet attributes = new AttributeSet();
            populatePermissionDetails( document, attributes );
            permissionDetails.set( attributes );
        }
        return permissionDetails.get();
    }
    
    protected final boolean permissionExistsByTemplate(String namespaceCode, String permissionTemplateName, Document document) {
        return getIdentityManagementService().isPermissionDefinedForTemplateName(namespaceCode, permissionTemplateName, getPermissionDetailValues(document));
    }
    
    public final boolean isAuthorized( Document document, String namespaceCode, String permissionName, String principalId ) {
        return getIdentityManagementService().isAuthorized(principalId, namespaceCode, permissionName, getPermissionDetailValues(document), getRoleQualification(document));
    }
    
    public final boolean isAuthorizedByTemplate( Document document, String namespaceCode, String permissionTemplateName, String principalId ) {
        return getIdentityManagementService().isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, getPermissionDetailValues(document), getRoleQualification(document));
    }
    
    public final boolean isAuthorized( Document document, String namespaceCode, String permissionName, String principalId, Map<String,String> additionalPermissionDetails, Map<String,String> additionalRoleQualifiers ) {
        AttributeSet roleQualifiers = null; 
        AttributeSet permissionDetails = null; 
        if ( additionalRoleQualifiers != null ) {
            roleQualifiers = new AttributeSet( getRoleQualification(document) );
            roleQualifiers.putAll(additionalRoleQualifiers);
        } else {
            roleQualifiers = getRoleQualification(document);
        }
        if ( additionalPermissionDetails != null ) {
            permissionDetails = new AttributeSet( getPermissionDetailValues(document) );
            permissionDetails.putAll( additionalPermissionDetails );
        } else {
            permissionDetails = getPermissionDetailValues(document);
        }
        return getIdentityManagementService().isAuthorized(principalId, namespaceCode, permissionName, permissionDetails, roleQualifiers );
    }
    
    public final boolean isAuthorizedByTemplate( Document document, String namespaceCode, String permissionTemplateName, String principalId, Map<String,String> additionalPermissionDetails, Map<String,String> additionalRoleQualifiers ) {
        AttributeSet roleQualifiers = null; 
        AttributeSet permissionDetails = null; 
        if ( additionalRoleQualifiers != null ) {
            roleQualifiers = new AttributeSet( getRoleQualification(document) );
            roleQualifiers.putAll(additionalRoleQualifiers);
        } else {
            roleQualifiers = getRoleQualification(document);
        }
        if ( additionalPermissionDetails != null ) {
            permissionDetails = new AttributeSet( getPermissionDetailValues(document) );
            permissionDetails.putAll( additionalPermissionDetails );
        } else {
            permissionDetails = getPermissionDetailValues(document);
        }
        return getIdentityManagementService().isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers );
    }
        
    
     /**
	  * @return the identityManagementService
	  */
	 public static IdentityManagementService getIdentityManagementService() {
			
		if (identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

    public static PersonService getPersonService() {
        if ( personService == null ) {
            personService = KIMServiceLocator.getPersonService();
        }
        return personService;
    }

    public static KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) {
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
        return kualiModuleService;
    }
        
    protected final KualiConfigurationService getKualiConfigurationService() {
        if ( kualiConfigurationService == null ) {
            kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
        }
        return kualiConfigurationService;
    }
        
    protected final AuthorizationService getAuthorizationService() {
        if ( authorizationService == null ) {
            authorizationService = KNSServiceLocator.getAuthorizationService();
        }
        return authorizationService;
    }
        
    protected final KualiWorkflowInfo getKualiWorkflowInfo() {
        if ( kualiWorkflowInfo == null ) {
            kualiWorkflowInfo = KNSServiceLocator.getWorkflowInfoService();
        }
        return kualiWorkflowInfo;
    }
    
}