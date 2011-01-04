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
package org.kuali.rice.kns.ui.control;

/**
 * Represents a HTML TextArea control. Generally used for values that are very
 * large (such as a description)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TextAreaControl extends ControlBase {
	private int rows;
	private int cols;

	public TextAreaControl() {

	}

	/**
	 * Number of rows the control should span (horizontal length)
	 * 
	 * @return int number of rows
	 */
	public int getRows() {
		return this.rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * Number of columns the control should span (vertical length)
	 * 
	 * @return int number of columns
	 */
	public int getCols() {
		return this.cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

}
