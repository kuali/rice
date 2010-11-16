/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A class for associating a text label with a given key.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KeyLabelPair implements Serializable, Comparable<KeyLabelPair>{
	private static final long serialVersionUID = 7997396592230527472L;
    public Object key;
    public String label;
    public int numPaddedSpaces;
    
    public KeyLabelPair() {

    }

    public KeyLabelPair(Object key, String label) {
        this.key = key;
        this.label = label;
    }

    public Object getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setKey(Object key) {
        this.key = key;
    }
    
    public void setNumPaddedSpaces(int numPaddedSpaces) {
    	this.numPaddedSpaces = numPaddedSpaces;
    }
 
    public String getHtmlSpacePadding() {
    	return StringUtils.repeat("&nbsp;", numPaddedSpaces);
    }

    /** {inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 39)
		.append(this.label)
		.append(this.key)
		.append(this.numPaddedSpaces)
		.toHashCode();
	}

	/** {inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof KeyLabelPair)) {
			return false;
		}
		
		final KeyLabelPair other = (KeyLabelPair) obj;
		return new EqualsBuilder()
		.append(this.label, other.label)
		.append(this.key, other.key)
		.append(this.numPaddedSpaces, other.numPaddedSpaces)
		.isEquals();
	}

	/** {inheritDoc} */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("label", this.label)
		.append("key", this.key)
		.append("numPaddedSpaces", this.numPaddedSpaces)
		.toString();
	}

	/** {inheritDoc} */
	public int compareTo(KeyLabelPair o) {
		if (o == null) {
			throw new NullPointerException("the object to compare to is null");
		}
		final CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.label, o.label, String.CASE_INSENSITIVE_ORDER);
		
		if (this.key instanceof String && o.key instanceof String) {
			builder.append(this.key, o.key, String.CASE_INSENSITIVE_ORDER);	
		} else {
			builder.append(this.key, o.key);
		}
		
		builder.append(this.numPaddedSpaces, o.numPaddedSpaces);
		return builder.toComparison();
	}
}
