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
package org.kuali.rice.location.config;

import org.kuali.rice.core.impl.config.module.ModuleConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the Spring based KIM configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationConfigurer extends ModuleConfigurer {
	private static final String LOCATION_KSB_SPRING_BEANS_PATH = "classpath:org/kuali/rice/location/config/LocationServiceBusSpringBeans.xml";
	
	@Override
	public List<String> getPrimarySpringFiles() {
		final List<String> springFileLocations = new ArrayList<String>(super.getPrimarySpringFiles());

		if ( isExposeServicesOnBus() ) {
		    springFileLocations.add(LOCATION_KSB_SPRING_BEANS_PATH);
		}
		return springFileLocations;
	}
}
