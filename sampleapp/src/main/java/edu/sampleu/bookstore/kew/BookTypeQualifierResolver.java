/**
 * 
 */
package edu.sampleu.bookstore.kew;

import edu.sampleu.bookstore.bo.Book;
import edu.sampleu.bookstore.bo.BookstoreKimAttributes;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.workflow.attribute.QualifierResolverBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Qualifier Resolver for Book Type
 *
 */
public class BookTypeQualifierResolver extends QualifierResolverBase {

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.role.QualifierResolver#resolve(org.kuali.rice.kew.engine.RouteContext)
	 */
	public List<Map<String, String>> resolve(RouteContext context) {
        List<Map<String, String>> qualifiers = new ArrayList<Map<String, String>>();
        MaintenanceDocument doc = (MaintenanceDocument) getDocument(context);
		Maintainable maint = doc.getNewMaintainableObject();
		Book book = (Book) maint.getDataObject();
		if (StringUtils.isNotEmpty(book.getTypeCode())) {
			qualifiers.add(Collections.singletonMap(BookstoreKimAttributes.BOOK_TYPE_CODE, book.getTypeCode()));
			decorateWithCommonQualifiers(qualifiers, context, null);
		}
		else {
            Map<String, String> basicQualifier = new HashMap<String, String>();
            qualifiers.add(basicQualifier);
		}
		return qualifiers;
	}

}
