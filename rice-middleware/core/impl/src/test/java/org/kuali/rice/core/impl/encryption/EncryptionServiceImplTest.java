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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.config.property.SimpleConfig;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Unit test for {@link EncryptionServiceImpl}
 *
 * @author Eric Westfall
 */
public class EncryptionServiceImplTest {

    private EncryptionServiceImpl service;
    private SimpleConfig config;
    private String desKey;
    private String desedeKey;

    @Before
    public void setUp() throws Exception {
        this.service = new EncryptionServiceImpl();
        this.config = new SimpleConfig();
        ConfigContext.init(this.config);
        this.desKey = generateDESKey();
        this.desedeKey = generateDESedeKey();
    }

    @After
    public void tearDown() {
        if (ConfigContext.isInitialized()) {
            ConfigContext.destroy();
        }
    }

    private String generateDESKey() throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();
        SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
        DESKeySpec desSpec = (DESKeySpec) desFactory.getKeySpec(desKey, javax.crypto.spec.DESKeySpec.class);
        byte[] rawDesKey = desSpec.getKey();
        return new String(Base64.encodeBase64(rawDesKey));
    }

    private String generateDESedeKey() throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        SecretKey desedeKey = keygen.generateKey();

        SecretKeyFactory desedeFactory = SecretKeyFactory.getInstance("DESede");
        DESedeKeySpec desedeSpec = (DESedeKeySpec) desedeFactory.getKeySpec(desedeKey, javax.crypto.spec.DESedeKeySpec.class);
        byte[] rawDesedeKey = desedeSpec.getKey();
        return new String(Base64.encodeBase64(rawDesedeKey));
    }

    @Test
    public void testDisabledEncryptionService() throws Exception {
        service.afterPropertiesSet();
        // service should not be enabled since we never set a secret key
        assertFalse(service.isEnabled());
    }

    /**
     * The default algorithm used should be DES so that it's compatible with the {@link DemonstrationGradeEncryptionServiceImpl}
     */
    @Test
    public void testDefaultAlgorithmIsDES() throws Exception {
        service.afterPropertiesSet();
        assertTrue(service.getEncryptionStrategy().getTransformation().startsWith("DES/"));
    }

    /**
     * Encryption service should be enabled once a key is injected
     */
    @Test
    public void testInjectSecretKey() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        assertTrue(service.isEnabled());
    }

    @Test
    public void testLoadSecretKeyFromConfig() throws Exception {
        // create one instance where it's loaded from config and one where it's injected, make sure they encrypt the same way
        config.putProperty("encryption.key", desKey);
        service.afterPropertiesSet();

        EncryptionServiceImpl service2 = new EncryptionServiceImpl();
        service2.setSecretKey(desKey);
        service2.afterPropertiesSet();

        String valueToEncrypt = "abcdefg";
        String encrypted1 = service.encrypt(valueToEncrypt);
        String encrypted2 = service2.encrypt(valueToEncrypt);

        assertEquals("encrypted values should be the same", encrypted1, encrypted2);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAlgorithm() throws Exception {
        config.putProperty("encryption.algorithm", "gobbletygook");
        service.afterPropertiesSet();
    }

    @Test
    public void testDESedeEncryptDecrypt() throws Exception {
        service.setSecretKey(desedeKey);
        config.putProperty("encryption.algorithm", "DESede");
        service.afterPropertiesSet();

        String valueToEncrypt = "abc";
        String encrypted = service.encrypt(valueToEncrypt);

        // now decrypt it, make sure it's the same
        assertEquals(valueToEncrypt, service.decrypt(encrypted));
    }

    @Test
    public void testDESedeEncryptDecryptBytes() throws Exception {
        service.setSecretKey(desedeKey);
        config.putProperty("encryption.algorithm", "DESede");
        service.afterPropertiesSet();

        byte[] bytesToEncrypt = { 1, 7, 4, 3};
        byte[] encrypted = service.encryptBytes(bytesToEncrypt);

        // now decrypt it, make sure it's the same
        assertTrue(Arrays.equals(bytesToEncrypt, service.decryptBytes(encrypted)));
    }

    @Test
    public void testDESEncryptDecrypt() throws Exception {
        service.setSecretKey(desKey);
        config.putProperty("encryption.algorithm", "DES");
        service.afterPropertiesSet();

        String valueToEncrypt = "abc";
        String encrypted = service.encrypt(valueToEncrypt);

        // now decrypt it, make sure it's the same
        assertEquals(valueToEncrypt, service.decrypt(encrypted));
    }

    @Test
    public void testDESEncryptDecryptBytes() throws Exception {
        service.setSecretKey(desKey);
        config.putProperty("encryption.algorithm", "DES");
        service.afterPropertiesSet();

        byte[] bytesToEncrypt = { 1, 7, 4, 3};
        byte[] encrypted = service.encryptBytes(bytesToEncrypt);

        // now decrypt it, make sure it's the same
        assertTrue(Arrays.equals(bytesToEncrypt, service.decryptBytes(encrypted)));
    }

    @Test(expected = IllegalStateException.class)
    public void testEncryptWhenDisabled() throws Exception {
        service.afterPropertiesSet();
        service.encrypt("yo");
    }

    @Test
    public void testEncryptNullValue() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        assertEquals("", service.encrypt(null));
    }

    @Test
    public void testEcryptNullByteArray() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        assertTrue(Arrays.equals(new byte[0], service.encryptBytes(null)));
    }

    @Test
    public void testDecryptNullValue() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        assertEquals("", service.decrypt(null));
    }

    @Test
    public void testDecryptBlankValue() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        assertEquals("", service.decrypt(""));
    }

    @Test
    public void testDecryptNullByteArray() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        assertTrue(Arrays.equals(new byte[0], service.decryptBytes(null)));
    }

    @Test
    public void testSetSecretKeyAfterInitialization() throws Exception {
        service.afterPropertiesSet();
        assertFalse(service.isEnabled());
        service.setSecretKey(desKey);
        assertTrue(service.isEnabled());

        // check that we can encrypt/decrypt
        String valueToEncrypt = "cba";
        assertEquals(valueToEncrypt, service.decrypt(service.encrypt(valueToEncrypt)));
    }

    @Test
    public void testHash() throws Exception {
        service.afterPropertiesSet();

        String valueToHash = "hashme";
        String hashed = service.hash(valueToHash);
        assertNotNull(hashed);
        assertEquals(hashed, service.hash(valueToHash));

    }

    @Test
    public void testHashNullAndEmpty() throws Exception {
        service.afterPropertiesSet();
        assertEquals("", service.hash(null));
        assertEquals("", service.hash(""));
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidCharset_Encrypt() throws Exception {
        service.setCharset("INVALID");
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        service.encrypt("test");
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidCharset_Decrypt() throws Exception {
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        String encrypted = service.encrypt("test");
        service.setCharset("INVALID");
        service.decrypt(encrypted);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidCharset_EncryptBytes() throws Exception {
        service.setCharset("INVALID");
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        service.encrypt("test".getBytes());
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidCharset_Hash() throws Exception {
        service.setCharset("INVALID");
        service.setSecretKey(desKey);
        service.afterPropertiesSet();
        service.hash("test");
    }

}
