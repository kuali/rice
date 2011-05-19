package edu.sampleu.bookstore.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.document.TransactionalDocumentBase;
import edu.sampleu.bookstore.bo.BookOrder;

/*
 * Transactional Document class file for Book Order.
 */

public class BookOrderDocument extends TransactionalDocumentBase {

	private static final long serialVersionUID = -1856169002927442467L;

	private List<BookOrder> bookOrders = new ArrayList<BookOrder>();

	public List<BookOrder> getBookOrders() {
		return bookOrders;
	}

	public void setBookOrders(List<BookOrder> bookOrders) {
		this.bookOrders = bookOrders;
	}
	
	public void addBookOrder(BookOrder bookOrder) {
		bookOrder.setDocumentId(getDocumentNumber());
		bookOrders.add(bookOrder);
    }
	
	public void removeBookOrder(int deleteIndex) {
		bookOrders.remove(deleteIndex);
	}
	
}
