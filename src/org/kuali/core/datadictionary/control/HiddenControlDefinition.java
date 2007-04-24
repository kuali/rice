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

package org.kuali.core.datadictionary.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A single HTML hidden field control.
 * 
 * 
 */
public class HiddenControlDefinition extends ControlDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(HiddenControlDefinition.class);

    public HiddenControlDefinition() {
        LOG.debug("creating new HiddenControlDefinition");
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isHidden()
     */
    public boolean isHidden() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "HiddenControlDefinition";
    }
}