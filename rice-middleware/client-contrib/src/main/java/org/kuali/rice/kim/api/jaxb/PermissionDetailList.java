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

import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An XML element that can have zero or more StringMapEntry elements. This is similar
 * to the StringMapEntryList, except this element's children are &lt;permissionDetail&gt; elements.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PermissionDetailListType", propOrder={"permissionDetails"})
public class PermissionDetailList implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="permissionDetail")
    private List<MapStringStringAdapter.StringMapEntry> permissionDetails;
    
    public PermissionDetailList() {
        this.permissionDetails = new ArrayList<MapStringStringAdapter.StringMapEntry>();
    }
    
    public PermissionDetailList(Map<String, String> map) {
        this();
        for (Map.Entry<String,String> tempEntry : map.entrySet()) {
            permissionDetails.add(new MapStringStringAdapter.StringMapEntry(tempEntry));
        }
    }

    /**
     * @return the permissionDetails
     */
    public List<MapStringStringAdapter.StringMapEntry> getPermissionDetails() {
        return this.permissionDetails;
    }

    /**
     * @param permissionDetails the permissionDetails to set
     */
    public void setPermissionDetails(List<MapStringStringAdapter.StringMapEntry> permissionDetails) {
        this.permissionDetails = permissionDetails;
    }
    
}
