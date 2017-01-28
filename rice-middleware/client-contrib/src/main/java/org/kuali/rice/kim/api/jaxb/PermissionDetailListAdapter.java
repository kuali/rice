/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.api.jaxb;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * An XML adapter that converts between PermissionDetailList objects and Map<String, String> objects.
 * Unmarshalled keys and values will automatically be trimmed if non-null.
 * 
 * <p>This adapter will throw an exception during unmarshalling if blank or duplicate keys are encountered.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PermissionDetailListAdapter extends XmlAdapter<PermissionDetailList,Map<String, String>> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Map<String, String> unmarshal(PermissionDetailList v) throws Exception {
        if (v != null) {
            NormalizedStringAdapter normalizedStringAdapter = new NormalizedStringAdapter();
            Map<String, String> map = new HashMap<String, String>();
            for (MapStringStringAdapter.StringMapEntry stringMapEntry : v.getPermissionDetails()) {
                String tempKey = normalizedStringAdapter.unmarshal(stringMapEntry.getKey());
                if (StringUtils.isBlank(tempKey)) {
                    throw new UnmarshalException("Cannot create a permission detail entry with a blank key");
                } else if (map.containsKey(tempKey)) {
                    throw new UnmarshalException("Cannot create more than one permission detail entry with a key of \"" + tempKey + "\"");
                }
                map.put(tempKey, normalizedStringAdapter.unmarshal(stringMapEntry.getValue()));
            }
        }
        return null;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public PermissionDetailList marshal(Map<String, String> v) throws Exception {
        return (v != null) ? new PermissionDetailList(v) : null;
    }

}
