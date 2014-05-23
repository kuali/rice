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
package org.kuali.rice.core.api.config.property;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;

/**
 * Uses the deploy time configuration variables to locate a string for replacement.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigStrLookup extends StrLookup {

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookup(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }

        return ConfigContext.getCurrentContextConfig().getProperty(propertyName);
    }

}