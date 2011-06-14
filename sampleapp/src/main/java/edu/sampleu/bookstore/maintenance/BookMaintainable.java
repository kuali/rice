package edu.sampleu.bookstore.maintenance;

import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.service.KRADServiceLocator;
import edu.sampleu.bookstore.bo.Account;
import edu.sampleu.bookstore.bo.Author;
import edu.sampleu.bookstore.bo.Book;

/**
 * maintainableClass for Book document. 
 * Action to be taken before saving the BO
 */

public class BookMaintainable extends KualiMaintainableImpl {

	
	private static final long serialVersionUID = 1L;

	@Override
	public void saveBusinessObject() {
		// TODO Auto-generated method stub
		Book book = (Book) this.getBusinessObject();
	

		// /-----------------------------------------------------///
		// /-----------------------------------------------------///
		// /---IF author has to be edited from Book Document-----///
		// /---follow the below mentioned code else comment out--///
		// /-----------------------------------------------------///
		// /-----------------------------------------------------///
		List<Author> authors = book.getAuthors();
		for (Author author : authors) {
			Account account = (Account) author.getExtension();
			if (account != null && account.getAuthorId() == null) {
				author.setExtension(null);
			}

			KRADServiceLocator.getBusinessObjectService().save(author);

			if (account != null && account.getAuthorId() == null) {
				account.setAuthorId(author.getAuthorId());
				KRADServiceLocator.getBusinessObjectService().save(account);
			}
		}

		book.setAuthors(authors);
		KRADServiceLocator.getBusinessObjectService().save(book);

	}

	@Override
	public void processAfterCopy(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		super.processAfterCopy(document, parameters);
		Book book = ((Book) document.getNewMaintainableObject()
				.getBusinessObject());
		book.setIsbn(null);
	}

}
