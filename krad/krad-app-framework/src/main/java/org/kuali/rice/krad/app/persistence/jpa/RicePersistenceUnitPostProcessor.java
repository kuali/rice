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
package org.kuali.rice.krad.app.persistence.jpa;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import javax.sql.DataSource;

public class RicePersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {
	static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RicePersistenceUnitPostProcessor.class);
	
	public static final String KNS_APPLICATION_PERSISTENCE_UNIT_NAME = "kns-application-unit";
	public static final String KNS_SERVER_PERSISTENCE_UNIT_NAME = "kns-server-unit";

	private DataSource jtaDataSource;
    
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo mutablePersistenceUnitInfo) {
        mutablePersistenceUnitInfo.setJtaDataSource(getJtaDataSource());
        addKNSManagedClassNames(mutablePersistenceUnitInfo);
        if (mutablePersistenceUnitInfo.getPersistenceUnitName().equals(KNS_APPLICATION_PERSISTENCE_UNIT_NAME) || mutablePersistenceUnitInfo.getPersistenceUnitName().equals(KNS_SERVER_PERSISTENCE_UNIT_NAME)) {
        	addRiceManagedClassNamesToKNSPersistenceUnit(mutablePersistenceUnitInfo);
        }
    }
    
    /**
     * 
     * Adds all the KNS Managed entities to the persistence unit - which is important, becuase all
     * persistence units get the KNS entities to manage
     * 
     * @param mutablePersistenceUnitInfo
     */
    public void addKNSManagedClassNames(MutablePersistenceUnitInfo mutablePersistenceUnitInfo) {
    	addManagedClassNames(mutablePersistenceUnitInfo, new KNSPersistableBusinessObjectClassExposer());
    }
    
    /**
     * Adds the class names listed by exposed by the given exposer into the persistence unit
     * 
     * @param mutablePersistenceUnitInfo the persistence unit to add managed JPA entity class names to
     * @param exposer the exposer for class names to manage
     */
    public void addManagedClassNames(MutablePersistenceUnitInfo mutablePersistenceUnitInfo, PersistableBusinessObjectClassExposer exposer) {
    	for (String exposedClassName : exposer.exposePersistableBusinessObjectClassNames()) {
    		if (LOG.isDebugEnabled()) {
    			LOG.debug("JPA will now be managing class: "+exposedClassName);
    		}
    		mutablePersistenceUnitInfo.addManagedClassName(exposedClassName);
    	}
    }
    
    public void addRiceManagedClassNamesToKNSPersistenceUnit(MutablePersistenceUnitInfo mutablePersistenceUnitInfo) {
    	addManagedClassNames(mutablePersistenceUnitInfo, new RiceToNervousSystemBusinessObjectClassExposer());
    }

    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }
	
}
