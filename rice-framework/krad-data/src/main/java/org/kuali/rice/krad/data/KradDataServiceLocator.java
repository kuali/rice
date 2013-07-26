package org.kuali.rice.krad.data;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.ProviderRegistry;

/**
 */
public class KradDataServiceLocator {
    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static DataObjectService getDataObjectService() {
        return getService("kd-dataObjectService");
    }

    public static MetadataRepository getMetadataRepository() {
        return getService("kd-metadataRepository");
    }

	public static ProviderRegistry getProviderRegistry() {
		return getService("kd-providerRegistry");
	}
}
