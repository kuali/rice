/*
 * Copyright 2008 The Kuali Foundation
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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;


@WebService(name = "CountryService", targetNamespace = Country.Elements.NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CountryService {

    /**
     * Lookup a country object based on the given country code.
     *
     * @param postalCountryCode the given country code
     * @return a country object with the given country code.  A null reference is returned if an invalid or
     *         non-existant postalCountryCode is supplied.
     */
    @WebMethod(operationName = "getByPrimaryId")
    @WebResult(name = "country")
    Country getByPrimaryId(@WebParam(name = "postalCountryCode") String postalCountryCode);

    /**
     * Get a country object based on the given alternate country code.
     * <p/>
     * <p>If the postal country code of the passed in country is same as the passed in country code,
     * return the existing country; otherwise, a Country is retrieved based on the passed in country code.</p>
     *
     * @param alternatePostalCountryCode the given country code
     * @param existingCountry            the given existing country
     * @return a country object with the given country code if necessary
     */
    @WebMethod(operationName = "getByAlternatePostalCountryCodeIfNecessary")
    @WebResult(name = "country")
    Country getByAlternatePostalCountryCodeIfNecessary(
            @WebParam(name = "alternatePostalCountryCode") String alternatePostalCountryCode,
            @WebParam(name = "existingCountry") Country existingCountry);

    /**
     * Get a country object based on an alternate country code
     *
     * @param alternatePostalCountryCode the given alternate country code
     * @return A country object with the given alternate country code if a country with that alternate country code
     *         exists.  Otherwise, null is returned.
     * @throws IllegalStateException if multiple Countries exist with the same passed in alternatePostalCountryCode
     */
    @WebMethod(operationName = "getByAlternatePostalCountryCode")
    @WebResult(name = "country")
    Country getByAlternatePostalCountryCode(
            @WebParam(name = "alternatePostalCountryCode") String alternatePostalCountryCode);

    /**
     * Get a Country based on the given country code.
     * <p/>
     * <p>If the postal country code of the passed in existing country is
     * the same as the passed in country code, the existing Country is returned;
     * otherwise a new Country is retrieved.</p>
     *
     * @param postalCountryCode the given country code
     * @param existingCountry   the given existing country
     * @return a country object with the given country code if necessary
     */
    @WebMethod(operationName = "getByPrimaryIdIfNecessary")
    @WebResult(name = "country")
    Country getByPrimaryIdIfNecessary(
            @WebParam(name = "postalCountryCode") String postalCountryCode,
            @WebParam(name = "existingCountry") Country existingCountry);

    /**
     * The system default Country.  The default Country is determined by a system parameter of "DEFAULT_COUNTRY".
     *
     * @return The system default Country
     * @see org.kuali.rice.kns.util.KNSConstants.SystemGroupParameterNames
     */
    @WebMethod(operationName = "getDefaultCountry")
    @WebResult(name = "country")
    Country getDefaultCountry();

    /**
     * Returns all Countries that are not restricted.
     *
     * @return all countries that are not restricted
     */
    @WebMethod(operationName = "findAllCountriesNotRestricted")
    @WebResult(name = "countriesNotRestricted")
    List<Country> findAllCountriesNotRestricted();

    /**
     * Returns all Countries
     *
     * @return all countries
     */
    @WebMethod(operationName = "findAllCountries")
    @WebResult(name = "allCountries")
    List<Country> findAllCountries();
}
