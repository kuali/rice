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

/**
 * TODO delyea - documentation
 * 
 */
public class KualiXMLBooleanTranslatorSearchableAttributeImpl extends KualiXmlSearchableAttributeImpl {

    public static final String VALUE_FOR_TRUE = "Yes";
    public static final String VALUE_FOR_FALSE = "No";

    private boolean alreadyTranslated = false;

    /**
     * TODO delyea - documentation
     * 
     * @see org.kuali.workflow.attribute.KualiXmlSearchableAttributeImpl#getConfigXML()
     */
    @Override
    public Element getConfigXML() {
        alreadyTranslated = true;
        String[] xpathElementsToInsert = new String[3];
        xpathElementsToInsert[0] = "concat( substring('" + getValueForXPathTrueEvaluation() + "', number(not(";
        xpathElementsToInsert[1] = "))*string-length('" + getValueForXPathTrueEvaluation() + "')+1), substring('" + getValueForXPathFalseEvaluation() + "', number(";
        xpathElementsToInsert[2] = ")*string-length('" + getValueForXPathFalseEvaluation() + "')+1))";
        Element root = super.getAttributeConfigXML();
        return new KualiXmlAttributeHelper().processConfigXML(root, xpathElementsToInsert);
    }

    public String getValueForXPathTrueEvaluation() {
        return VALUE_FOR_TRUE;
    }

    public String getValueForXPathFalseEvaluation() {
        return VALUE_FOR_FALSE;
    }

}
