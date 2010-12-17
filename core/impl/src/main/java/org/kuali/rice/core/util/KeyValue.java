/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.core.util;

import java.io.Serializable;


/**
 * This is a generic keyValue object.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KeyValue  implements Serializable {
	
	private String key;
	private String value;

	public KeyValue() {
	}

	public KeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
	public boolean equals(Object obj) {
		if(obj instanceof KeyValue && obj != null){			
			if(((KeyValue)obj).getKey().equals(this.getKey()) &&
					((KeyValue)obj).getValue().equals(this.getValue())	){
				return true;
			}else{
				return false;
			}
		}else{
			return super.equals(obj);
		}
	}

	
	
}
