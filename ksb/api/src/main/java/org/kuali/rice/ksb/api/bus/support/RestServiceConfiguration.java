package org.kuali.rice.ksb.api.bus.support;

import java.util.HashMap;
import java.util.Map;

public class RestServiceConfiguration extends AbstractServiceConfiguration {

	private static final long serialVersionUID = -4226512121638441108L;

	private String resourceClass;
	private Map<String, String> resourceToClassNameMap;
	
	private RestServiceConfiguration(Builder builder) {
		super(builder);
		this.resourceClass = builder.getResourceClass();
		this.resourceToClassNameMap = new HashMap<String, String>(builder.getResourceToClassNameMap());
	}
	
	public static RestServiceConfiguration fromServiceDefinition(RestServiceDefinition restServiceDefinition) {
		return Builder.create(restServiceDefinition).build();
	}
		
	public String getResourceClass() {
		return this.resourceClass;
	}
	
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
			if (resourceToClassNameMap == null) {
				throw new IllegalArgumentException("resourceToClassNameMap was null");
			}
			this.resourceToClassNameMap = resourceToClassNameMap;
		}
		
		private Builder() {
			setResourceToClassNameMap(new HashMap<String, String>());
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
	
}
