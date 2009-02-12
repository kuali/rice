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
package org.kuali.rice.kim.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityDefaultInfoCacheImpl;
import org.kuali.rice.kim.service.IdentityCacheService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityCacheServiceImpl implements IdentityCacheService {
	private static final Logger LOG = Logger.getLogger( IdentityCacheServiceImpl.class );
	
	private BusinessObjectService businessObjectService;

	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	public KimEntityDefaultInfo getEntityDefaultInfoFromPersistentCache( String entityId ) {
    	Map<String,String> criteria = new HashMap<String, String>(1);
    	criteria.put(KimConstants.PrimaryKeyConstants.ENTITY_ID, entityId);
    	KimEntityDefaultInfoCacheImpl cachedValue = (KimEntityDefaultInfoCacheImpl)getBusinessObjectService().findByPrimaryKey(KimEntityDefaultInfoCacheImpl.class, criteria);
    	if ( cachedValue == null ) {
    		return null;
    	}
    	return cachedValue.convertCacheToEntityDefaultInfo();
    }

    public KimEntityDefaultInfo getEntityDefaultInfoFromPersistentCacheByPrincipalId( String principalId ) {
    	Map<String,String> criteria = new HashMap<String, String>(1);
    	criteria.put("principals.principalId", principalId);
    	KimEntityDefaultInfoCacheImpl cachedValue = (KimEntityDefaultInfoCacheImpl)getBusinessObjectService().findByPrimaryKey(KimEntityDefaultInfoCacheImpl.class, criteria);
    	if ( cachedValue == null ) {
    		return null;
    	}
    	return cachedValue.convertCacheToEntityDefaultInfo();
    }

    @SuppressWarnings("unchecked")
	public KimEntityDefaultInfo getEntityDefaultInfoFromPersistentCacheByPrincipalName( String principalName ) {
    	Map<String,String> criteria = new HashMap<String, String>(1);
    	criteria.put("principals.principalName", principalName);
    	Collection<KimEntityDefaultInfoCacheImpl> entities = getBusinessObjectService().findMatching(KimEntityDefaultInfoCacheImpl.class, criteria);
    	if ( entities.isEmpty()  ) {
    		return null;
    	}
    	return entities.iterator().next().convertCacheToEntityDefaultInfo();
    }

    public void saveDefaultInfoToCache( KimEntityDefaultInfo entity ) {
		KSBServiceLocator.getThreadPool().execute( new SaveEntityDefaultInfoToCacheRunnable( entity ) );
    }
    
	// store the person to the database cache

	// but do this an alternate thread to prevent transaction issues since this service is non-transactional


	private class SaveEntityDefaultInfoToCacheRunnable implements Runnable {
		private KimEntityDefaultInfo entity;
		/**
		 * 
		 */
		public SaveEntityDefaultInfoToCacheRunnable( KimEntityDefaultInfo entity ) {
			this.entity = entity;
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				PlatformTransactionManager transactionManager = KNSServiceLocator.getTransactionManager();
				TransactionTemplate template = new TransactionTemplate(transactionManager);
				template.execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						getBusinessObjectService().save( new KimEntityDefaultInfoCacheImpl( entity ) );
						return null;
					}
				});
			} catch (Throwable t) {
				LOG.error("Failed to load transaction manager.", t);
			}
		}
	}

}
