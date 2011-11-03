/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.component;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of <code>Configurable</code> that contains a Map<String, String> for holding
 * property expressions
 *
 * <p>
 * Should be extended by other UIF classes (such as <code>Component</code> or <code>LayoutManager</code>) to
 * provide property expression support
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigurableBase implements Configurable {
    private Map<String, String> propertyExpressions;

    public ConfigurableBase() {
        propertyExpressions = new HashMap<String, String>();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ConfigurableBase#getPropertyExpressions
     */
    public Map<String, String> getPropertyExpressions() {
        return propertyExpressions;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ConfigurableBase#setPropertyExpressions
     */
    public void setPropertyExpressions(Map<String, String> propertyExpressions) {
        this.propertyExpressions = propertyExpressions;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ConfigurableBase#getPropertyExpression
     */
    public String getPropertyExpression(String propertyName) {
        if (this.propertyExpressions.containsKey(propertyName)) {
            return this.propertyExpressions.get(propertyName);
        }

        return null;
    }
}
