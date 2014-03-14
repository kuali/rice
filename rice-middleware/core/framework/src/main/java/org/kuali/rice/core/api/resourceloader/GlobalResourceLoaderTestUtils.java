/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.resourceloader;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to allow the injection of services that are fetched via the
 * {@link GlobalResourceLoader}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class GlobalResourceLoaderTestUtils {

    private static Map<String, Object> mockServiceMap;

    private volatile boolean initialized = false;

    // private constructor, instances are not allowed
    private GlobalResourceLoaderTestUtils() {
        throw new UnsupportedOperationException("Don't construct me, I'm all about the static methods.");
    }

    /**
     * Inject a service into the {@link org.kuali.rice.core.api.resourceloader.GlobalResourceLoader} such that anytime
     * that service is fetched by serviceName, the supplied mockService will be returned.
     *
     * @param serviceName the name that the service is known by in the GRL
     * @param mockService the service to inject
     */
    public static void addMockService(final String serviceName, final Object mockService) {
        getMockServiceMap().put(serviceName, mockService);
    }

    /**
     * Getter for the mockServiceMap, which on first call constructs it and also initializes our hack of the GRL.
     *
     * @return the mockServiceMap
     */
    private static synchronized Map<String, Object> getMockServiceMap() {
        // the first time this is called, inject our special resource loader
        if (mockServiceMap == null) {
            mockServiceMap = new ConcurrentHashMap<String, Object>();

            // this little dance is required to prevent issues when

            SimpleConfig config = new SimpleConfig();
            config.putProperty(CoreConstants.Config.APPLICATION_ID, "TEST");
            ConfigContext.init(config);

            try {
                GlobalResourceLoader.stop();
            } catch (Exception e) {
                throw new RiceRuntimeException(e);
            }

            // Add to the front of the line our hacked resource loader which will fetch services from the mockServiceMap
            GlobalResourceLoader.addResourceLoaderFirst(new BaseResourceLoader(new QName("TEST", "TEST")) {
                @Override
                public Object getService(QName serviceQName) {

                    String localPart = serviceQName.getLocalPart();
                    return mockServiceMap.get(localPart);
                }
            });
        }

        return mockServiceMap;
    }
}
