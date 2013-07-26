package org.kuali.rice.krad.data.provider.exception;

public class MetadataConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 3518029933066928419L;

	public MetadataConfigurationException() {
		super();
	}

	public MetadataConfigurationException(String message) {
		super(message);
	}

	public MetadataConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetadataConfigurationException(Throwable cause) {
		super(cause);
	}
}
