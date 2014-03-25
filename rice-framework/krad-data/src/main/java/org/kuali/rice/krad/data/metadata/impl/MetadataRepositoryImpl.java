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
package org.kuali.rice.krad.data.metadata.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.springframework.beans.factory.annotation.Required;

/**
 * MetadataRepository implementation backed by the ProviderRegistry
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MetadataRepositoryImpl implements MetadataRepository {

    private ProviderRegistry registry;

    @Required
    public void setProviderRegistry(ProviderRegistry registry) {
        this.registry = registry;
    }

    @Override
	public synchronized DataObjectMetadata getMetadata(Class<?> type) {
        // don't compose, just return first provider result
        for (MetadataProvider provider: registry.getMetadataProviders()) {
            if (provider.handles(type)) {
                return provider.getMetadataForType(type);
            }
        }
        return null;
    }

    @Override
	public Map<Class<?>, DataObjectMetadata> getAllMetadata() {
		Map<Class<?>, DataObjectMetadata> allMetaData = new HashMap<Class<?>, DataObjectMetadata>();
        // accumulate all metadata
        // subsequent mappings for given data object type override previous mappings
        // so provider order matters - last one wins
        for (MetadataProvider provider: registry.getMetadataProviders()) {
            allMetaData.putAll(provider.provideMetadata());
        }
        return allMetaData;
    }

    @Override
    public boolean contains(Class<?> type) {
        return getMetadata(type) != null;
    }

}
