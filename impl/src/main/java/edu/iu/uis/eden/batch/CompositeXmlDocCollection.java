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
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * In someway allows muliple xml documents to be pushed into the xml loading 
 * 'pipeline'.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CompositeXmlDocCollection implements XmlDocCollection {
    protected Collection collections; 
    public CompositeXmlDocCollection(Collection xmlDocCollections) {
        collections = xmlDocCollections;
    }

    public File getFile() {
        return null;
    }

    public List getXmlDocs() {
        List docs = new LinkedList();
        Iterator collectionIt = collections.iterator();
        while (collectionIt.hasNext()) {
            XmlDocCollection coll = (XmlDocCollection) collectionIt.next();
            docs.addAll(coll.getXmlDocs());
        }
        return docs;
    }

    public void close() throws IOException {
        Iterator collectionIt = collections.iterator();
        while (collectionIt.hasNext()) {
            XmlDocCollection coll = (XmlDocCollection) collectionIt.next();
            coll.close();
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[CompositeXmlDocCollection: ");
        Iterator collectionIt = collections.iterator();
        while (collectionIt.hasNext()) {
            XmlDocCollection coll = (XmlDocCollection) collectionIt.next();
            sb.append(coll.toString() + ", ");
        }
        if (collections.size() > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }
}