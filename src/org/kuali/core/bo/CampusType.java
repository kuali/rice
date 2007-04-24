/*
 * Copyright 2007 The Kuali Foundation.
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
 * 
 */
public class CampusType extends PersistableBusinessObjectBase {

	private String campusTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private String campusTypeName;

	/**
	 * Default constructor.
	 */
	public CampusType() {
        dataObjectMaintenanceCodeActiveIndicator = true;
	}

	/**
	 * Gets the campusTypeCode attribute.
	 * 
	 * @return Returns the campusTypeCode
	 * 
	 */
	public String getCampusTypeCode() { 
		return campusTypeCode;
	}

	/**
	 * Sets the campusTypeCode attribute.
	 * 
	 * @param campusTypeCode The campusTypeCode to set.
	 * 
	 */
	public void setCampusTypeCode(String campusTypeCode) {
		this.campusTypeCode = campusTypeCode;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the campusTypeName attribute.
	 * 
	 * @return Returns the campusTypeName
	 * 
	 */
	public String getCampusTypeName() { 
		return campusTypeName;
	}

	/**
	 * Sets the campusTypeName attribute.
	 * 
	 * @param campusTypeName The campusTypeName to set.
	 * 
	 */
	public void setCampusTypeName(String campusTypeName) {
		this.campusTypeName = campusTypeName;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("campusTypeCode", this.campusTypeCode);
	    return m;
    }
}
