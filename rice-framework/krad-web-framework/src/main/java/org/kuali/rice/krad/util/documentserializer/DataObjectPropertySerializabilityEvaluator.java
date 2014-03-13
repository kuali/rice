/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.util.documentserializer;

import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.annotation.SerializationContext;
import org.kuali.rice.krad.service.KRADServiceLocator;

/**
 * Determines the serializability of fields based on their data object metadata.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectPropertySerializabilityEvaluator extends PropertySerializabilityEvaluatorBase {

    private final SerializationContext serializationContext;
    private MetadataRepository metadataRepository;

    /**
     * C'tor for DataObjectPropertySerializabilityEvaluator.
     *
     * <p>note that the serializationContext specified must not be
     * {@link org.kuali.rice.krad.data.provider.annotation.SerializationContext}.ALL</p>
     *
     * @param serializationContext sets the serialization type to use when checking against the metadata
     */
    public DataObjectPropertySerializabilityEvaluator(SerializationContext serializationContext) {
        if (serializationContext == SerializationContext.ALL) {
            throw new IllegalArgumentException("The serializationContext must be specific");
        }
        this.serializationContext = serializationContext;
    }

    /**
     * Determines if a property is serializable by looking at the data object metadata and checking if the attribute
     * is set to be serialized for the {@link SerializationContext} this DataObjectPropertySerializabilityEvaluator
     * instance was constructed with.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isPropertySerializable(SerializationState state, Object containingObject, String childPropertyName,
            Object childPropertyValue) {

        DataObjectMetadata dataObjectMetadata = getMetadataRepository().getMetadata(containingObject.getClass());

        if (dataObjectMetadata != null) {
            DataObjectAttribute dataObjectAttribute = dataObjectMetadata.getAttribute(childPropertyName);
            if (dataObjectAttribute != null) return dataObjectAttribute.isSerialized(serializationContext);
        }

        return true;
    }

    /**
     * @return the metadata repository service.
     */
    public MetadataRepository getMetadataRepository() {
        if (metadataRepository == null) {
            metadataRepository = KRADServiceLocator.getMetadataRepository();
        }

        return metadataRepository;
    }

    /**
     * Sets the metadata repository service.
     *
     * @param metadataRepository the metadata repository service to set.
     */
    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }
}
