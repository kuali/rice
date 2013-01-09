package edu.samplu.krad.travelview;

import edu.samplu.common.KradMenuLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class MaintenanceRouteLogLegacyIT extends KradMenuLegacyITBase {
    @Override
    protected String getLinkLocator() {
        return "Travel Account Maintenance (New)";
    }

    @Test 
    /**
     * Verify the Route Log section exists and contains an IFrame
     */
    public void testVerifyRouteLog() throws Exception {
        gotoMenuLinkLocator();
        waitAndClickByLinkText("Route Log");
        waitForElementPresent("//iframe[contains(@src,'RouteLog.do')]");
        passed();
    }
}
