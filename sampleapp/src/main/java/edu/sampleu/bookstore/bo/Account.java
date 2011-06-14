package edu.sampleu.bookstore.bo;

import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

/**
 * Account Business Object extension of Author Business Object.
 */

public class Account extends PersistableBusinessObjectExtensionBase {

	private static final long serialVersionUID = -8624654503247320725L;

	private Long authorId;
	private String bankName;
	private String accountNumber;

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
 

}
