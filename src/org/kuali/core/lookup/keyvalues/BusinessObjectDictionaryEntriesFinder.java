/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class returns list of business objects defined in the data dictionary.
 */
public class BusinessObjectDictionaryEntriesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List businessObjects = KNSServiceLocator.getBusinessObjectDictionaryService().getBusinessObjectClassnames();
        List boKeyLabels = new ArrayList();

        for (Iterator iter = businessObjects.iterator(); iter.hasNext();) {
            String className = (String) iter.next();
            boKeyLabels.add(new KeyLabelPair(className, className));
        }

        return boKeyLabels;
    }

}