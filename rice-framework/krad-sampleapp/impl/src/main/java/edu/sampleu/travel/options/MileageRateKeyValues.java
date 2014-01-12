/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.travel.options;

import edu.sampleu.travel.dataobject.TravelMileageRate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a simple key values for mileage rate (needed for lookup screens on Travel Per Diem Expense)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MileageRateKeyValues  extends KeyValuesBase {
    private static final long serialVersionUID = 1L;

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        QueryResults<TravelMileageRate> bos = KRADServiceLocator.getDataObjectService().findMatching( TravelMileageRate.class, QueryByCriteria
                .Builder.create().build() );

        keyValues.add(new ConcreteKeyValue("", ""));
        for ( TravelMileageRate typ : bos.getResults() ) {
            keyValues.add(new ConcreteKeyValue(typ.getMileageRateId(), typ.getMileageRateCd()));
        }

        return keyValues;
    }

}
