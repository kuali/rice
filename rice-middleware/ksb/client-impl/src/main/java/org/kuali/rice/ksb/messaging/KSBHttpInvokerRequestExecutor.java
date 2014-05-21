/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.messaging;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.security.HttpClientHeaderDigitalSigner;
import org.kuali.rice.ksb.security.SignatureVerifyingInputStream;
import org.kuali.rice.ksb.security.admin.service.JavaSecurityManagementService;
import org.kuali.rice.ksb.security.service.DigitalSignatureService;
import org.kuali.rice.ksb.util.KSBConstants;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.cert.CertificateFactory;


/**
 * At HttpInvokerRequestExecutor which is capable of digitally signing and verifying messages.  It's capabilities
 * to execute the signing and verification can be turned on or off via an application constant.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KSBHttpInvokerRequestExecutor extends HttpComponentsHttpInvokerRequestExecutor {

    private Boolean secure = Boolean.TRUE;

    public KSBHttpInvokerRequestExecutor() {
        super();
    }

    public KSBHttpInvokerRequestExecutor(Boolean secure) {
        super();
        this.secure = secure;
    }

    public KSBHttpInvokerRequestExecutor(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * Signs the outgoing request by generating a digital signature from the bytes in the ByteArrayOutputStream and attaching the
     * signature and our alias to the headers of the PostMethod.
     */
    @Override
    protected void setRequestBody(HttpInvokerClientConfiguration config, HttpPost httpPost, ByteArrayOutputStream baos) throws IOException {
        if (isSecure()) {
            try {
                signRequest(httpPost, baos);
            } catch (Exception e) {
                throw new RuntimeException("Failed to sign the outgoing message.", e);
            }
        }
        super.setRequestBody(config, httpPost, baos);
    }

    /**
     * Returns a wrapped InputStream which is responsible for verifying the digital signature on the response after all
     * data has been read.
     */
    @Override
    protected InputStream getResponseBody(HttpInvokerClientConfiguration config, HttpResponse postMethod) throws IOException {
        if (isSecure()) {
            // extract and validate the headers
            Header digitalSignatureHeader = postMethod.getFirstHeader(KSBConstants.DIGITAL_SIGNATURE_HEADER);
            Header keyStoreAliasHeader = postMethod.getFirstHeader(KSBConstants.KEYSTORE_ALIAS_HEADER);
            Header certificateHeader = postMethod.getFirstHeader(KSBConstants.KEYSTORE_CERTIFICATE_HEADER);

            if (digitalSignatureHeader == null || StringUtils.isEmpty(digitalSignatureHeader.getValue())) {
                throw new RuntimeException("A digital signature header was required on the response but none was found.");
            }

            boolean foundValidKeystoreAlias = (keyStoreAliasHeader != null && StringUtils.isNotBlank(keyStoreAliasHeader.getValue()));
            boolean foundValidCertificate = (certificateHeader != null && StringUtils.isNotBlank(certificateHeader.getValue()));

            if (!foundValidCertificate && !foundValidKeystoreAlias) {
                throw new RuntimeException("Either a key store alias header or a certificate header was required on the response but neither were found.");
            }

            // decode the digital signature from the header into binary
            byte[] digitalSignature = Base64.decodeBase64(digitalSignatureHeader.getValue().getBytes("UTF-8"));
            String errorQualifier = "General Security Error";

            try {
                Signature signature = null;

                if (foundValidCertificate) {
                    errorQualifier = "Error with given certificate";
                    // get the Signature for verification based on the alias that was sent to us
                    byte[] encodedCertificate = Base64.decodeBase64(certificateHeader.getValue().getBytes("UTF-8"));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    signature = getDigitalSignatureService().getSignatureForVerification(cf.generateCertificate(new ByteArrayInputStream(encodedCertificate)));
                } else if (foundValidKeystoreAlias) {
                    // get the Signature for verification based on the alias that was sent to us
                    String keystoreAlias = keyStoreAliasHeader.getValue();
                    errorQualifier = "Error with given alias " + keystoreAlias;
                    signature = getDigitalSignatureService().getSignatureForVerification(keystoreAlias);
                }

                // wrap the InputStream in an input stream that will verify the signature
                return new SignatureVerifyingInputStream(digitalSignature, signature, super.getResponseBody(config, postMethod));
            } catch (GeneralSecurityException e) {
                throw new RuntimeException("Problem verifying signature: " + errorQualifier,e);
            }
        }

        return super.getResponseBody(config, postMethod);
    }



    @Override
    protected void validateResponse(HttpInvokerClientConfiguration config, HttpResponse response) throws HttpException {
        int statusCode = response.getStatusLine().getStatusCode();

        // HTTP status codes in the 200-299 range indicate success
        if (statusCode >= HttpStatus.SC_MULTIPLE_CHOICES /* 300 */) {
            throw new HttpException(statusCode, "Did not receive successful HTTP response: status code = " + statusCode +
                    ", status message = [" + response.getStatusLine().getReasonPhrase() + "]");
        }
    }

    /**
     * Signs the request by adding headers to the PostMethod.
     */
    protected void signRequest(HttpPost postMethod, ByteArrayOutputStream baos) throws Exception {
        Signature signature = getDigitalSignatureService().getSignatureForSigning();
        HttpClientHeaderDigitalSigner signer =
                new HttpClientHeaderDigitalSigner(signature, postMethod, getJavaSecurityManagementService().getModuleKeyStoreAlias());
        signer.getSignature().update(baos.toByteArray());
        signer.sign();
    }

    protected boolean isSecure() {
        return getSecure();// && Utilities.getBooleanConstant(KewApiConstants.SECURITY_HTTP_INVOKER_SIGN_MESSAGES, false);
    }

    public Boolean getSecure() {
        return this.secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    protected DigitalSignatureService getDigitalSignatureService() {
        return (DigitalSignatureService) GlobalResourceLoader.getService(KSBConstants.ServiceNames.DIGITAL_SIGNATURE_SERVICE);
    }

    protected JavaSecurityManagementService getJavaSecurityManagementService() {
        return (JavaSecurityManagementService)GlobalResourceLoader.getService(KSBConstants.ServiceNames.JAVA_SECURITY_MANAGEMENT_SERVICE);
    }

}
