/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krad.uif.control;

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifKeyValuesFinderBase extends KeyValuesBase implements UifKeyValuesFinder {

    private boolean addBlankOption;

    public UifKeyValuesFinderBase() {
        addBlankOption = true;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.UifKeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.UifKeyValuesFinder#isAddBlankOption()
     */
    public boolean isAddBlankOption() {
        return addBlankOption;
    }

    /**
     * Setter for the addBlankOption indicator
     *
     * @param addBlankOption
     */
    public void setAddBlankOption(boolean addBlankOption) {
        this.addBlankOption = addBlankOption;
    }
}
