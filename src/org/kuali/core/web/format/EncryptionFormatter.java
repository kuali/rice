/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.web.format;

import java.security.GeneralSecurityException;

import org.kuali.rice.KNSServiceLocator;

/**
 * This formatter calls the encryption service to encrypt/decrypt values.
 * 
 * 
 */
public class EncryptionFormatter extends Formatter {
    private static final long serialVersionUID = -4109390572922205211L;

    protected Object convertToObject(String target) {
        if (Formatter.isEmptyValue(target))
            return null;

        String decryptedValue = null;
        try {
            decryptedValue = KNSServiceLocator.getEncryptionService().decrypt(target);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to decrypt value.");
        }

        return decryptedValue;
    }

    public Object format(Object target) {
        String encryptedValue = null;
        try {
            encryptedValue = KNSServiceLocator.getEncryptionService().encrypt(target);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to encrypt secure field.");
        }

        return encryptedValue;
    }
}
