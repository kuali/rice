/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.web;

import java.io.Serializable;

/**
 * A simple bean for storing key/value pairs that can be used for a number of
 * tasks. Right now it is used to hold information that will be display on a jsp
 * for drop down boxes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KeyValue implements Serializable {

	private static final long serialVersionUID = 8020755656995670021L;
	private String key;
	private String value;

	public KeyValue() {
	}

	public KeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getkey() {
		return key;
	}

	public String getvalue() {
		return value;
	}

	public void setkey(String key) {
		this.key = key;
	}

	public void setvalue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
