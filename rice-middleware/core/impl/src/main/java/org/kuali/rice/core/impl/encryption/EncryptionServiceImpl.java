/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.impl.encryption;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.springframework.beans.factory.InitializingBean;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import static org.kuali.rice.core.api.config.property.ConfigContext.getCurrentContextConfig;

/**
 * A configurable implementation of the EncryptionService.
 *
 * <p>Leverages a configured {@link EncryptionStrategy} for determining what algorithm to use for encryption. The
 * strategy can either be injected into this class or configured in the {@link ConfigContext}. The algorithm is
 * configured using the "encryption.algorithm" parameter. The only two options are "DES" and "DESede".</p>
 *
 * <p>If no encryption strategy is injected or configured, then this class will default to using "DES" as the
 * algorithm.</p>
 *
 * <p>This class defaults to using the UTF-8 charset, but that can be modified via {@link #setCharset(String)}</p>
 *
 * @author Eric Westfall
 */
public class EncryptionServiceImpl implements EncryptionService, InitializingBean {

    private static final String HASH_ALGORITHM = "SHA";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String ENCRYPTION_KEY_PARAM = "encryption.key";
    private static final String ENCRYPTION_ALGORITHM_PARAM = "encryption.algorithm";

    private EncryptionStrategy encryptionStrategy;

    private transient SecretKey secretKey;
    private transient String secretKeyValue;

    private String charset = DEFAULT_CHARSET;
    private boolean isEnabled = false;
    private boolean initialized = false;

    @Override
    public void afterPropertiesSet() throws GeneralSecurityException {
        initializeEncryptionStrategy();
        // note that the following method depends on the encryption strategy being established, so it must come second
        initializeSecretKey();
        initialized = true;
    }

    private void initializeSecretKey() throws GeneralSecurityException {
        if (StringUtils.isBlank(secretKeyValue)) {
            secretKeyValue = getCurrentContextConfig().getProperty(ENCRYPTION_KEY_PARAM);
        }
        loadSecretKey();
    }

    private void loadSecretKey() throws GeneralSecurityException {
        if (!StringUtils.isBlank(secretKeyValue)) {
            this.secretKey = this.unwrapEncodedKey(secretKeyValue);
            this.isEnabled = true;
        }
    }

