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
package org.kuali.rice.core.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;

/**
 * Do jax-ws mapping of Map<String, String> for KIM service method parameters, etc.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StringToKimEntityNameInfoMapAdapter extends XmlAdapter<ArrayList<StringEntityNameInfoMapEntry>, Map<String, KimEntityNameInfo>> {

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public ArrayList<StringEntityNameInfoMapEntry> marshal(Map<String, KimEntityNameInfo> map) throws Exception {
		if(null == map) return null;
		ArrayList<StringEntityNameInfoMapEntry> entryList = new ArrayList<StringEntityNameInfoMapEntry>();
		for (Map.Entry<String, KimEntityNameInfo> e : map.entrySet()) {
			entryList.add(new StringEntityNameInfoMapEntry(e.getKey(), e.getValue()));
		}
		return entryList;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Map<String, KimEntityNameInfo> unmarshal(ArrayList<StringEntityNameInfoMapEntry> entryList) throws Exception {
		if (null == entryList) return null;
		Map<String, KimEntityNameInfo> resultMap = new HashMap<String, KimEntityNameInfo>();
		for (StringEntityNameInfoMapEntry entry : entryList) {
			resultMap.put(entry.key, entry.value);
		}
		return resultMap;
	}
}
