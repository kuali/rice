/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.shareddata.impl.country;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.shareddata.api.country.Country;
import org.kuali.rice.shareddata.api.country.CountryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CountryServiceImpl implements CountryService {
    private final static Logger LOG = Logger.getLogger(CountryServiceImpl.class);

    private KualiModuleService kualiModuleService;

    @Override
    public Country getByPrimaryId(final String postalCountryCode) {
        if (StringUtils.isBlank(postalCountryCode)) {
            LOG.debug("The postalCountryCode cannot be empty String.");
            return null;
        }

        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        postalCountryMap.put(KNSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);

        CountryBo countryBo = kualiModuleService.getResponsibleModuleService(CountryBo.class)
                .getExternalizableBusinessObject(CountryBo.class, postalCountryMap);

        return CountryBo.to(countryBo);
    }

    /**
     * @see org.kuali.rice.shareddata.api.country.CountryService#getByAlternatePostalCountryCode(java.lang.String)
     */
    @Override
    public Country getByAlternatePostalCountryCode(final String alternatePostalCountryCode) {
        if (StringUtils.isBlank(alternatePostalCountryCode)) {
            LOG.debug("The alternatePostalCountryCode cannot be empty String.");
            return null;
        }

        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        postalCountryMap.put(KNSPropertyConstants.ALTERNATE_POSTAL_COUNTRY_CODE, alternatePostalCountryCode);

        List<CountryBo> countryList = kualiModuleService.getResponsibleModuleService(CountryBo.class).getExternalizableBusinessObjectsList(CountryBo.class, postalCountryMap);
        if (countryList == null || countryList.isEmpty()) {
            return null;
        } else if (countryList.size() == 1) {
            return CountryBo.to(countryList.get(0));
        } else throw new IllegalStateException("Multiple countries found with same alternatePostalCountryCode");
    }

    @Override
    public Country getDefaultCountry() {
        String postalCountryCode = CoreFrameworkServiceLocator.getClientParameterService().getParameterValueAsString(KNSConstants.KNS_NAMESPACE,
                KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.DEFAULT_COUNTRY);
        return this.getByPrimaryId(postalCountryCode);
    }

    @Override
    public List<Country> findAllCountriesNotRestricted() {
        List<String> criteriaValues = new ArrayList<String>();
        criteriaValues.add(null);
        criteriaValues.add("N");

        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        postalCountryMap.put(KNSPropertyConstants.POSTAL_COUNTRY_RESTRICTED_INDICATOR, criteriaValues);

        List<CountryBo> countryBos = kualiModuleService.getResponsibleModuleService(CountryBo.class)
                .getExternalizableBusinessObjectsList(CountryBo.class, postalCountryMap);

        return convertListOfBosToImmutables(countryBos);
    }

    @Override
    public List<Country> findAllCountries() {
        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        List<CountryBo> countryBos = kualiModuleService.getResponsibleModuleService(CountryBo.class).
                getExternalizableBusinessObjectsList(CountryBo.class, postalCountryMap);
        return convertListOfBosToImmutables(countryBos);
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(final KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Converts a List<CountryBo> to an Unmodifiable List<Country>
     *
     * @param countryBos a mutable List<CountryBo> to made completely immutable.
     * @return An unmodifiable List<Country>
     */
    List<Country> convertListOfBosToImmutables(final List<CountryBo> countryBos) {
        ArrayList<Country> countries = new ArrayList<Country>();
        for (CountryBo bo : countryBos) {
            Country country = CountryBo.to(bo);
            countries.add(country);
        }
        return Collections.unmodifiableList(countries);
    }
}
