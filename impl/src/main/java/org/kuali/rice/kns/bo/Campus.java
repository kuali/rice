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
 * Campus Externalizable Business Object 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface Campus extends ExternalizableBusinessObject {

	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return Returns the campusCode
	 * 
	 */
	public String getCampusCode();

	/**
	 * Gets the campusName attribute.
	 * 
	 * @return Returns the campusName
	 * 
	 */
	public String getCampusName();

	/**
	 * Gets the campusShortName attribute.
	 * 
	 * @return Returns the campusShortName
	 * 
	 */
	public String getCampusShortName();

	/**
	 * Gets the campusTypeCode attribute.
	 * 
	 * @return Returns the campusTypeCode
	 * 
	 */
	public String getCampusTypeCode();

	/**
	 * Gets the campusType attribute. 
	 * @return Returns the campusType.
	 */
	public CampusType getCampusType();

	/**
	 * @return the active
	 */
	public boolean isActive();

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     * 
     */
    public void setCampusCode(String campusCode);

    /**
     * Sets the campusName attribute.
     * 
     * @param campusName The campusName to set.
     * 
     */
    public void setCampusName(String campusName);

    /**
     * Sets the campusShortName attribute.
     * 
     * @param campusShortName The campusShortName to set.
     * 
     */
    public void setCampusShortName(String campusShortName);

    /**
     * Sets the campusTypeCode attribute.
     * 
     * @param campusTypeCode The campusTypeCode to set.
     * 
     */
    public void setCampusTypeCode(String campusTypeCode);

    /**
     * Sets the campusType attribute value.
     * @param campusType The campusType to set.
     * @deprecated
     */
    public void setCampusType(CampusType campusType);

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active);
}