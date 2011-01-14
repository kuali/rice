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
package org.kuali.rice.kns.uif.control;

import org.kuali.rice.kns.uif.widget.DatePicker;

/**
 * Represents a HTML Text control, generally rendered as a input field of type
 * 'text'. This can display and receive a single value
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TextControl extends ControlBase {
	private int size;

	private DatePicker datePicker;

	public TextControl() {

	}

	/**
	 * Horizontal display size of the control (in number of characters)
	 * 
	 * @return int size
	 */
	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Renders a calendar that can be used to select a date value for the text
	 * control. The <code>Calendar</code> instance contains configuration such
	 * as the date format string
	 * 
	 * @return Calendar
	 */
	public DatePicker getDatePicker() {
		return this.datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

}
