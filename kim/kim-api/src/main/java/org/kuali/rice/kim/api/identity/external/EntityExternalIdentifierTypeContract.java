package org.kuali.rice.kim.api.identity.external;

import org.kuali.rice.kim.api.identity.TypeContract;

public interface EntityExternalIdentifierTypeContract extends TypeContract {
    /**
     * This value determines if the encryption is required for this type.
     *
     * @return the boolean value representing if encryption is required for this type
     */
    boolean isEncryptionRequired();
}
