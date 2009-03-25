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
package org.kuali.rice.kns.document.authorization;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a business object used to lock a document pessimistically.
 * Pessimistic locking is more strick than optimistic locking and assumes if a
 * lock exists that a user should only have read-only access to a document. For
 * more information see documentation pages.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class PessimisticLock extends PersistableBusinessObjectBase {
    
    private static final long serialVersionUID = -5210762282545093555L;
    
    public static final String DEFAULT_LOCK_DESCRIPTOR = null;
    
    // id is sequence number and primary key
    private Long id;
    private String ownedByPrincipalIdentifier;
    private String lockDescriptor; // this will be defaulted to the value of DEFAULT_LOCK_DESCRIPTOR constant above
    private Timestamp generatedTimestamp;
    private String documentNumber; // foreign key to document

    private Person ownedByUser;

    /**
     * This constructs an empty lock using the logged in user and default lock descriptor type
     * but will NOT assign a document number.  Use another constructor.
     * @deprecated
     */
    @Deprecated
    public PessimisticLock() {}
    
    /**
     * This constructs a lock object using the logged in user and given lock type
     */
    public PessimisticLock(String documentNumber, String lockDescriptor, Person user) {
        this.documentNumber = documentNumber;
        this.ownedByPrincipalIdentifier = user.getPrincipalId();
        this.lockDescriptor = lockDescriptor;  
        this.generatedTimestamp = KNSServiceLocator.getDateTimeService().getCurrentTimestamp();
    }
    
    public boolean isOwnedByUser(Person user) {
        return user.getPrincipalId().equals(getOwnedByPrincipalIdentifier());
    }
    
    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the ownedByPrincipalIdentifier
     */
    public String getOwnedByPrincipalIdentifier() {
        return this.ownedByPrincipalIdentifier;
    }

    /**
     * @param ownedByPrincipalIdentifier the ownedByPrincipalIdentifier to set
     */
    public void setOwnedByPrincipalIdentifier(String ownedByPrincipalIdentifier) {
        this.ownedByPrincipalIdentifier = ownedByPrincipalIdentifier;
    }

    /**
     * @return the lockDescriptor
     */
    public String getLockDescriptor() {
        return this.lockDescriptor;
    }

    /**
     * @param lockDescriptor the lockDescriptor to set
     */
    public void setLockDescriptor(String lockDescriptor) {
        this.lockDescriptor = lockDescriptor;
    }

    /**
     * @return the generatedTimestamp
     */
    public Timestamp getGeneratedTimestamp() {
        return this.generatedTimestamp;
    }

    /**
     * @param generatedTimestamp the generatedTimestamp to set
     */
    public void setGeneratedTimestamp(Timestamp generatedTimestamp) {
        this.generatedTimestamp = generatedTimestamp;
    }

    /**
     * @return the documentNumber
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }

    /**
     * @param documentNumber the documentNumber to set
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return the ownedByUser
     */
    public Person getOwnedByUser() {
        ownedByUser = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().updatePersonIfNecessary(ownedByPrincipalIdentifier, ownedByUser);
        return ownedByUser;
    }

    /**
     * @param ownedByUser the ownedByUser to set
     */
    public void setOwnedByUser(Person ownedByUser) {
        this.ownedByUser = ownedByUser;
    }

    /**
     * This helper method used to define fields and field values to use
     * in toString() method
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("id", this.id);
        m.put("ownedByPrincipalIdentifier", this.ownedByPrincipalIdentifier);
        m.put("lockDescriptor", this.lockDescriptor);
        m.put("generatedTimestamp", this.generatedTimestamp);
        m.put("documentNumber", this.documentNumber);
        return m;
    }

}

