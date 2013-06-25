package edu.samplu.krad.demo.uif.library.elements.action;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsActionSTJUnitBkMrkGen extends DemoLibraryElementsActionSTJUnitBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testActionBookmark() throws Exception {
        testActionBookmark(this);
    }
}
