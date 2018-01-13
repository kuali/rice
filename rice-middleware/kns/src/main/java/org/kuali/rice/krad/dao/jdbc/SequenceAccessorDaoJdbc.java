/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.dao.jdbc;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.dao.SequenceAccessorDao;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springmodules.orm.ojb.OjbFactoryUtils;

import javax.sql.DataSource;

/**
 * This class uses the KualiDBPlatform to get the next number from a given sequence.
 */
@Deprecated
public class SequenceAccessorDaoJdbc extends PlatformAwareDaoBaseJdbc implements SequenceAccessorDao {
	private KualiModuleService kualiModuleService;

	private Long nextAvailableSequenceNumber(String sequenceName, Class clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if ( moduleService == null )
        	throw new ConfigurationException("moduleService is null");
        	        	
        ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
        if ( moduleConfig == null )
        	throw new ConfigurationException("moduleConfiguration is null");

        String dataSourceName = moduleConfig.getDataSourceName();

        if (StringUtils.isEmpty(dataSourceName)) {
            return nextAvailableSequenceNumber(sequenceName);
        } else {
            PBKey key = new PBKey(dataSourceName);
            PersistenceBroker broker = OjbFactoryUtils.getPersistenceBroker(key, false);

            if (broker != null) {
                return getDbPlatform().getNextValSQL(sequenceName, broker);
            } else {
                throw new ConfigurationException("PersistenceBroker is null");
            }
        }
	}

    private Long nextAvailableSequenceNumber(String sequenceName) {
        DataSource dataSource = (DataSource) ConfigContext.getCurrentContextConfig().getObject(KRADConstants.KRAD_APPLICATION_DATASOURCE);
        if (dataSource == null) {
            dataSource = KRADServiceLocator.getKradApplicationDataSource();
        }
        return Long.valueOf(MaxValueIncrementerFactory.getIncrementer(dataSource, sequenceName).nextLongValue());
    }
	
	public Long getNextAvailableSequenceNumber(String sequenceName, Class clazz) {
        if (!LegacyUtils.useLegacy(clazz)) {
            throw new ConfigurationException("SequenceAccessorService should not be used with new data framework! Use "
                    + MaxValueIncrementerFactory.class.getName() + " instead.");
        }

		// There are situations where a module hasn't been configured with
		// a dataSource.  In these cases, this method would have previously
		// thrown an error.  Instead, we've opted to factor out the code,
		// catch any configuration-related exceptions, and if one occurs,
		// attempt to use the dataSource associated with KNS. -- tbradford
		
		try {
			return nextAvailableSequenceNumber(sequenceName, clazz);
		}
		catch ( ConfigurationException e  ) {
	    	// Use kradApplication.datasource to get the dataSource associated with KNS
			return nextAvailableSequenceNumber(sequenceName);
		}
	}
	
    /**
     * @see org.kuali.rice.krad.dao.SequenceAccessorDao#getNextAvailableSequenceNumber(java.lang.String)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName) {
    	// Use kradApplication.datasource to get the dataSource associated with KNS
    	return nextAvailableSequenceNumber(sequenceName);
    }
    
    private KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) 
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        return kualiModuleService;
    }
}
