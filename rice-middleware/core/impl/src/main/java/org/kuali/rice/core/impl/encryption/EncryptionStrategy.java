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
