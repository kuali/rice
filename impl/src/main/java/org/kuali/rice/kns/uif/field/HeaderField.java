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
package org.kuali.rice.kns.uif.field;

/**
 * Field that contains a header element and optionally a <code>Group</code> to
 * present along with the header text
 * 
 * <p>
 * Generally the group is used to display content to the right of the header,
 * such as links for the group or other information
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HeaderField extends GroupField {
	private static final long serialVersionUID = -6950408292923393244L;

	private String headerText;
	private String headerLevel;
	private String headerStyleClass;
	private String headerStyle;

	public HeaderField() {
		super();
	}

	public String getHeaderText() {
		return this.headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public String getHeaderLevel() {
		return this.headerLevel;
	}

	public void setHeaderLevel(String headerLevel) {
		this.headerLevel = headerLevel;
	}

	public String getHeaderStyleClass() {
		return this.headerStyleClass;
	}

	public void setHeaderStyleClass(String headerStyleClass) {
		this.headerStyleClass = headerStyleClass;
	}

	public String getHeaderStyle() {
		return this.headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

}
