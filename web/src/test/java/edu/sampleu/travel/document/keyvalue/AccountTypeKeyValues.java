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
package edu.sampleu.travel.document.keyvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

import edu.sampleu.travel.bo.TravelAccountType;

public class AccountTypeKeyValues extends KeyValuesBase {
	
    public List getKeyValues() {
        List keyValues = new ArrayList();

        Collection<TravelAccountType> bos = KNSServiceLocator.getBusinessObjectService().findAll( TravelAccountType.class );
        
        keyValues.add(new KeyLabelPair("", ""));
        for ( TravelAccountType typ : bos ) {
        	keyValues.add(new KeyLabelPair(typ.getAccountTypeCode(), typ.getName()));
        }

        return keyValues;
    }

}