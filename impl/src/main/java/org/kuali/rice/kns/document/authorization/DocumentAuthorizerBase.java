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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.dto.ValidActionsDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.exception.PessimisticLockingException;
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;


/**
 * DocumentAuthorizer containing common, reusable document-level authorization code.
 */
public class DocumentAuthorizerBase implements DocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(DocumentAuthorizerBase.class);
    
    public static final String EDIT_MODE_DEFAULT_TRUE_VALUE = "TRUE";
    public static final String USER_SESSION_METHOD_TO_CALL_OBJECT_KEY = "METHOD_TO_CALL_KEYS_METHOD_OBJECT_KEY";
    public static final String USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY = "METHOD_TO_CALL_KEYS_COMPLETE_OBJECT_KEY";

    private static AuthorizationService authorizationService;
    private static KualiWorkflowInfo kualiWorkflowInfo;
    private static KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
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
    public DocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");

        DocumentActionFlags flags = new DocumentActionFlags(); // all flags default to false

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        boolean hasInitiateAuthorization = hasInitiateAuthorization(document, user);

        flags.setCanClose(true); // can always close a document

        // if a document is canceled, everything other than close should be set to false
        // if a document is NOT canceled, then we want to process the rest
        if (!workflowDocument.stateIsCanceled()) {
            flags.setCanReload(!workflowDocument.stateIsInitiated());

            flags.setCanBlanketApprove(workflowDocument.isBlanketApproveCapable());

            // The only exception to the supervisor user canSupervise is when the supervisor
            // user is also the initiator, and does NOT have an approval request. In other words if they
            // are the document initiator, and its still in Initiated or Saved phase, they cant have access
            // to the supervisor buttons. If they're the initiator, but for some reason they are also
            // approving the document, then they can have the supervisor button & functions.
            boolean canSuperviseAsInitiator = !(hasInitiateAuthorization && !workflowDocument.isApprovalRequested());
            flags.setCanSupervise(org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE,	KNSServiceLocator.getKualiConfigurationService().getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KNSConstants.CoreApcParms.SUPERVISOR_WORKGROUP)) && canSuperviseAsInitiator);

            // default normal documents to be unable to copy
            flags.setCanCopy(false);
            // default route report to false and set individually based on workflow doc status below
            flags.setCanPerformRouteReport(allowsPerformRouteReport(document, user));

            if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
                boolean hasPreRouteEditAuthorization = hasPreRouteEditAuthorization(document, user);
                flags.setCanCancel(hasPreRouteEditAuthorization && validActions.contains(KEWConstants.ACTION_TAKEN_CANCELED_CD));

                flags.setCanSave(hasPreRouteEditAuthorization && validActions.contains(KEWConstants.ACTION_TAKEN_SAVED_CD));

                flags.setCanRoute(hasPreRouteEditAuthorization && validActions.contains(KEWConstants.ACTION_TAKEN_ROUTED_CD));

                flags.setCanAcknowledge(workflowDocument.isAcknowledgeRequested());
                flags.setCanFYI(workflowDocument.isFYIRequested());

                flags.setCanAdHocRoute(flags.getCanSave() || flags.getCanRoute());
            }
            else if (workflowDocument.stateIsEnroute()) {
                flags.setCanApprove(workflowDocument.isApprovalRequested());

                flags.setCanDisapprove(workflowDocument.isApprovalRequested());

                flags.setCanAcknowledge(workflowDocument.isAcknowledgeRequested());
                flags.setCanFYI(workflowDocument.isFYIRequested());

                flags.setCanAdHocRoute(workflowDocument.isApprovalRequested() || workflowDocument.isAcknowledgeRequested());
            }
            else if (workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal() || workflowDocument.stateIsDisapproved()) {
                flags.setCanAcknowledge(workflowDocument.isAcknowledgeRequested());
                flags.setCanFYI(workflowDocument.isFYIRequested());

                flags.setCanAdHocRoute(false);
            }
            else if (workflowDocument.stateIsException()) {
            try {
                    ActionRequestDTO[] requests = getKualiWorkflowInfo().getActionRequests(workflowDocument.getRouteHeaderId());
                    boolean reqFound = false;
                    for ( ActionRequestDTO req : requests ) {
                        if ( req.isExceptionRequest() && req.getActionTakenId() == null ) {
                        if ( req.getGroupVO()!= null ) {
                        	
                        List<String> users = KIMServiceLocator.getIdentityManagementService().getMemberGroupIds(req.getGroupVO().getGroupId());
                        for ( String usr : users ) {
                            if ( usr.equals( user.getPrincipalId() ) ) {
                            flags.setCanCancel( true );
                                    flags.setCanApprove( true );
                                    flags.setCanDisapprove( true );
                                    reqFound = true; // used to break out of outer loop
                                    break;
                            }
                        }
                        if ( reqFound ) {
                            break;
                        }
                        } else {
                        LOG.error( "Unable to retrieve user list for exceptiongroup.  ActionRequestVO.getGroupVO() returned null" );
                        LOG.error( "request: " + req );
                        }
                    }
                    }
            } catch( WorkflowException ex ) {
                LOG.error("Unable to retrieve action requests for document: " + document.getDocumentNumber(),ex);
            }

                flags.setCanAdHocRoute(false);
            }
        }

        setAnnotateFlag(flags);

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
        if (!getAuthorizationService().isAuthorized(user, "initiate", documentTypeName)) {
            // build authorized workgroup list for error message
            Set authorizedWorkgroups = getAuthorizationService().getAuthorizedWorkgroups("initiate", documentTypeName);
            String workgroupList = StringUtils.join(authorizedWorkgroups.toArray(), ",");
            throw new DocumentInitiationAuthorizationException(new String[] {workgroupList,documentTypeName});
        }
    }

    /**
     * Default implementation here is if a user cannot initiate a document they cannot copy one.
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canCopy(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    public boolean canCopy(String documentTypeName, Person user) {
        return getAuthorizationService().isAuthorized(user, "initiate", documentTypeName);
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
     * This method creates a new {@link PessimisticLock} when Workflow processing requires one
     * 
     * @param document - the document to create the lock against and add the lock to
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#establishWorkflowPessimisticLocking(org.kuali.rice.kns.document.Document)
     */
    public void establishWorkflowPessimisticLocking(Document document) {
        PessimisticLock lock = createNewPessimisticLock(document, new HashMap(), getWorkflowPessimisticLockOwnerUser());
        document.addPessimisticLock(lock);
    }
    
    /**
     * This method releases locks created via the {@link #establishWorkflowPessimisticLocking(Document)} method for the given document
     * 
     * @param document - document to release locks from
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#releaseWorkflowPessimisticLocking(org.kuali.rice.kns.document.Document)
     */
    public void releaseWorkflowPessimisticLocking(Document document) {
        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(document.getPessimisticLocks(), getWorkflowPessimisticLockOwnerUser());
        document.refreshPessimisticLocks();
    }

    /**
     * This method identifies the user that should be used to create and clear {@link PessimisticLock} objects required by
     * Workflow.<br>
     * <br>
     * The default is the Kuali system user defined by {@link RiceConstants#SYSTEM_USER}. This method can be overriden by
     * implementing documents if another user is needed.
     * 
     * @return a valid {@link Person} object
     */
    protected Person getWorkflowPessimisticLockOwnerUser() {
        String networkId = KNSConstants.SYSTEM_USER;
        return org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(networkId);
    }
    
    /**
     * This implementation will check the given document, editMode map, and user object to verify Pessimistic Locking. If the
     * given edit mode map contains an 'entry type' edit mode then the system will check the locks already in existance on
     * the document. If a valid lock for the given user is found the system will return the given edit mode map. If a valid
     * lock is found but is owned by another user the edit mode map returned will have any 'entry type' edit modes removed. If the
     * given document has no locks and the edit mode map passed in has at least one 'entry type' mode then a new
     * {@link PessimisticLock} object will be created and set on the document for the given user.<br>
     * <br> 
     * NOTE: This method is only called if the document uses pessimistic locking as described in the data dictionary file.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#establishLocks(org.kuali.rice.kns.document.Document,
     *      java.util.Map, org.kuali.rice.kim.bo.Person)
     */
    public Map establishLocks(Document document, Map editMode, Person user) {
        Map editModeMap = new HashMap();
        // givenUserLockDescriptors is a list of lock descriptors currently held on the document by the given user
        List<String> givenUserLockDescriptors = new ArrayList<String>();
        // lockDescriptorUsers is a map with lock descriptors as keys and users other than the given user who hold a lock of each descriptor 
        Map<String,Set<Person>> lockDescriptorUsers = new HashMap<String,Set<Person>>();

        // build the givenUserLockDescriptors set and the lockDescriptorUsers map
        for (PessimisticLock lock : document.getPessimisticLocks()) {
            if (lock.isOwnedByUser(user)) {
                // lock is owned by given user
                givenUserLockDescriptors.add(lock.getLockDescriptor());
            } else {
                // lock is not owned by the given user
                if (!lockDescriptorUsers.containsKey(lock.getLockDescriptor())) {
                    lockDescriptorUsers.put(lock.getLockDescriptor(), new HashSet<Person>());
                }
                ((Set<Person>) lockDescriptorUsers.get(lock.getLockDescriptor())).add(lock.getOwnedByUser());
            }
        }

        // verify that no locks held by current user exist for any other user
        for (String givenUserLockDescriptor : givenUserLockDescriptors) {
            if ( (lockDescriptorUsers.containsKey(givenUserLockDescriptor)) && (lockDescriptorUsers.get(givenUserLockDescriptor).size() > 0) ) {
                Set<Person> users = lockDescriptorUsers.get(givenUserLockDescriptor);
                if ( (users.size() != 1) || (!getWorkflowPessimisticLockOwnerUser().getPrincipalId().equals(users.iterator().next().getPrincipalId())) ) {
                    String descriptorText = (useCustomLockDescriptors()) ? " using lock descriptor '" + givenUserLockDescriptor + "'" : "";
                    String errorMsg = "Found an invalid lock status on document number " + document.getDocumentNumber() + "with current user and other user both having locks" + descriptorText + " concurrently";
                    LOG.debug(errorMsg);
                    throw new PessimisticLockingException(errorMsg);
                }
            }
        }
        
        // check to see if the given user has any locks in the system at all
        if (givenUserLockDescriptors.isEmpty()) {
            // given user has no locks... check for other user locks
            if (lockDescriptorUsers.isEmpty()) {
                // no other user has any locks... set up locks for given user if user has edit privileges
                if (isLockRequiredByUser(document, editMode, user)) {
                    document.addPessimisticLock(createNewPessimisticLock(document, editMode, user));
                }
                editModeMap.putAll(editMode);
            } else {
                // at least one other user has at least one other lock... adjust edit mode for read only
                if (useCustomLockDescriptors()) {
                    // check to see if the custom lock descriptor is already in use
                    String customLockDescriptor = getCustomLockDescriptor(document, editMode, user);
                    if (lockDescriptorUsers.containsKey(customLockDescriptor)) {
                        // at least one other user has this descriptor locked... remove editable edit modes
                        editModeMap = getEditModeWithEditableModesRemoved(editMode);
                    } else {
                        // no other user has a lock with this descriptor
                        if (isLockRequiredByUser(document, editMode, user)) {
                            document.addPessimisticLock(createNewPessimisticLock(document, editMode, user));
                        }
                        editModeMap.putAll(editMode);
                    }
                } else {
                    editModeMap = getEditModeWithEditableModesRemoved(editMode);
                }
            }
        } else {
            // given user already has at least one lock descriptor
            if (useCustomLockDescriptors()) {
                // get the custom lock descriptor and check to see if if the given user has a lock with that descriptor
                String customLockDescriptor = getCustomLockDescriptor(document, editMode, user);
                if (givenUserLockDescriptors.contains(customLockDescriptor)) {
                    // user already has lock that is required
                    editModeMap.putAll(editMode);
                } else {
                    // user does not have lock for descriptor required
                    if (lockDescriptorUsers.containsKey(customLockDescriptor)) {
                        // another user has the lock descriptor that the given user requires... disallow lock and alter edit modes to have read only
                        editModeMap = getEditModeWithEditableModesRemoved(editMode);
                    } else {
                        // no other user has a lock with this descriptor... check if this user needs a lock
                        if (isLockRequiredByUser(document, editMode, user)) {
                            document.addPessimisticLock(createNewPessimisticLock(document, editMode, user));
                        }
                        editModeMap.putAll(editMode);
                    }
                }
            } else {
                // user already has lock and no descriptors are being used... use the existing edit modes
                editModeMap.putAll(editMode);
            }
        }

        return editModeMap;
    }
    
    /**
     * This method is used to check if the given parameters warrant a new lock to be created for the given user. This method
     * utilizes the {@link #isEntryEditMode(java.util.Map.Entry)} method.
     * 
     * @param document -
     *            document to verify lock creation against
     * @param editMode -
     *            edit modes list to check for 'entry type' edit modes
     * @param user -
     *            user the lock will be 'owned' by
     * @return true if the given edit mode map has at least one 'entry type' edit mode... false otherwise
     */
    protected boolean isLockRequiredByUser(Document document, Map editMode, Person user) {
        // check for entry edit mode
        for (Iterator iterator = editMode.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (isEntryEditMode(entry)) {
                return true;
            }
        }
        return false;
    }
    
   /**
     * This method is used to remove edit modes from the given map that allow the user to edit data on the document. This
     * method utilizes the {@link #isEntryEditMode(java.util.Map.Entry)} method to identify if an edit mode is defined as an
     * 'entry type' edit mode. It also uses the {@link #getEntryEditModeReplacementMode(java.util.Map.Entry)} method to replace
     * any 'entry type' edit modes it finds.
     * 
     * @param currentEditMode -
     *            current set of edit modes the user has assigned to them
     * @return an adjusted edit mode map where 'entry type' edit modes have been removed or replaced using the
     *         {@link #getEntryEditModeReplacementMode()} method
     */
    protected Map getEditModeWithEditableModesRemoved(Map currentEditMode) {
        Map editModeMap = new HashMap();
        for (Iterator iterator = currentEditMode.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (isEntryEditMode(entry)) {
                editModeMap.putAll(getEntryEditModeReplacementMode(entry));
            } else {
                editModeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return editModeMap;
    }
    
    /**
     * This method is used to check if the given {@link Map.Entry} is an 'entry type' edit mode and that the value is set to
     * signify that this user has that edit mode available to them
     * 
     * @param entry -
     *            the {@link Map.Entry} object that contains an edit mode such as the ones returned but
     *            {@link #getEditMode(Document, Person)}
     * @return true if the given entry has a key signifying an 'entry type' edit mode and the value is equal to
     *         {@link #EDIT_MODE_DEFAULT_TRUE_VALUE}... false if not
     */
    protected boolean isEntryEditMode(Map.Entry entry) {
        // check for FULL_ENTRY edit mode set to default true value
        if (AuthorizationConstants.EditMode.FULL_ENTRY.equals(entry.getKey())) {
            String fullEntryEditModeValue = (String)entry.getValue();
            return ( (ObjectUtils.isNotNull(fullEntryEditModeValue)) && (EDIT_MODE_DEFAULT_TRUE_VALUE.equals(fullEntryEditModeValue)) );
        }
        return false;
    }
    
    /**
     * This method is used to return values needed to replace the given 'entry type' edit mode {@link Map.Entry} with one that will not allow the user to enter data on the document 
     * 
     * @param entry - the current 'entry type' edit mode to replace 
     * @return a Map of edit modes that will be used to replace this edit mode (represented by the given entry parameter)
     */
    protected Map getEntryEditModeReplacementMode(Map.Entry entry) {
        Map editMode = new HashMap();
        editMode.put(AuthorizationConstants.EditMode.VIEW_ONLY, EDIT_MODE_DEFAULT_TRUE_VALUE);
        return editMode;
    }
    
    /**
     * This method creates a new {@link PessimisticLock} object using the given document and user. If the
     * {@link #useCustomLockDescriptors()} method returns true then the new lock will also have a custom lock descriptor
     * value set to the return value of {@link #getCustomLockDescriptor(Document, Map, Person)}.
     * 
     * @param document -
     *            document to place the lock on
     * @param editMode -
     *            current edit modes for given user
     * @param user -
     *            user who will 'own' the new lock object
     * @return the newly created lock object
     */
    protected PessimisticLock createNewPessimisticLock(Document document, Map editMode, Person user) {
        if (useCustomLockDescriptors()) {
            return KNSServiceLocator.getPessimisticLockService().generateNewLock(document.getDocumentNumber(), getCustomLockDescriptor(document, editMode, user), user);
        } else {
            return KNSServiceLocator.getPessimisticLockService().generateNewLock(document.getDocumentNumber(), user);
        }
    }
    
    /**
     * This method should be overriden by groups requiring the lock descriptor field in the {@link PessimisticLock} objects.
     * If it is not overriden and {@link #useCustomLockDescriptors()} returns true then this method will throw a
     * {@link PessimisticLockingException}
     * 
     * @param document - document being checked for locking
     * @param editMode - editMode generated for passed in user
     * @param user - user attempting to establish locks
     * @return a {@link PessimisticLockingException} will be thrown as the default implementation
     */
    protected String getCustomLockDescriptor(Document document, Map editMode, Person user) {
        throw new PessimisticLockingException("Document " + document.getDocumentNumber() + " is using Pessimistic Locking with lock descriptors but the authorizer class has not defined the getCustomLockDescriptor() method");
    }

    /**
     * This method should be used to define Document Authorizer classes which will use custom lock descriptors in the
     * {@link PessimisticLock} objects NOTE: if this method is overriden to return true then the method
     * {@link #getCustomLockDescriptor(Document, Map, Person)} must be overriden
     * 
     * @return false if the document will not be using lock descriptors or true if the document will use lock descriptors.
     *         The default return value is false
     */
    protected boolean useCustomLockDescriptors() {
        return false;
    }
    
}
