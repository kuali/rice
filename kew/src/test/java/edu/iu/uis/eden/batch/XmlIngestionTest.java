/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.batch;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;
import org.kuali.workflow.test.TestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.FileCopyUtils;


/**
 * Tests XML "ingestion" pipeline
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class XmlIngestionTest extends KEWTestCase {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlIngestionTest.class);

	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"), "XmlIngestionTest_dir");
    private static final File PENDING_DIR = new File(TMP_DIR, "pending");
    private static final File LOADED_DIR = new File(TMP_DIR, "loaded");
    private static final File PROBLEM_DIR = new File(TMP_DIR, "problem");

    public void setUp() throws Exception {
        super.setUp();
        deleteDirectories();
        TMP_DIR.mkdirs();
        PENDING_DIR.mkdirs();
        LOADED_DIR.mkdirs();
        PROBLEM_DIR.mkdirs();
    }

    private void deleteContentsOfDir(File dir, int depth) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory() && depth > 0) {
                // decrement depth
                // to avoid the possibility of inadvertent
                // recursive delete!
                deleteContentsOfDir(files[i], depth - 1);
            }
            boolean success = files[i].delete();
            LOG.info("deleting: " + files[i] + "..." + (success ? "succeeded" : "failed"));
        }
    }

    public void tearDown() throws Exception {
        try {
            deleteDirectories();
        } finally {
            super.tearDown();
        }
    }

    protected void deleteDirectories() {
        deleteContentsOfDir(PENDING_DIR, 0);
        deleteContentsOfDir(LOADED_DIR, 2);
        deleteContentsOfDir(PROBLEM_DIR, 2);
        deleteContentsOfDir(TMP_DIR, 0);
        TMP_DIR.delete();
    }

    protected boolean verifyFileExists(File dir, File file) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(dir.toURL() + "/**/" + file.getName());
        if (resources == null) {
            return false;
        }
        for (int i = 0; i < resources.length; i++) {
            if (resources[i].exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO: beef this up
     * need a reliable way to test if the file arrived in the right date-stamped
     * subdirectory (maybe just pick the last, or first directory?)
     */
    @Test public void testXmlIngestion() throws IOException {
        XmlPollerServiceImpl poller = new XmlPollerServiceImpl();
        poller.setPollIntervalSecs(1);
        poller.setXmlParentDirectory(TMP_DIR.toString());
        poller.setXmlPendingLocation(PENDING_DIR.toString());
        poller.setXmlCompletedLocation(LOADED_DIR.toString());
        poller.setXmlProblemLocation(PROBLEM_DIR.toString());

        Properties filesToIngest = new Properties();
        filesToIngest.load(getClass().getResourceAsStream("XmlIngestionTest.txt"));
        List pendingFiles = new LinkedList();
        List shouldPass = new LinkedList();
        List shouldFail = new LinkedList();
        Iterator entries = filesToIngest.entrySet().iterator();
        int i = 0;
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String filePath = entry.getKey().toString();
            filePath = filePath.replace("${"+TestUtils.BASEDIR_PROP+"}", TestUtils.getBaseDir());
            File testFile = new File(filePath);
            File pendingDir = new File(PENDING_DIR + "/TestDoc-" + i);
            pendingDir.mkdirs();
            assertTrue(pendingDir.isDirectory());
            File pending = new File(pendingDir, testFile.getName());
            pendingFiles.add(pending);
            if (Boolean.valueOf(entry.getValue().toString()).booleanValue()) {
                shouldPass.add(pending);
            } else {
                shouldFail.add(pending);
            }
            FileCopyUtils.copy(testFile, pending);
            LOG.info("created: " + pending);
            i++;
        }

        // poller should not throw exceptions
        poller.run();

        // check that all files have been processed
        Iterator it = pendingFiles.iterator();
        while (it.hasNext()) {
            File pending = (File) it.next();
            assertTrue(!pending.isFile());
        }

        // check that they landed in the appropriate location

        // loaded files should be in the loaded dir...
        it = shouldPass.iterator();
        while (it.hasNext()) {
            File file = (File) it.next();
            assertTrue("Loaded file " + file + " was not moved to loaded directory " + LOADED_DIR, verifyFileExists(LOADED_DIR, file));
            assertFalse("Loaded file " + file + " was moved to problem directory " + PROBLEM_DIR, verifyFileExists(PROBLEM_DIR, file));
        }
        // and problem files should be in the problem dir...
        it = shouldFail.iterator();
        while (it.hasNext()) {
            File file = (File) it.next();
            assertTrue("Problem file " + file + " was not moved to problem directory" + PROBLEM_DIR, verifyFileExists(PROBLEM_DIR, file));
            assertFalse("Problem file " + file + " was moved to loaded directory" + LOADED_DIR, verifyFileExists(LOADED_DIR, file));
        }
    }
}