package org.kuali.core.bo;

import java.util.LinkedHashMap;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DocumentStatus extends PersistableBusinessObjectBase {

	private String financialDocumentStatusCode;
	private String financialDocumentStatusName;

	/**
	 * Default constructor.
	 */
	public DocumentStatus() {

	}

	/**
	 * Gets the financialDocumentStatusCode attribute.
	 * 
	 * @return Returns the financialDocumentStatusCode
	 * 
	 */
	public String getFinancialDocumentStatusCode() { 
		return financialDocumentStatusCode;
	}

	/**
	 * Sets the financialDocumentStatusCode attribute.
	 * 
	 * @param financialDocumentStatusCode The financialDocumentStatusCode to set.
	 * 
	 */
	public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
		this.financialDocumentStatusCode = financialDocumentStatusCode;
	}


	/**
	 * Gets the financialDocumentStatusName attribute.
	 * 
	 * @return Returns the financialDocumentStatusName
	 * 
	 */
	public String getFinancialDocumentStatusName() { 
		return financialDocumentStatusName;
	}

	/**
	 * Sets the financialDocumentStatusName attribute.
	 * 
	 * @param financialDocumentStatusName The financialDocumentStatusName to set.
	 * 
	 */
	public void setFinancialDocumentStatusName(String financialDocumentStatusName) {
		this.financialDocumentStatusName = financialDocumentStatusName;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialDocumentStatusCode", this.financialDocumentStatusCode);
	    return m;
    }
}
