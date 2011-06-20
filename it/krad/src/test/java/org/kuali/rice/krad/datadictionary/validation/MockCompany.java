/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation;

import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockCompany {

	private String name;
	private List<String> contactEmails;
	private List<MockAddress> addresses;
	
	
	public MockCompany(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the addresses
	 */
	public List<MockAddress> getAddresses() {
		return this.addresses;
	}
	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(List<MockAddress> addresses) {
		this.addresses = addresses;
	}

	/**
	 * @return the contactEmails
	 */
	public List<String> getContactEmails() {
		return this.contactEmails;
	}

	/**
	 * @param contactEmails the contactEmails to set
	 */
	public void setContactEmails(List<String> contactEmails) {
		this.contactEmails = contactEmails;
	}
	
}
