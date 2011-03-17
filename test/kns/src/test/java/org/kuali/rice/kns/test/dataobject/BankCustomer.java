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
package org.kuali.rice.kns.test.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test object
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BankCustomer {

	private int bankId;
	private int customerId;
	private String customerName;
	private Date customerCreateDate;
	private boolean active;

	private Bank bank;

	private List<BankAccount> accounts;

	private Map<String, String> customerAttributes;
	private Map<String, BankAccount> appliedAccounts;

	public BankCustomer() {
		accounts = new ArrayList<BankAccount>();
		customerAttributes = new HashMap<String, String>();
		appliedAccounts = new HashMap<String, BankAccount>();
	}

	public int getBankId() {
		return this.bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getCustomerCreateDate() {
		return this.customerCreateDate;
	}

	public void setCustomerCreateDate(Date customerCreateDate) {
		this.customerCreateDate = customerCreateDate;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Bank getBank() {
		return this.bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public List<BankAccount> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(List<BankAccount> accounts) {
		this.accounts = accounts;
	}

	public Map<String, String> getCustomerAttributes() {
		return this.customerAttributes;
	}

	public void setCustomerAttributes(Map<String, String> customerAttributes) {
		this.customerAttributes = customerAttributes;
	}

	public Map<String, BankAccount> getAppliedAccounts() {
		return this.appliedAccounts;
	}

	public void setAppliedAccounts(Map<String, BankAccount> appliedAccounts) {
		this.appliedAccounts = appliedAccounts;
	}

}
