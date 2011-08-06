/*
 * Copyright 2005-20011 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MultiValuedStringMapAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "searchableAttributeServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface SearchableAttribute {

    @WebMethod(operationName = "generateSearchContent")
    @WebResult(name = "searchContent")
    public String generateSearchContent(
            @WebParam(name = "extensionDefinition") ExtensionDefinition extensionDefinition,
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "attributeDefinition") WorkflowAttributeDefinition attributeDefinition
    );

    @WebMethod(operationName = "getDocumentAttributes")
    @WebResult(name = "documentAttributes")
    @XmlElementWrapper(name = "documentAttributes", required = false)
    @XmlElement(name = "documentAttribute", required = false)
    public List<DocumentAttribute<?>> getDocumentAttributes(
            @WebParam(name = "extensionDefinition") ExtensionDefinition extensionDefinition,
            @WebParam(name = "documentSearchContext") DocumentSearchContext documentSearchContext
    );

    @WebMethod(operationName = "getSearchFields")
    @WebResult(name = "searchFields")
    @XmlElementWrapper(name = "searchFields", required = false)
    @XmlElement(name = "searchField", required = false)
    public List<RemotableAttributeField> getSearchFields(
            @WebParam(name = "extensionDefinition") ExtensionDefinition extensionDefinition,
            @WebParam(name = "documentTypeName") String documentTypeName
    );

    @WebMethod(operationName = "validateSearchParameters")
    @WebResult(name = "validationErrors")
    @XmlElementWrapper(name = "validationErrors", required = false)
    @XmlElement(name = "validationError", required = false)
    public List<RemotableAttributeError> validateSearchFieldParameters(
            @WebParam(name = "extensionDefinition") ExtensionDefinition extensionDefinition,
            @WebParam(name = "parameters")
            @XmlJavaTypeAdapter(MultiValuedStringMapAdapter.class) Map<String, List<String>> parameters,
            @WebParam(name = "documentTypeName") String documentTypeName
    );

}
