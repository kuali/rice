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
package org.kuali.rice.ken.config;

import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.ojb.BaseOjbConfigurer;

/**
 * The KEN Configurer
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KENConfigurer extends ModuleConfigurer {
    public KENConfigurer() {
        super();
        setModuleName( "KEN" );
    }
	/**
     * Registers an OjbConfigurer and ResourceLoader for the module, adding it first to the GlobalResourceLoader.
     * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
     */
    @Override
    protected List<Lifecycle> loadLifecycles() throws Exception {
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info("Loading " + getModuleName() + " module lifecycles");
    	}
        List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
        lifecycles.add(new BaseOjbConfigurer(getModuleName()));
        return lifecycles;
    }

}
