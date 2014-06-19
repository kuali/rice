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

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WebDriverScreenshotHelper {

    /**
     * -Dremote.driver.screenshot.filename= appended with a formatted date time stamp and png file extension.
     */
    private static final String REMOTE_DRIVER_SCREENSHOT_FILENAME = "remote.driver.screenshot.filename";

    /**
     * -Dremote.driver.screenshot.dir= default is java working dir.
     */
    private static final String REMOTE_DRIVER_SCREENSHOT_DIR = "remote.driver.screenshot.dir";

    /**
     * -Dremote.driver.screenshot.archive.url= default is empty string.
     *
     * Used to link Jenkins output of screenshots to their archive.
     */
    private static final String REMOTE_DRIVER_SCREENSHOT_ARCHIVE_URL = "remote.driver.screenshot.archive.url";

    /**
     * -Dremote.driver.failure.screenshot= default is false
     *
     */
    private static final String REMOTE_DRIVER_FAILURE_SCREENSHOT = "remote.driver.failure.screenshot";

    /**
     * -Dremote.driver.step.screenshot= default is false
     */
    private static final String REMOTE_DRIVER_STEP_SCREENSHOT = "remote.driver.step.screenshot";

    /**
     * Screenshots will be saved using either the value of (#REMOTE_DRIVER_SCREENSHOT_FILENAME or if none, testName.testNameMethod)
     * appended with a date time stamp and the png file extension.
     *
     * @see WebDriverUtils#getDateTimeStampFormatted
     *
     * @param driver to use, if not of type TakesScreenshot no screenshot will be taken
     * @param testName to save test as, unless #REMOTE_DRIVER_SCREENSHOT_FILENAME is set
     * @param testMethodName to save test as, unless #REMOTE_DRIVER_SCREENSHOT_FILENAME is set
     * @throws IOException
     */
    public void screenshot(WebDriver driver, String testName, String testMethodName) throws IOException {
        if (driver instanceof TakesScreenshot) {
            File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            String screenshotFileName = System.getProperty(REMOTE_DRIVER_SCREENSHOT_FILENAME, testName
                    + "." + testMethodName) + "-" + WebDriverUtils.getDateTimeStampFormatted() + ".png";
            FileUtils.copyFile(scrFile, new File(System.getProperty(REMOTE_DRIVER_SCREENSHOT_DIR, ".")
                    + File.separator, screenshotFileName));
            String archiveUrl = System.getProperty(REMOTE_DRIVER_SCREENSHOT_ARCHIVE_URL, "");
            WebDriverUtils.jGrowl(driver, "Screenshot", false, archiveUrl + screenshotFileName);
        }
    }

    /**
     * @return false unless #REMOTE_DRIVER_FAILURE_SCREENSHOT is set to true.
     */
    public boolean screenshotOnFailure() {
        return "true".equals(System.getProperty(REMOTE_DRIVER_FAILURE_SCREENSHOT, "false"));
    }

    /**
     * @return false unless #REMOTE_DRIVER_STEP_SCREENSHOT is set to true.
     */
    public boolean screenshotSteps() {
        return "true".equals(System.getProperty(REMOTE_DRIVER_STEP_SCREENSHOT, "false"));
    }
}
