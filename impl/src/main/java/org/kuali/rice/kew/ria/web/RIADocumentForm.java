package org.kuali.rice.kew.ria.web;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;


/**
 * PDF Form used by the PDF document.
 * 
 * @author mpk35
 *
 */
public class RIADocumentForm extends KualiTransactionalDocumentFormBase {
	
	private static final long serialVersionUID = 3931062558243923369L;
	private RIADocTypeMap riaDocTypeMap;
	private DocumentType documentType;

	/**
	 * the document type name sent from url
	 */
	private String riaDocTypeName;
	
	public RIADocumentForm() {
        super();
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}


	public RIADocTypeMap getRiaDocTypeMap() {
		return this.riaDocTypeMap;
	}


	public void setRiaDocTypeMap(RIADocTypeMap riaDocTypeMap) {
		this.riaDocTypeMap = riaDocTypeMap;
	}


	public String getRiaDocTypeName() {
		return this.riaDocTypeName;
	}


	public void setRiaDocTypeName(String riaDocTypeName) {
		this.riaDocTypeName = riaDocTypeName;
	}
}