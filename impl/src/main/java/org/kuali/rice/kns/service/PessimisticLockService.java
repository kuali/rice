/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.authorization.PessimisticLock;

/**
 * This is the service interface for documents to use the Pessimistic Locking mechanism 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface PessimisticLockService {
    
    /**
     * This method deletes the given lock object
     * 
     * @param id - the id of the lock to delete
     */
    public void delete(String id);
    
    /**
     * This method will generate a new {@link PessimisticLock} object with a 'document'
     * lock descriptor
     * 
     * @param documentNumber - the document number of the document associated with the new lock
     * @return the newly generated document descriptor {@link PessimisticLock}
     */
    public PessimisticLock generateNewLock(String documentNumber);

    /**
     * This method will generate a new {@link PessimisticLock} object with a lock descriptor of
     * the given parameter
     * 
     * @param documentNumber - the document number of the document associated with the new lock
     * @param lockDescriptor - the lock descriptor the new PessimisticLock object should contain
     * @return the newly generated {@link PessimisticLock} containing the given lockDescriptor
     */
    public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor);
    
    /**
     * This method will generate a new {@link PessimisticLock} object with a 'document'
     * lock descriptor
     * 
     * @param documentNumber - the document number of the document associated with the new lock
     * @param user - the user to set on the new lock being generated
     * @return the newly generated document descriptor {@link PessimisticLock}
     */
    public PessimisticLock generateNewLock(String documentNumber, Person user);

    /**
     * This method will generate a new {@link PessimisticLock} object with a lock descriptor of
     * the given parameter
     * 
     * @param documentNumber - the document number of the document associated with the new lock
     * @param lockDescriptor - the lock descriptor the new PessimisticLock object should contain
     * @param user - the user to set on the new lock being generated
     * @return the newly generated {@link PessimisticLock} containing the given lockDescriptor
     */
    public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor, Person user);
    
    /**
     * This method gets all locks associated with the given document number
     * 
     * @param documentNumber - the document number of the document requiring locks
     * @return an empty list if no locks are found or the list of {@link PessimisticLock} objects
     * found for the given documentNumber
     */
    public List<PessimisticLock> getPessimisticLocksForDocument(String documentNumber);
    
    /**
     * This method is used to identify who is an admin user for {@link PessimisticLock} objects
     * 
     * @param user - user to verify as admin
     * @return true if the given use is an admin user or false if not
     */
    public boolean isPessimisticLockAdminUser(Person user);
    
    /**
     * This method will release all locks in the given list that are owned by the given user
     * 
     * @param locks - locks to release if owned by given user
     * @param user - user to check for lock ownership
     */
    public void releaseAllLocksForUser(List<PessimisticLock> locks, Person user);

    /**
     * This method will release all locks in the given list that are owned by the given user that have a matching lock
     * descriptor value
     * 
     * @param locks - locks to release if owned by given user
     * @param user - user to check for lock ownership
     * @param lockDescriptor - lock descriptor value to match locks against
     */
    public void releaseAllLocksForUser(List<PessimisticLock> locks, Person user, String lockDescriptor);

    /**
     * This method saves the given lock object
     * 
     */
    public void save(PessimisticLock lock);
}

