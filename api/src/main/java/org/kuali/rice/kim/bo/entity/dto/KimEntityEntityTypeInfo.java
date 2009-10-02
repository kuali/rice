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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

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
			this.active = kimEntityEntityType.isActive();
			this.entityTypeCode = unNullify(kimEntityEntityType.getEntityTypeCode());
			this.entityType = unNullify(new EntityTypeInfo(kimEntityEntityType.getEntityType()), EntityTypeInfo.class);
			if (kimEntityEntityType.getAddresses() == null) {
				this.addresses = new ArrayList<KimEntityAddressInfo>();
			}
			else {
				this.addresses = new ArrayList<KimEntityAddressInfo>( kimEntityEntityType.getAddresses().size() );
				for ( KimEntityAddress p : kimEntityEntityType.getAddresses() ) {
					this.addresses.add( new KimEntityAddressInfo( p ) );
				}
			}
			this.defaultAddress = unNullify(new KimEntityAddressInfo(kimEntityEntityType.getDefaultAddress()), KimEntityAddressInfo.class);
			if (kimEntityEntityType.getEmailAddresses() == null) {
				this.emailAddresses = new ArrayList<KimEntityEmailInfo>();
			}
			else {
				this.emailAddresses = new ArrayList<KimEntityEmailInfo>( kimEntityEntityType.getEmailAddresses().size() );
				for ( KimEntityEmail p : kimEntityEntityType.getEmailAddresses() ) {
					this.emailAddresses.add( new KimEntityEmailInfo( p ) );
				}
			}
			this.defaultEmailAddress = unNullify(new KimEntityEmailInfo(kimEntityEntityType.getDefaultEmailAddress()), KimEntityEmailInfo.class);
			if (kimEntityEntityType.getPhoneNumbers() == null) {
				this.phoneNumbers = new ArrayList<KimEntityPhoneInfo>();
			}
			else {
				this.phoneNumbers = new ArrayList<KimEntityPhoneInfo>( kimEntityEntityType.getPhoneNumbers().size() );
				for ( KimEntityPhone p : kimEntityEntityType.getPhoneNumbers() ) {
					this.phoneNumbers.add( new KimEntityPhoneInfo( p ) );
				}
			}
			this.defaultPhoneNumber = unNullify(new KimEntityPhoneInfo(kimEntityEntityType.getDefaultPhoneNumber()), KimEntityPhoneInfo.class);
		}
	}

	/**
	 * @return the addresses
	 */
	public List<KimEntityAddressInfo> getAddresses() {
		return unNullify(this.addresses);
	}

	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(List<KimEntityAddressInfo> addresses) {
		this.addresses = addresses;
	}

	/**
	 * @return the defaultAddress
	 */
	public KimEntityAddressInfo getDefaultAddress() {
		return unNullify(this.defaultAddress, KimEntityAddressInfo.class);
	}

	/**
	 * @param defaultAddress the defaultAddress to set
	 */
	public void setDefaultAddress(KimEntityAddressInfo defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	/**
	 * @return the emailAddresses
	 */
	public List<KimEntityEmailInfo> getEmailAddresses() {
		return unNullify(this.emailAddresses);
	}

	/**
	 * @param emailAddresses the emailAddresses to set
	 */
	public void setEmailAddresses(List<KimEntityEmailInfo> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	/**
	 * @return the defaultEmailAddress
	 */
	public KimEntityEmailInfo getDefaultEmailAddress() {
		return unNullify(this.defaultEmailAddress, KimEntityEmailInfo.class);
	}

	/**
	 * @param defaultEmailAddress the defaultEmailAddress to set
	 */
	public void setDefaultEmailAddress(KimEntityEmailInfo defaultEmailAddress) {
		this.defaultEmailAddress = defaultEmailAddress;
	}

	/**
	 * @return the entityType
	 */
	public EntityTypeInfo getEntityType() {
		return unNullify(this.entityType, EntityTypeInfo.class);
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(EntityTypeInfo entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return the entityTypeCode
	 */
	public String getEntityTypeCode() {
		return unNullify(this.entityTypeCode);
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * @return the phoneNumbers
	 */
	public List<KimEntityPhoneInfo> getPhoneNumbers() {
		return unNullify(this.phoneNumbers);
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<KimEntityPhoneInfo> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * @return the defaultPhoneNumber
	 */
	public KimEntityPhoneInfo getDefaultPhoneNumber() {
		return unNullify(this.defaultPhoneNumber, KimEntityPhoneInfo.class);
	}

	/**
	 * @param defaultPhoneNumber the defaultPhoneNumber to set
	 */
	public void setDefaultPhoneNumber(KimEntityPhoneInfo defaultPhoneNumber) {
		this.defaultPhoneNumber = defaultPhoneNumber;
	}

}
