/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;


import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krms.framework.engine.expression.ComparisonOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PropositionOpCodeValuesFinder extends KeyValuesBase {

    private static final List<KeyValue> LABELS;
    static {
        final List<KeyValue> labels = new ArrayList<KeyValue>( 8 );
        labels.add(new ConcreteKeyValue(ComparisonOperator.EQUALS.getCode(), ComparisonOperator.EQUALS.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.GREATER_THAN.getCode(), ComparisonOperator.GREATER_THAN.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.GREATER_THAN_EQUAL.getCode(), ComparisonOperator.GREATER_THAN_EQUAL.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.LESS_THAN.getCode(), ComparisonOperator.LESS_THAN.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.LESS_THAN_EQUAL.getCode(), ComparisonOperator.LESS_THAN_EQUAL.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.NOT_EQUALS.getCode(), ComparisonOperator.NOT_EQUALS.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.EXISTS.getCode(), ComparisonOperator.EXISTS.getCode()));
        labels.add(new ConcreteKeyValue(ComparisonOperator.DOES_NOT_EXIST.getCode(), ComparisonOperator.DOES_NOT_EXIST.getCode()));
        LABELS = Collections.unmodifiableList(labels);
    }
    
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return LABELS;
    }    
}
