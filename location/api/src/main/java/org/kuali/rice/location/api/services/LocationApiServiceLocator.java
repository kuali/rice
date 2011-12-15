/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.location.api.services;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.county.CountyService;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.state.StateService;

import javax.xml.namespace.QName;

/**
 * <p>LocationApiServiceLocator class.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationApiServiceLocator {

    public static final QName COUNTRY_SERVICE = buildName("countryService");

    public static final QName CAMPUS_SERVICE = buildName("campusService");

    public static final QName STATE_SERVICE = buildName("stateService");

    public static final QName COUNTY_SERVICE = buildName("countyService");

    public static final QName POSTAL_CODE_SERVICE = buildName("postalCodeService");

    private static QName buildName(String serviceName) {
        return new QName(LocationConstants.Namespaces.LOCATION_NAMESPACE_2_0, serviceName);
    }

    static <T> T getService(QName serviceName) {
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
