package edu.iu.uis.eden.security;

import java.security.Signature;

import org.apache.commons.httpclient.HttpMethod;
import org.kuali.rice.RiceConstants;

/**
 * A DigitalSigner implementation which places the alias and digital signature into the request
 * headers of the commons HttpClient's HttpMethod.
 * 
 * @author Eric Westfall
 */
public class HttpClientHeaderDigitalSigner extends AbstractDigitalSigner {

	private HttpMethod method;
	private String alias;
	
	
	public HttpClientHeaderDigitalSigner(Signature signature, HttpMethod method, String alias) {
		super(signature);
		this.method = method;
		this.alias = alias;
	}
	
	public void sign() throws Exception {
	    this.method.addRequestHeader(RiceConstants.KEYSTORE_ALIAS_HEADER, this.alias);
	    this.method.addRequestHeader(RiceConstants.DIGITAL_SIGNATURE_HEADER, getEncodedSignature());
	}

}
