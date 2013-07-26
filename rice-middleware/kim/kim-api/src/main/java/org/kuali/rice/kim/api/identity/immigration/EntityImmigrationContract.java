package org.kuali.rice.kim.api.identity.immigration;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.CodedAttribute;

/**
 * residency info for a KIM immigration record
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface EntityImmigrationContract extends Versioned, GloballyUnique, Inactivatable, Identifiable {
    String getAlienRegistrationNumber();
    DateTime getFirstEntryIntoUSDate();
    boolean isImmigrationI20RequestInd();
    DateTime getNonImmigrationVisaIssueDate();
    String getNonImmigrantVisaNumber();
    String getNonImmigrantVisaStatusChangeCode();
    DateTime getNonImmigrantVisaStateChangeDate();
    String getNonImmigrantVisa();  //CodedAttribute??
    DateTime getRequiredFormsReceivedDate();
    //SponsorType getSponsorType();
    String getUSStudyFormsReceipt();  //enum or CodedAttribute
    DateTime getVisaExpirationDate();

}
