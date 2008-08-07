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
package org.kuali.rice.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.SimpleConfig;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.core.service.impl.DemonstrationGradeEncryptionServiceImpl;
import org.kuali.rice.kns.KNSServiceLocator;
import org.kuali.rice.server.test.ServerTestBase;

/**
 * This is a class that tests the {@link DemonstrationGradeEncryptionServiceImpl} class
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DemonstrationGradeEncryptionServiceImplTest extends ServerTestBase {

    // it would be a terrible idea to ever use this particular secret key outside of this unit test
    private final static String NOT_SO_SECRET_KEY_ONLY_INTENDED_FOR_TESTING = "sm3H8KUU8BTzpFULS9ME7g==";
    private static final String TEST_VALUE = "77789";

    private static boolean failed = false;

    @Test
    public void testEncrypt() throws Exception {
        assertEquals(KNSServiceLocator.getNervousSystemContextBean(EncryptionService.class).decrypt(KNSServiceLocator.getNervousSystemContextBean(EncryptionService.class).encrypt(TEST_VALUE)), TEST_VALUE);
        EncryptionService encryptionService = new DemonstrationGradeEncryptionServiceImpl();
        ((DemonstrationGradeEncryptionServiceImpl) encryptionService).setSecretKey(NOT_SO_SECRET_KEY_ONLY_INTENDED_FOR_TESTING);

        String valueToHide = "The quick brown fox jumps over a lazy dog";
        assertTrue("Byte array should be equivalent to the target String", valueToHide.equals(new String(valueToHide.getBytes())));
        String encrypted = encryptionService.encrypt(valueToHide);

        // Verify the string can be decrypted:
        String clearText = encryptionService.decrypt(encrypted);
        assertTrue(clearText.equals(valueToHide));

        // Make sure it can be decrypted from a freshly created copy:
        encryptionService = new DemonstrationGradeEncryptionServiceImpl();
        ((DemonstrationGradeEncryptionServiceImpl) encryptionService).setSecretKey(NOT_SO_SECRET_KEY_ONLY_INTENDED_FOR_TESTING);
        clearText = encryptionService.decrypt(encrypted);
        assertTrue(clearText.equals(valueToHide));
        System.err.println("This should be unintelligible: " + encrypted);
        System.err.println("Here is a freshly generated secret key: " + ((DemonstrationGradeEncryptionServiceImpl) encryptionService).generateEncodedKey());

        encryptionService = KNSServiceLocator.getNervousSystemContextBean(EncryptionService.class);
        valueToHide = "999999999";
        // valueToHide = StringUtils.rightPad(valueToHide, 16);
        encrypted = encryptionService.encrypt(valueToHide) + EncryptionService.ENCRYPTION_POST_PREFIX;
        System.out.print(encrypted);
        encrypted = StringUtils.stripEnd(encrypted, EncryptionService.ENCRYPTION_POST_PREFIX);
        clearText = KNSServiceLocator.getNervousSystemContextBean(EncryptionService.class).decrypt(encrypted);
        assertTrue(clearText.equals(valueToHide));

        valueToHide = "My friend Joe";
        valueToHide = StringUtils.rightPad(valueToHide, 16);
        encrypted = encryptionService.encrypt(valueToHide);
        System.out.print(encrypted);
        clearText = encryptionService.decrypt(encrypted);
        assertTrue(clearText.equals(valueToHide));

    }

    /**
     * Verfies that the DemonstrationGradeEncryptionServiceImpl is thread-safe. We had problems originally with the thread-safety of the
     * implementation so we added this test to verify and prevent regression.
     */
    // method copied from KEW where it was originally testing DESEncryptionService class
    public void testEncryptionMultiThreaded() throws Exception {
        String key = DemonstrationGradeEncryptionServiceImpl.generateEncodedKey();
        Config config = ConfigContext.getCurrentContextConfig();
        if (config == null) {
            // because of previously running tests, the config might already be initialized
            config = new SimpleConfig();
            ConfigContext.init(config);
        }
        config.overrideProperty("encryption.key", key);

        final EncryptionService service = new DemonstrationGradeEncryptionServiceImpl();
        List<Thread> threads = new ArrayList<Thread>();
        failed = false;
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread() {
                public void run() {
                    try {
                        for (int j = 0; j < 100; j++) {
                            String badText = "This is so going to no longer explode";
                            String badEnc = service.encrypt(badText);
                            String badDec = service.decrypt(badEnc);
                            assertEquals(badText, badDec);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        failed = true;
                        fail("Encryption service use to be non-thread safe, but it should be now!");
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        // assert that the encryption doesn't fail any longer in a multi-threaded environment, this verifies
        // the fix to the encryption service
        assertFalse(failed);
    }

    /**
     * Similar to the test above except that a new DemonstrationGradeEncryptionServiceImpl is created for each thread.
     */
    // method copied from KEW where it was originally testing DESEncryptionService class
    public void testEncryptionMultiThreadedSafe() throws Exception {
        String key = DemonstrationGradeEncryptionServiceImpl.generateEncodedKey();
        Config config = ConfigContext.getCurrentContextConfig();
        if (config == null) {
            // because of previously running tests, the config might already be initialized
            config = new SimpleConfig();
            ConfigContext.init(config);
        }
        config.overrideProperty("encryption.key", key);
        List<Thread> threads = new ArrayList<Thread>();
        failed = false;
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread() {
                public void run() {
                    try {
                        final EncryptionService service = new DemonstrationGradeEncryptionServiceImpl();
                        for (int j = 0; j < 100; j++) {
                            String badText = "This is so going to NOT explode";
                            String badEnc = service.encrypt(badText);
                            String badDec = service.decrypt(badEnc);
                            assertEquals(badText, badDec);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        e.printStackTrace();
                        failed = true;
                        fail("Encryption service failed in mysterious ways.");
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        // assert that the encryption/decription did not fail
        assertFalse(failed);
    }
}
