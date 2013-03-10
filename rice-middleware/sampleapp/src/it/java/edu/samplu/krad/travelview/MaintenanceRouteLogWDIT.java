package edu.samplu.krad.travelview;

import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class MaintenanceRouteLogWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Travel%20Account%20Maintenance%20(New)&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&viewName=ks_maintenance&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
   
    @Test 
    /**
     * Verify the Route Log section exists and contains an IFrame
     */
    public void testVerifyRouteLog() throws Exception {
        super.testVerifyRouteLog();
    }
}
