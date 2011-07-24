/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.framework.docsearch;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeValidationError;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "searchableAttributeServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface SearchableAttribute extends Serializable {


    public String generateSearchContent(DocumentSearchContext documentSearchContext);

    public List<DocumentAttribute<?>> getDocumentAttributes(DocumentSearchContext documentSearchContext);

    public List<RemotableAttributeField> getAttributeFields(DocumentSearchContext documentSearchContext);

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(@XmlJavaTypeAdapter(MapStringStringAdapter.class) Map<String, String> paramMap, DocumentSearchContext searchContext);

}
