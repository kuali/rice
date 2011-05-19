package edu.sampleu.bookstore.maintenance;

import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;
import edu.sampleu.bookstore.bo.Account;
import edu.sampleu.bookstore.bo.Author;

/**
 * maintainableClass for Author document.
 * Action to be taken before saving the BO
 */

public class AuthorMaintainable extends KualiMaintainableImpl {
	
	
	private static final long serialVersionUID = 1L;

	@Override
	public void saveBusinessObject() {
		// TODO Auto-generated method stub
		Author author = (Author) this.getBusinessObject();
		 
		Account account = (Account)author.getExtension();
		if(account != null && account.getAuthorId() == null) {			
			author.setExtension(null);
		}
		
		KNSServiceLocator.getBusinessObjectService().save(author);
		
		if(account != null && account.getAuthorId() == null) {			
			account.setAuthorId(author.getAuthorId());			
			KNSServiceLocator.getBusinessObjectService().save(account);
		}
		
	}

}
