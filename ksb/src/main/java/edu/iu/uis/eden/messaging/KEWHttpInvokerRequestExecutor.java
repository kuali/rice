/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Signature;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.springframework.remoting.httpinvoker.CommonsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;

import edu.iu.uis.eden.security.HttpClientHeaderDigitalSigner;
import edu.iu.uis.eden.security.SignatureVerifyingInputStream;

/**
 * At HttpInvokerRequestExecutor which is capable of digitally signing and verifying messages.  It's capabilities
 * to execute the signing and verification can be turned on or off via an application constant.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KEWHttpInvokerRequestExecutor extends CommonsHttpInvokerRequestExecutor {
	
	private Boolean secure = Boolean.TRUE;
	
	public KEWHttpInvokerRequestExecutor() {
		super();
	}
	
	public KEWHttpInvokerRequestExecutor(Boolean secure) {
		super();
		this.secure = secure;
	}

	public KEWHttpInvokerRequestExecutor(HttpClient httpClient) {
		super(httpClient);
	}
	
	/**
	 * Signs the outgoing request by generating a digital signature from the bytes in the ByteArrayOutputStream and attaching the
	 * signature and our alias to the headers of the PostMethod.
	 */
	@Override
	protected void setRequestBody(HttpInvokerClientConfiguration config, PostMethod postMethod, ByteArrayOutputStream baos) throws IOException {
		if (isSecure()) {
			try {
				signRequest(postMethod, baos);	
			} catch (Exception e) {
				throw new RuntimeException("Failed to sign the outgoing message.", e);
			}
		}
		super.setRequestBody(config, postMethod, baos);
	}
	
	/**
	 * Returns a wrapped InputStream which is responsible for verifying the digital signature on the response after all
	 * data has been read.
	 */
	@Override
	protected InputStream getResponseBody(HttpInvokerClientConfiguration config, PostMethod postMethod) throws IOException {
		if (isSecure()) {
			// extract and validate the headers
			Header digitalSignatureHeader = postMethod.getResponseHeader(RiceConstants.DIGITAL_SIGNATURE_HEADER);
			Header keyStoreAliasHeader = postMethod.getResponseHeader(RiceConstants.KEYSTORE_ALIAS_HEADER);
			if (digitalSignatureHeader == null || StringUtils.isEmpty(digitalSignatureHeader.getValue())) {
				throw new RuntimeException("A digital signature header was required on the response but none was found.");
			}
			if (keyStoreAliasHeader == null || StringUtils.isEmpty(keyStoreAliasHeader.getValue())) {
				throw new RuntimeException("A key store alias header was required on the response but none was found.");
			}
			// decode the digital signature from the header into binary
			byte[] digitalSignature = Base64.decodeBase64(digitalSignatureHeader.getValue().getBytes("UTF-8"));
			String keystoreAlias = keyStoreAliasHeader.getValue();
			try {
				// get the Signature for verification based on the alias that was sent to us
				Signature signature = KSBServiceLocator.getDigitalSignatureService().getSignatureForVerification(keystoreAlias);
				// wrap the InputStream in an input stream that will verify the signature
				return new SignatureVerifyingInputStream(digitalSignature, signature, super.getResponseBody(config, postMethod));
			} catch (GeneralSecurityException e) {
				throw new RuntimeException(e);
			}
		}
		return super.getResponseBody(config, postMethod);
	}

	
	
	@Override
	protected void validateResponse(HttpInvokerClientConfiguration config, PostMethod postMethod) throws IOException {
		if (postMethod.getStatusCode() >= 300) {
			throw new HttpException(postMethod.getStatusCode(), "Did not receive successful HTTP response: status code = " + postMethod.getStatusCode() +
					", status message = [" + postMethod.getStatusText() + "]");
		}
	}

	/**
	 * Signs the request by adding headers to the PostMethod.
	 */
	protected void signRequest(PostMethod postMethod, ByteArrayOutputStream baos) throws Exception {
		Signature signature = KSBServiceLocator.getDigitalSignatureService().getSignatureForSigning();
		HttpClientHeaderDigitalSigner signer = new HttpClientHeaderDigitalSigner(signature, postMethod, KSBServiceLocator.getDigitalSignatureService().getKeyStoreAlias());
		signer.getSignature().update(baos.toByteArray());
		signer.sign();
	}
	
	protected boolean isSecure() {
		return getSecure();// && Utilities.getBooleanConstant(EdenConstants.SECURITY_HTTP_INVOKER_SIGN_MESSAGES, false);
	}

	public Boolean getSecure() {
		return this.secure;
	}

	public void setSecure(Boolean secure) {
		this.secure = secure;
	}	
}