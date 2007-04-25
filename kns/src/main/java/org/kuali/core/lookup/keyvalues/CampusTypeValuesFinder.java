/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.CampusType;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class...
 * 
 * 
 */
public class CampusTypeValuesFinder extends KeyValuesBase {

    public List getKeyValues() {

        // get a list of all CampusTypes
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List campusTypes = (List) boService.findAll(CampusType.class);

        // calling comparator.
        CampusTypeComparator campusTypeComparator = new CampusTypeComparator();

        // sort using comparator.
        Collections.sort(campusTypes, campusTypeComparator);

        // create a new list (code, descriptive-name)
        List labels = new ArrayList();

        for (Iterator iter = campusTypes.iterator(); iter.hasNext();) {
            CampusType campusType = (CampusType) iter.next();
            labels.add(new KeyLabelPair(campusType.getCampusTypeCode(), campusType.getCampusTypeCode() + " - " + campusType.getCampusTypeName()));
        }

        return labels;
    }
}