package org.kuali.rice.kim;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.service.EntityService;
import org.kuali.rice.kim.service.GroupTypeService;
import org.kuali.rice.kim.service.NamespaceService;

/**
 * Service locator for KIM.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public final class KIMServiceLocator {

	private static final Logger LOG = Logger.getLogger(KIMServiceLocator.class);

	public static final String KIM_ENTITY_SERVICE = "entityService";

    public static final String KIM_NAMESPACE_SERVICE = "namespaceService";
    
    public static final String KIM_GROUP_TYPE_SERVICE = "groupTypeService";
	
	public static Object getService(String serviceName) {
		return getBean(serviceName);
	}

	public static Object getBean(String serviceName) {
		LOG.debug("Fetching service " + serviceName);
		return GlobalResourceLoader.getResourceLoader().getService(new QName(serviceName));
	}
    
    public static EntityService getEntityService() {       
        return (EntityService) getService(KIM_ENTITY_SERVICE);
    }

    public static NamespaceService getNamespaceService() {       
        return (NamespaceService) getService(KIM_NAMESPACE_SERVICE);
    }
    
    public static GroupTypeService getGroupTypeService() {       
        return (GroupTypeService) getService(KIM_GROUP_TYPE_SERVICE);
    }
}