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
package org.kuali.rice.kew.config;

/**
 * A configurer which configures KEW Thin-Client mode.
 *      
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
//FIXME: RICE MODULARITY
// need to fix this class at some point
public abstract class ThinClientKEWConfigurer /*extends RiceConfigurerBase*/ {
//	
//	private KEWConfigurer kewConfigurer;
//	
//	public ThinClientKEWConfigurer() {
//		// thin client allows us to still have access to the DigitalSignatureService but not use the full capabilities of the bus
//		getModules().add(new KSBThinClientConfigurer());		
//		
//		this.kewConfigurer = new KEWConfigurer();
//		
//		getModules().add(kewConfigurer);
//		
//	}
//
//		getModules().add(new KIMThinClientConfigurer());
//	@Override
//	protected void addModulesResourceLoaders() throws Exception {
//		// TODO: this seems like a total hack the way this is happening, see the addModulesResourceLoaders
//		// method RiceConfigurer as well
//		kewConfigurer.getResourceLoadersToRegister();
//	}
	
	
}
