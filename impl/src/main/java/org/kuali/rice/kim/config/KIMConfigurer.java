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
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.config.event.RiceConfigEventListener;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * This class handles the Spring based KIM configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KIMConfigurer extends ModuleConfigurer {
	private static final String KIM_INTERFACE_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMInterfaceSpringBeans.xml";
	private static final String KIM_IMPL_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMImplementationSpringBeans.xml";
	
	private List<RiceConfigEventListener> configEventListeners = new ArrayList<RiceConfigEventListener>();

	/**
	 * 
	 */
	public KIMConfigurer() {
		super();
		setModuleName( "KIM" );
		setHasWebInterface( true );
	}

	@Override
	public String getSpringFileLocations() {
		if ( getRunMode().equals( LOCAL_RUN_MODE ) || getRunMode().equals( EMBEDDED_RUN_MODE ) ) {
			return KIM_INTERFACE_SPRING_BEANS_PATH+","+KIM_IMPL_SPRING_BEANS_PATH;
		}
		return KIM_INTERFACE_SPRING_BEANS_PATH;
	}
	
	public void registerConfigEventListener(RiceConfigEventListener listener) {
		configEventListeners.add(listener);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.config.ModuleConfigurer#onEvent(org.kuali.rice.core.config.event.RiceConfigEvent)
	 */
	@Override
	public void onEvent(RiceConfigEvent event) throws Exception {
		super.onEvent(event);
		for (RiceConfigEventListener listener : configEventListeners) {
			listener.onEvent(event);
		}
	}

}
