/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.reference.dto.EntityTypeInfo;

/**
 * Contains entity type info.  Plus, contains addresses, emails, and phone information.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEntityTypeInfo extends KimInactivatableInfo implements KimEntityEntityType {

	private static final long serialVersionUID = -6849727371078726861L;

	private List<KimEntityAddressInfo> addresses;
	private KimEntityAddressInfo defaultAddress;
	private List<KimEntityEmailInfo> emailAddresses;
	private KimEntityEmailInfo defaultEmailAddress;
	private EntityTypeInfo entityType;
	private String entityTypeCode = "";
	private List<KimEntityPhoneInfo> phoneNumbers;
	private KimEntityPhoneInfo defaultPhoneNumber;

	public KimEntityEntityTypeInfo() {
		super();
		active = true;
	}

	public KimEntityEntityTypeInfo(KimEntityEntityType kimEntityEntityType) {
		this();
		if ( kimEntityEntityType != null ) {
			active = kimEntityEntityType.isActive();
			entityTypeCode = kimEntityEntityType.getEntityTypeCode();
			if (kimEntityEntityType.getEntityType() != null) {
			    entityType = new EntityTypeInfo(kimEntityEntityType.getEntityType());
			}
			if (kimEntityEntityType.getAddresses() != null) {
				addresses = new ArrayList<KimEntityAddressInfo>( kimEntityEntityType.getAddresses().size() );
				for ( KimEntityAddress p : kimEntityEntityType.getAddresses() ) if (p != null) {
					addresses.add( new KimEntityAddressInfo( p ) );
				}
			}
			if (kimEntityEntityType.getDefaultAddress() != null) {
			    defaultAddress = new KimEntityAddressInfo(kimEntityEntityType.getDefaultAddress());
			}
			if (kimEntityEntityType.getEmailAddresses() != null) {
				emailAddresses = new ArrayList<KimEntityEmailInfo>( kimEntityEntityType.getEmailAddresses().size() );
				for ( KimEntityEmail p : kimEntityEntityType.getEmailAddresses() ) if (p != null) {
					emailAddresses.add( new KimEntityEmailInfo( p ) );
				}
			}
			if (kimEntityEntityType.getDefaultEmailAddress() != null) {
			    defaultEmailAddress = new KimEntityEmailInfo(kimEntityEntityType.getDefaultEmailAddress());
			}
			if (kimEntityEntityType.getPhoneNumbers() != null) {
				phoneNumbers = new ArrayList<KimEntityPhoneInfo>( kimEntityEntityType.getPhoneNumbers().size() );
				for ( KimEntityPhone p : kimEntityEntityType.getPhoneNumbers() ) if (p != null) {
					phoneNumbers.add( new KimEntityPhoneInfo( p ) );
				}
			}
			if (kimEntityEntityType.getDefaultPhoneNumber() != null) {
			    defaultPhoneNumber = new KimEntityPhoneInfo(kimEntityEntityType.getDefaultPhoneNumber());
			}
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getAddresses()
	 */
	public List<KimEntityAddressInfo> getAddresses() {
	    // assign and return an empty collection if our reference is null.
		return (addresses != null) ? addresses : (addresses = new ArrayList<KimEntityAddressInfo>());
	}

	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(List<KimEntityAddressInfo> addresses) {
		this.addresses = addresses;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultAddress()
	 */
	public KimEntityAddressInfo getDefaultAddress() {
		return defaultAddress;
	}

	/**
	 * @param defaultAddress the defaultAddress to set
	 */
	public void setDefaultAddress(KimEntityAddressInfo defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEmailAddresses()
	 */
	public List<KimEntityEmailInfo> getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * @param emailAddresses the emailAddresses to set
	 */
	public void setEmailAddresses(List<KimEntityEmailInfo> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultEmailAddress()
	 */
	public KimEntityEmailInfo getDefaultEmailAddress() {
		return defaultEmailAddress;
	}

	/**
	 * @param defaultEmailAddress the defaultEmailAddress to set
	 */
	public void setDefaultEmailAddress(KimEntityEmailInfo defaultEmailAddress) {
		this.defaultEmailAddress = defaultEmailAddress;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEntityType()
	 */
	public EntityTypeInfo getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(EntityTypeInfo entityType) {
		this.entityType = entityType;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getPhoneNumbers()
	 */
	public List<KimEntityPhoneInfo> getPhoneNumbers() {
	    // assign and return an empty collection if our reference is null.
        return (phoneNumbers != null) ? phoneNumbers : (phoneNumbers = new ArrayList<KimEntityPhoneInfo>());
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<KimEntityPhoneInfo> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEntityType#getDefaultPhoneNumber()
	 */
	public KimEntityPhoneInfo getDefaultPhoneNumber() {
		return defaultPhoneNumber;
	}

	/**
	 * @param defaultPhoneNumber the defaultPhoneNumber to set
	 */
	public void setDefaultPhoneNumber(KimEntityPhoneInfo defaultPhoneNumber) {
		this.defaultPhoneNumber = defaultPhoneNumber;
	}

}
