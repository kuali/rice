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
package org.kuali.rice.kns.bo;

/**
 * Interface for CampusType.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface CampusType extends ExternalizableBusinessObject {

	/**
	 * Returns the campusTypeCode
	 *
	 * @see org.kuali.rice.kns.bo.CampusTypeEBO#getCampusTypeCode()
	 */
	public String getCampusTypeCode();

	/**
	 * Sets the campusTypeCode attribute.
	 *
	 * @param campusTypeCode The campusTypeCode to set.
	 *
	 */
	public void setCampusTypeCode(String campusTypeCode);

	/**
	 * returns dataObjectMaintenanceCodeActiveIndicator
	 *
	 * @see org.kuali.rice.kns.bo.CampusTypeEBO#getDataObjectMaintenanceCodeActiveIndicator()
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator();

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 *
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 *
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(
			boolean dataObjectMaintenanceCodeActiveIndicator);

	/**
	 * returns campusName
	 *
	 * @see org.kuali.rice.kns.bo.CampusTypeEBO#getCampusTypeName()
	 */
	public String getCampusTypeName();

	/**
	 * Sets the campusTypeName attribute.
	 *
	 * @param campusTypeName The campusTypeName to set.
	 *
	 */
	public void setCampusTypeName(String campusTypeName);

	/**
	 * returns isActive
	 *
	 * @see org.kuali.rice.kns.bo.CampusTypeEBO#isActive()
	 */
	public boolean isActive();

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active);

}