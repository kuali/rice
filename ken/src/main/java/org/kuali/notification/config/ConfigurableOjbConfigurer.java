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
package org.kuali.notification.config;

import org.kuali.rice.ojb.BaseOjbConfigurer;

/**
 * BaseOjbConfigurer subclass which allows configuration of the jcd aliases
 * and OJB repository metadata location.
 * TODO: pull functionality up into base
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ConfigurableOjbConfigurer extends BaseOjbConfigurer {
    private String[] jcdAliases;
    private String metadataLocation;

    public ConfigurableOjbConfigurer(String moduleName) {
	this.metadataLocation = "classpath:OJB-repository-" + moduleName.toLowerCase() + ".xml";
	this.jcdAliases = new String[] { moduleName.toLowerCase() + "DataSource" };
    }

    public ConfigurableOjbConfigurer(String metadataLocation, String[] jcdAliases) {
	this.metadataLocation = metadataLocation;
	this.jcdAliases = jcdAliases;
    }
    
    /**
     * @see org.kuali.rice.ojb.BaseOjbConfigurer#getJcdAliases()
     */
    @Override
    protected String[] getJcdAliases() {
	return jcdAliases;
    }

    /**
     * @see org.kuali.rice.ojb.BaseOjbConfigurer#getMetadataLocation()
     */
    @Override
    protected String getMetadataLocation() {
	return metadataLocation;
    }

    public void setJcdAliases(String[] jcdAliases) {
        this.jcdAliases = jcdAliases;
    }

    public void setMetadataLocation(String metaDataLocation) {
        this.metadataLocation = metaDataLocation;
    }
}