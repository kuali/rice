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
package org.kuali.rice.ksb.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.kuali.rice.ksb.service.KSBServiceLocator;

public class DigitalSignatureServiceImpl implements DigitalSignatureService {

	public Signature getSignatureForSigning() throws IOException, GeneralSecurityException {
		Signature signature = getSignature();
		signature.initSign(KSBServiceLocator.getJavaSecurityManagementService().getModulePrivateKey());
		return signature;
	}

    public Signature getSignatureForVerification(String verificationAlias) throws IOException, GeneralSecurityException {
        Certificate cert = KSBServiceLocator.getJavaSecurityManagementService().getCertificate(verificationAlias);
        return getSignatureForVerification(cert);
    }

    public Signature getSignatureForVerification(Certificate certificate) throws IOException, GeneralSecurityException {
        if (certificate == null) {
            throw new CertificateException("Could not find certificate");
        }
        PublicKey publicKey = certificate.getPublicKey();
        if (publicKey == null) {
            throw new KeyException("Could not find the public key from valid certificate");
        }
        Signature signature = getSignature();
        signature.initVerify(publicKey);
        return signature;
    }
    
	protected Signature getSignature() throws GeneralSecurityException {
		return Signature.getInstance(KSBServiceLocator.getJavaSecurityManagementService().getModuleSignatureAlgorithm());
	}

}
