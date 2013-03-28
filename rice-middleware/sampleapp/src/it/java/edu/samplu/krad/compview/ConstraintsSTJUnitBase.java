package edu.samplu.krad.compview;

import org.junit.Assert;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class ConstraintsSTJUnitBase extends ConstraintsAbstractSmokeTestBase {

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }
}
