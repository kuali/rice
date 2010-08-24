/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.dao.proxy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.dao.impl.DocumentDaoJpa;
import org.kuali.rice.kns.dao.impl.DocumentDaoOjb;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentAdHocService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DocumentDaoProxy implements DocumentDao {

	private static Logger LOG = Logger.getLogger(DocumentDaoProxy.class);

    private BusinessObjectDao businessObjectDao;
    private DocumentDao documentDaoJpa;
    private DocumentDao documentDaoOjb;

    private static KualiModuleService kualiModuleService;
    private static Map<String, DocumentDao> documentDaoValues = new ConcurrentHashMap<String, DocumentDao>();

    private static final String DOCUMENT_AD_HOC_SERVICE_NAME = "documentAdHocService";

    private DocumentDao getDao(Class clazz) {
    	ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService != null) {
            ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
            String dataSourceName = "";
            EntityManager entityManager = null;
            if (moduleConfig != null) {
                dataSourceName = moduleConfig.getDataSourceName();
                entityManager = moduleConfig.getEntityManager();
            }

            if (StringUtils.isNotEmpty(dataSourceName)) {
                if (documentDaoValues.get(dataSourceName) != null) {
                    return documentDaoValues.get(dataSourceName);
                } else {
                    if (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) {
                        //using JPA
                    	DocumentDaoJpa documentDaoJpa = new DocumentDaoJpa();
                        if (entityManager != null) {
                            documentDaoJpa.setEntityManager(entityManager);
                            documentDaoJpa.setBusinessObjectDao(businessObjectDao);
                            documentDaoJpa.setDocumentAdHocService((DocumentAdHocService)KNSServiceLocator.getService(DOCUMENT_AD_HOC_SERVICE_NAME));
                            documentDaoValues.put(dataSourceName, documentDaoJpa);
                            return documentDaoJpa;
                        } else {
                            throw new ConfigurationException("EntityManager is null. EntityManager must be set in the Module Configuration bean in the appropriate spring beans xml. (see nested exception for details).");
                        }

                    } else {
                        //using OJB
                    	DocumentDaoOjb documentDaoOjb = new DocumentDaoOjb();
                        documentDaoOjb.setJcdAlias(dataSourceName);
                        documentDaoOjb.setBusinessObjectDao(businessObjectDao);
                        documentDaoOjb.setDocumentAdHocService((DocumentAdHocService)KNSServiceLocator.getService(DOCUMENT_AD_HOC_SERVICE_NAME));
                        documentDaoValues.put(dataSourceName, documentDaoOjb);
                        return documentDaoOjb;
                    }

                }

            }
        }
        return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? documentDaoJpa : documentDaoOjb;
    }

	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#findByDocumentHeaderId(java.lang.Class, java.lang.String)
	 */
	public Document findByDocumentHeaderId(Class clazz, String id) {
		return getDao(clazz).findByDocumentHeaderId(clazz, id);
	}

	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#findByDocumentHeaderIds(java.lang.Class, java.util.List)
	 */
	public List findByDocumentHeaderIds(Class clazz, List idList) {
		return getDao(clazz).findByDocumentHeaderIds(clazz, idList);
	}

	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#getBusinessObjectDao()
	 */
	public BusinessObjectDao getBusinessObjectDao() {
		return businessObjectDao;
	}

	public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#save(org.kuali.rice.kns.document.Document)
	 */
	public void save(Document document) {
		getDao(document.getClass()).save(document);
	}

	public void setDocumentDaoJpa(DocumentDao documentDaoJpa) {
		this.documentDaoJpa = documentDaoJpa;
	}

	public void setDocumentDaoOjb(DocumentDao documentDaoOjb) {
		this.documentDaoOjb = documentDaoOjb;
	}

    private synchronized static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
        return kualiModuleService;
    }

}
