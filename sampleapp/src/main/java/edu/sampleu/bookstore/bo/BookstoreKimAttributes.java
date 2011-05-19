/**
 * 
 */
package edu.sampleu.bookstore.bo;

import java.util.LinkedHashMap;

import org.kuali.rice.kim.bo.impl.KimAttributes;

/**
 * Class to hold all Bookstore application specific KIM attributes
 *
 */
public class BookstoreKimAttributes extends KimAttributes {

	private static final long serialVersionUID = -1095291722496057450L;

	public static final String BOOK_TYPE_CODE = "bookTypeCode";

	protected String bookTypeCode;

	protected BookType bookType;

	 

	public String getBookTypeCode() {
		return bookTypeCode;
	}

	public void setBookTypeCode(String bookTypeCode) {
		this.bookTypeCode = bookTypeCode;
	}

	public BookType getBookType() {
		return bookType;
	}

	public void setBookType(BookType bookType) {
		this.bookType = bookType;
	}

}
