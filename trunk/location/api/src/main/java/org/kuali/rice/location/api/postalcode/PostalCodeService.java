/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.location.api.postalcode;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.location.api.LocationConstants;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Service for interacting with {@link PostalCode PostalCodes}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "PostalCodeService", targetNamespace = LocationConstants.Namespaces.LOCATION_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface PostalCodeService {

    /**
     * Gets a {@link PostalCode} from a postal country code and postal code value.
     *
     * <p>
     *   This method will return null if the state does not exist.
     * </p>
     *
     * <p>
     *     This method will return active or inactive postal codes.
     * </p>
     *
     * @param countryCode country code. cannot be blank.
     * @param code postal code value. cannot be blank.
     * @return a {@link PostalCode} or null
     * @throws IllegalArgumentException country code or postal code value is blank
     */
    @WebMethod(operationName="getPostalCode")
    @WebResult(name = "postalCode")
    @Cacheable(value=PostalCode.Cache.NAME, key="'countryCode=' + #p0 + '|' + 'code=' + #p1")
    PostalCode getPostalCode(@WebParam(name = "countryCode") String countryCode, @WebParam(name = "code") String code)
            throws RiceIllegalArgumentException;

    /**
     * Gets all the {@link PostalCode PostalCode} for postal country code.
     *
     * <p>
     *   This method will always return an <b>immutable</b> Collection
     *   even when no values exist.
     * </p>
     *
     *  <p>
     *     This method will only return active postal codes.
     * </p>
     *
     * @param countryCode state code. cannot be blank.
     * @return an immutable collection of states
     * @throws IllegalArgumentException country code is blank
     */
    @WebMethod(operationName="findAllPostalCodesInCountry")
    @WebResult(name = "postalCodes")
    @Cacheable(value=PostalCode.Cache.NAME, key="'countryCode=' + #p0")
    List<PostalCode> findAllPostalCodesInCountry(@WebParam(name = "countryCode") String countryCode)
            throws RiceIllegalArgumentException;
}
