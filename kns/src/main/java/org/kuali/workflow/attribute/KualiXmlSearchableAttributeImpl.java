/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import org.w3c.dom.Element;

import edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute;

public class KualiXmlSearchableAttributeImpl extends StandardGenericXMLSearchableAttribute implements KualiXmlAttribute {
    private static final long serialVersionUID = -5759823164605651979L;

	/**
     * Constructs a KualiXmlRuleAttributeImpl.java.
     */
    public KualiXmlSearchableAttributeImpl() {
        super();
    }

    public Element getConfigXML() {
        Element root = getAttributeConfigXML();
        KualiXmlAttributeHelper attributeHelper = new KualiXmlAttributeHelper();
        // this adds the name and title to the xml based on the data dictionary
        return attributeHelper.processConfigXML(root);
    }

    public Element getAttributeConfigXML() {
        return super.getConfigXML();
    }

}
