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
package org.kuali.rice.ksb.security.admin;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.kuali.rice.ksb.security.admin.JavaSecurityManagementServiceImpl;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MockJavaSecurityManagementServiceImpl extends JavaSecurityManagementServiceImpl implements MockJavaSecurityManagementService {
    
    private static final String FAKE_KEYSTORE_ALIAS = "test_keystore_alias";
    private static final String FAKE_KEYSTORE_PASSWORD = "test_keystore_pass";

    private KeyStore moduleKeyStore;
    private PrivateKey modulePrivateKey;

    @Override
    public void afterPropertiesSet() throws Exception {
    }
    
    private void setUpService() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(CLIENT_KEY_GENERATOR_ALGORITHM);
//            SecureRandom random = SecureRandom.getInstance(CLIENT_SECURE_RANDOM_ALGORITHM);
            keyGen.initialize(CLIENT_KEY_PAIR_KEY_SIZE);
//            keyGen.initialize(new RSAKeyGenParameterSpec(512,RSAKeyGenParameterSpec.F0));
            KeyPair pair = keyGen.generateKeyPair();

            this.modulePrivateKey = pair.getPrivate();
            Certificate cert = generateCertificate(pair, getModuleKeyStoreAlias());
            this.moduleKeyStore = generateKeyStore(cert, pair.getPrivate(), getModuleKeyStoreAlias(), getModuleKeyStorePassword());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getModuleKeyStoreLocation() {
        throw new RuntimeException("KeyStoreLocation should not be needed in unit tests");
    }

    @Override
    public String getModuleKeyStoreAlias() {
        return FAKE_KEYSTORE_ALIAS;
    }

    @Override
    public String getModuleKeyStorePassword() {
        return FAKE_KEYSTORE_PASSWORD;
    }

    @Override
    public KeyStore getModuleKeyStore() {
        if (this.moduleKeyStore == null) {
            setUpService();
        }
        return this.moduleKeyStore;
    }
    
    @Override
    public PrivateKey getModulePrivateKey() {
        if (this.modulePrivateKey == null) {
            setUpService();
        }
        return this.modulePrivateKey;
    }

}
