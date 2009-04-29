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
package org.kuali.rice.kim.bo.entity.impl;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.reference.ExternalIdentifierType;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.Guid;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_EXT_ID_T")
public class KimEntityExternalIdentifierImpl extends KimEntityDataBase implements KimEntityExternalIdentifier {

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
	}
	
	public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
		super.beforeInsert(persistenceBroker);
		Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode);
	    ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierType!= null && externalIdentifierType.isEncryptionRequired()){
			if(this.externalId != null){
				try{
					externalId = KNSServiceLocator.getEncryptionService().encrypt(externalId);
				}catch (GeneralSecurityException e) {
		            throw new RuntimeException("Unable to encrypt value : " + e.getMessage());
		        }

			}
		}
		
	}
	
	public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
		super.beforeUpdate(persistenceBroker);
		Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode);
	    ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierType!= null && externalIdentifierType.isEncryptionRequired()){
			if(this.externalId != null){
				try{
					externalId = KNSServiceLocator.getEncryptionService().encrypt(externalId);
				}catch (GeneralSecurityException e) {
		            throw new RuntimeException("Unable to encrypt value : " + e.getMessage());
		        }

			}
		}
	}
	
	public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.afterLookup(persistenceBroker);
        Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode);
	    ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierType!= null && externalIdentifierType.isEncryptionRequired()){
			if(this.externalId != null){
				try{
					externalId = KNSServiceLocator.getEncryptionService().decrypt(externalId);
				}catch (GeneralSecurityException e) {
		            throw new RuntimeException("Unable to decrypt value : " + e.getMessage());
		        }

			}
		}
	}
	
	public void beforeInsert() {
		super.beforeInsert();
		Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode);
	    ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierType!= null && externalIdentifierType.isEncryptionRequired()){
			if(this.externalId != null){
				try{
					externalId = KNSServiceLocator.getEncryptionService().encrypt(externalId);
				}catch (GeneralSecurityException e) {
		            throw new RuntimeException("Unable to encrypt value : " + e.getMessage());
		        }

			}
		}
    }

	public void beforeUpdate() {
		super.beforeUpdate();
		Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode);
	    ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierType!= null && externalIdentifierType.isEncryptionRequired()){
			if(this.externalId != null){
				try{
					externalId = KNSServiceLocator.getEncryptionService().encrypt(externalId);
				}catch (GeneralSecurityException e) {
		            throw new RuntimeException("Unable to encrypt value : " + e.getMessage());
		        }

			}
		}
	}
	
	@javax.persistence.PostLoad 
	public void afterLookup(){
		Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode);
	    ExternalIdentifierType externalIdentifierType = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierType!= null && externalIdentifierType.isEncryptionRequired()){
				if(this.externalId != null){
					try{
						externalId = KNSServiceLocator.getEncryptionService().decrypt(externalId);
					}catch (GeneralSecurityException e) {
			            throw new RuntimeException("Unable to decrypt value : " + e.getMessage());
			        }

				}
			}
	}
}
