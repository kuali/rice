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
package org.kuali.rice.core.util;

import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * A mutable, comparable key value pair of Strings.
 *  
 * This class is not meant to be extended.
 * 
 * For extension see AbstractKeyValue & KeyValue.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ConcreteKeyValue extends AbstractKeyValue implements Comparable<KeyValue> {
	private static final long serialVersionUID = 1176799455504861488L;

	public ConcreteKeyValue() {
		super();
	}

	public ConcreteKeyValue(String key, String value) {
		super(key, value);
	}

	public ConcreteKeyValue(KeyValue keyValue) {
		super(keyValue);
	}
	
	public ConcreteKeyValue(Map.Entry<String, String> entry) {
		super(entry);
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(KeyValue o) {
		if (o == null) {
			throw new NullPointerException("o is null");
		}

		return new CompareToBuilder()
			.append(this.getValue(), o.getValue(), String.CASE_INSENSITIVE_ORDER)
			.append(this.getKey(), o.getKey(), String.CASE_INSENSITIVE_ORDER)
			.toComparison();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getKey() == null) ? 0 : this.getKey().hashCode());
		result = prime * result
				+ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValue other = (KeyValue) obj;
		if (this.getKey() == null) {
			if (other.getKey() != null)
				return false;
		} else if (!this.getKey().equals(other.getKey()))
			return false;
		if (this.getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!this.getValue().equals(other.getValue()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "ConcreteKeyValue [getKey()=" + this.getKey() + ", getValue()="
				+ this.getValue() + "]";
	}
}
