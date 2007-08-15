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
// Created on Aug 29, 2006

package org.kuali.rice.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.InitializingBean;

/**
 * A simple node settings store that backs the settings with a properties file
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class SimpleNodeSettingsStore implements NodeSettings, InitializingBean {

    private static final Logger LOG = Logger.getLogger(SimpleNodeSettingsStore.class);

    private boolean enabled;
    private String propertiesPath;
    private Properties properties;

    public void afterPropertiesSet() throws Exception {
        this.enabled = false;
    	if (StringUtils.isEmpty(this.propertiesPath)) {
    	    this.propertiesPath = Core.getCurrentContextConfig().getProperty(Config.NODE_PROPERTIES_PATH);
    	}
    	// if it's still empty, then node settings are not available
    	if (StringUtils.isEmpty(this.propertiesPath)) {
    		LOG.warn("No node-level settings are available, the NodeSettingsStore will be disabled.");
    		this.properties = new Properties();
    	} else {
    	    this.properties = load();
    	}
    }

    protected Properties load() throws IOException {
    	Properties p = new Properties();
    	File file = new File(this.propertiesPath);
    	try {
    		if (!file.exists()) {
    			LOG.warn("Properties path '" + this.propertiesPath + "' does not exist, attempting to create it.");
    			if (!file.getParentFile().exists()) {
    				file.getParentFile().mkdirs();
    			}
    			file.createNewFile();
    		}
            FileInputStream fis = new FileInputStream(this.propertiesPath);
            try {
                p.load(fis);
                this.enabled = true;
            } finally {
                fis.close();
            }
        } catch (IOException e) {
            LOG.warn("Properties path '" + this.propertiesPath + "' does not exist despite efforts to create it at: " + file.getAbsolutePath(), e);
        }
        return p;
    }

    public void setPropertiesPath(String path) {
        this.propertiesPath = path;
    }

    protected synchronized void store(Properties p) {
        try {
        	FileOutputStream fos = new FileOutputStream(this.propertiesPath);
        	try {
        		p.store(fos, null);
        	} finally {
        		fos.close();
        	}
        } catch (IOException e) {
        	throw new ConfigurationException("Failed to persist node-specific settings.", e);
        }
    }

    public synchronized String getSetting(String key) {
    	if (!isEnabled()) {
    		LOG.warn("Node settings are not enabled, getSetting('"+key+"') is returning null.");
    		return null;
    	}
        return this.properties.getProperty(key);
    }

    public synchronized void setSetting(String key, String value) {
    	if (!isEnabled()) {
    		LOG.warn("Node settings are not enabled, setSetting('"+key+"', '"+value+"') will have no effect.");
    		return;
    	}
    	this.properties.put(key, value);
        store(this.properties);
    }

    public synchronized String removeSetting(String key) {
    	if (!isEnabled()) {
    		LOG.warn("Node settings are not enabled, removeSetting('"+key+"') will have no effect.");
    		return null;
    	}
        String property = (String)this.properties.remove(key);
        store(this.properties);
        return property;
    }

    public synchronized Map<String, String> getSettings() {
    	if (!isEnabled()) {
    		LOG.warn("Node settings are not enabled, returning empty map for getSettings().");
    		return Collections.emptyMap();
    	}
    	Map<String, String> settings = new HashMap<String, String>();
    	for (Object key : this.properties.keySet()) {
    		settings.put((String)key, (String)this.properties.get(key));
    	}
        return Collections.unmodifiableMap(settings);
    }

    public synchronized boolean isEnabled() {
    	return this.enabled;
    }

}