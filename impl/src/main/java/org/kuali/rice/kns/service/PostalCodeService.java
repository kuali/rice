/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.kuali.rice.kns.bo.PostalCode;

public interface PostalCodeService {

    /**
     * get the postal code object based on the given postal code and default country code. The default country code is set up in
     * the system.
     * 
     * @param postalCode the given postal code
     * @return the postal code object with the given postal code and default country code.
     */
    public PostalCode getByPostalCodeInDefaultCountry(String postalCode);

    /**
     * get the postal zip code object based on the given postal code and country code
     * 
     * @param postalCountryCode the given country code
     * @param postalCode the given postal code
     * @return the postal code object with the given postal code and country code.
     */
    public PostalCode getByPrimaryId(String postalCountryCode, String postalCode);

    /**
     * get the postal code obpostalt based on the given postal code and default country code. The default country code is set up in
     * the system. If the given postal postale is same as that of the given existing postal code, return the existing postal code;
     * otherwise, retrieve a postal code object.
     * 
     * @param postalCode the given postal code
     * @param existingPostalCode the given existing postal code
     * @return the postal code object with the given postal code and default country code if necessary
     */
    public PostalCode getByPostalCodeInDefaultCountryIfNecessary(String postalCode, PostalCode existingPostalCode);

    /**
     * get the postal code object based on the given postal code and country code. If the given postal code and country code
     * are same as those of the given existing postal code, return the existing postal code; otherwise, retrieve a postal code
     * object.
     * 
     * @param postalCountryCode the given country code
     * @param postalCode the given postal code
     * @param existingPostalCode the given existing postal code
     * @return the postal code object with the given postal code and country code if necessary
     */
    public PostalCode getByPrimaryIdIfNecessary(String postalCountryCode, String postalCode, PostalCode existingPostalCode);
}
