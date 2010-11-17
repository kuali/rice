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
package org.kuali.rice.core.jaxb;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kuali.rice.core.xml.dto.AttributeSet;

/**
 * This class allows for a {@link org.kuali.rice.core.xml.dto.AttributeSet} instance to be passed across the wire by jaxws enabled services
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeSetAdapter extends XmlAdapter<StringMapEntry[], AttributeSet> {

	/**
	 * @see org.kuali.rice.core.jaxb.MapStringStringAdapter#marshal(java.lang.Object)
	 */
	@Override
	public StringMapEntry[] marshal(AttributeSet attributeSet) throws Exception {
		if(null == attributeSet) return null;
		StringMapEntry[] entryArray = new StringMapEntry[attributeSet.size()];
		int i = 0;
		for (Map.Entry<String, String> e : attributeSet.entrySet()) {
			entryArray[i] = new StringMapEntry(e);
			i++;
		}
		return entryArray;
	}

	/**
	 * @see org.kuali.rice.core.jaxb.MapStringStringAdapter#unmarshal(java.util.ArrayList)
	 */
	@Override
	public AttributeSet unmarshal(StringMapEntry[] entryArray) throws Exception {
		if (null == entryArray) return null;
		AttributeSet resultMap = new AttributeSet(entryArray.length);
		for (int i = 0; i < entryArray.length; i++) {
			StringMapEntry stringMapEntry = entryArray[i];
			resultMap.put(stringMapEntry.key, stringMapEntry.value);
		}
		return resultMap;
	}

}
