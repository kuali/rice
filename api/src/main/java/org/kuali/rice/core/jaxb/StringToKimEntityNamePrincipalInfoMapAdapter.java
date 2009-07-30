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

import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;

/**
 * Do jax-ws mapping of Map<String, String> for KIM service method parameters, etc.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StringToKimEntityNamePrincipalInfoMapAdapter extends XmlAdapter<ArrayList<StringEntNmPrncpInfoMapEntry>, Map<String, KimEntityNamePrincipalNameInfo>> {

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public ArrayList<StringEntNmPrncpInfoMapEntry> marshal(Map<String, KimEntityNamePrincipalNameInfo> map) throws Exception {
		if(null == map) return null;
		ArrayList<StringEntNmPrncpInfoMapEntry> entryList = new ArrayList<StringEntNmPrncpInfoMapEntry>();
		for (Map.Entry<String, KimEntityNamePrincipalNameInfo> e : map.entrySet()) {
			entryList.add(new StringEntNmPrncpInfoMapEntry(e.getKey(), e.getValue()));
		}
		return entryList;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Map<String, KimEntityNamePrincipalNameInfo> unmarshal(ArrayList<StringEntNmPrncpInfoMapEntry> entryList) throws Exception {
		if (null == entryList) return null;
		Map<String, KimEntityNamePrincipalNameInfo> resultMap = new HashMap<String, KimEntityNamePrincipalNameInfo>();
		for (StringEntNmPrncpInfoMapEntry entry : entryList) {
			resultMap.put(entry.key, entry.value);
		}
		return resultMap;
	}
}
