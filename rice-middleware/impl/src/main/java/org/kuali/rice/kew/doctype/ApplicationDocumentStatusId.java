/**
 * Copyright 2005-2014 The Kuali Foundation
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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Composite primary key for {@link ApplicationDocumentStatus}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Embeddable
public class ApplicationDocumentStatusId implements Serializable {

    @Column(name="DOC_TYP_ID", nullable = false)
    private String documentTypeId;
    @Column(name="DOC_STAT_NM", nullable = false)
    private String statusName;

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }
    
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.documentTypeId == null) ? 0 : this.documentTypeId
						.hashCode());
		result = prime * result
				+ ((this.statusName == null) ? 0 : this.statusName.hashCode());
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
		final ApplicationDocumentStatusId other = (ApplicationDocumentStatusId) obj;
		if (this.documentTypeId == null) {
			if (other.documentTypeId != null)
				return false;
		} else if (!this.documentTypeId.equals(other.documentTypeId))
			return false;
		if (this.statusName == null) {
			if (other.statusName != null)
				return false;
		} else if (!this.statusName.equals(other.statusName))
			return false;
		return true;
	}

}

