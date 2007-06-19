package org.kuali.rice.resourceloader;

import org.kuali.rice.exceptions.RiceRuntimeException;

public class ResourceLoaderException extends RiceRuntimeException {

	private static final long serialVersionUID = -9089140992612301469L;

	public ResourceLoaderException(String message) {
		super(message);
	}

	public ResourceLoaderException() {
		super();
	}

	public ResourceLoaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceLoaderException(Throwable cause) {
		super(cause);
	}

}
