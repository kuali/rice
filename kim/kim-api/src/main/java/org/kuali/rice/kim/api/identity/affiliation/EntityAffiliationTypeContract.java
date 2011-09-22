package org.kuali.rice.kim.api.identity.affiliation;

import org.kuali.rice.kim.api.identity.CodedAttributeContract;


public interface EntityAffiliationTypeContract extends CodedAttributeContract {
    /**
     * This value determines if the Affiliation Type is an employment type.
     *
     * @return the boolean value representing if type is an employment type
     */
    boolean isEmploymentAffiliationType();
}
