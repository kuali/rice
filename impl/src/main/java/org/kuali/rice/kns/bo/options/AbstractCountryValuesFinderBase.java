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
package org.kuali.rice.kns.bo.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class AbstractCountryValuesFinderBase extends KeyValuesBase {
	/**
	 * Returns a list of countries that will be added to the result of {@link #getKeyValues()}.  Note that the result may
	 * be filtered by active status
	 * 
	 * @return
	 */
	protected abstract List<Country> retrieveCountriesForValuesFinder();
	
    public List<KeyLabelPair> getKeyValues() {
		List<Country> boList = retrieveCountriesForValuesFinder();
		final Country defaultCountry = KNSServiceLocator.getCountryService().getDefaultCountry();
		List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>( boList.size() + 1 );
		
        labels.add(new KeyLabelPair("", ""));
       	labels.add(new KeyLabelPair(defaultCountry.getPostalCountryCode(), defaultCountry.getPostalCountryName()));
        	
        Collections.sort(boList, new Comparator<Country>() {
			public int compare(Country o1, Country o2) {
				// some institutions may prefix the country name with an asterisk if the country no longer exists
				// the country names will be compared without the asterisk
				String sortValue1 = StringUtils.trim(StringUtils.removeStart(o1.getPostalCountryName(), "*"));
				String sortValue2 = StringUtils.trim(StringUtils.removeStart(o2.getPostalCountryName(), "*"));
				return sortValue1.compareToIgnoreCase(sortValue2);
			}
        	
        });
        
        // the default country may show up twice, but that's fine
        for (Country country : boList) {
        	if (country.isActive()) {
        		labels.add(new KeyLabelPair(country.getPostalCountryCode(), country.getPostalCountryName()));
        	}
        }
        return labels;
    }
}
