/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.samplu.common;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import com.saucelabs.saucerest.SauceREST;
import org.junit.Assert;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple {@link org.openqa.selenium.remote.RemoteWebDriver} test that demonstrates how to run your Selenium tests with <a href="http://saucelabs.com/ondemand">Sauce OnDemand</a>.
 *
 * This test also includes the <a href="">Sauce JUnit</a> helper classes, which will use the Sauce REST API to mark the Sauce Job as passed/failed.
 *
 * In order to use the {@link SauceOnDemandTestWatcher}, the test must implement the {@link SauceOnDemandSessionIdProvider} interface.
 *
 */
public class SauceLabsWebDriverHelper implements SauceOnDemandSessionIdProvider {

    /**
     * remote.driver.saucelabs.share
     */
    public static final String SAUCE_SHARE_PROPERTY = "remote.driver.saucelabs.share";

    /**
     * remote.driver.saucelabs.pop.disable
     */
    public static final String SAUCE_POPUP_PROPERTY = "remote.driver.saucelabs.pop.disable";

    /**
     * remote.driver.saucelabs
     */
    public static final String SAUCE_PROPERTY = "remote.driver.saucelabs";

    /**
     * remote.driver.saucelabs.platform
     */
    public static final String SAUCE_PLATFORM_PROPERTY = "remote.driver.saucelabs.platform";

    /**
     * remote.driver.saucelabs.browser
     */
    public static final String SAUCE_BROWSER_PROPERTY = "remote.driver.saucelabs.browser";

    /**
     * remote.driver.saucelabs.user
     */
    public static final String SAUCE_USER_PROPERTY = "remote.driver.saucelabs.user";

    /**
     * remote.driver.saucelabs.key
     */
    public static final String SAUCE_KEY_PROPERTY = "remote.driver.saucelabs.key";

    /**
     * remote.driver.saucelabs.version
     */
    public static final String SAUCE_VERSION_PROPERTY = "remote.driver.saucelabs.version";

