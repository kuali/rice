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

import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.LinkedHashMap;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="FP_DOC_GROUP_T")
public class DocumentGroup extends PersistableBusinessObjectBase {

	@Id
	@Column(name="FDOC_GRP_CD")
	private String financialDocumentGroupCode;
	@Column(name="FDOC_GRP_NM")
	private String financialDocumentGroupName;
	@Column(name="FDOC_CLASS_CD")
	private String financialDocumentClassCode;

	/**
	 * Default constructor.
	 */
	public DocumentGroup() {

	}

	/**
	 * Gets the financialDocumentGroupCode attribute.
	 * 
	 * @return Returns the financialDocumentGroupCode
	 * 
	 */
	public String getFinancialDocumentGroupCode() { 
		return financialDocumentGroupCode;
	}

	/**
	 * Sets the financialDocumentGroupCode attribute.
	 * 
	 * @param financialDocumentGroupCode The financialDocumentGroupCode to set.
	 * 
	 */
	public void setFinancialDocumentGroupCode(String financialDocumentGroupCode) {
		this.financialDocumentGroupCode = financialDocumentGroupCode;
	}


	/**
	 * Gets the financialDocumentGroupName attribute.
	 * 
	 * @return Returns the financialDocumentGroupName
	 * 
	 */
	public String getFinancialDocumentGroupName() { 
		return financialDocumentGroupName;
	}

	/**
	 * Sets the financialDocumentGroupName attribute.
	 * 
	 * @param financialDocumentGroupName The financialDocumentGroupName to set.
	 * 
	 */
	public void setFinancialDocumentGroupName(String financialDocumentGroupName) {
		this.financialDocumentGroupName = financialDocumentGroupName;
	}


	/**
	 * Gets the financialDocumentClassCode attribute.
	 * 
	 * @return Returns the financialDocumentClassCode
	 * 
	 */
	public String getFinancialDocumentClassCode() { 
		return financialDocumentClassCode;
	}

	/**
	 * Sets the financialDocumentClassCode attribute.
	 * 
	 * @param financialDocumentClassCode The financialDocumentClassCode to set.
	 * 
	 */
	public void setFinancialDocumentClassCode(String financialDocumentClassCode) {
		this.financialDocumentClassCode = financialDocumentClassCode;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialDocumentGroupCode", this.financialDocumentGroupCode);
	    return m;
    }
}

