package edu.samplu.krad.travelview;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class MaintenanceRouteLogNavIT extends KradMenuNavITBase {
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
        super.testVerifyRouteLog();
    }
}
