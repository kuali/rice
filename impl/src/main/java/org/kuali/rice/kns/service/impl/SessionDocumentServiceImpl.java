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
package org.kuali.rice.kns.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.SessionDocument;
import org.kuali.rice.kns.dao.SessionDocumentDao;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.SessionDocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiLRUMap;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for a SessionDocument. This is
 * the default, Kuali delivered implementation.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Transactional
public class SessionDocumentServiceImpl implements SessionDocumentService, InitializingBean  {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SessionDocumentServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    
    private SessionDocumentDao sessionDocumentDao;
    
	private KualiLRUMap cachedObjects;
	
	private int maxCacheSize;

	/** Special list class which doesn't blow up when setting an index which doesn't exist. */
    public static class CachedObject  {
        UserSession userSession;
		String formKey;
        CachedObject(){}
		public UserSession getUserSession() {
			return this.userSession;
		}
		
		public void setUserSession(UserSession userSession) {
			this.userSession = userSession;
		}
	
		public String getFormKey() {
			return this.formKey;
		}
		
		public void setFormKey(String formKey) {
			this.formKey = formKey;
		}
    }

	public void afterPropertiesSet() throws Exception {
		cachedObjects = new KualiLRUMap(maxCacheSize);		
	}

	/**
     * @see org.kuali.rice.kns.service.SessionDocumentService#getDocumentForm(String documentNumber, String docFormKey, UserSession userSession)
     */
    public KualiDocumentFormBase getDocumentForm( String documentNumber, String docFormKey, UserSession userSession){
    	KualiDocumentFormBase documentForm = null;
    	
        /*if(userSession.retrieveObject(docFormKey) != null){
    		 LOG.debug("getDecomentForm KualiDocumentFormBase from session");
             documentForm = (KualiDocumentFormBase) userSession.retrieveObject(docFormKey);
    	}else{*/
    	
    		LOG.debug("getDocumentForm KualiDocumentFormBase from db");
    		try{
    			HashMap<String, String> primaryKeys = new HashMap<String, String>(2);
    			primaryKeys.put("sessionId", userSession.getKualiSessionId());
    			primaryKeys.put("documentNumber", documentNumber);
   
    			SessionDocument sessionDoc = (SessionDocument)businessObjectService.findByPrimaryKey(SessionDocument.class, primaryKeys);
    			if(sessionDoc != null){
    				byte[] formAsBytes = sessionDoc.getSerializedDocumentForm();
    				ByteArrayInputStream baip = new ByteArrayInputStream(formAsBytes);
    				ObjectInputStream ois = new ObjectInputStream(baip);
    	        
    				// re-create the KualiDocumentFormBase object
    				documentForm = (KualiDocumentFormBase)ois.readObject();
    	         
    				//re-store workFlowDocument into session
    				KualiWorkflowDocument workflowDocument = documentForm.getDocument().getDocumentHeader().getWorkflowDocument();
    				userSession.setWorkflowDocument(workflowDocument);
    				
    				//Need to setGeneateDefaultValues, otherwise there is problem in maintenance create page and session is time out.  
    				if (documentForm instanceof KualiMaintenanceForm) {
						KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) documentForm;
						MaintenanceDocument document = (MaintenanceDocument) maintenanceForm.getDocument();
						//document.getNewMaintainableObject().setGenerateDefaultValues(false);
    			
    				}
    			}
    		} catch(Exception e) {
    		      LOG.error("getDocumentForm failed", e);
    		}

    	//}
        return documentForm;
    }

    /**
     * @see org.kuali.rice.kns.service.SessionDocumentService#purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession )
     */
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession ){
    	
    		LOG.debug("purge document form from session");
    		userSession.removeObject(docFormKey);
    	
    	try{
    		LOG.debug("purge document form from database");
			HashMap<String, String> primaryKeys = new HashMap<String, String>(2);
			primaryKeys.put("sessionId", userSession.getKualiSessionId());
			primaryKeys.put("documentNumber", documentNumber);
			getBusinessObjectService().deleteMatching(SessionDocument.class, primaryKeys );
			
		} catch(Exception e) {
		      LOG.error("purgeDocumentForm failed", e);
		}
    	
        
    }

    /**
     * @see org.kuali.rice.kns.service.SessinoDocumentService#setDocumentForm()
     */
    
    public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession ){
 
		 String formKey = form.getFormKey();
		 if (StringUtils.isBlank(formKey)
				|| userSession.retrieveObject(formKey) == null) {
			 LOG.debug("set Document Form into session");
			 formKey = GlobalVariables.getUserSession().addObject(form);
			 form.setFormKey(formKey);
		 }
		 String key = userSession.getKualiSessionId() + "-" + formKey;
		 CachedObject cachedObject = new CachedObject(); 
		 cachedObject.setUserSession(userSession);
		 cachedObject.setFormKey(formKey);
		 cachedObjects.put(key, cachedObject);
		 
        String documentNumber = form.getDocument().getDocumentNumber(); 
    	
        try {
           LOG.debug("set Document Form into database");
            java.util.Date today = new java.util.Date();
            Timestamp currentTime = new Timestamp(today.getTime());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(form);
            // serialize the KualiDocumentFormBase object into a byte array
            byte[] formAsBytes = baos.toByteArray();

            SessionDocument sessionDocument = new SessionDocument();
            sessionDocument.setSessionId(userSession.getKualiSessionId());
    	    sessionDocument.setDocumentNumber(documentNumber);
    	    sessionDocument.setSerializedDocumentForm(formAsBytes);
    	    sessionDocument.setLastUpdatedDate(currentTime);
    	    
    	    if(documentNumber != null){
    	    	businessObjectService.save(sessionDocument);
    	    }else{
    	    	LOG.warn("documentNumber is null");
    	    }
    	  
        }catch(Exception e){
        	 LOG.error("setDocumentForm failed", e);
        }
    	
    }
    
    public void purgeAllSessionDocuments(Timestamp expirationDate){
    	sessionDocumentDao.purgeAllSessionDocuments(expirationDate);
    }
    
    

    /**
	 * @return the sessionDocumentDao
	 */
	public SessionDocumentDao getSessionDocumentDao() {
		return this.sessionDocumentDao;
	}

	/**
	 * @param sessionDocumentDao the sessionDocumentDao to set
	 */
	public void setSessionDocumentDao(SessionDocumentDao sessionDocumentDao) {
		this.sessionDocumentDao = sessionDocumentDao;
	}

	/**
	 * @return the businessObjectService
	 */
	public BusinessObjectService getBusinessObjectService() {
		return this.businessObjectService;
	}

	/**
	 * @param businessObjectService the businessObjectService to set
	 */
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
    
	/**
	 * @return the maxCacheSize
	 */
	public int getMaxCacheSize() {
		return this.maxCacheSize;
	}


	/**
	 * @param maxCacheSize the maxCacheSize to set
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}

}
