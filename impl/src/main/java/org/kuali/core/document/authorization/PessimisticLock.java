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
package org.kuali.core.document.authorization;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.rice.KNSServiceLocator;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PessimisticLock extends PersistableBusinessObjectBase {
    
    private static final long serialVersionUID = -5210762282545093555L;
    
    public static final String DEFAUL_LOCK_DESCRIPTOR = null;
    
    // id is sequence number and primary key
    private Long id;
    // how does below merge with KIM?
    private String ownedByPersonUniversalIdentifier;
    private String lockDescriptor; // should this be defaulted to something or 'null means whole doc'
    private Timestamp generatedTimestamp;
    private String documentNumber; // foreign key to document

    private UniversalUser ownedByUser;

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
    public PessimisticLock(String documentNumber, String lockDescriptor, UniversalUser user) {
        this.documentNumber = documentNumber;
        this.ownedByPersonUniversalIdentifier = user.getPersonUniversalIdentifier();
        this.lockDescriptor = lockDescriptor;  
        this.generatedTimestamp = KNSServiceLocator.getDateTimeService().getCurrentTimestamp();
    }
    
    public boolean isOwnedByUser(UniversalUser user) {
        return user.getPersonUniversalIdentifier().equals(getOwnedByPersonUniversalIdentifier());
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
     * @return the ownedByPersonUniversalIdentifier
     */
    public String getOwnedByPersonUniversalIdentifier() {
        return this.ownedByPersonUniversalIdentifier;
    }

    /**
     * @param ownedByPersonUniversalIdentifier the ownedByPersonUniversalIdentifier to set
     */
    public void setOwnedByPersonUniversalIdentifier(String ownedByPersonUniversalIdentifier) {
        this.ownedByPersonUniversalIdentifier = ownedByPersonUniversalIdentifier;
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
    public UniversalUser getOwnedByUser() {
        ownedByUser = KNSServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(ownedByPersonUniversalIdentifier, ownedByUser);
        return ownedByUser;
    }

    /**
     * @param ownedByUser the ownedByUser to set
     */
    public void setOwnedByUser(UniversalUser ownedByUser) {
        this.ownedByUser = ownedByUser;
    }

    /**
     * This helper method used to define fields and field values to use
     * in toString() method
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("id", this.id);
        m.put("ownedByPersonUniversalIdentifier", this.ownedByPersonUniversalIdentifier);
        m.put("lockDescriptor", this.lockDescriptor);
        m.put("generatedTimestamp", this.generatedTimestamp);
        m.put("documentNumber", this.documentNumber);
        return m;
    }

}
