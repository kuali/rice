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
package org.kuali.rice.shareddata.framework.country;

import java.util.List;

import org.kuali.rice.shareddata.api.country.Country;
import org.kuali.rice.shareddata.api.country.CountryService;
import org.kuali.rice.shareddata.api.services.SharedDataApiServiceLocator;

/**
 * This class returns list of country value pairs.
 */
public class CountryValuesFinder extends AbstractCountryValuesFinderBase {

	/**
	 * Returns all countries, regardless of active status or restricted status
	 */
	@Override
	protected List<Country> retrieveCountriesForValuesFinder() {
        CountryService countryService = SharedDataApiServiceLocator.getCountryService();
        return countryService.findAllCountries();
	}

}
