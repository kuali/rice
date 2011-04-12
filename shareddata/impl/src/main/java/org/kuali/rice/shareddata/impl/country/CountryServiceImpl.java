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

package org.kuali.rice.shareddata.impl.country;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.shareddata.api.country.Country;
import org.kuali.rice.shareddata.api.country.CountryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CountryServiceImpl implements CountryService {

    private BusinessObjectService businessObjectService;

    @Override
    public Country getCountry(final String code) {
        if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException("code is blank");
        }

        CountryBo countryBo = businessObjectService.findByPrimaryKey(CountryBo.class, Collections.singletonMap(KNSPropertyConstants.POSTAL_COUNTRY_CODE, code));

        return CountryBo.to(countryBo);
    }

    @Override
    public Country getCountryByAlternateCode(final String alternateCode) {
        if (StringUtils.isBlank(alternateCode)) {
            throw new RiceIllegalArgumentException("alt code is blank");
        }

        Collection<CountryBo> countryList = businessObjectService.findMatching(CountryBo.class, Collections.singletonMap(KNSPropertyConstants.ALTERNATE_POSTAL_COUNTRY_CODE, alternateCode));
        if (countryList == null || countryList.isEmpty()) {
            return null;
        } else if (countryList.size() == 1) {
            return CountryBo.to(countryList.iterator().next());
        } else throw new RiceIllegalStateException("Multiple countries found with same alternateCode");
    }

    @Override
    public List<Country> findAllCountriesNotRestricted() {
        List<String> criteriaValues = new ArrayList<String>();
        criteriaValues.add(null);
        criteriaValues.add("N");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(KNSPropertyConstants.POSTAL_COUNTRY_RESTRICTED_INDICATOR, criteriaValues);
        map.put("active", Boolean.TRUE);

        Collection<CountryBo> countryBos = businessObjectService.findMatching(CountryBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(countryBos);
    }

    @Override
    public List<Country> findAllCountries() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);

        Collection<CountryBo> countryBos = businessObjectService.findMatching(CountryBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(countryBos);
    }

    /**
     * Sets the businessObjectServiceMockFor attribute value.
     *
     * @param businessObjectService The businessObjectServiceMockFor to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Converts a List<CountryBo> to an Unmodifiable List<Country>
     *
     * @param countryBos a mutable List<CountryBo> to made completely immutable.
     * @return An unmodifiable List<Country>
     */
    List<Country> convertListOfBosToImmutables(final Collection<CountryBo> countryBos) {
        ArrayList<Country> countries = new ArrayList<Country>();
        for (CountryBo bo : countryBos) {
            Country country = CountryBo.to(bo);
            countries.add(country);
        }
        return Collections.unmodifiableList(countries);
    }
}
