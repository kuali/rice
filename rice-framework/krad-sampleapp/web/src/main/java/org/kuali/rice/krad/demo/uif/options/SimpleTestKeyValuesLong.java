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

        if (blankOption) {
            keyValues.add(new ConcreteKeyValue("", ""));
        }

        int x = 1;
        int y =1;
        HashMap<String, Vector<String>> data = new HashMap();
            while(x<5){
                data.put((String) "group" + x, new Vector());
                while(y<5){
                    data.get((String) "group" + x).add((String) "sub" + y);
                    y++;
                }
                y=1;
                x++;
            }
            for (String key : data.keySet()) {
                keyValues.add(new UifOptionGroupLabel(key));
                for (String subs : data.get(key)) {
                    keyValues.add(new UifKeyValue(subs, subs));
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
