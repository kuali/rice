package org.kuali.rice.krms.api;

public class AssetResolutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public AssetResolutionException() {
	}

	public AssetResolutionException(String message) {
		super(message);
	}

	public AssetResolutionException(Throwable cause) {
		super(cause);
	}

	public AssetResolutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
