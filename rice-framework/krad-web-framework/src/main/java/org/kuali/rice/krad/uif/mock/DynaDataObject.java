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
package org.kuali.rice.krad.uif.mock;

import java.io.Serializable;
import java.util.Map;

/**
 * Data object class that has generic data map.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DynaDataObject implements Serializable {
    private static final long serialVersionUID = 2083672967298199749L;

    private Map<String, Object> data;
    private Map<String, Boolean> booleanData;

    /**
     * Default constructor.
     */
    public DynaDataObject() {
    }

    /**
     * Map containing non-boolean data for the view.
     *
     * @return map where key is property name and value is the property value
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @see DynaDataObject#getData()
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Map containing boolean data for the view.
     *
     * @return map where key is property name and value is the property value
     */
    public Map<String, Boolean> getBooleanData() {
        return booleanData;
    }

    /**
     * @see DynaDataObject#getBooleanData()
     */
    public void setBooleanData(Map<String, Boolean> booleanData) {
        this.booleanData = booleanData;
    }
}
