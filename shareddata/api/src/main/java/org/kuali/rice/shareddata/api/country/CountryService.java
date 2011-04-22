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

package org.kuali.rice.shareddata.api.country;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.util.jaxb.ImmutableListAdapter;
import org.kuali.rice.shareddata.api.SharedDataConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;


@WebService(name = "CountryService", targetNamespace = SharedDataConstants.Namespaces.SHAREDDATA_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CountryService {

    /**
     * Lookup a country object based on the given country code.
     *
     * @param code the given country code
     * @return a country object with the given country code.  A null reference is returned if an invalid or
     *         non-existant code is supplied.
     */
    @WebMethod(operationName = "getCountry")
    @WebResult(name = "country")
    Country getCountry(@WebParam(name = "code") String code) throws RiceIllegalArgumentException;

    /**
     * Get a country object based on an alternate country code
     *
     * @param alternateCode the given alternate country code
     * @return A country object with the given alternate country code if a country with that alternate country code
     *         exists.  Otherwise, null is returned.
     * @throws IllegalStateException if multiple Countries exist with the same passed in alternateCode
     * @throws IllegalArgumentException if alternateCode is null or is a whitespace only string.
     */
    @WebMethod(operationName = "getCountryByAlternateCode")
    @WebResult(name = "country")
    Country getCountryByAlternateCode(@WebParam(name = "alternateCode") String alternateCode)
            throws RiceIllegalStateException, RiceIllegalArgumentException;

   /**
     * Returns all Countries that are not restricted.
     *
     * @return all countries that are not restricted
     */
    @WebMethod(operationName = "findAllCountriesNotRestricted")
    @WebResult(name = "countriesNotRestricted")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<Country> findAllCountriesNotRestricted();

    /**
     * Returns all Countries
     *
     * @return all countries
     */
    @WebMethod(operationName = "findAllCountries")
    @WebResult(name = "allCountries")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<Country> findAllCountries();
}
