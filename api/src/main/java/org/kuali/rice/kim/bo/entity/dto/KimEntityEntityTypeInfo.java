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
package org.kuali.rice.kim.bo.entity.dto;

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.getDefaultAndUnNullify;
import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.reference.EntityType;

/**
 * Contains entity type info.  Plus, contains addresses, emails, and phone information.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEntityTypeInfo extends KimInactivatableInfo implements KimEntityEntityType {

	private static final long serialVersionUID = 1L;
	
	private List<KimEntityAddress> addresses;
	private List<KimEntityEmail> emailAddresses;
	private EntityType entityType;
	private String entityTypeCode;
	private List<KimEntityPhone> phoneNumbers;
	
	/**
	 * Gets all the entity addresses.
	 * 
	 * @return the entity addresses
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getAddresses()
	 */
	public List<KimEntityAddress> getAddresses() {
		return unNullify(this.addresses);
	}

	/**
	 * Sets the entity addresses.
	 * 
	 * @param addresses the entity addresses
	 */
	public void setAddresses(List<KimEntityAddress> addresses) {
		this.addresses = addresses;
	}
	
	/**
	 * Gets the entity default address.
	 * 
	 * @return the entity default address
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultAddress()
	 */
	public KimEntityAddress getDefaultAddress() {
		return getDefaultAndUnNullify(this.addresses, KimEntityAddressInfo.class);
	}

	/**
	 * Gets the entity default email address.
	 * 
	 * @return the entity default email address
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultEmailAddress()
	 */
	public KimEntityEmail getDefaultEmailAddress() {
		return getDefaultAndUnNullify(this.emailAddresses, KimEntityEmailInfo.class);
	}

	/**
	 * Gets the entity default phone number.
	 * 
	 * @return the entity default phone number
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultPhoneNumber()
	 */
	public KimEntityPhone getDefaultPhoneNumber() {
		return getDefaultAndUnNullify(this.phoneNumbers, KimEntityPhoneInfo.class);
	}

	/**
	 * Gets all the entity email addresses.
	 * 
	 * @return the entity email addresses
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEmailAddresses()
	 */
	public List<KimEntityEmail> getEmailAddresses() {
		return unNullify(this.emailAddresses);
	}

	/**
	 * Sets the entity email addresses.
	 * 
	 * @param emailAddresses the entity email addresses
	 */
	public void setEmailAddresses(List<KimEntityEmail> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	
	/**
	 * Gets the entity type.
	 * 
	 * @return the entity type
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEntityType()
	 */
	public EntityType getEntityType() {
		return unNullify(this.entityType, EntityTypeInfo.class);
	}

	/**
	 * Sets the entity type.
	 * 
	 * @param entityType the entity type
	 */
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	
	/**
	 * Gets the entity type code.
	 * 
	 * @return the entity type code
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return unNullify(this.entityTypeCode);
	}

	/**
	 * Sets the entity type code.
	 * 
	 * @param entityTypeCode the entity type code
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	
	/**
	 * Gets all the entity phone numbers.
	 * 
	 * @return the entity phone numbers
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getPhoneNumbers()
	 */
	public List<KimEntityPhone> getPhoneNumbers() {
		return unNullify(this.phoneNumbers);
	}

	/**
	 * Sets the entity phone numbers.
	 * 
	 * @param phoneNumbers the entity phone numbers
	 */
	public void setPhoneNumbers(List<KimEntityPhone> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
}
