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
package org.kuali.rice.kim.bo.entity.dto;

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import java.io.Serializable;

import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityNameInfo extends KimDefaultableInfo implements KimEntityName, Serializable {

	private static final long serialVersionUID = 1L;

	protected String entityNameId = "";
	protected String nameTypeCode = "";
	protected String firstName = "";
	protected String middleName = "";
	protected String lastName = "";
	protected String title = "";
	protected String suffix = "";
	
	protected boolean suppressName = false;

	/**
	 * 
	 */
	public KimEntityNameInfo() {
	}
	
	/**
	 * 
	 */
	public KimEntityNameInfo( KimEntityName name ) {
		if ( name != null ) {
		    this.entityNameId = name.getEntityNameId();
		    this.nameTypeCode = name.getNameTypeCode();
		    this.firstName = name.getFirstNameUnmasked();
		    this.middleName = name.getMiddleNameUnmasked();
		    this.lastName = name.getLastNameUnmasked();
		    this.title = name.getTitleUnmasked();
		    this.suffix = name.getSuffixUnmasked();
		    this.active = name.isActive();
		    this.dflt = name.isDefault();
		    this.suppressName = name.isSuppressName();
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getEntityNameId()
	 */
	public String getEntityNameId() {
		return entityNameId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFirstName()
	 */
	public String getFirstName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return firstName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getLastName()
	 */
	public String getLastName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return lastName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getMiddleName()
	 */
	public String getMiddleName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return middleName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getNameTypeCode()
	 */
	public String getNameTypeCode() {
		return nameTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getSuffix()
	 */
	public String getSuffix() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return suffix;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getTitle()
	 */
	public String getTitle() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return title;
	}

	public void setFirstName(String firstName) {
		this.firstName = unNullify( firstName );
	}

	public void setLastName(String lastName) {
		this.lastName = unNullify( lastName );
	}

	public void setMiddleName(String middleName) {
		this.middleName = unNullify( middleName );
	}

	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = unNullify( nameTypeCode );
	}

	public void setSuffix(String suffix) {
		this.suffix = unNullify( suffix );
	}

	public void setTitle(String title) {
		this.title = unNullify( title );
	}

	/**
	 * This default implementation formats the name as LAST, FIRST MIDDLE.
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedName()
	 */
	public String getFormattedName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return getLastName() + ", " + getFirstName() + (getMiddleName()==null?"":" " + getMiddleName());
	}

	public void setEntityNameId(String entityNameId) {
		this.entityNameId = unNullify( entityNameId );
	}

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getUnmaskedFirstName()
     */
    public String getFirstNameUnmasked() {
        return this.firstName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getUnmaskedFormattedName()
     */
    public String getFormattedNameUnmasked() {
        return this.lastName + ", " + this.firstName + (this.middleName==null?"":" " + this.middleName);
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getUnmaskedLastName()
     */
    public String getLastNameUnmasked() {
        return this.lastName;
    }

    /** 
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getUnmaskedMiddleName()
     */
    public String getMiddleNameUnmasked() {
        return this.middleName;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getUnmaskedSuffix()
     */
    public String getSuffixUnmasked() {
        return this.suffix;
    }

    /** 
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getUnmaskedTitle()
     */
    public String getTitleUnmasked() {
        return this.title;
    }
	
    public boolean isSuppressName() {
        return suppressName;
    }
}