    /**
     * Constructs a {@link SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(System.getProperty(SAUCE_USER_PROPERTY), System.getProperty(SAUCE_KEY_PROPERTY));

    private WebDriver driver;

    private String sessionId;

    /**
     * Saucelabs setup
     * @param className
     * @param testName
     * @throws Exception
     */
    public void setUp(String className, String testName) throws Exception {
        if (System.getProperty(SAUCE_USER_PROPERTY) == null || System.getProperty(SAUCE_KEY_PROPERTY) == null) {
            Assert.fail("-D" + SAUCE_USER_PROPERTY + " and -D" + SAUCE_KEY_PROPERTY + " must be set to saucelabs user and access key.");
        }

        DesiredCapabilities capabilities = null;
        if ("ff".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY))) {
            capabilities = DesiredCapabilities.firefox();
        } else if ("ie".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
        } else if ("chrome".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.chrome();
        } else if ("opera".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.opera();
        } else if ("android".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.android();
        } else if ("safari".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.safari();
        } else if ("ipad".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.ipad();
        } else if ("iphone".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.iphone();
        } else {
            capabilities = DesiredCapabilities.firefox();
        }

        String version = System.getProperty(SAUCE_VERSION_PROPERTY);
        if (version != null && "0".equals(version)) { // Blank or 0 leaves version blank for use with chrome

            if (!"chrome".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY))) {
                throw new RuntimeException("Blank or 0 version for a browser not chrome " + System.getProperty(SAUCE_BROWSER_PROPERTY));
            }

            capabilities.setCapability("version", version);
        }

        capabilities.setCapability("platform", System.getProperty(SAUCE_PLATFORM_PROPERTY, Platform.UNIX.toString()).replaceAll("_", " "));
        capabilities.setCapability("idle-timeout", 180);
        capabilities.setCapability("max-duration", 480);
        capabilities.setCapability("name",  className + "." + testName + "-" + ITUtil.DTS);
        capabilities.setCapability("disable-popup-handler", System.getProperty(SAUCE_POPUP_PROPERTY, "false"));
        capabilities.setCapability("public", System.getProperty(SAUCE_SHARE_PROPERTY, "share"));

        this.driver = new RemoteWebDriver(
                new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),
                capabilities);
        this.sessionId = ((RemoteWebDriver)driver).getSessionId().toString();

        // TODO it would be better to do these at tear down, passing state could then be included in names, but requires more parameters
        try {
            String dir = determineSaveDir(className, testName);
            String resources = "mkdir " + dir + " ; cd " + dir + " ; \n"
                    + curlSaveResourceString(className, testName, "selenium-server.log") + " ; \n"
                    + curlSaveResourceString(className, testName, "video.flv") + " ; \n"
                    + wgetnSaveResourceString(className, testName) + " ; \n"
                    + "cd ../\n";
            System.out.println(resources);
            writeFile("SauceLabsResources" + dir + ".sh", resources);
        } catch (Exception e) {
            System.out.println("Exception while writing SauceLabsResources.sh " + e.getMessage());
            System.out.println(curlSaveResourceString(className, testName, "selenium-server.log"));
            System.out.println(curlSaveResourceString(className, testName, "video.flv"));
            //            System.out.println(curlSaveResourceString(className, testName, "XXXXscreenshot.png (where XXXX is a number between 0000 and 9999)")); // TODO
        }
    }

    /**
     * Do Suacelabs related teardown things.  Mostly flag the tests as passed or failed.
     * @param passed
     * @param sessionId
     * @param sauceUser
     * @param sauceKey
     * @throws Exception
     */
    public static void tearDown(boolean passed, String sessionId, String sauceUser, String sauceKey) throws Exception {
        if (sessionId != null && System.getProperty(SAUCE_PROPERTY) != null) {
            SauceREST client = new SauceREST(sauceUser, sauceKey);
            /* Using a map of udpates:
            * (http://saucelabs.com/docs/sauce-ondemand#alternative-annotation-methods)
            */
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("passed", passed);
            updates.put("build", System.getProperty("rice.version", "unknown"));
            client.updateJobInfo(sessionId, updates);

            if (passed) {
                System.out.println("Registering session passed " + sessionId);
                client.jobPassed(sessionId);
            } else {
                System.out.println("Registering session failed " + sessionId);
                client.jobFailed(sessionId);
            }

            Thread.sleep(5000); // give the client message a chance to get processed on saucelabs side
        }
    }

    private String curlSaveResourceString(String className, String testName, String resource) {
        return "curl -o " + deriveResourceBaseNames(className, testName, resource)
                + " -u " + authentication.getUsername() + ":" + authentication.getAccessKey()
                + " http://saucelabs.com/rest/" + authentication.getUsername()+ "/jobs/" + sessionId + "/results/" + resource;
    }

    private String deriveResourceBaseNames(String className, String testName, String resource) {
        return className + "." + testName + "-"
                + System.getProperty(SAUCE_PLATFORM_PROPERTY, Platform.UNIX.toString()) + "-"
                + System.getProperty(SAUCE_BROWSER_PROPERTY) + "-"
                + System.getProperty(SAUCE_VERSION_PROPERTY) + "-"
                + System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USER_PROPERTY, "admin") + "-"
                + System.getProperty("rice.version", "unknown_build") + "-"
                + ITUtil.DTS + "-"
                + resource;
    }

    /**
     * Returns the driver
     * @return WebDriver
     */
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    private String wgetnSaveResourceString(String className, String testName) {
        String dir = determineSaveDir(className, testName);
        // http://www.jwz.org/hacks/wgetn
        return "wgetn https://saucelabs.com/" + sessionId + "/results/%04dscreenshot.png 0 50";
    }

    private String determineSaveDir(String className, String testName) {
        String dir = deriveResourceBaseNames(className, testName, "");
        dir = dir.substring(0, dir.length() -1);
        return dir;
    }

    private void writeFile(String fileName, String content) throws IOException {
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.flush();
        bw.close();
    }
}
