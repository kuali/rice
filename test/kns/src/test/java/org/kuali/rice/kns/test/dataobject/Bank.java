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
import java.util.List;

/**
 * Test object
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Bank {

	private int bankId;
	private String bankName;
	private String bankAddress;
	private boolean foreignIndicator;
	private boolean creditIndicator;

	private List<BankCustomer> customers;

	public Bank() {
		customers = new ArrayList<BankCustomer>();
	}

	public int getBankId() {
		return this.bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAddress() {
		return this.bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public boolean isForeignIndicator() {
		return this.foreignIndicator;
	}

	public void setForeignIndicator(boolean foreignIndicator) {
		this.foreignIndicator = foreignIndicator;
	}

	public boolean isCreditIndicator() {
		return this.creditIndicator;
	}

	public void setCreditIndicator(boolean creditIndicator) {
		this.creditIndicator = creditIndicator;
	}

	public List<BankCustomer> getCustomers() {
		return this.customers;
	}

	public void setCustomers(List<BankCustomer> customers) {
		this.customers = customers;
	}

}
