/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.doctype.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.services.ServiceConstants;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Action for doing document type stuff from the web.  This is exists for reporting purposes 
 * only at this point.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeAction extends WorkflowAction {
	private static String DOCUMENT_TYPE = "EDENSERVICE-DOCS.DocumentType";

	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentTypeForm documentTypeForm = (DocumentTypeForm) form;
		// setRouteLevelToException(documentTypeForm.getRouteLevel());
		documentTypeForm.getDocumentType().setActiveInd(new Boolean(true));
		documentTypeForm.setDocTypeVisible(true);
		documentTypeForm.setPolicyVisible(true);
		documentTypeForm.setRouteLevelVisible(true);
		documentTypeForm.setSearchableAttributeVisible(true);
		documentTypeForm.setNewRouteModuleVisible("no");
		documentTypeForm.setDefaultApprove("true");
		documentTypeForm.setPreApprove("true");
		documentTypeForm.setInitiatorMustRoute("true");
        documentTypeForm.setInitiatorMustSave("true");
		return mapping.findForward("basic");
	}

	public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
		DocumentTypeForm documentTypeForm = (DocumentTypeForm) form;

		if (getWorkgroupService().isUserMemberOfGroup(new GroupNameId(ServiceConstants.DOC_TYPE_BLANKET_APPRV_WRKGRP), getUserSession(request).getWorkflowUser())) {
			documentTypeForm.setShowBlanketApproveButton(true);
		}

		if (documentTypeForm.getDocumentType().getDocumentTypeId() != null && documentTypeForm.getDocumentType().getDocumentTypeId().longValue() != 0) {
			DocumentType existing = getDocumentTypeService().findById(documentTypeForm.getDocumentType().getDocumentTypeId());
//			Workgroup workgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(existing.getWorkgroupId()));
//			if (workgroup != null) {
//				existing.setWorkgroupId(workgroup.getWorkflowGroupId().getGroupId());
//			}
//			if (existing.getBlanketApproveWorkgroupId() != null) {
//				Workgroup blanketApproveWorkgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(existing.getBlanketApproveWorkgroupId()));
//				if (blanketApproveWorkgroup != null) {
//					existing.setBlanketApproveWorkgroupId(blanketApproveWorkgroup.getWorkflowGroupId().getGroupId());
//				}
//			}
//			if (existing.getParentDocType() != null) {
//				existing.setCustomActionListAttributeClassName(existing
//						.retrieveAttributeClassName(existing.getCustomActionListAttributeClassName(), EdenConstants.ACTION_LIST_ATTRIBUTE_CLASS_PROPERTY));
//				existing.setCustomEmailAttributeClassName(existing.retrieveAttributeClassName(existing.getCustomEmailAttributeClassName(), EdenConstants.EMAIL_ATTRIBUTE_CLASS_PROPERTY));
//				existing.setCustomNoteAttributeClassName(existing.retrieveAttributeClassName(existing.getCustomNoteAttributeClassName(), EdenConstants.NOTE_ATTRIBUTE_CLASS_PROPERTY));
//			}
			documentTypeForm.setExistingDocumentType(existing);
		}
		String parentDocTypeName = documentTypeForm.getParentDocTypeName();
		if (parentDocTypeName != null && !"".equals(parentDocTypeName.trim())) {
			documentTypeForm.getDocumentType().setDocTypeParentId(getDocumentTypeService().findByName(parentDocTypeName).getDocumentTypeId());
//			if (documentTypeForm.getDocumentType().getParentDocType() != null) {
//				documentTypeForm.getDocumentType().setCustomActionListAttributeClassName(
//						documentTypeForm.getDocumentType().retrieveAttributeClassName(documentTypeForm.getDocumentType().getCustomActionListAttributeClassName(),
//								EdenConstants.ACTION_LIST_ATTRIBUTE_CLASS_PROPERTY));
//				documentTypeForm.getDocumentType().setCustomEmailAttributeClassName(
//						documentTypeForm.getDocumentType().retrieveAttributeClassName(documentTypeForm.getDocumentType().getCustomEmailAttributeClassName(),
//								EdenConstants.EMAIL_ATTRIBUTE_CLASS_PROPERTY));
//				documentTypeForm.getDocumentType().setCustomNoteAttributeClassName(
//						documentTypeForm.getDocumentType()
//								.retrieveAttributeClassName(documentTypeForm.getDocumentType().getCustomNoteAttributeClassName(), EdenConstants.NOTE_ATTRIBUTE_CLASS_PROPERTY));
//			}
		}

		// this code sucks and needs reworked RK
		if (documentTypeForm.getDocId() != null) { // I'm for the dochandler
			documentTypeForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), documentTypeForm.getDocId()));
			documentTypeForm.establishVisibleActionRequestCds();
		} else if (documentTypeForm.getDocTypeId() != null) { // I'm for the
																// report
			// do nothing I'm doing work in report method
		} else { // I'm for editing and creating new
			documentTypeForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), DOCUMENT_TYPE));
			documentTypeForm.setDocId(documentTypeForm.getFlexDoc().getRouteHeaderId());
			documentTypeForm.establishVisibleActionRequestCds();
		}
		// seems like work could all be done on the generic docId to clear up
		// this mess
		return null;
	}

	public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form) throws Exception {
		return null;
	}


	public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentTypeForm documentTypeForm = (DocumentTypeForm) form;
		DocumentType docType = getDocumentTypeService().findById(new Long(request.getParameter("docTypeId")));

		if (docType.getRouteHeaderId() != null && docType.getRouteHeaderId().longValue() != 0) {
			documentTypeForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), docType.getRouteHeaderId()));
		}
		documentTypeForm.establishVisibleActionRequestCds();

		documentTypeForm.setDocumentType(docType);

        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        dataSet.getDocumentTypes().add(docType);
        byte[] data = KEWServiceLocator.getXmlExporterService().export(dataSet.getFormat(), dataSet);
        documentTypeForm.setExportedXml(new String(data));

		return mapping.findForward("report");
	}

	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentType docType = getDocumentTypeService().findById(new Long(request.getParameter("docTypeId")));
		ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
		dataSet.getDocumentTypes().add(docType);
		return exportDataSet(request, dataSet);
	}

	public ActionForward exportHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentType docType = getDocumentTypeService().findById(new Long(request.getParameter("docTypeId")));
		ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
		List docTypes = dataSet.getDocumentTypes();
		do {
			docTypes.add(docType);
			docType = docType.getParentDocType();
		} while (docType != null);
		return exportDataSet(request, dataSet);
	}

	private DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
	}

	private WorkgroupService getWorkgroupService() {
		return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
	}

}