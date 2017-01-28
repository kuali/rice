/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.api.impex.xml;

import java.io.File;
import java.io.FileFilter;


/**
 * An XmlDocCollection backed by a directory of XML files
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DirectoryXmlDocCollection extends BaseXmlDocCollection {
    private static class DirectoryXmlDocsCollectionFileFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().endsWith(".xml");
        }
    }
    private static final DirectoryXmlDocsCollectionFileFilter FILTER = new DirectoryXmlDocsCollectionFileFilter();

    public DirectoryXmlDocCollection(File dir) {
        super(dir);
        File[] xmlDataFiles = file.listFiles(FILTER);
        if (xmlDataFiles != null) {
            for (File xmlDataFile : xmlDataFiles)
            {
                xmlDocs.add(new FileXmlDoc(xmlDataFile, this));
            }
        }
    }
}
