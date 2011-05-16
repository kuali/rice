package org.kuali.rice.ksb.api.bus.support;



public class SoapServiceConfiguration extends AbstractServiceConfiguration {

	private static final long serialVersionUID = -4226512121638441108L;

	private String serviceInterface;
	private boolean jaxWsService = false;
	
	private SoapServiceConfiguration(Builder builder) {
		super(builder);
		this.serviceInterface = builder.getServiceInterface();
		this.jaxWsService = builder.isJaxWsService();
	}
	
	public static SoapServiceConfiguration fromServiceDefinition(SoapServiceDefinition soapServiceDefinition) {
		return Builder.create(soapServiceDefinition).build();
	}
	
	public String getServiceInterface() {
		return serviceInterface;
	}

	public boolean isJaxWsService() {
		return jaxWsService;
	}
	
	public static final class Builder extends AbstractServiceConfiguration.Builder<SoapServiceConfiguration> {

		private static final long serialVersionUID = 722267174667364588L;

		private String serviceInterface;
		private boolean jaxWsService = false;		
				
		public String getServiceInterface() {
			return serviceInterface;
		}

		public void setServiceInterface(String serviceInterface) {
			this.serviceInterface = serviceInterface;
		}

		public boolean isJaxWsService() {
			return jaxWsService;
		}

		public void setJaxWsService(boolean jaxWsService) {
			this.jaxWsService = jaxWsService;
		}

		private Builder() {}
		
		public static Builder create() {
			return new Builder();
		}
		
		public static Builder create(SoapServiceDefinition restServiceDefinition) {
			Builder builder = create();
			builder.copyServiceDefinitionProperties(restServiceDefinition);
			builder.setServiceInterface(restServiceDefinition.getServiceInterface());
			builder.setJaxWsService(restServiceDefinition.isJaxWsService());
			return builder;
		}

		@Override
		public SoapServiceConfiguration build() {
			return new SoapServiceConfiguration(this);
		}
		
	}
	
}
