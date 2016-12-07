package org.kuali.rice.krad.util.documentserializer;

/**
 * A very simple implementation of {@link PropertySerializabilityEvaluator} which simply uses the
 * provided {@link PropertySerializerTrie} as metadata to define what should be serialized.
 *
 * @author Eric Westfall
 */
public class MetadataPropertySerializabilityEvaluator extends PropertySerializabilityEvaluatorBase {

    public MetadataPropertySerializabilityEvaluator(PropertySerializerTrie metadata) {
        if (metadata == null) {
            throw new IllegalArgumentException("metadata is required but was null");
        }
        this.serializableProperties = metadata;
    }

}
