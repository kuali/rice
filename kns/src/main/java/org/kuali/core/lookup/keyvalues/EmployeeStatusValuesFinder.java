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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.EmployeeStatus;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

public class EmployeeStatusValuesFinder extends KeyValuesBase {

    /**
     * This class returns a list of active employee statuses.
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List<EmployeeStatus> codes = new ArrayList<EmployeeStatus>();
        for (Object employeeStatusAsObj: boService.findAll(EmployeeStatus.class)) {
            codes.add((EmployeeStatus)employeeStatusAsObj);
        }
        Collections.sort(codes, new Comparator<EmployeeStatus>() {
	    public int compare(EmployeeStatus empStatusA, EmployeeStatus empStatusB) {
		return empStatusA.getCode().compareTo(empStatusB.getCode());
	    }
            
        });
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));
        Iterator iter = codes.iterator();
        while (iter.hasNext()) {
            EmployeeStatus status = (EmployeeStatus)iter.next();
            labels.add(new KeyLabelPair(status.getCode(), status.getCodeAndDescription()));
        }

        return labels;
    }

}
