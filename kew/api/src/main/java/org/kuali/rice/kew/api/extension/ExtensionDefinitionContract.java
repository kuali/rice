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
package org.kuali.rice.kew.api.extension;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.Map;

/**
 * Defines an extension to some component of Kuali Enterprise Workflow.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ExtensionDefinitionContract extends Identifiable, Versioned {

    String getName();

    String getApplicationId();

    String getLabel();

    String getDescription();

    String getType();

    /**
     * Retrieves the resource descriptor for this extension.  This gives the calling code the
     * information it needs to locate and execute the extension resource if it needs to.
     *
     * @return the resource descriptor for this extension, this value should never be blank or null
     */
    String getResourceDescriptor();

    Map<String, String> getConfiguration();

}
