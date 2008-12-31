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
package org.kuali.rice.kim.bo.ui;

import java.util.Comparator;

import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimAttributeDataComparator implements Comparator<KimAttributeDataImpl> {

    public int compare(KimAttributeDataImpl attrData1, KimAttributeDataImpl attrData2) {
       // int retval = 0;
        return getSortCode(attrData1).compareTo(getSortCode(attrData2));
    }

    private String getSortCode(KimAttributeDataImpl attrData) {
    	String sortCode = "";
		for (KimTypeAttributeImpl typeAttribute : attrData.getKimType().getAttributeDefinitions()) {
			AttributeDefinition definition;
			if (typeAttribute.getKimAttribute().getKimAttributeId().equals(attrData.getKimAttributeId())) {
				sortCode = typeAttribute.getSortCode();
				break;
			}
		}
		return sortCode;

    }
}
