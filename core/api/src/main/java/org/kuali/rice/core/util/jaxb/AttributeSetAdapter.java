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
package org.kuali.rice.core.util.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kuali.rice.core.util.AttributeSet;

/**
 * This class allows for a {@link org.kuali.rice.core.util.AttributeSet} instance to be passed across the wire by jaxws enabled services
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeSetAdapter extends XmlAdapter<StringMapEntryList, AttributeSet> {

	/**
	 * @see org.kuali.rice.core.util.jaxb.MapStringStringAdapter#marshal(java.lang.Object)
	 */
	@Override
	public StringMapEntryList marshal(AttributeSet attributeSet) throws Exception {
		if (attributeSet == null) {
			return null;
		}
		List<StringMapEntry> entries = new ArrayList<StringMapEntry>(attributeSet.size());
		for (Map.Entry<String, String> entry : attributeSet.entrySet()) {
			entries.add(new StringMapEntry(entry));
		}
		return new StringMapEntryList(entries);
	}

	/**
	 * @see org.kuali.rice.core.util.jaxb.MapStringStringAdapter#unmarshal(java.util.ArrayList)
	 */
	@Override
	public AttributeSet unmarshal(StringMapEntryList entries) throws Exception {
		if (entries == null || entries.getEntries() == null) {
			return null;
		}
		AttributeSet resultMap = new AttributeSet(entries.getEntries().size());
		for (StringMapEntry entry : entries.getEntries()) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		return resultMap;
	}

}
