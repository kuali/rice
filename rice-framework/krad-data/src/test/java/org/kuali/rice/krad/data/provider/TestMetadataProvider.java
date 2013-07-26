package org.kuali.rice.krad.data.provider;

import java.util.Collection;
import java.util.Map;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;

/**
 */
public class TestMetadataProvider implements MetadataProvider {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
	public Map<Class<?>, DataObjectMetadata> provideMetadata() {
        return null;
    }

    @Override
    public DataObjectMetadata getMetadataForType(Class<?> dataObjectType) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean handles(Class<?> type) {
        return true;
    }

    @Override
	public Collection<Class<?>> getSupportedTypes() {
        return null;
    }

	@Override
	public boolean requiresListOfExistingTypes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<Class<?>, DataObjectMetadata> provideMetadataForTypes(Collection<Class<?>> types) {
		// TODO Auto-generated method stub
		return null;
	}
}