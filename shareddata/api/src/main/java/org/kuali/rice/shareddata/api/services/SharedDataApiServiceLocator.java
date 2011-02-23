package org.kuali.rice.shareddata.api.services;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.shareddata.api.campus.CampusService;
import org.kuali.rice.shareddata.api.country.CountryService;

public class SharedDataApiServiceLocator {
    public static final String COUNTRY_SERVICE = "countryService";
    public static final String CAMPUS_SERVICE = "campusService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static CountryService getCountryService() {
        return (CountryService) getService(COUNTRY_SERVICE);
    }
    
    public static CampusService getCampusService() {
        return (CampusService) getService(CAMPUS_SERVICE);
    }
}
