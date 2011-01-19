/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.config;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.RunMode;

/**
 * This class handles the Spring based KIM configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KIMConfigurer extends ModuleConfigurer {
	private static final String KIM_INTERFACE_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMInterfaceSpringBeans.xml";
	private static final String KIM_IMPL_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMImplementationSpringBeans.xml";
	private static final String KIM_KSB_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMServiceBusSpringBeans.xml";
	private static final String KIM_KSB_SOAP_DEFAULT_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMServiceBusSOAPDefaultSpringBeans.xml";
	private static final String KIM_UI_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMUserInterfaceSpringBeans.xml";
	
	@Override
	public List<String> getPrimarySpringFiles() {
		final List<String> springFileLocations = new ArrayList<String>();
		springFileLocations.add( KIM_INTERFACE_SPRING_BEANS_PATH );
		if ( getRunMode().equals( RunMode.LOCAL ) || getRunMode().equals( RunMode.EMBEDDED ) ) {
			springFileLocations.add(KIM_IMPL_SPRING_BEANS_PATH);
		}
		if ( isExposeServicesOnBus() ) {
			if (isSetSOAPServicesAsDefault()) {
				springFileLocations.add(KIM_KSB_SOAP_DEFAULT_SPRING_BEANS_PATH);
			} else {
				springFileLocations.add(KIM_KSB_SPRING_BEANS_PATH);
			}
		}
		if ( isIncludeUserInterfaceComponents() ) {
			springFileLocations.add(KIM_UI_SPRING_BEANS_PATH);
		}
		return springFileLocations;
	}
}
