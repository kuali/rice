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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class CountryServiceImpl implements CountryService {
    private static Logger LOG = Logger.getLogger(CountryServiceImpl.class);

    private KualiModuleService kualiModuleService;

    /**
     * @see org.kuali.kfs.sys.service.CountryService#getByPrimaryId(java.lang.String)
     */
    public Country getByPrimaryId(String postalCountryCode) {
        if (StringUtils.isBlank(postalCountryCode)) {
            LOG.debug("The postalCountryCode cannot be empty String.");
            return null;
        }

        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        postalCountryMap.put(KNSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);

        return kualiModuleService.getResponsibleModuleService(Country.class).getExternalizableBusinessObject(Country.class, postalCountryMap);
    }

    public Country getByPrimaryIdIfNecessary(String postalCountryCode, Country existingCountry) {
        if (existingCountry != null) {
            if (StringUtils.equals(postalCountryCode, existingCountry.getPostalCountryCode())) {
                return existingCountry;
            }
        }

        return this.getByPrimaryId(postalCountryCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountryService#getDefaultCountry()
     */
    public Country getDefaultCountry() {
        String postalCountryCode = KNSServiceLocator.getParameterService().getParameterValue(KNSConstants.KNS_NAMESPACE,
	        	KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.DEFAULT_COUNTRY);
        return this.getByPrimaryId(postalCountryCode);
    }
    
    /**
     * @see org.kuali.kfs.sys.service.CountryService#findAllCountriesNotRestricted()
     */
    public List<Country> findAllCountriesNotRestricted() {
        List<String> criteriaValues = new ArrayList<String>();
        criteriaValues.add(null);
        criteriaValues.add("N");
        
        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        postalCountryMap.put(KNSPropertyConstants.POSTAL_COUNTRY_RESTRICTED_INDICATOR, criteriaValues);
        
        return kualiModuleService.getResponsibleModuleService(Country.class).getExternalizableBusinessObjectsList(Country.class, postalCountryMap);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountryService#findAllCountries()
     */
    public List<Country> findAllCountries() {
        Map<String, Object> postalCountryMap = new HashMap<String, Object>();
        return kualiModuleService.getResponsibleModuleService(Country.class).getExternalizableBusinessObjectsList(Country.class, postalCountryMap);
    }

    /**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
    
}
