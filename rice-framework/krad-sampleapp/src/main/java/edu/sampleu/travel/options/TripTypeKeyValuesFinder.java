/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This class provides trip types for the travel and entertainment module
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TripTypeKeyValuesFinder extends UifKeyValuesFinderBase {

    public enum TripType {
        IS("IS", "In State"), IN("IN", "International"), OS("OS", "Out of State");

        private final String code;
        private final String label;

        TripType(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

    }

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> options = new ArrayList<KeyValue>();

        for (TripType type : EnumSet.allOf(TripType.class)) {
            options.add(new ConcreteKeyValue(type.getCode(), type.getLabel()));
        }

        return options;
    }

}
