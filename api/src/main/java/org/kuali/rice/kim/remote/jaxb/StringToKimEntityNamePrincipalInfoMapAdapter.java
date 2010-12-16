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
package org.kuali.rice.kim.remote.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;

/**
 * Do jax-ws mapping of Map<String, String> for KIM service method parameters, etc.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StringToKimEntityNamePrincipalInfoMapAdapter extends XmlAdapter<StringEntNmPrncpInfoMapEntry[], Map<String, KimEntityNamePrincipalNameInfo>> {

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public StringEntNmPrncpInfoMapEntry[] marshal(Map<String, KimEntityNamePrincipalNameInfo> map) throws Exception {
		if(null == map) return null;
		StringEntNmPrncpInfoMapEntry[] entryArray = new StringEntNmPrncpInfoMapEntry[map.size()];
		int i = 0;
		for (Map.Entry<String, KimEntityNamePrincipalNameInfo> e : map.entrySet()) {
			entryArray[i] = new StringEntNmPrncpInfoMapEntry(e.getKey(), e.getValue());
			i++;
		}
		return entryArray;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Map<String, KimEntityNamePrincipalNameInfo> unmarshal(StringEntNmPrncpInfoMapEntry[] entryArray) throws Exception {
		if (null == entryArray) return null;
		Map<String, KimEntityNamePrincipalNameInfo> resultMap = new HashMap<String, KimEntityNamePrincipalNameInfo>(entryArray.length);
		for (int i = 0; i < entryArray.length; i++) {
			StringEntNmPrncpInfoMapEntry entry = entryArray[i];
			resultMap.put(entry.key, entry.value);
		}
		return resultMap;
	}
}
