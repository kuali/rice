/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.doctype;

import org.kuali.rice.kew.doctype.bo.DocumentType;

import java.io.Serializable;

/**
 * Compound primary key for {@link DocumentTypePolicy}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentTypePolicyId implements Serializable {

    private static final long serialVersionUID = -8024479878884387727L;

    private String documentType;
    private String policyName;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.getDocumentType() == null) ? 0 : this.getDocumentType()
						.hashCode());
		result = prime * result
				+ ((this.policyName == null) ? 0 : this.policyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DocumentTypePolicyId other = (DocumentTypePolicyId) obj;
		if (this.getDocumentType() == null) {
			if (other.getDocumentType() != null)
				return false;
		} else if (!this.getDocumentType().equals(other.getDocumentType()))
			return false;
		if (this.policyName == null) {
			if (other.policyName != null)
				return false;
		} else if (!this.policyName.equals(other.policyName))
			return false;
		return true;
	}


}

