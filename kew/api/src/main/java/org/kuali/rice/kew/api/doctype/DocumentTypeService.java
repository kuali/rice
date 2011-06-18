package org.kuali.rice.kew.api.doctype;

import javax.jws.WebParam;


public interface DocumentTypeService {

	String getDocumentTypeIdByName(@WebParam(name = "documentTypeName") String documentTypeName);
	
	//DocumentType getDocumentType(@WebParam(name = "documentTypeId"), String documentTypeId);
	
	// TODO add the following methods to this service
	
//	public DocumentTypeDTO getDocumentType(
//			@WebParam(name = "documentTypeId") Long documentTypeId)
//			throws WorkflowException;
//
//	public DocumentTypeDTO getDocumentTypeByName(
//			@WebParam(name = "documentTypeName") String documentTypeName)
//			throws WorkflowException;
//	
//	public boolean hasRouteNode(
//			@WebParam(name = "documentTypeName") String documentTypeName,
//			@WebParam(name = "routeNodeName") String routeNodeName)
//			throws WorkflowException;
//
//	public boolean isCurrentActiveDocumentType(
//			@WebParam(name = "documentTypeName") String documentTypeName)
//			throws WorkflowException;
	
}
