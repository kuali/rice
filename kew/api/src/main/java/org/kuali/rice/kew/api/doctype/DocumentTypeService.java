/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api.doctype;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiConstants;

/**
 * TODO ...
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "documentTypeSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentTypeService {

    @WebMethod(operationName = "getDocumentTypeIdByName")
    @WebResult(name = "documentTypeId")
    @XmlElement(name = "documentTypeId", required = false)
    String getDocumentTypeIdByName(@WebParam(name = "documentTypeName") String documentTypeName)
            throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getDocumentTypeById")
    @WebResult(name = "documentType")
    @XmlElement(name = "documentType", required = false)
    DocumentType getDocumentTypeById(@WebParam(name = "documentTypeId") String documentTypeId)
            throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getDocumentTypeByName")
    @WebResult(name = "documentType")
    @XmlElement(name = "documentType", required = false)
    DocumentType getDocumentTypeByName(@WebParam(name = "documentTypeName") String documentTypeName)
            throws RiceIllegalArgumentException;
    
    @WebMethod(operationName = "isSuperUser")
    @WebResult(name = "isSuperUser")
    @XmlElement(name = "isSuperUser", required = true)
    boolean isSuperUser(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "documentTypeId") String documentTypeId)
            throws RiceIllegalArgumentException;

    //	public boolean hasRouteNode(
    //			@WebParam(name = "documentTypeName") String documentTypeName,
    //			@WebParam(name = "routeNodeName") String routeNodeName)
    //			throws WorkflowException;
    //
    //	public boolean isCurrentActiveDocumentType(
    //			@WebParam(name = "documentTypeName") String documentTypeName)
    //			throws WorkflowException;

}
