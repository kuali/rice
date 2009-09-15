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
package org.kuali.rice.kns.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.SessionDocument;
import org.kuali.rice.kns.dao.SessionDocumentDao;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SessionDocumentService;
import org.kuali.rice.kns.util.KualiLRUMap;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
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
    protected static final String IP_ADDRESS = "ipAddress";
	protected static final String PRINCIPAL_ID = "principalId";
	protected static final String DOCUMENT_NUMBER = "documentNumber";
	protected static final String SESSION_ID = "sessionId";

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SessionDocumentServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private SessionDocumentDao sessionDocumentDao;    
	private Map<String,CachedObject> cachedObjects;
	private EncryptionService encryptionService;
	private int maxCacheSize;

    public static class CachedObject {
        private UserSession userSession;
        private String formKey;
		CachedObject(UserSession userSession, String formKey) {
			this.userSession = userSession;
			this.formKey = formKey;
		}

		@Override
        public String toString() {
        	return "CachedObject: principalId="+userSession.getPrincipalId()+" / objectWithFormKey="+userSession.retrieveObject(formKey);
        }

		public UserSession getUserSession() {
			return this.userSession;
		}

		public String getFormKey() {
			return this.formKey;
		}
    }

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		cachedObjects = Collections.synchronizedMap( new KualiLRUMap(maxCacheSize) );		
	}

	/**
     * @see org.kuali.rice.kns.service.SessionDocumentService#getDocumentForm(String documentNumber, String docFormKey, UserSession userSession)
     */
    public KualiDocumentFormBase getDocumentForm( String documentNumber, String docFormKey, UserSession userSession, String ipAddress){
    	KualiDocumentFormBase documentForm = null;
    	
		LOG.debug("getDocumentForm KualiDocumentFormBase from db");
		try{
			HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
			primaryKeys.put(SESSION_ID, userSession.getKualiSessionId());
			primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
			primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
			primaryKeys.put(IP_ADDRESS, ipAddress);
   
			SessionDocument sessionDoc = (SessionDocument)getBusinessObjectService().findByPrimaryKey(SessionDocument.class, primaryKeys);
			if(sessionDoc != null){
				byte[] formAsBytes = sessionDoc.getSerializedDocumentForm();
				if ( sessionDoc.isEncrypted() ) {
					formAsBytes = getEncryptionService().decryptBytes(formAsBytes);
				}
				ByteArrayInputStream baip = new ByteArrayInputStream(formAsBytes);
				ObjectInputStream ois = new ObjectInputStream(baip);
    	        
				// re-create the KualiDocumentFormBase object
				documentForm = (KualiDocumentFormBase)ois.readObject();
	         
				//re-store workFlowDocument into session
				KualiWorkflowDocument workflowDocument = documentForm.getDocument().getDocumentHeader().getWorkflowDocument();
				userSession.setWorkflowDocument(workflowDocument);				
			}
		} catch(Exception e) {
		    LOG.error("getDocumentForm failed for SessId/DocNum/PrinId/IP:"
   			 + userSession.getKualiSessionId()+"/"+documentNumber+"/"+userSession.getPrincipalId()+"/"+ipAddress, e);
		}

        return documentForm;
    }

    /**
	 * @see org.kuali.rice.kns.service.SessionDocumentService#purgeDocumentForm(String
	 *      documentNumber, String docFormKey, UserSession userSession )
	 */
	public void purgeDocumentForm(String documentNumber, String docFormKey,
			UserSession userSession, String ipAddress) {
		synchronized (userSession) {
			
		LOG.debug("purge document form from session");
		userSession.removeObject(docFormKey);	
			try {
				LOG.debug("purge document form from database");
				HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
				primaryKeys.put(SESSION_ID, userSession.getKualiSessionId());
				primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
				primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
				primaryKeys.put(IP_ADDRESS, ipAddress);
				getBusinessObjectService().deleteMatching(SessionDocument.class, primaryKeys);
			} catch (Exception e) {
				LOG.error("purgeDocumentForm failed for SessId/DocNum/PrinId/IP:"
       			 + userSession.getKualiSessionId()+"/"+documentNumber+"/"+userSession.getPrincipalId()+"/"+ipAddress, e);
			}
		}
	}

    /**
     * @see org.kuali.rice.kns.service.SessinoDocumentService#setDocumentForm()
     */
    
    public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress){
    	synchronized ( userSession ) { 
	    	//formKey was set in KualiDocumentActionBase execute method
			String formKey = form.getFormKey();
			String key = userSession.getKualiSessionId() + "-" + formKey;
			cachedObjects.put(key, new CachedObject( userSession, formKey ));
			
	        String documentNumber = form.getDocument().getDocumentNumber(); 
	    	
		    if( StringUtils.isNotBlank(documentNumber)) {
		        try {
		            LOG.debug("set Document Form into database");
		            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            ObjectOutputStream oos = new ObjectOutputStream(baos);
		            oos.writeObject(form);
		            // serialize the KualiDocumentFormBase object into a byte array
		            byte[] formAsBytes = baos.toByteArray();
					boolean encryptContent = false;
					if ( form.getDocTypeName() != null ) {
						DocumentEntry documentEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry( form.getDocTypeName() );
						if ( documentEntry != null ) {
							encryptContent = documentEntry.isEncryptDocumentDataInPersistentSessionStorage();
						}
					}					 
		            if ( encryptContent ) {
		            	formAsBytes = getEncryptionService().encryptBytes(formAsBytes);
		            }
		
		            // check if a record is already there in the database
		            // this may only happen under jMeter testing, but there is no way to be sure
					HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
					primaryKeys.put(SESSION_ID, userSession.getKualiSessionId());
					primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
					primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
					primaryKeys.put(IP_ADDRESS, ipAddress);
		
					SessionDocument sessionDocument = (SessionDocument)getBusinessObjectService().findByPrimaryKey(SessionDocument.class, primaryKeys);
					if ( sessionDocument == null ) {
						sessionDocument = new SessionDocument();
			            sessionDocument.setSessionId(userSession.getKualiSessionId());
			    	    sessionDocument.setDocumentNumber(documentNumber);
			    	    sessionDocument.setPrincipalId(userSession.getPrincipalId());
			    	    sessionDocument.setIpAddress(ipAddress);
					}
					sessionDocument.setSerializedDocumentForm(formAsBytes);
					sessionDocument.setEncrypted(encryptContent);
		    	    sessionDocument.setLastUpdatedDate(currentTime);
		    	    
	    	    	businessObjectService.save(sessionDocument);
		    	  
		        } catch(Exception e) {
		        	 LOG.error("setDocumentForm failed for SessId/DocNum/PrinId/IP:"
		        			 + userSession.getKualiSessionId()+"/"+documentNumber+"/"+userSession.getPrincipalId()+"/"+ipAddress, e);
		        }
		    } else {
		    	LOG.warn("documentNumber is null on form's document: " + form);
		    }
    	}    	
    }
    
    public void purgeAllSessionDocuments(Timestamp expirationDate){
    	sessionDocumentDao.purgeAllSessionDocuments(expirationDate);
    }
    
    

    /**
	 * @return the sessionDocumentDao
	 */
	protected SessionDocumentDao getSessionDocumentDao() {
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
	protected BusinessObjectService getBusinessObjectService() {
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
		return maxCacheSize;
	}

	/**
	 * @param maxCacheSize the maxCacheSize to set
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}

	protected EncryptionService getEncryptionService() {
		if ( encryptionService == null ) {
			encryptionService = KNSServiceLocator.getEncryptionService();
		}
		return encryptionService;
	}

	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

}
