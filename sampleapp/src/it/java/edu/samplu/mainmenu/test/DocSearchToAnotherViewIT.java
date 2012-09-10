package edu.samplu.mainmenu.test;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

/**
 * test that after doc search, navigating to people flow maintenance view does not cause Javascript errors
 * and therefore interfere with JS functionality like validation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocSearchToAnotherViewIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

	@Test
    /**
     * test that after doc search, navigating to people flow maintenance view does not cause Javascript errors
     * and therefore interfere with JS functionality like validation
     */
	public void testDocSearchToAnotherView() throws Exception {
		waitAndClick("css=img[alt=\"doc search\"]");
		waitForPageToLoad();
		selectFrame("iframeportlet");
		waitAndClick("css=td.infoline > input[name=\"methodToCall.search\"]");
		waitForPageToLoad();
		selectFrame("relative=top");
		waitAndClick("link=Main Menu");
		waitForPageToLoad();
        setSpeed("2000");
		waitAndClick("link=People Flow");
		waitForPageToLoad();
		selectFrame("iframeportlet");
		waitAndClick("link=Create New");
		waitForPageToLoad();
		focusAndType("name=document.documentHeader.documentDescription", "sample description");
		focusAndType("name=document.documentHeader.explanation", "sample explanation");		
        focus("link=Cancel");
		waitAndClick("link=Cancel");
        //assertTrue(getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$")); //Removed Confirmation Panel From the page itself
	}
}
