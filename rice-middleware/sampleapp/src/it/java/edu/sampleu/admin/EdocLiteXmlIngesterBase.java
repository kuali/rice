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
package edu.sampleu.admin;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class EdocLiteXmlIngesterBase extends AdminTmplMthdAftNavBase {
    // values set by default for repeatable testing; left as configurable for load tests
    protected List<File> fileUploadList;

    @Override
    protected String getBookmarkUrl() {
        return null; // no bookmark test yet
    }

    /**
     * This overridden method ...
     *
     * @see edu.sampleu.common.NavTemplateMethodAftBase#getLinkLocator()
     */
    @Override
    protected String getLinkLocator() {
        return "XML Ingester";
    }

    /**
     * Performs Ingesting files to fileupload component and asserts succesful ingestion.
     *
     */
    private void fileIngester(List<File> fileToUpload) throws Exception {
        int cnt = 0;

        for (File file : fileToUpload) {
            String path = file.getAbsolutePath().toString();
            driver.findElement(By.name("file[" + cnt + "]")).sendKeys(path);
            cnt++;
        }

        waitAndClickById("imageField");
    }

    /**
     * Divides fileUploadList from resources into sublists to match the maximum number of file
     * upload components available on XML Ingester Screen
     *
     */
    private List<List<File>> getSubListsForFile(List<File> fileList, final int L) {
        List<List<File>> subLists = new ArrayList<List<File>>();
        final int N = fileList.size();
        for (int i = 0; i < N; i += L) {
            subLists.add(new ArrayList<File>(fileList.subList(i, Math.min(N, i + L))));
        }

        return subLists;
    }

    protected void setUpResourceDir(String resourceDir) {
        try {
            setUpFiles("src/it/resources/" + resourceDir);
        } catch (Exception e) {
            System.out.println("Problem loading files from filesystem ( " + e.getMessage() + "). If running from Intellij make sure working directory is rice-middleware/sampleapp attempt to load as resource.");
            try {
                setUpResourceFiles(resourceDir);
            } catch (Exception e1) {
                e1.printStackTrace();
                jiraAwareFail("Problems loading files as resources " + e1.getMessage());
            }
        }
    }

    protected void setUpResourceFiles(String resourceDir) throws Exception {
        String[] resources = getResourceListing(getClass(), resourceDir);
        fileUploadList = new ArrayList<File>();

        for (String resource : resources) {
            InputStream inputStream = getClass().getResourceAsStream(resource);
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + resource);
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            fileUploadList.add(file);
        }
        Collections.sort(fileUploadList);
    }

    protected String[] getResourceListing(Class clazz, String pathStartsWith) throws Exception {
        String classPath = clazz.getName().replace(".", "/")+".class";
        URL dirUrl = clazz.getClassLoader().getResource(classPath);

        if (!"jar".equals(dirUrl.getProtocol())) {
            throw new UnsupportedOperationException("Cannot list files for URL " + dirUrl);
        }

        String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!")); //strip out only the JAR file
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        Enumeration<JarEntry> entries = jar.entries();
        Set<String> result = new HashSet<String>();

        while(entries.hasMoreElements()) {
            String entry = entries.nextElement().getName();
            if (entry.startsWith(pathStartsWith) && !entry.endsWith("/")) { //filter according to the pathStartsWith skipping directories
                result.add(entry);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    protected void setUpFiles(String path) throws Exception {
        fileUploadList = new ArrayList<File>();

        File dir = new File(path);

        if (dir != null && dir.listFiles().length > 0) {
            Integer i = 1;
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".xml") && !file.getName().equalsIgnoreCase("sample-app-config.xml")) {
                    fileUploadList.add(file);
                }
                i++;
            }
            Collections.sort(fileUploadList);
        } else {
            throw new Exception("----Resources not found----");
        }
    }

    protected void testEdocLiteIngestion() throws Exception {
        testXmlIngesterSuccessfulFileUpload();

        Thread.sleep(2000);
        driver.switchTo().defaultContent();
        waitAndClickByLinkText("Main Menu");
        waitAndClickByLinkText("eDoc Lite");

        selectFrameIframePortlet();
        waitIsVisible(By.cssSelector("input.tinybutton:nth-child(1)")); // why name methodToCall.search fails?
        waitAndClick(By.cssSelector("input.tinybutton:nth-child(1)"));
        Thread.sleep(2000);
        driver.switchTo().defaultContent();
        Thread.sleep(1000);
        waitIsVisible(By.className("exportlinks"));
        selectFrameIframePortlet();
    }

    /**
     * Uploads file available from fileUploadList through XML Ingester.
     * Uploads each sublist from main fileUploadList if size greater than 10.
     *
     */
    public void testXmlIngesterSuccessfulFileUpload() throws Exception {
        if (fileUploadList == null && fileUploadList.isEmpty()) {
            return;
        }

        if (fileUploadList.size() > 10) {
            List<List<File>> subLists = getSubListsForFile(fileUploadList, 10);
            for (List<File> fileSet : subLists) {
                fileIngester(fileSet);
                for (File file : fileSet) {
                    checkMessages(file);
                }
            }
        } else {
            fileIngester(fileUploadList);
            for (File file : fileUploadList) {
                checkMessages(file);
            }
        }
    }

    private void checkMessages(File file) throws InterruptedException {
        waitIsVisible(By.className("error")); // messages appear in error too.
        if (!isTextPresent("without allowOverwrite set")) { // docs should still be present
            // from previous run, if not we'll fail when we assert they exist.
            // xml ingestion can take a long, long time
            waitForTextPresent("Ingested xml doc: " + file.getName(), 360);
        }
    }
}
