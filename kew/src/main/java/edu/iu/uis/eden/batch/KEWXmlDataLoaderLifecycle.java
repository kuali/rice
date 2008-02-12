/*
 * Copyright 2007 The Kuali Foundation
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import edu.iu.uis.eden.KEWServiceLocator;

/**
 * A lifecycle for loading KEW XML datasets.
 * This lifecycle will not be run (even if it is listed in the lifecycles list)
 * if the 'use.kewXmlmlDataLoaderLifecycle' configuration property is defined, and is
 * not 'true'.  If the property is omitted the lifecycle runs as normal.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KEWXmlDataLoaderLifecycle extends BaseLifecycle {
    private static final Logger LOG = Logger.getLogger(KEWXmlDataLoaderLifecycle.class);

    private String filename;

    public KEWXmlDataLoaderLifecycle() {
        this("classpath:DefaultTestData.xml");
    }

    public KEWXmlDataLoaderLifecycle(String filename) {
        this.filename = filename;
    }

    public void start() throws Exception {
        String useKewXmlDataLoaderLifecycle = Core.getCurrentContextConfig().getProperty("use.kewXmlmlDataLoaderLifecycle");

        if (useKewXmlDataLoaderLifecycle != null && !Boolean.valueOf(useKewXmlDataLoaderLifecycle)) {
            LOG.debug("Skipping KEWXmlDataLoaderLifecycle due to property: use.kewXmlmlDataLoaderLifecycle=" + useKewXmlDataLoaderLifecycle);
            return;
        }

        loadDefaultTestData();
        super.start();
    }

    /**
     * By default this loads the "default" data set from the DefaultTestData.sql
     * and DefaultTestData.xml files. Subclasses can override this to change
     * this behaviour or pass in a filename to the constructor
     */
    protected void loadDefaultTestData() throws Exception {
        this.loadXmlFile(filename);
    }

    protected void loadXmlFile(String fileName) throws Exception {
        Resource resource = new DefaultResourceLoader().getResource(fileName);
        InputStream xmlFile = resource.getInputStream();
        if (xmlFile == null) {
            throw new ConfigurationException("Didn't find file " + fileName);
        }
        List<XmlDocCollection> xmlFiles = new ArrayList<XmlDocCollection>();
        XmlDocCollection docCollection = getFileXmlDocCollection(xmlFile, "UnitTestTemp");
        xmlFiles.add(docCollection);
        KEWServiceLocator.getXmlIngesterService().ingest(xmlFiles);
        for (Iterator iterator = docCollection.getXmlDocs().iterator(); iterator.hasNext();) {
            XmlDoc doc = (XmlDoc) iterator.next();
            if (!doc.isProcessed()) {
                throw new RuntimeException("Failed to ingest xml doc: " + doc.getName());
            }
        }
    }

    protected FileXmlDocCollection getFileXmlDocCollection(InputStream xmlFile, String tempFileName) throws IOException {
        if (xmlFile == null) {
            throw new RuntimeException("Didn't find the xml file " + tempFileName);
        }
        File temp = File.createTempFile(tempFileName, ".xml");
        temp.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(temp);
        int data = -1;
        while ((data = xmlFile.read()) != -1) {
            fos.write(data);
        }
        fos.close();
        return new FileXmlDocCollection(temp);
    }

}
