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
package edu.iu.uis.eden.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.kuali.bus.services.KSBServiceLocator;

public class DigitalSignatureServiceImpl implements DigitalSignatureService {

	public Signature getSignatureForSigning() throws IOException, GeneralSecurityException {
		Signature signature = getSignature();
		signature.initSign(KSBServiceLocator.getJavaSecurityManagementService().getModulePrivateKey());
		return signature;
	}

	public Signature getSignatureForVerification(String verificationAlias) throws IOException, GeneralSecurityException {
		Signature signature = getSignature();
		Certificate cert = KSBServiceLocator.getJavaSecurityManagementService().getModuleCertificate(verificationAlias);
		String keyStoreLocation = KSBServiceLocator.getJavaSecurityManagementService().getModuleKeyStoreLocation();
		if (cert == null) {
            throw new CertificateException("Could not find certificate for the given alias: " + verificationAlias + " in keystore " + keyStoreLocation);
		}
		PublicKey publicKey = cert.getPublicKey();
		if (publicKey == null) {
			throw new KeyException("Could not find the public key from valid certificate with given alias: " + verificationAlias + " in keystore " + keyStoreLocation);
		}
	    signature.initVerify(publicKey);
	    return signature;
	}

	protected Signature getSignature() throws GeneralSecurityException {
		return Signature.getInstance(KSBServiceLocator.getJavaSecurityManagementService().getModuleSignatureAlgorithm());
	}

}
