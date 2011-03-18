/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.reference.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.reference.KimCode;
import org.kuali.rice.kns.bo.KualiCodeBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
@AttributeOverrides({
	@AttributeOverride(name="active",column=@Column(name="ACTV_IND"))
})
public abstract class KimCodeBase extends KualiCodeBase implements KimCode {
    
    private static final long serialVersionUID = 1660166679188995697L;
	@Column(name="DISPLAY_SORT_CD")
    protected String displaySortCode = "";

	/**
	 * @see org.kuali.core.bo.KualiCode#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.core.bo.KualiCode#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

    /**
     * Implements equals comparing code to code.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof KualiCodeBase) {
            return StringUtils.equals(this.getCode(), ((KualiCodeBase) obj).getCode());
        }
        return false;
    }

    /**
     * Overriding equals requires writing a hashCode method.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hashCode = 0;

        if (getCode() != null) {
            hashCode = getCode().hashCode();
        }

        return hashCode;
    }

	/**
	 * @return the displaySortCode
	 */
	public String getDisplaySortCode() {
		return this.displaySortCode;
	}

	/**
	 * @param displaySortCode the displaySortCode to set
	 */
	public void setDisplaySortCode(String displaySortCode) {
		this.displaySortCode = displaySortCode;
	}
	
}
