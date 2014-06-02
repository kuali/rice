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
package org.kuali.rice.krad.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.KimConstants.PermissionNames;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.exception.PessimisticLockingException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.PessimisticLockService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for pessimistic locking
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class PessimisticLockServiceImpl implements PessimisticLockService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PessimisticLockServiceImpl.class);

    protected DataObjectService dataObjectService;
    protected DataDictionaryService dataDictionaryService;
    protected PermissionService permissionService;
    protected PersonService personService;

	/**
     * @see org.kuali.rice.krad.service.PessimisticLockService#delete(java.lang.String)
     */
    @Override
	public void delete(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("An invalid blank id was passed to delete a Pessimistic Lock.");
        }
        PessimisticLock lock = dataObjectService.find(PessimisticLock.class, Long.valueOf(id));
        if ( lock == null ) {
            throw new IllegalArgumentException("Pessimistic Lock with id " + id + " cannot be found in the database.");
        }
        Person user = GlobalVariables.getUserSession().getPerson();
        if ( (!lock.isOwnedByUser(user)) && (!isPessimisticLockAdminUser(user)) ) {
            throw new AuthorizationException(user.getName(),"delete", "Pessimistick Lock (id " + id + ")");
        }
        delete(lock);
    }

    private void delete(PessimisticLock lock) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Deleting lock: " + lock);
    	}
        dataObjectService.delete(lock);
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#generateNewLock(String)
     */
    @Override
	public PessimisticLock generateNewLock(String documentNumber) {
        return generateNewLock(documentNumber, GlobalVariables.getUserSession().getPerson());
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#generateNewLock(java.lang.String)
     */
    @Override
	public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor) {
        return generateNewLock(documentNumber, lockDescriptor, GlobalVariables.getUserSession().getPerson());
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#generateNewLock(java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
	public PessimisticLock generateNewLock(String documentNumber, Person user) {
        return generateNewLock(documentNumber, PessimisticLock.DEFAULT_LOCK_DESCRIPTOR, user);
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#generateNewLock(java.lang.String, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
	public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor, Person user) {
        PessimisticLock lock = new PessimisticLock(documentNumber, lockDescriptor, user, GlobalVariables.getUserSession());
        lock = save(lock);
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Generated new lock: " + lock);
        }
        return lock;
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#getPessimisticLocksForDocument(java.lang.String)
     */
    @Override
	public List<PessimisticLock> getPessimisticLocksForDocument(String documentNumber) {
        return new ArrayList<PessimisticLock>( dataObjectService.findMatching(PessimisticLock.class,
        		QueryByCriteria.Builder.forAttribute(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber)
        		.build()).getResults() );
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#getPessimisticLocksForSession(java.lang.String)
     */
    @Override
	public List<PessimisticLock> getPessimisticLocksForSession(String sessionId) {
        return new ArrayList<PessimisticLock>( dataObjectService.findMatching(PessimisticLock.class,
        		QueryByCriteria.Builder.forAttribute(KRADPropertyConstants.SESSION_ID, sessionId)
        		.build()).getResults() );
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#isPessimisticLockAdminUser(org.kuali.rice.kim.api.identity.Person)
     */
    @Override
	public boolean isPessimisticLockAdminUser(Person user) {
    	return getPermissionService().isAuthorized( user.getPrincipalId(), KRADConstants.KNS_NAMESPACE, PermissionNames.ADMIN_PESSIMISTIC_LOCKING,
                Collections.<String, String>emptyMap() );
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#releaseAllLocksForUser(java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
	public void releaseAllLocksForUser(List<PessimisticLock> locks, Person user) {
        for (Iterator<PessimisticLock> iterator = locks.iterator(); iterator.hasNext();) {
            PessimisticLock lock = iterator.next();
            if (lock.isOwnedByUser(user)) {
                try {
                    delete(lock);
                } catch ( RuntimeException ex ) {
                	if ( ex.getCause() != null && ex.getCause().getClass().equals( LegacyDataAdapter.OPTIMISTIC_LOCK_OJB_EXCEPTION_CLASS ) ) {
                        LOG.warn( "Suppressing Optimistic Lock Exception. Document Num: " +  lock.getDocumentNumber());
                    } else {
                        throw ex;
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#releaseAllLocksForUser(java.util.List, org.kuali.rice.kim.api.identity.Person, java.lang.String)
     */
    @Override
	public void releaseAllLocksForUser(List<PessimisticLock> locks, Person user, String lockDescriptor) {
        for (Iterator<PessimisticLock> iterator = locks.iterator(); iterator.hasNext();) {
            PessimisticLock lock = iterator.next();
            if ( (lock.isOwnedByUser(user)) && (lockDescriptor.equals(lock.getLockDescriptor())) ) {
                try {
                    delete(lock);
                } catch ( RuntimeException ex ) {
                	if ( ex.getCause() != null && ex.getCause().getClass().equals( LegacyDataAdapter.OPTIMISTIC_LOCK_OJB_EXCEPTION_CLASS ) ) {
                        LOG.warn( "Suppressing Optimistic Lock Exception. Document Num: " +  lock.getDocumentNumber());
                    } else {
                        throw ex;
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.service.PessimisticLockService#save(org.kuali.rice.krad.document.authorization.PessimisticLock)
     */
    @Override
	public PessimisticLock save(PessimisticLock lock) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Saving lock: " + lock);
    	}
        return dataObjectService.save(lock);
    }

    /**
     * @param document
     * @param user
     * @return Set of actions are permitted the given user on the given document
     */
    @Override
	public Set getDocumentActions(Document document, Person user, Set<String> documentActions){
    	if(documentActions.contains(KRADConstants.KUALI_ACTION_CAN_CANCEL) && !hasPreRouteEditAuthorization(document, user) ){
    		documentActions.remove(KRADConstants.KUALI_ACTION_CAN_CANCEL);
    	}
    	if(documentActions.contains(KRADConstants.KUALI_ACTION_CAN_SAVE)  && !hasPreRouteEditAuthorization(document, user)){
    		documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
    	}
        if(documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ROUTE) && !hasPreRouteEditAuthorization(document, user)){
        	documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }
        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE) && !hasPreRouteEditAuthorization(document, user)){
        	documentActions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }
    	return documentActions;
    }


    /**
     * This method checks to see that the given user has a lock on the document and return true if one is found.
     *
     * @param document - document to check
     * @param user - current user
     * @return true if the document is using Pessimistic Locking, the user has initiate authorization (see
     *         {@link #hasInitiateAuthorization(Document, Person)}), and the document has a lock owned by the given
     *         user. If the document is not using Pessimistic Locking the value returned will be that returned by
     *         {@link #hasInitiateAuthorization(Document, Person)}.
     */
    protected boolean hasPreRouteEditAuthorization(Document document, Person user) {
    	if (document.getPessimisticLocks().isEmpty()) {
    		return true;
    	}
    	for (Iterator<PessimisticLock> iterator = document.getPessimisticLocks().iterator(); iterator.hasNext();) {
    		PessimisticLock lock = iterator.next();
    		if (lock.isOwnedByUser(user)) {
    			return true;
            }
        }
        return false;
    }


    protected boolean usesPessimisticLocking(Document document) {
        return getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName()).getUsePessimisticLocking();
    }


    /**
     * This method creates a new {@link PessimisticLock} when Workflow processing requires one
     *
     * @param document - the document to create the lock against and add the lock to
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#establishWorkflowPessimisticLocking(org.kuali.rice.krad.document.Document)
     */
    @Override
	public void establishWorkflowPessimisticLocking(Document document) {
        PessimisticLock lock = createNewPessimisticLock(document, new HashMap(), getWorkflowPessimisticLockOwnerUser());
        document.addPessimisticLock(lock);
    }

    /**
     * This method releases locks created via the {@link #establishWorkflowPessimisticLocking(Document)} method for the given document
     *
     * @param document - document to release locks from
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#releaseWorkflowPessimisticLocking(org.kuali.rice.krad.document.Document)
     */
    @Override
	public void releaseWorkflowPessimisticLocking(Document document) {
        releaseAllLocksForUser(document.getPessimisticLocks(), getWorkflowPessimisticLockOwnerUser());
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
        String networkId = KRADConstants.SYSTEM_USER;
        return getPersonService().getPersonByPrincipalName(networkId);
    }

    /**
     * This implementation will check the given document, editMode map, and user object to verify Pessimistic Locking. If the
     * given edit mode map contains an 'entry type' edit mode then the system will check the locks already in existence on
     * the document. If a valid lock for the given user is found the system will return the given edit mode map. If a valid
     * lock is found but is owned by another user the edit mode map returned will have any 'entry type' edit modes removed. If the
     * given document has no locks and the edit mode map passed in has at least one 'entry type' mode then a new
     * {@link PessimisticLock} object will be created and set on the document for the given user.<br>
     * <br>
     * NOTE: This method is only called if the document uses pessimistic locking as described in the data dictionary file.
     *
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#establishLocks(org.kuali.rice.krad.document.Document,
     *      java.util.Map, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
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
                lockDescriptorUsers.get(lock.getLockDescriptor()).add(lock.getOwnedByUser());
            }
        }

        // verify that no locks held by current user exist for any other user
        for (String givenUserLockDescriptor : givenUserLockDescriptors) {
            if ( (lockDescriptorUsers.containsKey(givenUserLockDescriptor)) && (lockDescriptorUsers.get(givenUserLockDescriptor).size() > 0) ) {
                Set<Person> users = lockDescriptorUsers.get(givenUserLockDescriptor);
                if ( (users.size() != 1) || (!getWorkflowPessimisticLockOwnerUser().getPrincipalId().equals(users.iterator().next().getPrincipalId())) ) {
                    String descriptorText = (document.useCustomLockDescriptors()) ? " using lock descriptor '" + givenUserLockDescriptor + "'" : "";
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
                if (document.useCustomLockDescriptors()) {
                    // check to see if the custom lock descriptor is already in use
                    String customLockDescriptor = document.getCustomLockDescriptor(user);
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
            if (document.useCustomLockDescriptors()) {
                // get the custom lock descriptor and check to see if if the given user has a lock with that descriptor
                String customLockDescriptor = document.getCustomLockDescriptor(user);
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
     *         {@link #getEntryEditModeReplacementMode} method
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
    		return ( StringUtils.equalsIgnoreCase(KRADConstants.KUALI_DEFAULT_TRUE_VALUE, fullEntryEditModeValue) );
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
        editMode.put(AuthorizationConstants.EditMode.VIEW_ONLY, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        return editMode;
    }

    /**
     * This method creates a new {@link PessimisticLock} object using the given document and user. If the document's
     * useCustomLockDescriptors() method returns true then the new lock will also have a custom lock descriptor
     * value set to the return value of the document's getCustomLockDescriptor(Person) method.
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
        if (document.useCustomLockDescriptors()) {
            return generateNewLock(document.getDocumentNumber(), document.getCustomLockDescriptor(user), user);
        } else {
            return generateNewLock(document.getDocumentNumber(), user);
        }
    }

    public PersonService getPersonService() {
        if ( personService == null ) {
            personService = KimApiServiceLocator.getPersonService();
        }
        return personService;
    }

	public DataDictionaryService getDataDictionaryService() {
		return dataDictionaryService;
	}

	@Required
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	public PermissionService getPermissionService() {
        if ( permissionService == null ) {
        	permissionService = KimApiServiceLocator.getPermissionService();
        }
		return permissionService;
	}


	@Required
    public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}
}

