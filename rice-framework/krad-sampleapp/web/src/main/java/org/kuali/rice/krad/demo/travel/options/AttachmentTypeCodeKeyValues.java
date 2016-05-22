/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.options;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.List;

public class AttachmentTypeCodeKeyValues extends KeyValuesBase {

    private static final long serialVersionUID = -4843103151826286724L;
    private boolean blankOption;

	@Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        if (blankOption) {
            keyValues.add(new ConcreteKeyValue("", ""));
        }

        keyValues.add(new ConcreteKeyValue("TXT", "TXT"));
        keyValues.add(new ConcreteKeyValue("PDF", "PDF"));
        keyValues.add(new ConcreteKeyValue("IMG", "IMG"));
        keyValues.add(new ConcreteKeyValue("OTH", "OTH"));

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
