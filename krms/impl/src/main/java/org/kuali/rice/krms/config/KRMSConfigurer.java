/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.config;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.impl.config.module.ModuleConfigurer;

/**
 * This class handles the Spring based KRMS configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KRMSConfigurer extends ModuleConfigurer {

	private static final String KRMS_SPRING_BEANS_PATH = "classpath:org/kuali/rice/krms/config/KRMSSpringBeans.xml";
    private static final String KRMS_KSB_SPRING_BEANS_PATH = "classpath:org/kuali/rice/krms/config/KRMSServiceBusSpringBeans.xml";

	
	@Override
	public List<String> getPrimarySpringFiles() {
		final List<String> springFileLocations = new ArrayList<String>();
		springFileLocations.add( KRMS_SPRING_BEANS_PATH );

//        if ( isExposeServicesOnBus() ) {
//		    springFileLocations.add(KRMS_KSB_SPRING_BEANS_PATH);
//		}

		return springFileLocations;
	}
	
}
