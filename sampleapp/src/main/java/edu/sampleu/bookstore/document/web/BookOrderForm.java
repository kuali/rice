package edu.sampleu.bookstore.document.web;

import edu.sampleu.bookstore.bo.BookOrder;
import edu.sampleu.bookstore.document.BookOrderDocument;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/*
 * BookOrderForm class file for BookOrder maintenance object
 */
public class BookOrderForm extends KualiTransactionalDocumentFormBase {

	private static final long serialVersionUID = -206564464059467788L;

	private BookOrder newBookOrder;

	public BookOrder getNewBookOrder() {
		return newBookOrder;
	}

	public void setNewBookOrder(BookOrder newBookOrder) {
		this.newBookOrder = newBookOrder;
	}
	
    public BookOrderDocument getBookOrderDocument() {
        return (BookOrderDocument) getDocument();
    }
	
}
