/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package org.kuali.rice.config;

import java.util.Map;

/**
 * A local store for node-specific settings.  The use of the word "Node" here describes an instance of KEW
 * (running standalone or embedded).  In a clustered environment, it is sometimes useful for individual
 * nodes within the cluster to have their own settings.  Depending on system configuration this configuration
 * store may or may not be available for use.  If the node settings store is not availabe then calls to
 * query or modify the settings should be no-ops.  The availablily can be queried using the isEnabled method.
 * 
 * <p>Since Node Settings are runtime-mutable, it is important that implementations of this class be thread-safe.
 * 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 * @author Eric Westfall
 */
public interface NodeSettings {
	
	/**
	 * Retrieve the value of the setting with the given name.  Will return null if the setting with the
	 * given name does not exist or node settings are not enabled.
	 * 
	 * @return the value of the setting, null if the setting does not exis or node settings are not enabled
	 */
    public String getSetting(String name);
    
    /**
     * Set the value of the setting with the given name.  Has no effect if node settings are not enabled.
     */
    public void setSetting(String name, String value);
    
    /**
     * Remove the given setting from the node settings.  If the setting with the given name does not
     * exist or node settings are not enabled, then null will be returned.
     * 
     * @return return the value of the removed setting, null if the setting does not exist
     *         or node settings are not enabled 
     */
    public String removeSetting(String name);
    
    /**
	 * Returns the settings of this node as an immutable Map.  If the node settings store
	 * is not enabled, then an empty Map will be returned.  The Map
	 * returned by the getSettings method is thread-safe.
	 * @return
	 */
    public Map getSettings();
    
    /**
     * Returns true if node-specific settings are enabled, false otherwise.  In the case that node settings
     * are not enabled, the various accessor methods will effectively behave as no-ops.
     * 
     * @return true if node settings are enabled, false otherwise
     */
    public boolean isEnabled();
    
}