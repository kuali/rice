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
package org.kuali.rice.krad.uif.control;

/**
 * Represents a HTML File control, generally rendered as an input control with
 * type 'file'. Allows user to upload a file to the application
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FileControl extends ControlBase {
	private static final long serialVersionUID = -5919326390841646189L;
	
	private int size;

	public FileControl() {

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

}
