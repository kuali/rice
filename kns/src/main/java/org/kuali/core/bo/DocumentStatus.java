/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
