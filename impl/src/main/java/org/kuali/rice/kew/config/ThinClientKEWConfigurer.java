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
package org.kuali.rice.kew.config;

import org.kuali.rice.core.config.RiceConfigurerBase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.config.KIMThinClientConfigurer;
import org.kuali.rice.ksb.messaging.config.KSBThinClientConfigurer;

/**
 * A configurer which configures KEW Thin-Client mode.
 *      
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ThinClientKEWConfigurer extends RiceConfigurerBase {
	
	private KEWConfigurer kewConfigurer;
	
	public ThinClientKEWConfigurer() {
		setServiceNamespace(KEWConstants.KEW_MESSAGING_ENTITY);
		// thin client allows us to still have access to the DigitalSignatureService but not use the full capabilities of the bus
		getModules().add(new KSBThinClientConfigurer());		
		
		this.kewConfigurer = new KEWConfigurer();
		
		// If this flag is not set, KEWConfigurer will need a database connection and other
		// things we haven't configured.
		kewConfigurer.setRunMode(KEWConfigurer.THIN_RUN_MODE);
		kewConfigurer.setClientProtocol(KEWConstants.WEBSERVICE_CLIENT_PROTOCOL);
		
		getModules().add(kewConfigurer);
		
		getModules().add(new KIMThinClientConfigurer());
	}

	@Override
	protected void addModulesResourceLoaders() throws Exception {
		// TODO: this seems like a total hack the way this is happening, see the addModulesResourceLoaders
		// method RiceConfigurer as well
		kewConfigurer.getResourceLoaderToRegister();
	}
	
	
}
