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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.EmployeeType;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

public class EmployeeTypeValuesFinder extends KeyValuesBase {

    public List getKeyValues() {
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        Collection codes = boService.findAll(EmployeeType.class);
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));
        Iterator iter = codes.iterator();
        while (iter.hasNext()) {
            EmployeeType employeeType = (EmployeeType)iter.next();
            labels.add(new KeyLabelPair(employeeType.getCode(), employeeType.getCodeAndDescription()));
        }

        return labels;
    }

}
