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
package org.kuali.rice.kns.document;

import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * List of business objects that this maintenance document is locking (prevents two documents from being routed trying to update the same object)
 * Most maintenance documents have only one lock, but globals have many 
 */
@Entity
@Table(name="KRNS_MAINT_LOCK_T")
public class MaintenanceLock extends PersistableBusinessObjectBase {
    @Id
	@Column(name="MAINT_LOCK_REP_TXT")
	private String lockingRepresentation;
    @Column(name="DOC_HDR_ID")
	private String documentNumber;

    public String getLockingRepresentation() {
        return lockingRepresentation;
    }

    public void setLockingRepresentation(String lockingRepresentation) {
        this.lockingRepresentation = lockingRepresentation;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("lockingRepresentation", this.lockingRepresentation);
        m.put(KNSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        return m;
    }
}

