package edu.samplu.krad.demo.uif.library.elements.action;

import org.junit.Assert;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsActionSTJUnitBase extends DemoLibraryElementsActionSmokeTestBase {

    @Override
    public void fail(String message) {
        passed = false;
        Assert.fail(message);
    }
}
