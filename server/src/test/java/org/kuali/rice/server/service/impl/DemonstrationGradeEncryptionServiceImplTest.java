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

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.core.service.impl.DemonstrationGradeEncryptionServiceImpl;
import org.kuali.rice.server.test.ServerTestBase;

/**
 * This is a class that tests the {@link DemonstrationGradeEncryptionServiceImpl} class 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DemonstrationGradeEncryptionServiceImplTest extends ServerTestBase {

    // it would be a terrible idea to ever use this particular secret key outside of this unit test
    private final static String NOT_SO_SECRET_KEY_ONLY_INTENDED_FOR_TESTING = "sm3H8KUU8BTzpFULS9ME7g==";
    private static final String TEST_VALUE = "77789";

    @Test
    public void testEncrypt() throws Exception {
        assertEquals(KNSServiceLocator.getBean(EncryptionService.class).decrypt(KNSServiceLocator.getBean(EncryptionService.class).encrypt(TEST_VALUE)), TEST_VALUE);
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

        encryptionService = KNSServiceLocator.getBean(EncryptionService.class);
        valueToHide = "999999999";
        // valueToHide = StringUtils.rightPad(valueToHide, 16);
        encrypted = encryptionService.encrypt(valueToHide) + EncryptionService.ENCRYPTION_POST_PREFIX;
        System.out.print(encrypted);
        encrypted = StringUtils.stripEnd(encrypted, EncryptionService.ENCRYPTION_POST_PREFIX);
        clearText = KNSServiceLocator.getBean(EncryptionService.class).decrypt(encrypted);
        assertTrue(clearText.equals(valueToHide));

        valueToHide = "My friend Joe";
        valueToHide = StringUtils.rightPad(valueToHide, 16);
        encrypted = encryptionService.encrypt(valueToHide);
        System.out.print(encrypted);
        clearText = encryptionService.decrypt(encrypted);
        assertTrue(clearText.equals(valueToHide));

    }

}
