package edu.samplu.mainmenu.test;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertTrue;

/**
 * test that after doc search, navigating to people flow maintenance view does not cause Javascript errors
 * and therefore interfere with JS functionality like validation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocSearchToAnotherViewIT {
    private Selenium selenium;

    @Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));//"http://dev1.rice.kuali.org/");
		selenium.start();
	}

	@Test
    /**
     * test that after doc search, navigating to people flow maintenance view does not cause Javascript errors
     * and therefore interfere with JS functionality like validation
     */
	public void testDocSearchToAnotherView() throws Exception {
        selenium.open(System.getProperty("remote.public.url"));
        selenium.waitForPageToLoad("30000");
		selenium.type("name=__login_user", "admin");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("css=img[alt=\"doc search\"]");
		selenium.waitForPageToLoad("30000");
		selenium.selectFrame("iframeportlet");
		selenium.click("css=td.infoline > input[name=\"methodToCall.search\"]");
		selenium.waitForPageToLoad("30000");
		selenium.selectFrame("relative=up");
		selenium.click("link=Main Menu");
		selenium.waitForPageToLoad("30000");
        selenium.setSpeed("2000");
		selenium.click("link=People Flow");
		selenium.waitForPageToLoad("30000");
		selenium.selectFrame("iframeportlet");
		selenium.click("id=19");
		selenium.waitForPageToLoad("30000");
        selenium.focus("id=160");
		selenium.type("id=160", "sample description");
        selenium.focus("id=185");
		selenium.type("id=185", "sample explanation");
		selenium.click("id=143");
		assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
