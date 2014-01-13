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
package org.kuali.rice.scripts

import groovy.util.logging.Log
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * This class //TODO ...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class ConversionUtilsTest {

    static def convTestDir = "ConversionUtilsTest/";
    public String tempFolderPath;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setupTestDir() {
        tempFolderPath = folder.getRoot().absolutePath;
        File createdFile= folder.newFile("AttributePropertySample.xml");
    }

    @Test
    void testFindFilesByName() {
        def files = ConversionUtils.findFilesByName(tempFolderPath, "AttributePropertySample.xml");
        Assert.assertEquals("file count does not match", 1, files.size());
    }

    @Test
    void testFindFilesByPattern() {
        def attrPattern = ~/AttributePropertySample\.xml$/
        def files = ConversionUtils.findFilesByPattern(tempFolderPath, attrPattern);
        Assert.assertEquals("file count does not match", 1, files.size());
    }

    @Test
    void testGetPomData() {
        def pomFileName = "pomTestData.xml";
        def pomFile = ConversionUtils.getResourceFile(convTestDir + pomFileName);
        def actualPomData = ConversionUtils.getPomData(pomFile.getParentFile().absolutePath, pomFileName);
        Assert.assertEquals("Project parent artifactId", "sampleu-common",actualPomData.parent.artifactId);
        Assert.assertEquals("Project artifactId", "travel-app",actualPomData.artifact.artifactId);
        Assert.assertEquals("Project modules count", 3, actualPomData.modules.size());
    }
}
