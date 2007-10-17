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
package edu.iu.uis.eden.batch;

import java.io.File;

/**
 * A "singleton" XmlDocCollection backed by a single File (FileXmlDoc)
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FileXmlDocCollection extends BaseXmlDocCollection {
    public FileXmlDocCollection(File file) {
        this(file, file.getName());
    }
    public FileXmlDocCollection(File file, String name) {
        super(file);
        this.file = file;
        xmlDocs.add(new FileXmlDoc(file, name, this));
    }
}