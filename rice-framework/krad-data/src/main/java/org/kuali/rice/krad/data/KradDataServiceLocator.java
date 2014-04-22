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
package org.kuali.rice.krad.data;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.ProviderRegistry;

/**
 * Uses to locate services for the given service name.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradDataServiceLocator {
    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static DataObjectService getDataObjectService() {
        return getService("dataObjectService");
    }

    public static MetadataRepository getMetadataRepository() {
        return getService("metadataRepository");
    }

	public static ProviderRegistry getProviderRegistry() {
		return getService("providerRegistry");
	}
}
