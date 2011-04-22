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
package org.kuali.rice.core.util.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Do jax-ws mapping of Map<String, String> for KIM service method parameters, etc.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class JaxbStringMapAdapter extends XmlAdapter<StringMapEntryList, Map<String, String>> {

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public StringMapEntryList marshal(Map<String, String> map) throws Exception {
		if(null == map) return null;
		StringMapEntryList entryList = new StringMapEntryList();
		for (Map.Entry<String, String> e : map.entrySet()) {
			entryList.getEntries().add(
					new StringMapEntry(e.getKey(), e.getValue()));
		}
		return entryList;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Map<String, String> unmarshal(StringMapEntryList entryList) throws Exception {
		if (null == entryList) return null;
		Map<String, String> resultMap = new HashMap<String, String>();
		for (StringMapEntry entry : entryList.getEntries()) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		return resultMap;
	}
}
