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

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

/**
 * Contract for an encryption strategy.
 *
 * Specifies what Cipher transformation to use for encryption (in the form algorithm/mode/padding), as well as being
 * able to load a SecretKey.
 */
public interface EncryptionStrategy {

    /**
     * Return a String value representing the transformation to use for encryption in the form "algorithm/mode/padding".
     *
     * @return the transformation to use for encryption
     */
    String getTransformation();

    /**
     * Loads the given raw key bytes into a {@link SecretKey} which is valid for the algorithm used by this encryption
     * strategy.
     *
     * @param key the raw key to load
     * @return the SecretKey object that was loaded from the given key
     */
    SecretKey loadSecretKey(byte[] key) throws GeneralSecurityException;

}
