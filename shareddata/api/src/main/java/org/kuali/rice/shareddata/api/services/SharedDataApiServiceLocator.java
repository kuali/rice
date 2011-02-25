package org.kuali.rice.shareddata.api.services;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.shareddata.api.campus.CampusService;
import org.kuali.rice.shareddata.api.country.CountryService;
import org.kuali.rice.shareddata.api.county.CountyService;
import org.kuali.rice.shareddata.api.postalcode.PostalCodeService;
import org.kuali.rice.shareddata.api.state.StateService;

public class SharedDataApiServiceLocator {
    public static final String COUNTRY_SERVICE = "countryService";
    public static final String CAMPUS_SERVICE = "campusService";
    public static final String STATE_SERVICE = "stateService";
    public static final String COUNTY_SERVICE = "countyService";
    public static final String POSTAL_CODE_SERVICE = "postalCodeService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static CountryService getCountryService() {
        return getService(COUNTRY_SERVICE);
    }

    public static CampusService getCampusService() {
        return getService(CAMPUS_SERVICE);
    }

    public static StateService getStateService() {
        return getService(STATE_SERVICE);
    }

    public static CountyService getCountyService() {
        return (CountyService) getService(COUNTY_SERVICE);
    }

    public static PostalCodeService getPostalCodeService() {
        return (PostalCodeService) getService(POSTAL_CODE_SERVICE);
    }
}
