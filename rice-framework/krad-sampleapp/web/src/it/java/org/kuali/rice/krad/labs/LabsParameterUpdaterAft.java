package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsParameterUpdaterAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/parameter?viewId=LabsParameterView
     */
    public static final String BOOKMARK_URL = "/kr-krad/parameter?viewId=LabsParameterView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Parameter Updater");
    }

    @Test
    public void testParameterUpdaterBookmark() throws Exception {
        testParameterUpdater();
        passed();
    }

    @Test
    public void testParameterUpdaterNav() throws Exception {
        testParameterUpdater();
        passed();
    }

    protected void testParameterUpdater()throws Exception {
    	waitAndTypeByName("namespaceCode","IAT");
    	waitAndTypeByName("componentCode","IAT");
    	waitAndTypeByName("parameterName","IAT");
    	waitAndTypeByName("parameterValue","IAT");
    	waitAndClickButtonByText("Update Parameter");
    }
}
