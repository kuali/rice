/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.api.validation;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "validationRuleServiceSoap", targetNamespace = KrmsConstants.Namespaces.KRMS_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ValidationRuleService {

    @WebMethod(operationName = "getValidation")
    @WebResult(name = "validation")
    RuleDefinition getValidationRule(@WebParam(name = "validationId") String validationId)
            throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getValidationByName")
    @WebResult(name = "validation")
    RuleDefinition getValidationRuleByName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name)
        throws RiceIllegalArgumentException;

    /**
     * TODO EGHM
     *
     * @param validation
     *
     * @return
     *
     * @throws RiceIllegalArgumentException if the given Validation definition is null
     * @throws RiceIllegalArgumentException if the given Validation definition has a non-null id.  When creating a new
     * Validation definition, the ID will be generated.
     * @throws RiceIllegalStateException if a Validation with the given namespace code and name already exists
     */
    @WebMethod(operationName = "createValidationRule")
    @WebResult(name = "validation")
    RuleDefinition createValidationRule(@WebParam(name = "validation") RuleDefinition validation)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     *
     * @param validation
     *
     * @return
     *
     * @throws RiceIllegalArgumentException
     * @throws RiceIllegalStateException if the Validation does not exist in the system under the given validationId
     */
    @WebMethod(operationName = "updateValidationRule")
    @WebResult(name = "validation")
    RuleDefinition updateValidationRule(@WebParam(name = "validation") RuleDefinition validation)
        throws RiceIllegalArgumentException, RiceIllegalStateException;


}
