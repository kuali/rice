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
package org.kuali.rice.krad.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

/**
 * ValuesFinder that derives values directly from a Java enum.
 * KeyValues are provided in enum definition order, enum name
 * is the key, capitalized lowercase enum name is the label.
 */
public class EnumValuesFinder extends KeyValuesBase {
    private Class<? extends Enum> enumeration;

    public EnumValuesFinder(Class<? extends Enum> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        for (Enum enumval: enumeration.getEnumConstants()) {
            labels.add(new ConcreteKeyValue(getEnumKey(enumval), getEnumLabel(enumval)));
        }
        return labels;
    }

    /**
     * Derives a key value from an enum
     */
    protected String getEnumKey(Enum enm) {
        return enm.name();
    }

    /**
     * Derives a label value from an enum
     */
    protected String getEnumLabel(Enum enm) {
        return WordUtils.capitalize(enm.name().toLowerCase());
    }
}