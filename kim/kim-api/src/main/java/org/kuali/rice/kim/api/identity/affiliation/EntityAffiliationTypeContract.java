package org.kuali.rice.kim.api.identity.affiliation;

import org.kuali.rice.kim.api.identity.TypeContract;


public interface EntityAffiliationTypeContract extends TypeContract {
    /**
     * This value determines if the Affiliation Type is an employment type.
     *
     * @return the boolean value representing if type is an employment type
     */
    boolean isEmploymentAffiliationType();
}
