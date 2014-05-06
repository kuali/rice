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
package org.kuali.rice.krad.bo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Index;

/*
 * Defines methods a business object should implement.
 */
@IdClass(SessionDocumentId.class)
@Entity
@Table(name="KRNS_SESN_DOC_T",uniqueConstraints= {
        @UniqueConstraint(name="KRNS_SESN_DOC_TC0",columnNames="OBJ_ID")
})
public class SessionDocument extends DataObjectBase {

	private static final long serialVersionUID = 2866566562262830639L;

    @Id
    @Column(name="SESN_DOC_ID",length=40)
    protected String sessionId;
    @Id
    @Column(name="DOC_HDR_ID",length=14)
    protected String documentNumber;
    @Id
    @Column(name="PRNCPL_ID",length=40)
    protected String principalId;
    @Id
    @Column(name="IP_ADDR",length=60)
    protected String ipAddress;

    @Column(name="LAST_UPDT_DT")
    @Index(name="KRNS_SESN_DOC_TI1")
	protected Timestamp lastUpdatedDate;
	@Lob
	@Column(name="SERIALZD_DOC_FRM")
	protected byte[] serializedDocumentForm;

	@Column(name="CONTENT_ENCRYPTED_IND",length=1)
	protected Boolean encrypted = false;

	/**
	 * @return the serializedDocumentForm
	 */
	public byte[] getSerializedDocumentForm() {
		return this.serializedDocumentForm;
	}

	/**
	 * @param serializedDocumentForm the serializedDocumentForm to set
	 */
	public void setSerializedDocumentForm(byte[] serializedDocumentForm) {
		this.serializedDocumentForm = serializedDocumentForm;
	}


	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	/**
	 * @return the lastUpdatedDate
	 */
	public Timestamp getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	/**
	 * @param lastUpdatedDate the lastUpdatedDate to set
	 */
	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
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
	 * @return the principalId
	 */
	public String getPrincipalId() {
		return this.principalId;
	}

	/**
	 * @param principalId the principalId to set
	 */
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public boolean isEncrypted() {
		return this.encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

}
