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
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kns.bo.Country;

public interface CountryService {

    /**
     * get a country object based on the given country code.
     * 
     * @param postalCountryCode the given country code
     * @return a country object with the given country code
     */
    Country getByPrimaryId(String postalCountryCode);

    /**
     * get a country object based on the given country code. If the postal country code of the existing country is same as the given
     * country code, return the existing country; otherwise, retrieve a new country object.
     * 
     * @param alternatePostalCountryCode the given country code
     * @param existingCountry the given existing ccountry
     * @return a country object with the given country code if necessary
     */
    Country getByAlternatePostalCountryCodeIfNecessary(String alternatePostalCountryCode, Country existingCountry);
    
    /**
     * get a country object based on the given country code.
     * 
     * @param alternatePostalCountryCode the given country code
     * @return a country object with the given country code
     */
    Country getByAlternatePostalCountryCode(String alternatePostalCountryCode);

    /**
     * get a country object based on the given country code. If the postal country code of the existing country is same as the given
     * country code, return the existing country; otherwise, retrieve a new country object.
     * 
     * @param postalCountryCode the given country code
     * @param existingCountry the given existing ccountry
     * @return a country object with the given country code if necessary
     */
    Country getByPrimaryIdIfNecessary(String postalCountryCode, Country existingCountry);
    
    /**
     * get the system default country, which is configured as a system parameter
     * @return the system default country
     */
    Country getDefaultCountry();

    /**
     * get all countries that are note restricated
     * 
     * @return all countries that are note restricated
     */
    List<Country> findAllCountriesNotRestricted();

    /**
     * get all countries
     * 
     * @return all countries
     */
    List<Country> findAllCountries();
}
