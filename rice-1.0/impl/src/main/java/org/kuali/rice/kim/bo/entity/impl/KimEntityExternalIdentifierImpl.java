/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.reference.ExternalIdentifierType;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_EXT_ID_T")
public class KimEntityExternalIdentifierImpl extends KimEntityDataBase implements KimEntityExternalIdentifier {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimEntityExternalIdentifierImpl.class);

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_EXT_ID_ID")
	protected String entityExternalIdentifierId;

	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	@Column(name = "EXT_ID_TYP_CD")
	protected String externalIdentifierTypeCode;

	@Column(name = "EXT_ID")
	protected String externalId;
	
	@ManyToOne(targetEntity=KimEntityEntityTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "EXT_ID_TYP_CD", insertable = false, updatable = false)
	protected ExternalIdentifierType externalIdentifierType;

	@Transient protected ExternalIdentifierType cachedExtIdType = null;	
	@Transient protected boolean encryptionRequired = false;

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#getEntityExternalIdentifierId()
	 */
	public String getEntityExternalIdentifierId() {
		return entityExternalIdentifierId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#getExternalId()
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#getExternalIdentifierTypeCode()
	 */
	public String getExternalIdentifierTypeCode() {
		return externalIdentifierTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#setExternalId(java.lang.String)
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#setExternalIdentifierTypeCode(java.lang.String)
	 */
	public void setExternalIdentifierTypeCode(String externalIdentifierTypeCode) {
		this.externalIdentifierTypeCode = externalIdentifierTypeCode;
		cachedExtIdType = null;		
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "entityExternalIdentifierId", entityExternalIdentifierId );
		m.put( "externalIdentifierTypeCode", externalIdentifierTypeCode );
		m.put( "externalId", externalId );		
		return m;
	}

	public void setEntityExternalIdentifierId(String entityExternalIdentifierId) {
		this.entityExternalIdentifierId = entityExternalIdentifierId;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public ExternalIdentifierType getExternalIdentifierType() {
		return this.externalIdentifierType;
	}

	public void setExternalIdentifierType(ExternalIdentifierType externalIdentifierType) {
		this.externalIdentifierType = externalIdentifierType;
		cachedExtIdType = null;	
	}
	
    @Override
	public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
		super.beforeInsert(persistenceBroker);
		encryptExternalId();
	}
	
	@Override
	public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
	    beforeUpdate();
	}
	
	@Override
	public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.afterLookup(persistenceBroker);
        decryptExternalId();
	}
	
	@Override
	@PrePersist
	public void beforeInsert() {
		super.beforeInsert();
		encryptExternalId();
    }

	@Override
	@PreUpdate
	public void beforeUpdate() {
		super.beforeUpdate();
		encryptExternalId();
	}
	
	@PostLoad 
	public void afterLookup(){
		decryptExternalId();
	}
	
	protected void evaluateExternalIdentifierType() {
		if ( cachedExtIdType == null ) {
			Map<String, String> criteria = new HashMap<String, String>();
		    criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_CODE, externalIdentifierTypeCode);
		    cachedExtIdType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		    encryptionRequired = cachedExtIdType!= null && cachedExtIdType.isEncryptionRequired(); 
		}
	}
	
	protected void encryptExternalId() {
		evaluateExternalIdentifierType();
		if ( encryptionRequired && StringUtils.isNotEmpty(externalId) ) {
			try {
				externalId = KNSServiceLocator.getEncryptionService().encrypt(externalId);
			}
			catch ( Exception e ) {
				LOG.info("Unable to encrypt value : " + e.getMessage() + " or it is already encrypted");
			}				
		}
	}
	
	protected void decryptExternalId() {
		evaluateExternalIdentifierType();
		if ( encryptionRequired && StringUtils.isNotEmpty(externalId) ) {
			try {
				externalId = KNSServiceLocator.getEncryptionService().decrypt(externalId);
			}
			catch ( Exception e ) {
				LOG.info("Unable to decrypt value : " + e.getMessage() + " or it is already decrypted");
	        }
		}
    }	
}
