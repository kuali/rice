package edu.samplu.krad.travelview;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MaintenanceAddDeleteFiscalOfficerNavIT.class,
        MaintenanceAddDeleteNoteNavIT.class,
        MaintenanceAdHocRecipientsNavIT.class,
        MaintenanceButtonsNavIT.class,
        MaintenanceConstraintTextNavIT.class,
        MaintenanceDisclosuresNavIT.class,
        MaintenanceDocumentOverviewNavIT.class,
        MaintenanceExpandCollapseNavIT.class,
        MaintenanceFieldsNavIT.class,
        MaintenanceHeaderNavIT.class,
        MaintenanceLookupAddMultipleLinesNavIT.class,
        MaintenanceNotesAndAttachmentsNavIT.class,
        MaintenanceQuickfinderIconsNavIT.class,
        MaintenanceRouteLogNavIT.class,
        MaintenanceSubsidizedPercentWatermarkNavIT.class
})
public class TravelMaintSuite {}
