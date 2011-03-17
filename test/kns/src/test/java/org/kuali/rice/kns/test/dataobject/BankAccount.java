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

import java.sql.Timestamp;

import org.kuali.rice.core.util.type.KualiDecimal;

/**
 * Test object
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BankAccount {

	private int accountId;
	private int customerId;
	private String accountType;
	private KualiDecimal accountBalance;
	private KualiDecimal accountLimit;
	private boolean overdue;
	private boolean active;
	private Timestamp accountCreateDate;

	private BankCustomer customer;

	public BankAccount() {

	}

	public int getAccountId() {
		return this.accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public KualiDecimal getAccountBalance() {
		return this.accountBalance;
	}

	public void setAccountBalance(KualiDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public KualiDecimal getAccountLimit() {
		return this.accountLimit;
	}

	public void setAccountLimit(KualiDecimal accountLimit) {
		this.accountLimit = accountLimit;
	}

	public boolean isOverdue() {
		return this.overdue;
	}

	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getAccountCreateDate() {
		return this.accountCreateDate;
	}

	public void setAccountCreateDate(Timestamp accountCreateDate) {
		this.accountCreateDate = accountCreateDate;
	}

	public BankCustomer getCustomer() {
		return this.customer;
	}

	public void setCustomer(BankCustomer customer) {
		this.customer = customer;
	}

}
