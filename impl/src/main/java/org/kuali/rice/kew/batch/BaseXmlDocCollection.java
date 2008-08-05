/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of XmlDocCollection interface
 * @see org.kuali.rice.kew.batch.BaseXmlDoc
 * @see org.kuali.rice.kew.batch.FileXmlDocCollection
 * @see org.kuali.rice.kew.batch.DirectoryXmlDocCollection
 * @see org.kuali.rice.kew.batch.ZipXmlDocCollection
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
abstract class BaseXmlDocCollection implements XmlDocCollection {
    protected File file;
    protected List xmlDocs = new ArrayList();
    public BaseXmlDocCollection(File file) {
        this.file = file;
    }
    public File getFile() {
        return file;
    }
    public List getXmlDocs() {
        return xmlDocs;
    }
    public void close() throws IOException {
    }
    public String toString() {
        return file.getName();
    }
}