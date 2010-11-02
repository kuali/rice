/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kew.ria.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.document.SessionDocument;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.exception.InactiveDocumentTypeAuthorizationException;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.Timer;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

@Entity
@Table(name="KREW_RIA_DOC_T")
public class RIADocument extends TransactionalDocumentBase implements SessionDocument, Copyable {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RIADocument.class);

	private static final long serialVersionUID = 7570724075156957665L;
	
	@Column(name="XML_CONTENT", length=4000)
	private String xmlContent;
	
	@Column(name="RIA_DOC_TYPE_NAME")
	private String riaDocTypeName;
	
	public static ThreadLocal<String> riaDocType = new ThreadLocal<String>();
	
	public RIADocument() {
		super();
	}
		
	public String getRiaDocTypeName() {
		RIADocument.riaDocType.set(riaDocTypeName);
		return riaDocTypeName;
	}

	public void setRiaDocTypeName(String riaDocTypeName) {
		this.riaDocTypeName = riaDocTypeName;
		RIADocument.riaDocType.set(riaDocTypeName);
	}
	
	/**
	 * Creates new RIA document for given document type and RIA document type.
	 * 
	 * @param documentTypeName the document type for which document will be created
	 * @param riaDocumentTypeName the RIA document type for which workflow document will be created
	 * @return RIADocument
	 * @throws WorkflowException
	 */
	public static RIADocument getNewDocument(String documentTypeName, String riaDocumentTypeName) throws WorkflowException {
		 // argument validation
        Timer t0 = new Timer("DocumentServiceImpl.getNewDocument");
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        if (GlobalVariables.getUserSession() == null) {
            throw new IllegalStateException("GlobalVariables must be populated with a valid UserSession before a new document can be created");
        }

        // get the class for RIA document
        Class documentClass = KNSServiceLocator.getDataDictionaryService().getDocumentClassByTypeName(documentTypeName);
        
        if (documentClass == null) {
            throw new UnknownDocumentTypeException("unknown document type '" + documentTypeName + "'");
        }

        // get the current user
        Person currentUser = GlobalVariables.getUserSession().getPerson();

        // document must be maint doc or finanancial doc
        if (!KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName).isDocTypeActive()) {
            throw new InactiveDocumentTypeAuthorizationException("initiate", documentTypeName);
        }

        // get the authorization
        DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentHelperService().getDocumentAuthorizer(documentTypeName);

        // make sure this person is authorized to initiate
        log.debug("calling canInitiate from getNewDocument()");
        documentAuthorizer.canInitiate(riaDocumentTypeName, currentUser);

        // create workflow document for riaDocumentTypeName
        KualiWorkflowDocument workflowDocument = 
        	KNSServiceLocator.getWorkflowDocumentService().createWorkflowDocument(riaDocumentTypeName, GlobalVariables.getUserSession().getPerson());
        //workflowDocument.getRouteHeader().setDocTypeName(documentTypeName);
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDocument);
        
        // create a new document header object
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
        // status and notes are initialized correctly in the constructor

        // build Document of specified type
        RIADocument document = null;
        try {
            document = (RIADocument) documentClass.newInstance();
          
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

        document.setDocumentHeader(documentHeader);
        document.setDocumentNumber(documentHeader.getDocumentNumber());
        document.setRiaDocTypeName(riaDocumentTypeName);
        t0.log();
        return document;
	}
	
	@Override
	public boolean getAllowsCopy() {
		return true;
	}

	public String getXmlContent() {
		return this.xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}
}
