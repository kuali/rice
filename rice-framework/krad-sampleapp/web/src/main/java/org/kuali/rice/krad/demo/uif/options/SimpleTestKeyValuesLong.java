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
package org.kuali.rice.krad.demo.uif.options;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.uif.util.UifKeyValue;
import org.kuali.rice.krad.uif.util.UifOptionGroupLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SimpleTestKeyValuesLong extends KeyValuesBase {

    private boolean blankOption;

    /**
     * This is a fake implementation of a key value finder, normally this would make a request to
     * a database to obtain the necessary values.  Used only for testing.
     *
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        String groupName;
        String optionName;
        Integer randomOption;
        Integer minimum=2;
        Integer maximum=25;

        if (blankOption) {
            keyValues.add(new UifKeyValue("", ""));
        }
        HashMap<String, Vector<String>> data = new HashMap();
        for(int x = 1; x <= 20; x++) {
            randomOption = minimum + (int)(Math.random()*maximum);
            groupName = (String) "group " + x;
            keyValues.add(new UifOptionGroupLabel(groupName));
            for(int y = 1; y <= randomOption; y++) {
                optionName = (String) "sub" + x + "_" + y;
                keyValues.add(new UifKeyValue(optionName, optionName));
            }
        }
        return keyValues;
    }

    /**
     * @return the blankOption
     */
    public boolean isBlankOption() {
        return this.blankOption;
    }

    /**
     * @param blankOption the blankOption to set
     */
    public void setBlankOption(boolean blankOption) {
        this.blankOption = blankOption;
    }

}
