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
package org.kuali.rice.krad.maintenance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Index;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * List of business objects that this maintenance document is locking (prevents two documents from being routed trying to update the same object)
 * Most maintenance documents have only one lock, but globals have many
 */
@Entity
@Table(name="KRNS_MAINT_LOCK_T",uniqueConstraints= {
        @UniqueConstraint(name="KRNS_MAINT_LOCK_TC0",columnNames="OBJ_ID")
})
public class MaintenanceLock extends DataObjectBase {

    private static final long serialVersionUID = 7766326835852387301L;

	@Id
    @GeneratedValue(generator = "KRNS_MAINT_LOCK_S")
    @PortableSequenceGenerator(name = "KRNS_MAINT_LOCK_S")
    @Column(name="MAINT_LOCK_ID",length=14)
    private String lockId;

	@Column(name="MAINT_LOCK_REP_TXT",length=500)
	private String lockingRepresentation;

    @Column(name="DOC_HDR_ID",length=14,nullable=false)
    @Index(name="KRNS_MAINT_LOCK_TI2")
	private String documentNumber;

    public String getLockId() {
		return this.lockId;
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}

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

}

