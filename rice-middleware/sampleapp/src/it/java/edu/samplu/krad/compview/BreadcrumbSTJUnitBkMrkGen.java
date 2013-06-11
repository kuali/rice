package edu.samplu.krad.compview;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class BreadcrumbSTJUnitBkMrkGen extends BreadcrumbSmokeTestBase {

    @Override
    public void fail(String message){
        passed = false;
        Assert.fail(message);
    }

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testBreadcrumbBookmark() throws Exception {
        testBreadcrumbs();
        passed();
    }
}
