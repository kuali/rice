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

import java.io.InputStream;

/**
 * XmlDocCollection that is not File-based 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StreamXmlDocCollection extends BaseXmlDocCollection {
    public StreamXmlDocCollection(InputStream stream) {
        super(null); // don't give our file to the superclass
        xmlDocs.add(new StreamXmlDoc(stream, this));
    }
    public String toString() {
        return "StreamXmlDocCollection";
    }
}
