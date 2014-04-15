/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.testtools.selenium;

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
 * <p>
 * Helper class for {@link org.openqa.selenium.remote.RemoteWebDriver} when writing Selenium tests making use of Saucelabs
 * or <a href="http://saucelabs.com/ondemand">Sauce OnDemand</a>.
 * </p><p>
 * Saucelabs properties need to be set as JVM arguments.  See the SAUCE_ Constants defined below.  Required <b>saucelabs</b>
 * parameters are: {@see #REMOTE_DRIVER_SAUCELABS_PROPERTY} (master use saucelabs flag), {@see #SAUCE_USER_PROPERTY},
 * {@see #SAUCE_KEY_PROPERTY}, {@see #SAUCE_VERSION_PROPERTY}.
 * </p><p>
 * An example:
 * <pre>{@code
 * -Dremote.public.url=env14.rice.kuali.org -Dremote.driver.saucelabs -Dsaucelabs.user=YOUR-SAUCELABS-USER
 * -Dsaucelabs.key=YOUR-SAUCELABS-KEY -Dsaucelabs.browser.version=22 -Dsaucelabs.platform=linux -Dsaucelabs.browser=ff
 * -Dremote.public.user=admin -Drice.version=42222
 * }</pre>
 * </p><p>
 * To make use of SauceLabsWebDriverHelper, call its {@see #setUp} with the Test Class and Test Name and retrieve the configured
 * WebDriver using {@see #getDriver}  You'll also need to call {@see #getSessionId} which you'll pass into the {@see #tearDown}
 * (along with the tests passed state).
 * </p><p>
 * In your test setUp:
 * </p><p>
 * <pre>
 * {@code
 * SauceLabsWebDriverHelper saucelabs = new SauceLabsWebDriverHelper();
 * saucelabs.setUp(className, testName);
 * driver = saucelabs.getDriver();
 * }
 * </pre>
 * </p><p>
 * In your test tearDown:
 * <pre>
 * {@code
 * SauceLabsWebDriverHelper.tearDown(passed, sessionId);
 * }
 * </pre>
 * </p><p>
 * Also includes the <a href="">Sauce JUnit</a> helper classes, which will use the Sauce REST API to mark the
 * Sauce Job as passed/failed.
 * </p><p>
 * In order to use {@link SauceOnDemandTestWatcher} the {@link SauceOnDemandSessionIdProvider} interface is implemented.
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SauceLabsWebDriverHelper implements SauceOnDemandSessionIdProvider {

    /**
     * <p>
     * Use Saucelabs flag, <b>required</b>.
     * </p><p>
     * For ease of disabling saucelabs without having to remove other saucelabs property settings, if not present saucelabs
     * will not be used.
     * </p><p>
     * -Dremote.driver.saucelabs
     * </p>
     */
    public static final String REMOTE_DRIVER_SAUCELABS_PROPERTY = "remote.driver.saucelabs";

    /**
     * <p>
     * Saucelabs browser, default is Firefox.
     * </p><p>
     * See <a href="https://saucelabs.com/docs/platforms">Saucelabs Resources</a>
     * <ul>
     * <li>ff = Firefox</li>
     * <li>ie = Internet Explorer</li>
     * <li>chrome = Google Chrome</li>
     * <li>opera = Opera</li>
     * <li>android = Android</li>
     * <li>safari = Safari</li>
     * <li>ipad = IPad</li>
     * <li>iphone = IPhone</li>
     * </ul></p><p>
     * -Dsaucelabs.browser=
     * <p>
     */
    public static final String SAUCE_BROWSER_PROPERTY = "saucelabs.browser";

    /**
     * <p>
     * Suacelabs build displayed as saucelabs build, default is unknown.
     * </p><p>
     * -Drice.version=
     * </p>
     */
    public static final String SAUCE_BUILD_PROPERTY = "rice.version";

    /**
     * <p>
     * Create a unix shell script to download saucelab resources, default is false.
     * </p><p>
     * Note - saucelabs history only goes back so far, if you run enough tests the resources will no longer
     * be available for downloading.
     * </p><p>
     * -Dsaucelabs.download.script=false
     * </p>
     */
    public static final String SAUCE_DOWNLOAD_SCRIPT_PROPERTY = "saucelabs.download.scripts";

    /**
     * <p>
     * Saucelabs idle timeout in seconds, default is 180.
     * </p><p>
     * -Dsaucelabs.idle.timeout.seconds=
     * </p>
     */
    public static final String SAUCE_IDLE_TIMEOUT_SECONDS_PROPERTY = "saucelabs.idle.timeout.seconds";

    /**
     * <p>
     * Saucelabs key, <b>required</b>.
     * </p><p>
     * -Dsaucelabs.key=
     * </p>
     */
    public static final String SAUCE_KEY_PROPERTY = "saucelabs.key";

    /**
     * <p>
     * Saucelabs max duration in seconds, default is 480.
     * </p><p>
     * -Dsaucelabs.max.duration.seconds=
     * </p>
     */
    public static final String SAUCE_MAX_DURATION_SECONDS_PROPERTY = "saucelabs.max.duration.seconds";

    /**
     * <p>
     * Saucelabs platform (OS) replace spaces with underscores, default is Linux.
     * </p><p>
     * See <a href="https://saucelabs.com/docs/platforms">Saucelabs Resources</a>
     * </p><p>
     * -Dsaucelabs.platform=
     * </p>
     */
    public static final String SAUCE_PLATFORM_PROPERTY = "saucelabs.platform";

    /**
     * <p>
     * Saucelabs ignore security domains in IE, which can introduce flakiness, default is true.
     * </p><p>
     * See <a href="http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_The_does_not_work_well_on_Vista._How_do_I_get_it_to_work_as_e">InternetExplorerDriver FAQ</a>
     * </p><p>
     * -Dsaucelabs.ie.ignore.domains=false
     * </p>
     */
    public static final String SAUCE_IE_INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS_PROPERTY = "saucelabs.ie.ignore.domains";

    /**
     * <p>
     * Saucelabs popup disable setting, default is false (not disabled).
     * </p><p>
     * See <a href="https://saucelabs.com/docs/additional-config#disable-popup-handler">DISABLE POPUP HANDLER</a>
     * </p><p>
     * -Dsaucelabs.pop.disable=
     * </p>
     */
    public static final String SAUCE_POPUP_PROPERTY = "saucelabs.pop.disable";

    /**
     * <p>
     * Saucelabs share setting, default is public restricted.
     * </p><p>
     * See <a href="https://saucelabs.com/docs/additional-config#sharing">Job Sharing</a>.  Valid values are: public, public restricted, team, share, and private.
     * </p><p>
     * -Dsaucelabs.share=
     * </p>
     */
    public static final String SAUCE_SHARE_PROPERTY = "saucelabs.share";

    /**
     * <p>
     * Saucelabs user, <b>required</b>.
     * </p><p>
     * -Dsaucelabs.user=
     * </p>
     */
    public static final String SAUCE_USER_PROPERTY = "saucelabs.user";

    /**
     * <p>
     * Saucelabs browser Version, <b>required</b>.
     * </p><p>
     * See <a href="https://saucelabs.com/docs/platforms">Saucelabs Resources</a> 0 or null is current version of <b>Chrome</b>.
     * If using a browser other than Chrome this must be set else an Exception will be thrown.
     * </p><p>
     * -Dsaucelabs.version=
     * </p>
     */
    public static final String SAUCE_VERSION_PROPERTY = "saucelabs.browser.version";

    /**
     * <p>
     * Saucelabs REST API delay in milliseconds, default is 5000.
     * </p><p>
     * -Dsaucelabs.rest.api.delay.ms=
     * </p>
     */
    public static final String SAUCE_REST_API_DELAY_MS = "saucelabs.rest.api.delay.ms";

    /**
     * <p>
     * Constructs a {@link SauceOnDemandAuthentication} instance using the supplied user name/access key.
     * </p><p>
     * To use the authentication supplied by environment variables or from an external file, use the no-arg
     * {@link SauceOnDemandAuthentication} constructor.
     * </p><p>
     * {@see #SAUCE_USER_PROPERTY} {@see #SAUCE_KEY_PROPERTY}
     * </p>
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(System.getProperty(SAUCE_USER_PROPERTY), System.getProperty(SAUCE_KEY_PROPERTY));

    private WebDriver driver;

    private String sessionId;

    /**
     * <p>
     * Saucelabs setUp.
     * </p><p>
     * Creates a {@link org.openqa.selenium.remote.RemoteWebDriver} instance with the DesiredCapabilities as configured
     * using the JVM arguments described as SAUCE_ Constants in this class.  After setUp the WebDriver can be accessed via
     * {@see #getDriver}.  You'll also need {@see #getSessionId} for when you call {@see #tearDown}
     * </p>
     *
     * @param className class name of the test being setup as a String
     * @param testName test name of the test being setup as a String
     * @throws Exception
     */
    public void setUp(String className, String testName) throws Exception {
        if (System.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY) == null) { // dup guard so WebDriverUtils doesn't have to be used.
            return;
        }

        if (System.getProperty(SAUCE_USER_PROPERTY) == null || System.getProperty(SAUCE_KEY_PROPERTY) == null) {
            Assert.fail("-D" + SAUCE_USER_PROPERTY + " and -D" + SAUCE_KEY_PROPERTY + " must be set to saucelabs user and access key.");
        }

        DesiredCapabilities capabilities = null;
        if ("ff".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY))) {
            capabilities = DesiredCapabilities.firefox();
        } else if ("ie".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY)))  {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                System.getProperty(SAUCE_IE_INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS_PROPERTY, "true"));
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
        if (version == null || "0".equals(version)) { // Blank or 0 leaves version blank for use with chrome

            if (!"chrome".equalsIgnoreCase(System.getProperty(SAUCE_BROWSER_PROPERTY))) {
                throw new RuntimeException("Blank or 0 version for a browser not chrome " + System.getProperty(SAUCE_BROWSER_PROPERTY));
            }

            capabilities.setCapability("version", ""); // saucelabs requires blank for chrome (latest version)
        } else {
            capabilities.setCapability("version", version); // saucelabs requires blank for chrome (latest version)
        }

        capabilities.setCapability("platform", System.getProperty(SAUCE_PLATFORM_PROPERTY, Platform.UNIX.toString()).replaceAll("_", " "));
        capabilities.setCapability("idle-timeout", Integer.parseInt(System.getProperty(SAUCE_IDLE_TIMEOUT_SECONDS_PROPERTY, "180")));
        capabilities.setCapability("max-duration", Integer.parseInt(System.getProperty(SAUCE_MAX_DURATION_SECONDS_PROPERTY, "480")));
        capabilities.setCapability("name",  className + "." + testName + "-" + AutomatedFunctionalTestUtils.DTS);
        capabilities.setCapability("disable-popup-handler", System.getProperty(SAUCE_POPUP_PROPERTY, "false"));
        capabilities.setCapability("public", System.getProperty(SAUCE_SHARE_PROPERTY, "public restricted"));

        System.out.println("Requesting Saucelabs RemoteWebDriver with DesiredCapabilities of " + capabilities.toString());

        this.driver = new RemoteWebDriver(
                new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),
                capabilities);
        this.sessionId = ((RemoteWebDriver)driver).getSessionId().toString();
    }

    private void downloadResults(String className, String testName) {
        if ("true".equals(System.getProperty(SAUCE_DOWNLOAD_SCRIPT_PROPERTY, "false"))) {
            try {
                String dir = determineSaveDir(className, testName);
                String resources = "mkdir " + dir + " ; cd " + dir + " ; \n"
                        + curlSaveResourceString(className, testName, "selenium-server.log") + " ; \n"
                        + curlSaveResourceString(className, testName, "video.flv") + " ; \n"
                        //                    + wgetnSaveResourceString(className, testName) + " ; \n"
                        + "cd ../\n";
                System.out.println(resources);
                writeFile("SauceLabsResources" + dir + ".sh", resources);

//                downloadResource(dir, "selenium-server.log");
//                downloadResource(dir, "video.flv");
            } catch (Exception e) {
                System.out.println("Exception while writing SauceLabsResources.sh " + e.getMessage());
                System.out.println(curlSaveResourceString(className, testName, "selenium-server.log"));
                System.out.println(curlSaveResourceString(className, testName, "video.flv"));
                //          System.out.println(curlSaveResourceString(className, testName, "XXXXscreenshot.png (where XXXX is a number between 0000 and 9999)")); // TODO
            }
        }
    }

    /* the curl command works, this doesn't, what's wrong?
    private void downloadResource(String dir, String resource) throws IOException {
        File file = new File(dir + resource);
        if(!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        CloseableHttpClient httpclient = HttpClients.custom().build();
        try {
            String userCredentials = "username:password";
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            HttpGet httpget = new HttpGet(resourceUrl(resource));
            httpget.setHeader("Authorization", basicAuth);
            httpget.setHeader("Content-Type", "text/html");
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                fileOutputStream.write(EntityUtils.toByteArray(response.getEntity()));
                fileOutputStream.close();
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
    */

    /**
     * <p>
     * Saucelabs tearDown, flag the tests as passed or failed.
     * </p><p>
     * Uses the SauceREST API to register a test as passed or failed.  {@see #SAUCE_REST_API_DELAY_MS}
     * </p>
     *
     * @param passed true if passed, falsed if failed, as a boolean
     * @param sessionId saucelabs test session id, as a String
     * @throws Exception
     */
    public void tearDown(boolean passed, String sessionId, String className, String testName) throws Exception {
        if (sessionId != null && System.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY) != null) { // dup guard so WebDriverUtils doesn't have to be used
            SauceREST client = new SauceREST(System.getProperty(SauceLabsWebDriverHelper.SAUCE_USER_PROPERTY),
                    System.getProperty(SauceLabsWebDriverHelper.SAUCE_KEY_PROPERTY));
            /* Using a map of udpates:
            * (http://saucelabs.com/docs/sauce-ondemand#alternative-annotation-methods)
            */
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("passed", passed);
            updates.put("build", System.getProperty(SAUCE_BUILD_PROPERTY, "unknown"));
            client.updateJobInfo(sessionId, updates);

            if (passed) {
                client.jobPassed(sessionId);
            } else {
                client.jobFailed(sessionId);
            }

            // give the client message a chance to get processed on saucelabs side
            Thread.sleep(Integer.parseInt(System.getProperty(SAUCE_REST_API_DELAY_MS, "5000")));

            downloadResults(className, testName);
        }
    }

    private String curlSaveResourceString(String className, String testName, String resource) {
        return "curl -o " + deriveResourceBaseNames(className, testName, resource) + " -u " + authentication
                .getUsername() + ":" + authentication.getAccessKey() + " " + resourceUrl(resource);
    }

    private String resourceUrl(String resource) {
        return "http://saucelabs.com/rest/" + authentication.getUsername() + "/jobs/" + sessionId + "/results/" + resource;
    }

    private String deriveResourceBaseNames(String className, String testName, String resource) {
        return className + "." + testName + "-"
                + System.getProperty(SAUCE_PLATFORM_PROPERTY, Platform.UNIX.toString()) + "-"
                + System.getProperty(SAUCE_BROWSER_PROPERTY) + "-"
                + System.getProperty(SAUCE_VERSION_PROPERTY) + "-"
                + System.getProperty(WebDriverUtils.REMOTE_PUBLIC_USER_PROPERTY, "admin") + "-"
                + System.getProperty(SAUCE_BUILD_PROPERTY, "unknown_build") + "-"
                + AutomatedFunctionalTestUtils.DTS + "-"
                + resource;
    }

    /**
     * <p>
     * Returns the (RemoteWebDriver) driver.
     * </p>
     *
     * @return WebDriver
     */
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    // Seems like sceenshot downloading has changed, this doesn't work anymore
    private String wgetnSaveResourceString(String className, String testName) {
        String dir = determineSaveDir(className, testName);
        // http://www.jwz.org/hacks/wgetn
        return "wgetn https://saucelabs.com/" + sessionId + "/%04dscreenshot.png 0 50";
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
