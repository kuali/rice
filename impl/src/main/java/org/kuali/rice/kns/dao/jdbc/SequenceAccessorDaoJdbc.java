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
package org.kuali.rice.kns.dao.jdbc;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.dao.SequenceAccessorDao;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.springmodules.orm.ojb.OjbFactoryUtils;

/**
 * This class uses the KualiDBPlatform to get the next number from a given sequence.
 */
public class SequenceAccessorDaoJdbc extends PlatformAwareDaoBaseJdbc implements SequenceAccessorDao {
	private KualiModuleService kualiModuleService;
	
	public Long getNextAvailableSequenceNumber(String sequenceName, 
			Class<? extends BusinessObject> clazz) {
		
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if ( moduleService == null )
        	throw new ConfigurationException("moduleService is null");
        	        	
        ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
        if ( moduleConfig == null )
        	throw new ConfigurationException("moduleConfiguration is null");
        
    	String dataSourceName = moduleConfig.getDataSourceName();
    	EntityManager entityManager = moduleConfig.getEntityManager();

        if ( StringUtils.isEmpty(dataSourceName) ) 
        	throw new ConfigurationException("dataSourceName is not set");       	
        	
    	if ( OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled() ) {
            if ( entityManager != null ) 
            	return getDbPlatform().getNextValSQL(sequenceName, entityManager);
            else
            	throw new ConfigurationException("EntityManager is null");
        } 
    	else {
    		PBKey key = new PBKey(dataSourceName);
    		PersistenceBroker broker = OjbFactoryUtils.getPersistenceBroker(key, true);
    		if ( broker != null )
    			return getDbPlatform().getNextValSQL(sequenceName, broker);
    		else
    			throw new ConfigurationException("PersistenceBroker is null");            			
        }
	}
	
    /**
     * @see org.kuali.rice.kns.dao.SequenceAccessorDao#getNextAvailableSequenceNumber(java.lang.String)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName) {
    	// Use DocumentHeader to get the dataSourceName associated with KNS
    	return getNextAvailableSequenceNumber(sequenceName, DocumentHeader.class);
    }
    
    private KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) 
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        return kualiModuleService;
    }
}