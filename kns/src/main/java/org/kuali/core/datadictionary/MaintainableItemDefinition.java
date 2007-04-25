/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MaintainableItemDefinition
 * 
 * 
 */
public abstract class MaintainableItemDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(MaintainableItemDefinition.class);

    private String name;

    public MaintainableItemDefinition() {
        LOG.debug("creating new MaintainableItemDefinition");
    }


    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name to the given value.
     * 
     * @param name
     * @throws IllegalArgumentException if the given name is blank
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        LOG.debug("calling setName '" + name + "'");

        this.name = name;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintainableItemDefinition for item " + getName();
    }
}