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

package org.kuali.rice.shareddata.api.state;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.shareddata.api.SharedDataConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * Service for interacting with {@link State States}.
 */
@WebService(name = "StateService", targetNamespace = SharedDataConstants.Namespaces.SHAREDDATA_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface StateService {

    /**
     * Gets a {@link State} from a postal country code and postal state code.
     * <p/>
     * <p>
     * This method will return null if the state does not exist.
     * </p>
     * <p/>
     * <p>
     * This method will return active or inactive states.
     * </p>
     *
     * @param countryCode country code. cannot be blank.
     * @param code        state code. cannot be blank.
     * @return a {@link State} or null
     * @throws IllegalArgumentException country code or state code is blank
     */
    @WebMethod(operationName = "getState")
    @WebResult(name = "state")
    State getState(@WebParam(name = "countryCode") String countryCode, @WebParam(name = "code") String code)
            throws RiceIllegalArgumentException;

    /**
     * Finds all the {@link State States} for postal country code.
     * <p/>
     * <p>
     * This method will always return an <b>immutable</b> Collection
     * even when no values exist.
     * </p>
     * <p/>
     * <p>
     * This method will only return active states.
     * </p>
     *
     * @param countryCode state code. cannot be blank.
     * @return an immutable collection of states
     * @throws IllegalArgumentException country code is blank
     */
    @WebMethod(operationName = "findAllStatesInCountry")
    @XmlElementWrapper(name = "states", required = true)
    @XmlElement(name = "state", required = false)
    @WebResult(name = "states")
    List<State> findAllStatesInCountry(@WebParam(name = "countryCode") String countryCode)
            throws RiceIllegalArgumentException;
}
