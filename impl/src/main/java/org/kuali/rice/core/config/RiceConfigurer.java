/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.core.config;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kcb.config.KCBConfigurer;
import org.kuali.rice.ken.config.KENConfigurer;
import org.kuali.rice.kew.config.KEWConfigurer;
import org.kuali.rice.kim.config.KIMConfigurer;
import org.kuali.rice.kns.config.KNSConfigurer;
import org.kuali.rice.ksb.messaging.config.KSBConfigurer;

/**
 * Used to configure common Rice configuration properties.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceConfigurer extends RiceConfigurerBase {
	
	private KSBConfigurer ksbConfigurer;
	private KNSConfigurer knsConfigurer;
	private KIMConfigurer kimConfigurer;
	private KCBConfigurer kcbConfigurer;
	private KEWConfigurer kewConfigurer;
	private KENConfigurer kenConfigurer;
		
	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#start()
	 */
	@Override
	public void start() throws Exception {
		//Add the configurers to modules list in the desired sequence.
		// and at the beginning if any other modules were specified

		int index = 0;
		if(getKsbConfigurer()!=null) getModules().add(index++,getKsbConfigurer());
		if(getKnsConfigurer()!=null) getModules().add(index++,getKnsConfigurer());
		if(getKimConfigurer()!=null) getModules().add(index++,getKimConfigurer());
		if(getKcbConfigurer()!=null) getModules().add(index++,getKcbConfigurer());
		if(getKewConfigurer()!=null) getModules().add(index++,getKewConfigurer());
		if(getKenConfigurer()!=null) getModules().add(index++,getKenConfigurer());
		
		addConfigToModules();
		// now execute the super class's start method which will initialize configuration and resource loaders
		super.start();
	}
	
	private void addConfigToModules() {
		for (ModuleConfigurer module : getModules()) {
			module.setConfig(getRootConfig());
		}
	}
	
	/**
	 * 
	 * This method decides the sequence of module resource loaders to be added to global resource loader (GRL).
	 * It asks the individual module configurers for the resource loader they want to register and adds them to GRL.
	 * 
	 * <p>TODO: the implementation of this method seems like a total HACK, it seems like the implementation on
	 * RiceConfigurerBase makes more sense since it is more general, also, very strange how the
	 * getResourceLoaderToRegister method on KEWConfigurer is side-affecting.  This whole thing looks like a mess.
	 * Somebody untangle this, please!
	 * 
	 * @throws Exception
	 */
	@Override
	protected void addModulesResourceLoaders() throws Exception {
		if(getKewConfigurer()!=null){
			// TODO: Check - In the method getResourceLoaderToRegister of KewConfigurer, 
			// does the call registry.start() depend on the preceding line GlobalResourceLoader.addResourceLoader(coreResourceLoader)?
			// Ideally we would like to register the resource loader into GRL over here
			getKewConfigurer().getResourceLoaderToRegister();
		}
		if(getKsbConfigurer()!=null){
			GlobalResourceLoader.addResourceLoader(getKsbConfigurer().getResourceLoaderToRegister());
		}
	}

	/**
	 * @return the kcbConfigurer
	 */
	public KCBConfigurer getKcbConfigurer() {
		return this.kcbConfigurer;
	}

	/**
	 * @param kcbConfigurer the kcbConfigurer to set
	 */
	public void setKcbConfigurer(KCBConfigurer kcbConfigurer) {
		this.kcbConfigurer = kcbConfigurer;
	}

	/**
	 * @return the kenConfigurer
	 */
	public KENConfigurer getKenConfigurer() {
		return this.kenConfigurer;
	}

	/**
	 * @param kenConfigurer the kenConfigurer to set
	 */
	public void setKenConfigurer(KENConfigurer kenConfigurer) {
		this.kenConfigurer = kenConfigurer;
	}

	/**
	 * @return the kewConfigurer
	 */
	public KEWConfigurer getKewConfigurer() {
		return this.kewConfigurer;
	}

	/**
	 * @param kewConfigurer the kewConfigurer to set
	 */
	public void setKewConfigurer(KEWConfigurer kewConfigurer) {
		this.kewConfigurer = kewConfigurer;
	}

	/**
	 * @return the kimConfigurer
	 */
	public KIMConfigurer getKimConfigurer() {
		return this.kimConfigurer;
	}

	/**
	 * @param kimConfigurer the kimConfigurer to set
	 */
	public void setKimConfigurer(KIMConfigurer kimConfigurer) {
		this.kimConfigurer = kimConfigurer;
	}

	/**
	 * @return the knsConfigurer
	 */
	public KNSConfigurer getKnsConfigurer() {
		return this.knsConfigurer;
	}

	/**
	 * @param knsConfigurer the knsConfigurer to set
	 */
	public void setKnsConfigurer(KNSConfigurer knsConfigurer) {
		this.knsConfigurer = knsConfigurer;
	}

	/**
	 * @return the ksbConfigurer
	 */
	public KSBConfigurer getKsbConfigurer() {
		return this.ksbConfigurer;
	}

	/**
	 * @param ksbConfigurer the ksbConfigurer to set
	 */
	public void setKsbConfigurer(KSBConfigurer ksbConfigurer) {
		this.ksbConfigurer = ksbConfigurer;
	}

}
