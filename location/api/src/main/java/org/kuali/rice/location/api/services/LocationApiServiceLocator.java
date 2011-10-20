/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.opensource.org/licenses/ecl2.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kuali.rice.location.api.services;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.county.CountyService;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.state.StateService;

/**
 * <p>LocationApiServiceLocator class.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationApiServiceLocator {
    /** Constant <code>COUNTRY_SERVICE="countryService"</code> */
    public static final String COUNTRY_SERVICE = "countryService";
    /** Constant <code>CAMPUS_SERVICE="campusService"</code> */
    public static final String CAMPUS_SERVICE = "campusService";
    /** Constant <code>STATE_SERVICE="stateService"</code> */
    public static final String STATE_SERVICE = "stateService";
    /** Constant <code>COUNTY_SERVICE="countyService"</code> */
    public static final String COUNTY_SERVICE = "countyService";
    /** Constant <code>POSTAL_CODE_SERVICE="postalCodeService"</code> */
    public static final String POSTAL_CODE_SERVICE = "postalCodeService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    /**
     * <p>getCountryService.</p>
     */
    public static CountryService getCountryService() {
        return getService(COUNTRY_SERVICE);
    }

    /**
     * <p>getCampusService.</p>
     */
    public static CampusService getCampusService() {
        return getService(CAMPUS_SERVICE);
    }

    /**
     * <p>getStateService.</p>
     */
    public static StateService getStateService() {
        return getService(STATE_SERVICE);
    }

    /**
     * <p>getCountyService.</p>
     */
    public static CountyService getCountyService() {
        return (CountyService) getService(COUNTY_SERVICE);
    }

    /**
     * <p>getPostalCodeService.</p>
     */
    public static PostalCodeService getPostalCodeService() {
        return (PostalCodeService) getService(POSTAL_CODE_SERVICE);
    }
}
