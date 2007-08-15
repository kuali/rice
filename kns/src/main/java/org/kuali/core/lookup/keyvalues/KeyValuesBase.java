/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.web.ui.KeyLabelPair;

/**
 * This class returns list of chart key value pairs.
 * 
 * 
 */
public abstract class KeyValuesBase implements KeyValuesFinder {
    public Collection getOptionLabels() {
        List optionLabels = new ArrayList();

        List keyLabels = getKeyValues();
        for (Iterator iter = keyLabels.iterator(); iter.hasNext();) {
            KeyLabelPair keyLabel = (KeyLabelPair) iter.next();
            optionLabels.add(keyLabel.getLabel());
        }
        return optionLabels;
    }

    public Collection getOptionValues() {
        List optionValues = new ArrayList();

        List keyLabels = getKeyValues();
        for (Iterator iter = keyLabels.iterator(); iter.hasNext();) {
            KeyLabelPair keyLabel = (KeyLabelPair) iter.next();
            optionValues.add(keyLabel.getKey());
        }
        return optionValues;
    }

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyLabelMap()
     */
    public Map getKeyLabelMap() {
        Map keyLabelMap = new HashMap();

        List keyLabels = getKeyValues();
        for (Iterator iter = keyLabels.iterator(); iter.hasNext();) {
            KeyLabelPair keyLabel = (KeyLabelPair) iter.next();
            keyLabelMap.put(keyLabel.getKey(), keyLabel.getLabel());
        }

        return keyLabelMap;
    }

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyLabel(java.lang.String)
     */
    public String getKeyLabel(String key) {
        Map keyLabelMap = getKeyLabelMap();

        if (keyLabelMap.containsKey(key)) {
            return (String) keyLabelMap.get(key);
        }
        else {
            return "";
        }
    }
}