/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kim.api.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.StringMapEntry;

/**
 * An XML element that can have zero or more StringMapEntry elements. This is similar
 * to the StringMapEntryList, except this element's children are &lt;qualification&gt; elements.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="QualificationListType", propOrder={"qualifications"})
public class QualificationList implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="qualification")
    private List<StringMapEntry> qualifications;
    
    public QualificationList () {
        qualifications = new ArrayList<StringMapEntry>();
    }
    
    public QualificationList(AttributeSet attributeSet) {
        this();
        for (Map.Entry<String,String> tempEntry : attributeSet.entrySet()) {
            qualifications.add(new StringMapEntry(tempEntry));
        }
    }

    /**
     * @return the qualifications
     */
    public List<StringMapEntry> getQualifications() {
        return this.qualifications;
    }

    /**
     * @param qualifications the qualifications to set
     */
    public void setQualifications(List<StringMapEntry> qualifications) {
        this.qualifications = qualifications;
    }

    
}
