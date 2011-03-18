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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.reference.EntityType;
import org.kuali.rice.kim.bo.reference.impl.EntityTypeImpl;

import javax.persistence.*;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@Entity
@IdClass(KimEntityEntityTypeId.class)
@Table(name = "KRIM_ENTITY_ENT_TYP_T")
public class KimEntityEntityTypeImpl extends KimInactivatableEntityDataBase implements KimEntityEntityType {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	@Id
	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;
	 
	@ManyToOne(targetEntity = EntityTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "ENT_TYP_CD", insertable = false, updatable = false)
	protected EntityType entityType;
	
	@OneToMany(targetEntity = KimEntityEmailImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumns({
	    @JoinColumn(name="ENTITY_ID", insertable = false, updatable = false), 
	    @JoinColumn(name="ENT_TYP_CD", insertable = false, updatable = false)
	})
	protected List<KimEntityEmail> emailAddresses;// = new AutoPopulatingList(KimEntityEmailImpl.class);
	
	@OneToMany(targetEntity = KimEntityPhoneImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumns({
	    @JoinColumn(name="ENTITY_ID", insertable = false, updatable = false), 
	    @JoinColumn(name="ENT_TYP_CD", insertable = false, updatable = false)
	})
	protected List<KimEntityPhone> phoneNumbers;// = new AutoPopulatingList(KimEntityPhoneImpl.class);
	
	@OneToMany(targetEntity = KimEntityAddressImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumns({
	    @JoinColumn(name="ENTITY_ID", insertable = false, updatable = false), 
	    @JoinColumn(name="ENT_TYP_CD", insertable = false, updatable = false)
	})
	protected List<KimEntityAddress> addresses;// = new AutoPopulatingList(KimEntityAddressImpl.class);
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getAddresses()
	 */
	public List<KimEntityAddress> getAddresses() {
		return addresses;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEmailAddresses()
	 */
	public List<KimEntityEmail> getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getPhoneNumbers()
	 */
	public List<KimEntityPhone> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEntityType()
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
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

	public void setEmailAddresses(List<KimEntityEmail> entityEmailAddresses) {
		this.emailAddresses = entityEmailAddresses;
	}

	public void setPhoneNumbers(List<KimEntityPhone> entityPhoneNumbers) {
		this.phoneNumbers = entityPhoneNumbers;
	}

	public void setAddresses(List<KimEntityAddress> entityAddresses) {
		this.addresses = entityAddresses;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultAddress()
	 */
	public KimEntityAddress getDefaultAddress() {
		return (KimEntityAddress)getDefaultItem( addresses );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultEmailAddress()
	 */
	public KimEntityEmail getDefaultEmailAddress() {
		return (KimEntityEmail)getDefaultItem( emailAddresses );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultPhoneNumber()
	 */
	public KimEntityPhone getDefaultPhoneNumber() {
		return (KimEntityPhone)getDefaultItem( phoneNumbers );
	}
}
