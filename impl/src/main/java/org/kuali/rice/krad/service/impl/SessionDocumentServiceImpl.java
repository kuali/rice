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
package org.kuali.rice.krad.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.SessionDocument;
import org.kuali.rice.krad.dao.SessionDocumentDao;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.KualiLRUMap;
import org.kuali.rice.krad.web.spring.form.DocumentFormBase;
import org.kuali.rice.krad.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.workflow.service.KualiWorkflowDocument;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for a SessionDocument. This is
 * the default, Kuali delivered implementation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
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

	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		cachedObjects = Collections.synchronizedMap( new KualiLRUMap(maxCacheSize) );		
	}

	/**
     * @see org.kuali.rice.krad.service.SessionDocumentService#getDocumentForm(String documentNumber, String docFormKey, UserSession userSession)
     */
    @Override
	public KualiDocumentFormBase getDocumentForm( String documentNumber, String docFormKey, UserSession userSession, String ipAddress){
    	KualiDocumentFormBase documentForm = null;
    	
		LOG.debug("getDocumentForm KualiDocumentFormBase from db");
		try{
			// re-create the KualiDocumentFormBase object
			documentForm = (KualiDocumentFormBase)retrieveDocumentForm(userSession, userSession.getKualiSessionId(), documentNumber, ipAddress);
         
			//re-store workFlowDocument into session
			KualiWorkflowDocument workflowDocument = documentForm.getDocument().getDocumentHeader().getWorkflowDocument();
			addDocumentToUserSession(userSession, workflowDocument);				
		} catch(Exception e) {
		    LOG.error("getDocumentForm failed for SessId/DocNum/PrinId/IP:"
   			 + userSession.getKualiSessionId()+"/"+documentNumber+"/"+userSession.getPrincipalId()+"/"+ipAddress, e);
		}

        return documentForm;
    }
    
	/**
	 * @see org.kuali.rice.krad.service.SessionDocumentService#getUifDocumentForm(org.kuali.rice.krad.web.spring.form.DocumentFormBase, org.kuali.rice.krad.UserSession, java.lang.String)
	 */
	@Override
	public DocumentFormBase getUifDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress) {
    	DocumentFormBase documentForm = null;
    	
		LOG.debug("getDocumentForm KualiDocumentFormBase from db");
		try{
			// re-create the DocumentFormBase object
			documentForm = (DocumentFormBase)retrieveDocumentForm(userSession, docFormKey, documentNumber, ipAddress);
         
			//re-store workFlowDocument into session
			KualiWorkflowDocument workflowDocument = documentForm.getDocument().getDocumentHeader().getWorkflowDocument();
			addDocumentToUserSession(userSession, workflowDocument);				
		} catch(Exception e) {
		    LOG.error("getDocumentForm failed for SessId/DocNum/PrinId/IP:"
   			 + userSession.getKualiSessionId()+"/"+documentNumber+"/"+userSession.getPrincipalId()+"/"+ipAddress, e);
		}

        return documentForm;
	}


	protected Object retrieveDocumentForm(UserSession userSession, String sessionId, String documentNumber, String ipAddress) throws Exception{
		HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
		primaryKeys.put(SESSION_ID, sessionId);
		if (documentNumber != null){
			primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
		}
		primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
		primaryKeys.put(IP_ADDRESS, ipAddress);

		SessionDocument sessionDoc = getBusinessObjectService().findByPrimaryKey(SessionDocument.class, primaryKeys);
		if(sessionDoc != null){
			byte[] formAsBytes = sessionDoc.getSerializedDocumentForm();
			if ( sessionDoc.isEncrypted() ) {
				formAsBytes = getEncryptionService().decryptBytes(formAsBytes);
			}
			ByteArrayInputStream baip = new ByteArrayInputStream(formAsBytes);
			ObjectInputStream ois = new ObjectInputStream(baip);
	        
			return ois.readObject();
		}
		
		return null;
	}
	
	@Override
	public KualiWorkflowDocument getDocumentFromSession(UserSession userSession, String docId) {
		@SuppressWarnings("unchecked")
		Map<String, KualiWorkflowDocument> workflowDocMap = (Map<String, KualiWorkflowDocument>) userSession.retrieveObject(KEWConstants.WORKFLOW_DOCUMENT_MAP_ATTR_NAME);
		
		if (workflowDocMap == null) {
			workflowDocMap = new HashMap<String,KualiWorkflowDocument>();
			userSession.addObject(KEWConstants.WORKFLOW_DOCUMENT_MAP_ATTR_NAME, workflowDocMap);
			return null;
		}
		return workflowDocMap.get(docId);
	}
	
	/**
	 * @see org.kuali.rice.krad.service.SessionDocumentService#addDocumentToUserSession(org.kuali.rice.krad.UserSession,
	 * 		org.kuali.rice.krad.workflow.service.KualiWorkflowDocument)
	 */
	@Override
	public void addDocumentToUserSession(UserSession userSession, KualiWorkflowDocument document) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, KualiWorkflowDocument> workflowDocMap = (Map<String, KualiWorkflowDocument>) userSession.retrieveObject(KEWConstants.WORKFLOW_DOCUMENT_MAP_ATTR_NAME);
			if (workflowDocMap == null) {
				workflowDocMap = new HashMap<String,KualiWorkflowDocument>();
			}
			workflowDocMap.put(document.getDocumentId(), document);
			userSession.addObject(KEWConstants.WORKFLOW_DOCUMENT_MAP_ATTR_NAME, workflowDocMap);
		} catch (WorkflowException e) {
			throw new IllegalStateException("could not save the document in the session", e);
		}
	}

    /**
	 * @see org.kuali.rice.krad.service.SessionDocumentService#purgeDocumentForm(String
	 *      documentNumber, String docFormKey, UserSession userSession )
	 */
	@Override
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
     * @see org.kuali.rice.krad.service.SessinoDocumentService#setDocumentForm()
     */
    
    @Override
	public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress){
    	synchronized ( userSession ) { 
	    	//formKey was set in KualiDocumentActionBase execute method
			String formKey = form.getFormKey();
			String key = userSession.getKualiSessionId() + "-" + formKey;
			cachedObjects.put(key, new CachedObject( userSession, formKey ));
			
	        String documentNumber = form.getDocument().getDocumentNumber(); 
	    	
		    if( StringUtils.isNotBlank(documentNumber)) {
		    	persistDocumentForm(form, userSession, ipAddress, userSession.getKualiSessionId(), documentNumber);
		    } else {
		    	LOG.warn("documentNumber is null on form's document: " + form);
		    }
    	}    	
    }
    
    /**
     * @see org.kuali.rice.krad.service.SessionDocumentService#setDocumentForm(org.kuali.rice.krad.web.spring.form.DocumentFormBase,
     * 		org.kuali.rice.krad.UserSession, java.lang.String)
     */
    @Override
	public void setDocumentForm(DocumentFormBase form, UserSession userSession, String ipAddress){
    	synchronized ( userSession ) { 
	    	//formKey was set in KualiDocumentActionBase execute method
			String formKey = form.getFormKey();
			String key = userSession.getKualiSessionId() + "-" + formKey;
			  	    	
			String documentNumber = form.getDocument().getDocumentNumber();
			if( StringUtils.isNotBlank(formKey)) {
				//FIXME: Currently using formKey for sessionId
				persistDocumentForm(form, userSession, ipAddress, formKey, documentNumber);
			} else {
		    	LOG.warn("documentNumber is null on form's document: " + form);
		    }
    	}    	
    }

    protected void persistDocumentForm(Object form, UserSession userSession, String ipAddress, String sessionId, String documentNumber){
        try {
            LOG.debug("set Document Form into database");
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(form);
            // serialize the KualiDocumentFormBase object into a byte array
            byte[] formAsBytes = baos.toByteArray();
			boolean encryptContent = false;
			if ((form instanceof KualiDocumentFormBase) && ((KualiDocumentFormBase)form).getDocTypeName() != null ) {
				DocumentEntry documentEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry( ((KualiDocumentFormBase)form).getDocTypeName() );
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
			primaryKeys.put(SESSION_ID, sessionId);
			primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
			primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
			primaryKeys.put(IP_ADDRESS, ipAddress);

			SessionDocument sessionDocument = getBusinessObjectService().findByPrimaryKey(SessionDocument.class, primaryKeys);
			if ( sessionDocument == null ) {
				sessionDocument = new SessionDocument();
	            sessionDocument.setSessionId(sessionId);
	    	    sessionDocument.setDocumentNumber(documentNumber);
	    	    sessionDocument.setPrincipalId(userSession.getPrincipalId());
	    	    sessionDocument.setIpAddress(ipAddress);
			}
			sessionDocument.setSerializedDocumentForm(formAsBytes);
			sessionDocument.setEncrypted(encryptContent);
    	    sessionDocument.setLastUpdatedDate(currentTime);
    	    
	    	businessObjectService.save(sessionDocument);
    	  
        } catch(Exception e) {
        	 final String className = form != null ? form.getClass().getName() : "null";
        	 LOG.error("setDocumentForm failed for SessId/DocNum/PrinId/IP/class:"
        			 + userSession.getKualiSessionId()+"/"+documentNumber+"/"+userSession.getPrincipalId()+"/"+ipAddress+"/"+className , e);
        }    	
    }

    /**
     * @see org.kuali.rice.krad.service.SessionDocumentService#purgeAllSessionDocuments(java.sql.Timestamp)
     */
    @Override
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
			encryptionService = CoreApiServiceLocator.getEncryptionService();
		}
		return encryptionService;
	}

	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

}
