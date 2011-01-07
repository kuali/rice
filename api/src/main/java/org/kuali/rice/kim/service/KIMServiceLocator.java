package org.kuali.rice.kim.service;

import org.apache.log4j.Logger;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

import javax.xml.namespace.QName;

public class KIMServiceLocator {
    private static final Logger LOG = Logger.getLogger(KIMServiceLocator.class);

    public static final String KIM_PERSON_SERVICE = "personService";

    @SuppressWarnings("unchecked")
	public static PersonService getPersonService() {
    	if (LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + KIM_PERSON_SERVICE);
		}
    	return (PersonService) GlobalResourceLoader.getResourceLoader().getService(new QName(KIM_PERSON_SERVICE));
    }
}
