/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.modifier;

import java.io.Serializable;

import org.kuali.rice.kns.uif.core.Ordered;


/**
 * Provides configuration for comparing an object with another object
 * 
 * <p>
 * Used with a comparison view (such as in maintenance documents edit mode)
 * where two objects with the same properties are compared. This class
 * configures the object paths for the objects that will be compared, and has
 * additional configuration for the generated comparison group
 * </p>
 * 
 * <p>
 * All comparison objects must have the same fields and collection rows
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.kns.uif.modifier.CompareFieldCreateModifier
 */
public class ComparableInfo implements Serializable, Ordered  {
	private static final long serialVersionUID = -5926058412202550266L;

	private String bindingObjectPath;
	private String headerText;
	private boolean readOnly;

	private int order;
	private String idSuffix;

	public ComparableInfo() {
		readOnly = false;
	}

	/**
	 * Returns the path (from the form) for the object to compare to
	 * 
	 * <p>
	 * When a comparison view is rendered, a group will be rendered for each
	 * comparison object using the fields defined on the view. This gives the
	 * path to one of the comparison objects
	 * </p>
	 * 
	 * <p>
	 * e.g. For maintenance documents the compare object paths would be
	 * document.newMaintainableObject.businessObject and
	 * document.oldMaintainableObject.businessObject
	 * </p>
	 * 
	 * @return String path to the compare object
	 */
	public String getBindingObjectPath() {
		return this.bindingObjectPath;
	}

	/**
	 * Setter for the path to the compare object
	 * 
	 * @param bindingObjectPath
	 */
	public void setBindingObjectPath(String bindingObjectPath) {
		this.bindingObjectPath = bindingObjectPath;
	}

	/**
	 * Text that should display on the header for the compare group
	 * 
	 * <p>
	 * In the comparison view each compare group can be labeled, this gives the
	 * text that should be used for that label. For example in the maintenance
	 * view the compare record is labeled 'Old' to indicate it is the old
	 * version of the record
	 * </p>
	 * 
	 * @return String header text
	 */
	public String getHeaderText() {
		return this.headerText;
	}

	/**
	 * Setter for the compare group header text
	 * 
	 * @param headerText
	 */
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	/**
	 * Indicates whether the compare group should be read-only
	 * 
	 * @return boolean true if the group should be read-only, false if edits are
	 *         allowed
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * Setter for the read-only indicator
	 * 
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Sets the order value that will be used to determine where the compare
	 * group should be placed in relation to the other compare groups
	 * 
	 * <p>
	 * For example if the compare groups are being rendered from left to right
	 * in columns, a lower order value would be placed to the left of a compare
	 * group with a higher order value
	 * </p>
	 * 
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Setter for the compare object order
	 * 
	 * @param order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Specifies an id suffix to use for the generated comparison fields
	 * 
	 * <p>
	 * For the given string, all components created for the comparison group
	 * will contain the string on their id. This can be helpful for scripting.
	 * If not given, the items will receive a default id suffix
	 * </p>
	 * 
	 * @return String id suffix for comparison group
	 */
	public String getIdSuffix() {
		return this.idSuffix;
	}

	/**
	 * Setter for the id prefix to use for the generated comparison components
	 * 
	 * @param idSuffix
	 */
	public void setIdSuffix(String idSuffix) {
		this.idSuffix = idSuffix;
	}

}
