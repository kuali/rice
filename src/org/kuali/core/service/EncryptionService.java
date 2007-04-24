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
package org.kuali.core.service;

import java.security.GeneralSecurityException;

/**
 * Interface defining the methods a Kuali encryption service must implement.
 * 
 * 
 */
public interface EncryptionService extends edu.iu.uis.eden.security.EncryptionService {
    /* string appended to an encrypted value by the frameworks for determine if a 
    value coming back from the ui is encrypted */
    public static final String ENCRYPTION_POST_PREFIX = "(&^#&)";
    public static final String HASH_POST_PREFIX = "(&^HSH#&)";
    
    /**
     * Encrypts a value
     * 
     * @param valueToHide - original value
     * @return encrypted value
     * @throws GeneralSecurityException
     */
    public String encrypt(Object valueToHide) throws GeneralSecurityException;

    /**
     * Decrypts a value
     * 
     * @param ciphertext - encrypted value
     * @return decrypted value
     * @throws GeneralSecurityException
     */
    public String decrypt(String ciphertext) throws GeneralSecurityException;

    /**
     * Hashes a value (for one-way transformations)
     * 
     * @param valueToHide - original value
     * @return encrypted value
     * @throws GeneralSecurityException
     */
    public String hash(Object valueToHide) throws GeneralSecurityException;
}
