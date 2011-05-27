package org.kuali.rice.ksb.api.bus.support;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;

@XmlRootElement(name = RestServiceConfiguration.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RestServiceConfiguration.Constants.TYPE_NAME, propOrder = {
		RestServiceConfiguration.Elements.RESOURCE_CLASS,
		RestServiceConfiguration.Elements.RESOURCE_TO_CLASS_NAME_MAP
})
public final class RestServiceConfiguration extends AbstractServiceConfiguration {

	private static final long serialVersionUID = -4226512121638441108L;

	@XmlElement(name = Elements.RESOURCE_CLASS, required = false)
	private final String resourceClass;
	
	@XmlJavaTypeAdapter(MapStringStringAdapter.class)
	@XmlElement(name = Elements.RESOURCE_TO_CLASS_NAME_MAP, required = false)
	private final Map<String, String> resourceToClassNameMap;
	
	/**
     * Private constructor used only by JAXB.
     */
	private RestServiceConfiguration() {
		super();
		this.resourceClass = null;
		this.resourceToClassNameMap = null;
	}
	
	private RestServiceConfiguration(Builder builder) {
		super(builder);
		this.resourceClass = builder.getResourceClass();
		if (builder.getResourceToClassNameMap() != null) {
			this.resourceToClassNameMap = new HashMap<String, String>(builder.getResourceToClassNameMap());
		} else {
			this.resourceToClassNameMap = null;
		}
	}
	
	public static RestServiceConfiguration fromServiceDefinition(RestServiceDefinition restServiceDefinition) {
		return Builder.create(restServiceDefinition).build();
	}
		
	public String getResourceClass() {
		return this.resourceClass;
	}
	
	/**
	 * Returns a map of resource names to resource classes.  Can be null if
	 * there are no mapped resources on this service configuration.
	 * 
	 * @return the resource to class name map, or null if no resources have
	 * been mapped
	 */
	public Map<String, String> getResourceToClassNameMap() {
		return this.resourceToClassNameMap;
	}
	
	/**
	 * @param className
	 * @return true if this service contains a resource for the given class name
	 */
	public boolean hasClass(String className) {
		if (resourceToClassNameMap == null) {
			return false;
		}
		return resourceToClassNameMap.containsValue(className);
	}
	
	public static final class Builder extends AbstractServiceConfiguration.Builder<RestServiceConfiguration> {

		private static final long serialVersionUID = 4300659121377259098L;

		private String resourceClass;
		private Map<String, String> resourceToClassNameMap;
				
		public String getResourceClass() {
			return resourceClass;
		}

		public void setResourceClass(String resourceClass) {
			this.resourceClass = resourceClass;
		}

		public Map<String, String> getResourceToClassNameMap() {
			return resourceToClassNameMap;
		}

		public void setResourceToClassNameMap(Map<String, String> resourceToClassNameMap) {
			this.resourceToClassNameMap = resourceToClassNameMap;
		}
		
		private Builder() {
		}
		
		public static Builder create() {
			return new Builder();
		}
		
		public static Builder create(RestServiceDefinition restServiceDefinition) {
			Builder builder = create();
			builder.copyServiceDefinitionProperties(restServiceDefinition);
			builder.setResourceClass(restServiceDefinition.getResourceClass());
			if (restServiceDefinition.getResourceToClassNameMap() != null) {
				builder.setResourceToClassNameMap(restServiceDefinition.getResourceToClassNameMap());
			}
			return builder;
		}

		@Override
		public RestServiceConfiguration build() {
			return new RestServiceConfiguration(this);
		}
		
	}
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
    	final static String ROOT_ELEMENT_NAME = "restServiceConfiguration";
        final static String TYPE_NAME = "RestServiceConfigurationType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
     static class Elements {
    	protected final static String RESOURCE_CLASS = "resourceClass";
    	protected final static String RESOURCE_TO_CLASS_NAME_MAP = "resourceToClassNameMap";
    }

	
}
