package org.kuali.rice.shareddata.api.services;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.shareddata.api.country.CountryService;

public class SharedDataApiServiceLocator {
    public static final String COUNTRY_SERVICE = "countryService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static CountryService getCountryService() {
        return (CountryService) getService(COUNTRY_SERVICE);
    }
}
