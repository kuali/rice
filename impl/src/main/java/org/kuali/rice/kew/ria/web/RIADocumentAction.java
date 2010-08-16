package org.kuali.rice.kew.ria.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.ria.RIAConstants;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;
import org.kuali.rice.kew.ria.document.RIADocument;
import org.kuali.rice.kew.ria.service.RIADocumentService;
import org.kuali.rice.kew.ria.service.RIAServiceLocator;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;


public class RIADocumentAction extends KualiTransactionalDocumentActionBase {
	 private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RIADocumentAction.class);
	 
	 @Override 
	 public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		 
		 RIADocumentForm riaForm = (RIADocumentForm) form;
		 RIADocument currentDocument = (RIADocument) riaForm.getDocument();
		 DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(currentDocument);
		 if(!documentAuthorizer.isAuthorized(currentDocument, RIAConstants.DEFAULT_GROUP_NAMESPACE, 
				 KNSConstants.KUALI_ACTION_CAN_COPY, GlobalVariables.getUserSession().getPerson().getPrincipalId())) {
			 throw buildAuthorizationException("copy", currentDocument);
		 }
		
		 RIADocumentService riaService = RIAServiceLocator.getRiaDocumentService();
		 RIADocTypeMap riaDocTypeMap = riaService.getRiaDocTypeMap(currentDocument.getRiaDocTypeName());
		 riaForm.setRiaDocTypeMap(riaDocTypeMap);
		 RIADocument doc = RIADocument.getNewDocument(RIAConstants.GENERIC_RIA_DOCUMENT, riaDocTypeMap.getRiaDocTypeName());
		 doc.setXmlContent(currentDocument.getXmlContent());
		 doc.setRiaDocTypeName(currentDocument.getRiaDocTypeName());
		 riaForm.setDocument(doc);
		 return mapping.findForward(RiceConstants.MAPPING_BASIC);
	 }
	 
	/*
	 * (non-Javadoc)
	 * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
	 */
	@Override
	protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
		super.loadDocument(kualiDocumentFormBase);
        RIADocumentForm form = (RIADocumentForm) kualiDocumentFormBase;
        RIADocument document = (RIADocument) form.getDocument(); 
        RIADocTypeMap riaDocTypeMap = RIAServiceLocator.getRiaDocumentService().getRiaDocTypeMap(document.getRiaDocTypeName());
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(riaDocTypeMap.getRiaDocTypeName());
        form.setDocumentType(documentType);
        form.setRiaDocTypeMap(riaDocTypeMap);
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
	 */
	@Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
		RIADocumentForm form = (RIADocumentForm) kualiDocumentFormBase;
		RIADocumentService riaService = RIAServiceLocator.getRiaDocumentService();
		RIADocTypeMap riaDocTypeMap = riaService.getRiaDocTypeMap(form.getRiaDocTypeName());
        form.setRiaDocTypeMap(riaDocTypeMap);
		Document doc = RIADocument.getNewDocument(kualiDocumentFormBase.getDocTypeName(), riaDocTypeMap.getRiaDocTypeName());
		form.setDocument(doc);
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(riaDocTypeMap.getRiaDocTypeName());
        form.setDocumentType(documentType);
    }
}
