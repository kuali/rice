/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.reference.dto;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.reference.KimCode;
import org.kuali.rice.kns.bo.KualiCode;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KimCodeInfoBase implements KimCode {

	private static final long serialVersionUID = 3391418027677414695L;

	protected String code;
    protected String name;
    protected boolean active;
    protected String displaySortCode = "";
	
    public KimCodeInfoBase() {
		super();
		active = true;
	}

    public KimCodeInfoBase(KimCode kimCode) {
		this();
		if ( kimCode != null ) {
			this.code = (kimCode.getCode() != null) ? kimCode.getCode() : "";
			this.name = (kimCode.getName() != null) ? kimCode.getName() : "";
			this.displaySortCode = "";
		}
	}

	/**
	 * @return the code
	 */
	@XmlTransient
	public String getCode() {
		return this.code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	@XmlTransient
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getCode() + " - " + getName(); 
	}

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
        if (obj instanceof KualiCode) {
            return StringUtils.equals(this.getCode(), ((KualiCode) obj).getCode());
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

	public void refresh() {}
}
