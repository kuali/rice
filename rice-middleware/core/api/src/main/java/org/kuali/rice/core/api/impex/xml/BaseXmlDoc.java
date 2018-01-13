/**
 * Copyright 2005-2018 The Kuali Foundation
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


/**
 * Base implementation of XmlDoc interface
 * @see org.kuali.rice.core.api.impex.xml.BaseXmlDocCollection
 * @see org.kuali.rice.core.api.impex.xml.FileXmlDoc
 * @see org.kuali.rice.core.api.impex.xml.ZipXmlDoc
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
abstract class BaseXmlDoc implements XmlDoc {
    protected XmlDocCollection collection;
    protected boolean processed;
    protected String message;

    public BaseXmlDoc(XmlDocCollection collection) {
        this.collection = collection;
    }
    public XmlDocCollection getCollection() {
        return collection;
    }
    public String toString() {
        return collection + ":" + getName();
    }
    public boolean isProcessed() {
        return processed;
    }
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    public String getProcessingMessage() {
        return message;
    }
    public void setProcessingMessage(String message) {
        this.message = message;
    }
}
