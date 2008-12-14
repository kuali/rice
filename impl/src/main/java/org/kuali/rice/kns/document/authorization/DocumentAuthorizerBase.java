
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
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
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
   
    
    
     public Map getEditMode(Document d, Person u) {
        Map editModeMap = new HashMap();
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = d.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(d, u)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
        }

        editModeMap.put(editMode, EDIT_MODE_DEFAULT_TRUE_VALUE);

        return editModeMap;
    }
    
    
    /**
     * Individual document families will need to reimplement this according to their own needs; this version should be good enough
     * to be usable during initial development.
     *
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    public Set getDocumentActionFlags(Document document, Person user, Set<String> documentActions) {
        if ( LOG.isDebugEnabled() ) {
        LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        }
        Set docActions = new HashSet();
        
        
    	if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_CLOSE)){
    		docActions.add(KNSConstants.KUALI_ACTION_CAN_CLOSE);
    	}
    	if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_RELOAD)){
    		docActions.add(KNSConstants.KUALI_ACTION_CAN_RELOAD);    	
    	}
    	if(documentActions.contains(KNSConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT)){
    		docActions.add(KNSConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);    	
    	}

        if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_COPY) && canCopy(document, user)){
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_COPY);
        }
        
        if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_EDIT) && canEdit(document, user)){
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_EDIT);
        }
        
       if(canBlanketApprove(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_CANCEL)&& hasPreRouteEditAuthorization(document, user) && canCancel(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_CANCEL);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_SAVE)&& hasPreRouteEditAuthorization(document, user) && canSave(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_SAVE);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_ROUTE)&& hasPreRouteEditAuthorization(document, user) && canRoute(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_ROUTE);
       }
        
       if(canAcknowledge(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
       }

       if(canClearFYI(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_FYI);
       }
       
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE) && canEdit(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE);
       }
        
       if(canApprove(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_APPROVE);
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_DISAPPROVE);
       }
       if(documentActions.contains(KNSConstants.KUALI_ACTION_CAN_ANNOTATE) && canAnnotate(document, user)){
    	   docActions.add(KNSConstants.KUALI_ACTION_CAN_ANNOTATE);
       }
        return docActions;
    }
    
    public DocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        }

        DocumentActionFlags flags = new DocumentActionFlags(); // all flags default to false

      
        flags.setCanClose(true); // can always close a document

        return flags;
    }
    
    protected KualiConfigurationService getKualiConfigurationService() {
    if ( kualiConfigurationService == null ) {
        kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
    }
    return kualiConfigurationService;
    }
    
    protected AuthorizationService getAuthorizationService() {
    if ( authorizationService == null ) {
        authorizationService = KNSServiceLocator.getAuthorizationService();
    }
    return authorizationService;
    }
    
    protected KualiWorkflowInfo getKualiWorkflowInfo() {
    if ( kualiWorkflowInfo == null ) {
        kualiWorkflowInfo = KNSServiceLocator.getWorkflowInfoService();
    }
    return kualiWorkflowInfo;
    }
    
    /**
     * Helper method to disallow the perform route report button globally for a particular authorizer class
     * @param document - current document
     * @param user - current user
     * @return boolean to allow or disallow route report button to show for user
     */
    public boolean allowsPerformRouteReport(Document document, Person user) {
        KualiConfigurationService kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
        return kualiConfigurationService.getIndicatorParameter( KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND);
    }
    
     /**
     * Helper method to set the annotate flag based on other workflow tags
     * @param flags
     */
    public void setAnnotateFlag(DocumentActionFlags flags) {
        boolean canWorkflow = flags.getCanSave() || flags.getCanRoute() || flags.getCanCancel() || flags.getCanBlanketApprove() || flags.getCanApprove() || flags.getCanDisapprove() || flags.getCanAcknowledge() || flags.getCanAdHocRoute();
        flags.setCanAnnotate(canWorkflow);
    }
    
    /**
     * DocumentTypeAuthorizationException can be extended to customize the initiate error message
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    public void canInitiate(String documentTypeName, Person user) {
    	
    }

    /**
     * Default implementation here is if a user cannot initiate a document they cannot copy one.
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canCopy(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    public boolean canCopy(String documentTypeName, Person user) {
        return false;
    }

    /**
     * This method checks to see if a document is using Pessimistic Locking first. If the document is not using Pessimistic
     * Locking this method will return the value returned by {@link #hasInitiateAuthorization(Document, Person)}. If
     * the document is using pessimistic locking and the value of {@link #hasInitiateAuthorization(Document, Person)}
     * is true, the system will check to see that the given user has a lock on the document and return true if one is found.
     * 
     * @param document - document to check
     * @param user - current user
     * @return true if the document is using Pessimistic Locking, the user has initiate authorization (see
     *         {@link #hasInitiateAuthorization(Document, Person)}), and the document has a lock owned by the given
     *         user. If the document is not using Pessimistic Locking the value returned will be that returned by
     *         {@link #hasInitiateAuthorization(Document, Person)}.
     */
    public boolean hasPreRouteEditAuthorization(Document document, Person user) {
        if (usesPessimisticLocking(document)) {
            if (hasInitiateAuthorization(document, user)) {
                for (Iterator iterator = document.getPessimisticLocks().iterator(); iterator.hasNext();) {
                    PessimisticLock lock = (PessimisticLock) iterator.next();
                    if (lock.isOwnedByUser(user)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return hasInitiateAuthorization(document, user);
        }
    }
    
    protected boolean usesPessimisticLocking(Document document) {
        return KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName()).getUsePessimisticLocking();
    }
    
    /**
     * Determines whether the given user should have initiate permissions on the given document.
     * @param document - current document
     * @param user - current user
     * @return boolean (true if they should have permissions)
     */
    public boolean hasInitiateAuthorization(Document document, Person user) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(user.getPrincipalName());
    }

    /**
     * Determines whether the given user should be able to view the attachment on the given document
     * and the attachment type
     * @param attachmentTypeName - the attachment type
     * @param document - current document
     * @param user - current user
     * @return boolean (true if they should have permissions)
     */
    public boolean canViewAttachment(String attachmentTypeName, Document document, Person user) {
        return true;
    }


    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canOpen(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
     */
    public boolean canOpen(Document document, Person user){
    	
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_OPEN_DOCUMENT, user.getPrincipalId());
	        	
	 }
	    
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canEdit(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canEdit(Document document, Person user){
		   
	    return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_EDIT_DOCUMENT, user.getPrincipalId());
    
	 }
	 
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canCopy(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canCopy(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_COPY_DOCUMENT, user.getPrincipalId());
	     	
	 }
	    
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canCancel(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canCancel(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_CANCEL_DOCUMENT, user.getPrincipalId());
	     	  	
	 }
	 
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canRoute(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canRoute(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_ROUTE_DOCUMENT, user.getPrincipalId());
  	  	   	
	 }
	
	
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canSave(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canSave(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_SAVE_DOCUMENT, user.getPrincipalId());
	  	   	   	
	 }
	 
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canBlanketApprove(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canBlanketApprove(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_BLANKET_APPROVE_DOCUMENT, user.getPrincipalId());
	   	     	
	 }
	 
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canReceiveAdHoc(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode){
	     return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_AD_HOC_REVIEW_DOCUMENT, user.getPrincipalId());
	   	    	
	 }
	 
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canApprove(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canApprove(Document document, Person user){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
		
	 }
	 
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canClearFYI(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canClearFYI(Document document, Person user){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_FYI_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
	}
	
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canAcknowledge(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canAcknowledge(Document document, Person user){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
	 }
	
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canComplete(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canComplete(Document document, Person user ){
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if(permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD) && KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(permissionDetails.get(KimAttributes.ACTION_REQUEST_CD))){
			return canTakeRequestedAction(document, user);
		}else{
			return false;
		}
	 }
	
	 /**
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canDisapprove(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
	public boolean canDisapprove(Document document, Person user){
		 return canApprove(document, user);
	 }
	
	
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canAnnotate(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
     */
    public boolean canAnnotate(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_ADD_NOTE, user.getPrincipalId());
	        	
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
    private final AttributeSet getRoleQualification(Document document) {
        if ( roleQualification.get() == null ) {
            AttributeSet attributes = new AttributeSet();
            populateRoleQualification( document, attributes );
            roleQualification.set( attributes );
        }
        return roleQualification.get();
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getPermissionDetailValues(org.kuali.rice.kns.document.Document)
     */
    private final AttributeSet getPermissionDetailValues(Document document) {
        if ( permissionDetails.get() == null ) {
            AttributeSet attributes = new AttributeSet();
            populatePermissionDetails( document, attributes );
            permissionDetails.set( attributes );
        }
        return permissionDetails.get();
    }
    
    public boolean isAuthorized( Document document, String namespaceCode, String permissionName, String principalId ) {
        return getIdentityManagementService().isAuthorized(principalId, namespaceCode, permissionName, getPermissionDetailValues(document), getRoleQualification(document));
    }
    
    public boolean isAuthorizedByTemplate( Document document, String namespaceCode, String permissionTemplateName, String principalId ) {
        return getIdentityManagementService().isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, getPermissionDetailValues(document), getRoleQualification(document));
    }
    
    public boolean isAuthorized( Document document, String namespaceCode, String permissionName, String principalId, Map<String,String> additionalPermissionDetails, Map<String,String> additionalRoleQualifiers ) {
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
    
    public boolean isAuthorizedByTemplate( Document document, String namespaceCode, String permissionTemplateName, String principalId, Map<String,String> additionalPermissionDetails, Map<String,String> additionalRoleQualifiers ) {
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
    
    private boolean canTakeRequestedAction(Document document, Person user){
		return isAuthorizedByTemplate(document, KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PERMISSION_TAKE_REQUESTED_ACTION, user.getPrincipalId());
	}
    
}