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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Do JAXB mapping of Map<String, String> to a format like the following for a
 * map containing { key1:value1, key2:value2 }:
 * 
 * <pre>
 * {@code
 * <...>
 *   <entry key="key1">value1</entry>
 *   <entry key="key2">value2</entry>
 * </...>
 * }
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MapStringStringAdapter extends XmlAdapter<StringMapEntryList, Map<String, String>> {

	@Override
	public StringMapEntryList marshal(Map<String, String> map) throws Exception {
		if (map == null) {
			return null;
		}
		List<StringMapEntry> entries = new ArrayList<StringMapEntry>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			entries.add(new StringMapEntry(entry));
		}
		return new StringMapEntryList(entries);
	}

	@Override
	public Map<String, String> unmarshal(StringMapEntryList entryList) throws Exception {
		if (entryList == null) {
			return null;
		}
		List<StringMapEntry> entries = entryList.getEntries();
		Map<String, String> resultMap = new HashMap<String, String>(entries.size());
		for (StringMapEntry entry : entries) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		return resultMap;
	}
}
