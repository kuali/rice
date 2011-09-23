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
package org.kuali.rice.krms.impl.ui;


import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krms.api.repository.LogicalOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ValidTermsValuesFinder extends KeyValuesBase {
    // TODO: STATIC List is NOT going to work.

    private static List<KeyValue> LABELS;
    static {
        final List<KeyValue> labels = new ArrayList<KeyValue>( 2 );

        //TODO: get the list of valid terms for this context.
        labels.add(new ConcreteKeyValue("001", "campusCodeTermSpec"));  // stub


        LABELS = Collections.unmodifiableList(labels);
    }

    
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return LABELS;
    }

    public static void setLabels(List<KeyValue> labels){
        LABELS = Collections.unmodifiableList(labels);
    }
}
