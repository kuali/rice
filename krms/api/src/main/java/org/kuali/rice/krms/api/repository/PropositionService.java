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


@WebService(name = "PropositionService", targetNamespace = KrmsType.Constants.KRMSNAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface PropositionService {

    /**
     * This will create a {@link Proposition} exactly like the parameter passed in.
     *
     * @param prop the proposition to create
     * @throws IllegalArgumentException if the proposition is null
     * @throws IllegalStateException if the proposition already exists in the system
     */
    @WebMethod(operationName="createProposition")
    void createProposition(@WebParam(name = "prop") Proposition prop);

    /**
     * This will update a {@link Proposition}.
     *
     *
     * @param prop the proposition to update
     * @throws IllegalArgumentException if the proposition is null
     * @throws IllegalStateException if the proposition does not exist in the system
     */
    @WebMethod(operationName="updateProposition")
    void updateProposition(@WebParam(name = "prop") Proposition prop);

    /**
     * Lookup the proposition based on the given proposition id.
     *
     * @param propId the given proposition id
     * @return a proposition associated with the given proposition id.  A null reference is returned if an invalid or
     *         non-existent id is supplied.
     */
    @WebMethod(operationName = "getPropositionById")
    @WebResult(name = "prop")
    Proposition getPropositionById(
            @WebParam(name = "propId") String propId);


}
