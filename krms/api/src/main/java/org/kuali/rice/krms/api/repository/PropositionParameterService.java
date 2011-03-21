/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.api.repository;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


import java.util.List;


@WebService(name = "PropositionParameterService", targetNamespace = KrmsType.Constants.KRMSNAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface PropositionParameterService {

    /**
     * This will create a {@link PropositionParameter} exactly like the parameter passed in.
     *
     * @param parameter the proposition parameter to create
     * @throws IllegalArgumentException if the proposition parameter is null
     * @throws IllegalStateException if the proposition parameter is already existing in the system
     */
    @WebMethod(operationName="createParameter")
    void createParameter(@WebParam(name = "parameter") PropositionParameter parameter);

    /**
     * This will update a {@link PropositionParameter}.
     *
     *
     * @param parameter the proposition parameter to update
     * @throws IllegalArgumentException if the proposition parameter is null
     * @throws IllegalStateException if the proposition parameter does not exist in the system
     */
    @WebMethod(operationName="updateParameter")
    void updateParameter(@WebParam(name = "parameter") PropositionParameter parameter);

    /**
     * Lookup the proposition parameters based on the given proposition id.
     *
     * @param id the given proposition id
     * @return a list of PropositionParameters associated with the given proposition id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getParameters")
    @WebResult(name = "parameters")
    List<PropositionParameter> getParameters(
            @WebParam(name = "propId") String propId);

    /**
     * Lookup the proposition parameter based on the id.
     *
     * @param id the given proposition id
     * @return an immutable PropositionParameters associated with the given id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getParameterById")
    @WebResult(name = "parameter")
    PropositionParameter getParameterById(
            @WebParam(name = "id") String id);

    /**
     * Lookup the proposition parameter based on the proposition id and sequence number.
     *
     * @param id the given proposition id
     * @return an immutable PropositionParameters associated with the given proposition id and sequence number.  A null reference is returned if an invalid or
     *         non-existant.
     */
    @WebMethod(operationName = "getParameterByPropIdAndSequenceNumber")
    @WebResult(name = "parameter")
    PropositionParameter getParameterByPropIdAndSequenceNumber(
            @WebParam(name = "propId") String propId,
            @WebParam(name = "sequenceNumber")Integer sequenceNumber);

}