    private void initializeEncryptionStrategy() {
        if (getEncryptionStrategy() == null) {
            String algorithm = ConfigContext.getCurrentContextConfig().getProperty(ENCRYPTION_ALGORITHM_PARAM);
            if (StringUtils.isBlank(algorithm)) {
                algorithm = DESEncryptionStrategy.ALGORITHM;
            }
            if (algorithm.equals(DESEncryptionStrategy.ALGORITHM)) {
                setEncryptionStrategy(new DESEncryptionStrategy());
            } else if (algorithm.equals(DESedeEncryptionStrategy.ALGORITHM)) {
                setEncryptionStrategy(new DESedeEncryptionStrategy());
            } else {
                throw new IllegalArgumentException("Invalid encryption.algorithm property specified: '" + algorithm + "'");
            }
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String encrypt(Object valueToHide) throws GeneralSecurityException {
        checkEnabled();

        if (valueToHide == null) {
            return "";
        }

        // Initialize the cipher for encryption
        Cipher cipher = Cipher.getInstance(encryptionStrategy.getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try {
            // Our cleartext
            byte[] cleartext = valueToHide.toString().getBytes(charset);

            // Encrypt the cleartext
            byte[] ciphertext = cipher.doFinal(cleartext);

            return new String(Base64.encodeBase64(ciphertext), charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Ensure a valid charset has been configured.", e);
        }
    }

    public String decrypt(String ciphertext) throws GeneralSecurityException {
        checkEnabled();

        if (StringUtils.isBlank(ciphertext)) {
            return "";
        }

        // Initialize the same cipher for decryption
        Cipher cipher = Cipher.getInstance(encryptionStrategy.getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try {
            // un-Base64 encode the encrypted data
            byte[] encryptedData = Base64.decodeBase64(ciphertext.getBytes(charset));

            // Decrypt the ciphertext
            byte[] cleartext1 = cipher.doFinal(encryptedData);
            return new String(cleartext1, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Ensure a valid charset has been configured.", e);
        }
    }

    public byte[] encryptBytes(byte[] valueToHide) throws GeneralSecurityException {
        checkEnabled();

        if (valueToHide == null) {
            return new byte[0];
        }

        // Initialize the cipher for encryption
        Cipher cipher = Cipher.getInstance(encryptionStrategy.getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Our cleartext
        byte[] cleartext = valueToHide;

        // Encrypt the cleartext
        byte[] ciphertext = cipher.doFinal(cleartext);

        return ciphertext;
    }

    public byte[] decryptBytes(byte[] ciphertext) throws GeneralSecurityException {
        checkEnabled();

        if (ciphertext == null) {
            return new byte[0];
        }

        // Initialize the same cipher for decryption
        Cipher cipher = Cipher.getInstance(encryptionStrategy.getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // un-Base64 encode the encrypted data
        byte[] encryptedData = ciphertext;

        // Decrypt the ciphertext
        byte[] cleartext1 = cipher.doFinal(encryptedData);
        return cleartext1;
    }

    private SecretKey unwrapEncodedKey(String key) throws GeneralSecurityException {
        byte[] bytes = Base64.decodeBase64(key.getBytes());
        return encryptionStrategy.loadSecretKey(bytes);
    }

    /**
     * Sets the secretKey attribute value.
     *
     * @param secretKeyValue The secretKey to set.
     * @throws Exception
     */
    public void setSecretKey(String secretKeyValue) throws GeneralSecurityException {
        if (!StringUtils.isBlank(secretKeyValue)) {
            this.secretKeyValue = secretKeyValue;
            if (initialized) {
                loadSecretKey();
            }
        }
    }

    public EncryptionStrategy getEncryptionStrategy() {
        return encryptionStrategy;
    }

    public void setEncryptionStrategy(EncryptionStrategy encryptionStrategy) {
        this.encryptionStrategy = encryptionStrategy;
    }

    /**
     * Hash the value by converting to a string, running the hash algorithm, and then base64'ng the results.
     * Returns a blank string if any problems occur or the input value is null or empty.
     */
    public String hash(Object valueToHide) throws GeneralSecurityException {
        if ( valueToHide == null || StringUtils.isEmpty( valueToHide.toString() ) ) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            return new String( Base64.encodeBase64( md.digest( valueToHide.toString().getBytes( charset ) ) ), charset );
        } catch ( UnsupportedEncodingException e) {
            throw new IllegalStateException("Ensure a valid charset has been configured.", e);
        }
    }

    /**
     * Performs a check to see if the encryption service is enabled.  If it is not then an
     * IllegalStateException will be thrown.
     */
    protected void checkEnabled() {
        if (!isEnabled()) {
            throw new IllegalStateException("Illegal use of encryption service.  Ecryption service is disabled, to enable please configure 'encryption.key'.");
        }
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * An EncryptionStrategy for the DESede encryption algorithm.
     *
     * @author Eric Westfall
     */
    public static class DESedeEncryptionStrategy implements EncryptionStrategy {

        private static final String ALGORITHM = "DESede";
        private static final String MODE = "ECB";
        private static final String PADDING = "PKCS5Padding";
        private static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;

        @Override
        public String getTransformation() {
            return TRANSFORMATION;
        }

        @Override
        public SecretKey loadSecretKey(byte[] key) throws GeneralSecurityException {
            SecretKeyFactory desedeFactory = SecretKeyFactory.getInstance(ALGORITHM);
            DESedeKeySpec keyspec = new DESedeKeySpec(key);
            return desedeFactory.generateSecret(keyspec);
        }

    }

    /**
     * An EncryptionStrategy for the DES encryption algorithm.
     *
     * @author Eric Westfall
     */
    public static class DESEncryptionStrategy implements EncryptionStrategy {

        private static final String ALGORITHM = "DES";
        private static final String MODE = "ECB";
        private static final String PADDING = "PKCS5Padding";
        private static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;

        @Override
        public String getTransformation() {
            return TRANSFORMATION;
        }

        @Override
        public SecretKey loadSecretKey(byte[] key) throws GeneralSecurityException {
            SecretKeyFactory desedeFactory = SecretKeyFactory.getInstance(ALGORITHM);
            DESKeySpec keyspec = new DESKeySpec(key);
            return desedeFactory.generateSecret(keyspec);
        }


    }
}
