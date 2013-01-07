package edu.samplu.krad.travelview;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MaintenanceAddDeleteFiscalOfficerLegacyIT.class,
        MaintenanceAddDeleteNoteLegacyIT.class,
        MaintenanceAdHocRecipientsLegacyIT.class,
        MaintenanceButtonsLegacyIT.class,
        MaintenanceConstraintTextLegacyIT.class,
        MaintenanceDisclosuresLegacyIT.class,
        MaintenanceDocumentOverviewLegacyIT.class,
        MaintenanceExpandCollapseLegacyIT.class,
        MaintenanceFieldsLegacyIT.class,
        MaintenanceHeaderLegacyIT.class,
        MaintenanceLookupAddMultipleLinesLegacyIT.class,
        MaintenanceNotesAndAttachmentsLegacyIT.class,
        MaintenanceQuickfinderIconsLegacyIT.class,
        MaintenanceRouteLogLegacyIT.class,
        MaintenanceSubsidizedPercentWatermarkLegacyIT.class
})
public class TravelMaintSuite {}
