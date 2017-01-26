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
package edu.sampleu.bookstore.document.keyvalue;

import edu.sampleu.bookstore.bo.BSAddressType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is a description of what this class does - Administrator don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */

public class AddressTypeKeyValues extends KeyValuesBase {
	
    public List getKeyValues() {
        List keyValues = new ArrayList();

        Collection<BSAddressType> bos = KNSServiceLocator.getBusinessObjectService().findAll( BSAddressType.class );
        
        keyValues.add(new ConcreteKeyValue("", ""));
        for ( BSAddressType typ : bos ) {
        	keyValues.add(new ConcreteKeyValue(typ.getType(), typ.getType()));
        }

        return keyValues;
    }

}