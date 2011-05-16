package org.kuali.rice.ksb.api.bus.support;

import java.util.ArrayList;
import java.util.List;

public class JavaServiceConfiguration extends AbstractServiceConfiguration {

	private static final long serialVersionUID = -4226512121638441108L;

	private List<String> serviceInterfaces = new ArrayList<String>();
	
	private JavaServiceConfiguration(Builder builder) {
		super(builder);
		this.serviceInterfaces = new ArrayList<String>(builder.getServiceInterfaces());
	}
	
	public static JavaServiceConfiguration fromServiceDefinition(JavaServiceDefinition javaServiceDefinition) {
		return Builder.create(javaServiceDefinition).build();
	}
		
	public List<String> getServiceInterfaces() {
		return this.serviceInterfaces;
	}
	
	protected void setServiceInterfaces(List<String> serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
	}
	
	public static final class Builder extends AbstractServiceConfiguration.Builder<JavaServiceConfiguration> {

		private static final long serialVersionUID = 4300659121377259098L;

		private List<String> serviceInterfaces;
				
		public List<String> getServiceInterfaces() {
			return serviceInterfaces;
		}
		
		public void setServiceInterfaces(List<String> serviceInterfaces) {
			this.serviceInterfaces = serviceInterfaces;
		}
		
		private Builder() {}
		
		public static Builder create() {
			Builder builder = new Builder();
			builder.setServiceInterfaces(new ArrayList<String>());
			return builder;
		}
		
		public static Builder create(JavaServiceDefinition javaServiceDefinition) {
			Builder builder = create();
			builder.copyServiceDefinitionProperties(javaServiceDefinition);
			builder.setServiceInterfaces(javaServiceDefinition.getServiceInterfaces());
			return builder;
		}

		@Override
		public JavaServiceConfiguration build() {
			return new JavaServiceConfiguration(this);
		}
		
	}
	
}
