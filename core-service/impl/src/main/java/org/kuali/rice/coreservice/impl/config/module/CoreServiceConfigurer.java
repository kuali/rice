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
package org.kuali.rice.coreservice.impl.config.module;

import org.kuali.rice.core.framework.config.module.ModuleConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a place to put some of the common configuration logic that used to be done by the RiceConfigurer.
 */
public class CoreServiceConfigurer extends ModuleConfigurer {
    private static final String CLIENT_SPRING_BEANS_PATH = "classpath:org/kuali/rice/coreservice/config/CORESERVICESpringBeans.xml";
    private static final String CLIENT_KSB_SPRING_BEANS_PATH = "classpath:org/kuali/rice/coreservice/config/CoreServiceBusSpringBeans.xml";

	@Override
	public List<String> getPrimarySpringFiles() {
		final List<String> springFileLocations = new ArrayList<String>();
		springFileLocations.add( CLIENT_SPRING_BEANS_PATH );

		if ( isExposeServicesOnBus() ) {
		    springFileLocations.add(CLIENT_KSB_SPRING_BEANS_PATH);
		}
		return springFileLocations;
	}

}
