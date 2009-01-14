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

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.entity.EntityEmail;
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.EntityPhone;
import org.kuali.rice.kim.bo.reference.EntityType;
import org.kuali.rice.kim.bo.reference.impl.EntityTypeImpl;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_ENT_TYP_T")
public class EntityEntityTypeImpl extends InactivatableEntityDataBase implements EntityEntityType {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Id
	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;
	 
	@ManyToOne(targetEntity = EntityTypeImpl.class, fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "ENT_TYP_CD", insertable = false, updatable = false)
	protected EntityType entityType;
	
	@OneToMany(targetEntity = EntityEmailImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityEmail> emailAddresses = new TypedArrayList(EntityEmailImpl.class);
	
	@OneToMany(targetEntity = EntityPhoneImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityPhone> phoneNumbers = new TypedArrayList(EntityPhoneImpl.class);
	
	@OneToMany(targetEntity = EntityAddressImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityAddress> addresses = new TypedArrayList(EntityAddressImpl.class);
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getAddresses()
	 */
	public List<EntityAddress> getAddresses() {
		return addresses;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getEmailAddresses()
	 */
	public List<EntityEmail> getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getPhoneNumbers()
	 */
	public List<EntityPhone> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getEntityType()
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "entityId", entityId );
		m.put( "entityTypeCode", entityTypeCode );
		m.put( "active", active );
		return m;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	public void setEmailAddresses(List<EntityEmail> entityEmailAddresses) {
		this.emailAddresses = entityEmailAddresses;
	}

	public void setPhoneNumbers(List<EntityPhone> entityPhoneNumbers) {
		this.phoneNumbers = entityPhoneNumbers;
	}

	public void setAddresses(List<EntityAddress> entityAddresses) {
		this.addresses = entityAddresses;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getDefaultAddress()
	 */
	public EntityAddress getDefaultAddress() {
		return (EntityAddress)getDefaultItem( addresses );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getDefaultEmailAddress()
	 */
	public EntityEmail getDefaultEmailAddress() {
		return (EntityEmail)getDefaultItem( emailAddresses );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.EntityEntityType#getDefaultPhoneNumber()
	 */
	public EntityPhone getDefaultPhoneNumber() {
		return (EntityPhone)getDefaultItem( phoneNumbers );
	}
}
