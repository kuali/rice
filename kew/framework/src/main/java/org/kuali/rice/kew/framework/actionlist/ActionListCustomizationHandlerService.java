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
package org.kuali.rice.kew.framework.actionlist;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.action.ActionItemCustomization;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;

/**
 * A remotable service which handles processing of a client application's custom processing of
 * action list attributes.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.ACTION_LIST_CUSTOMIZATION_HANDLER_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ActionListCustomizationHandlerService {
    
    // TODO Modeled from DocumentSecurityHandlerService, but get a better description
    /**
     * Returns a list of Action Item Customizations from the given list of Action Items and Principal ID.
     *
     * <p>This method essentially invokes CustomActionListAttribute???
     * {@link DocumentSecurityAttribute#isAuthorizedForDocument(String, org.kuali.rice.kew.api.document.Document)}
     * method for each of the security attributes supplied in the document security directives, passing the associated
     * list of document ids.</p>
     *
     * @param principalId the id of the Principal ???
     * @param actionItems the list of Action Items to be customized.
     * 
     * @return the list of Action Item Customizations
     *
     * @throws RiceIllegalArgumentException if the given principalId is a null or blank value
     * @throws RiceIllegalArgumentException if any of the action items ???
     */
    @WebMethod(operationName = "customizeActionList")
    @WebResult(name = "actionList")
    @XmlElement(name = "actionList", required = false)
    List<ActionItemCustomization> customizeActionList(
            @WebParam(name = "principalId") String principalId, 
            @WebParam(name = "actionItems") List<ActionItem> actionItems)
        throws RiceIllegalArgumentException;
}
