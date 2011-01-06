/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.test.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.test.document.bo.AccountType;

public class AccountTypeKeyValues extends KeyValuesBase {
	
    public List getKeyValues() {
        List keyValues = new ArrayList();

        Collection<AccountType> bos = KNSServiceLocator.getBusinessObjectService().findAll( AccountType.class );
        
        keyValues.add(new ConcreteKeyValue("", ""));
        for ( AccountType typ : bos ) {
        	keyValues.add(new ConcreteKeyValue(typ.getAccountTypeCode(), typ.getName()));
        }

        return keyValues;
    }

}
