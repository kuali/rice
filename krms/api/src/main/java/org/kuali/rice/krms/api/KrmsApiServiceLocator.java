package org.kuali.rice.krms.api;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.ksb.api.bus.ServiceBus;
import org.kuali.rice.ksb.api.messaging.MessageHelper;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;

import javax.xml.namespace.QName;

/**
 * A static service locator which aids in locating the various services that form the Kuali Rule Management System API.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KrmsApiServiceLocator {

	public static final String ENGINE = "rice.krms.engine";
	public static final QName RULE_REPOSITORY_SERVICE = new QName(KrmsConstants.Namespaces.KRMS_NAMESPACE_2_0, "ruleRepositoryServiceSoap");
    public static final QName KRMS_TYPE_REPOSITORY_SERVICE = new QName(KrmsConstants.Namespaces.KRMS_NAMESPACE_2_0, "krmsTypeRepositoryServiceSoap");

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    static <T> T getService(QName serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static Engine getEngine() {
        return getService(ENGINE);
    }
    
    public static RuleRepositoryService getRuleRepositoryService() {
    	return getService(RULE_REPOSITORY_SERVICE);
    }

    public static KrmsTypeRepositoryService getKrmsTypeRepositoryService() {
        return getService(KRMS_TYPE_REPOSITORY_SERVICE);
    }

}
