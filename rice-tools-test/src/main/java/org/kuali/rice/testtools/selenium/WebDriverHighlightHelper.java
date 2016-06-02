/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility for highlighting a WebDriver WebElement in green (#66FF33) for 400ms.  Executes JavaScript to change the
 * WebElement style and then revert back to the original style.  Set the JVM argument -Dremote.driver.highlight=true to
 * enable.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WebDriverHighlightHelper {

    protected boolean jsHighlightEnabled = false;

    /**
     * green (#66FF33)
     */
    public static final String JS_HIGHLIGHT_BACKGROUND = "#66FF33";

    /**
     * green (#66FF33)
     */
    public static final String JS_HIGHLIGHT_BOARDER = "#66FF33";

    /**
     * 400 milliseconds.
     */
    public static final int JS_HIGHLIGHT_MS = 400;

    /**
     * <p>
     * {@see JS_HIGHLIGHT_MS} as default.
     * </p><p>
     * -Dremote.driver.highlight.ms=
     * </p>
     */
    public static final String JS_HIGHLIGHT_MS_PROPERTY = "remote.driver.highlight.ms";

    /**
     * <p>
     * Highlighting of elements as selenium runs.
     * </p><p>
     * -Dremote.driver.highlight=true
     * </p>
     */
    public static final String JS_HIGHLIGHT_PROPERTY = "remote.driver.highlight";

    /**
     * TODO: playback for javascript highlighting.
     *
     * -Dremote.driver.highlight.input=
     */
    public static final String JS_HIGHLIGHT_INPUT_PROPERTY = "remote.driver.highlight.input";

    /**
     * Create a WebDriverHighlightHelp which is enabled if JS_HIGHLIGHT_PROPERTY is set to true.
     */
    public WebDriverHighlightHelper() {
        if ("true".equals(System.getProperty(JS_HIGHLIGHT_PROPERTY, "false"))) {
            jsHighlightEnabled = true;
            if (System.getProperty(JS_HIGHLIGHT_INPUT_PROPERTY) != null) {
                InputStream in = WebDriverUtils.class.getResourceAsStream(System.getProperty(JS_HIGHLIGHT_INPUT_PROPERTY));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                List<String> lines = new LinkedList<String>();
                try {
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading javascript highlight playback file " + System.getProperty(JS_HIGHLIGHT_INPUT_PROPERTY));
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>
     * Highlight given WebElement.
     * </p>
     *
     * @param webDriver to execute highlight on
     * @param webElement to highlight
     */
    public void highlightElement(WebDriver webDriver, WebElement webElement) {
        if (jsHighlightEnabled && webElement != null) {
            try {
                //                System.out.println("highlighting " + webElement.toString() + " on url " + webDriver.getCurrentUrl());
                JavascriptExecutor js = (JavascriptExecutor) webDriver;
                String jsHighlight = "element = arguments[0];\n"
                        + "originalStyle = element.getAttribute('style');\n"
                        + "element.setAttribute('style', originalStyle + \"; background: "
                        + JS_HIGHLIGHT_BACKGROUND + "; border: 2px solid " + JS_HIGHLIGHT_BOARDER + ";\");\n"
                        + "setTimeout(function(){\n"
                        + "    element.setAttribute('style', originalStyle);\n"
                        + "}, " + System.getProperty(JS_HIGHLIGHT_MS_PROPERTY, JS_HIGHLIGHT_MS + "") + ");";
                js.executeScript(jsHighlight, webElement);
            } catch (Throwable t) {
                System.out.println("Throwable during javascript highlight element");
                t.printStackTrace();
            }
        }
    }
}
