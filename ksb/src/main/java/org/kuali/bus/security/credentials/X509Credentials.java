package org.kuali.bus.security.credentials;

import java.security.cert.X509Certificate;

import org.kuali.rice.security.credentials.Credentials;

public class X509Credentials implements Credentials {

	private final X509Certificate certificate;
	
	public X509Credentials(final X509Certificate certificate) {
		this.certificate = certificate;
	}
	
	public X509Certificate getX509Certificate() {
		return this.certificate;
	}
}
