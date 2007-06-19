package edu.iu.uis.eden.messaging;

public class HttpException extends org.apache.commons.httpclient.HttpException {


	private static final long serialVersionUID = -2660638986164631692L;
	
	private int responseCode;
	
	public HttpException(int responseCode) {
		super();
		this.responseCode = responseCode;
	}

	public HttpException(int responseCode, String message, Throwable cause) {
		super(message, cause);
		this.responseCode = responseCode;
	}

	public HttpException(int responseCode, String arg0) {
		super(arg0);
		this.responseCode = responseCode;
	}

	public int getResponseCode() {
		return this.responseCode;
	}
	
}
