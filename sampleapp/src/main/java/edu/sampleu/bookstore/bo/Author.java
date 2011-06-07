package edu.sampleu.bookstore.bo;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Author Business Object class file for Author maintenance object.
 */

public class Author extends PersistableBusinessObjectBase implements Inactivateable{

	private static final long serialVersionUID = -4883752918652513985L;
	private Long authorId;
	private String authorName;
	private String email;
	private String phoneNbr;
	private boolean active = true;

	private List<Address> addresses = new ArrayList<Address>();

	private List<Book> books = new ArrayList<Book>();

	// private Account account;
	//	
	// public Account getAccount() {
	// return account;
	// }
	//
	// public void setAccount(Account account) {
	// this.account = account;
	// }

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNbr() {
		return phoneNbr;
	}

	public void setPhoneNbr(String phoneNbr) {
		this.phoneNbr = phoneNbr;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	 

}
