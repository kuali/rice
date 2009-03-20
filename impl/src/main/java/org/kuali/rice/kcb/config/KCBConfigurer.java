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
package org.kuali.rice.kcb.config;

import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.ojb.BaseOjbConfigurer;

/**
 * This class handles the Spring based KCB configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KCBConfigurer extends ModuleConfigurer {
    private static final String MODULE_NAME = "kcb";
 
    /**
     * Constructs a KCBConfigurer
     */
    public KCBConfigurer() {
        super();
        setModuleName(MODULE_NAME);
        LOG.info("KCBConfigurer constructed");
    }

}