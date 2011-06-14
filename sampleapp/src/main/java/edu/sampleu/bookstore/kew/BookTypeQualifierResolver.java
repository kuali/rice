/**
 * 
 */
package edu.sampleu.bookstore.kew;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.engine.RouteContext;

import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.workflow.attribute.QualifierResolverBase;
import edu.sampleu.bookstore.bo.Book;
import edu.sampleu.bookstore.bo.BookstoreKimAttributes;

/**
 * Qualifier Resolver for Book Type
 *
 */
public class BookTypeQualifierResolver extends QualifierResolverBase {

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.role.QualifierResolver#resolve(org.kuali.rice.kew.engine.RouteContext)
	 */
	public List<AttributeSet> resolve(RouteContext context) {
        List<AttributeSet> qualifiers = new ArrayList<AttributeSet>();
        MaintenanceDocument doc = (MaintenanceDocument) getDocument(context);
		Maintainable maint = doc.getNewMaintainableObject();
		Book book = (Book) maint.getBusinessObject();
		if (StringUtils.isNotEmpty(book.getTypeCode())) {
			qualifiers.add(new AttributeSet(BookstoreKimAttributes.BOOK_TYPE_CODE,book.getTypeCode()));
			decorateWithCommonQualifiers(qualifiers, context, null);
		}
		else {
            AttributeSet basicQualifier = new AttributeSet();
            qualifiers.add(basicQualifier);
		}
		return qualifiers;
	}

}
