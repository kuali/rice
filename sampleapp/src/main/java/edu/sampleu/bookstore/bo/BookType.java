/**
 * 
 */
package edu.sampleu.bookstore.bo;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import java.util.ArrayList;
import java.util.List;

/**
 * BookType Business Object class file relative to BookType maintenance Object.
 */

public class BookType extends PersistableBusinessObjectBase implements MutableInactivatable {
	
	
	private static final long serialVersionUID = 8499633675478827977L;
	private String typeCode;
	private String name;
	private String description;
	private boolean active = true;
	
    
    private List<Book> books = new ArrayList<Book>();


	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
 

}
