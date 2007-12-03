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
package edu.iu.uis.eden.xml.export;

import java.util.Date;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Namespace;

import edu.iu.uis.eden.EdenConstants;

/**
 * A helper class which helps with building the XML for export.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ExportRenderer {

    private Namespace namespace;
    
    public ExportRenderer(Namespace namespace) {
        this.namespace = namespace;
    }
    
    public Element renderElement(Element parent, String elementName) {
        Element element = new Element(elementName, namespace);
        if (parent != null) {
            parent.addContent(element);
        }
        return element;
    }
    
    public Element renderTextElement(Element parent, String elementName, String text) {
        Element element = null;
        if (text != null) {
            element = renderElement(parent, elementName);
            element.setText(text);
        }
        return element;
    }
    
    public Element renderBooleanElement(Element parent, String elementName, Boolean bool, boolean defaultValue) {
        if (bool == null) {
            bool = new Boolean(defaultValue);
        }
        return renderTextElement(parent, elementName, (bool.booleanValue() ? "true" : "false"));   
    }
    
    public Element renderDateElement(Element parent, String elementName, Date date) {
        Element element = null;
        if (date != null) {
            element = renderTextElement(parent, elementName, EdenConstants.getDefaultDateFormat().format(date));
        }
        return element;
    }
    
    public void renderAttribute(Element element, String attributeName, String attributeValue) {
        element.setAttribute(attributeName, attributeValue);
    }
    
    public Element renderCDATAElement(Element parent, String elementName, String data) {
    	Element element = null;
        if (data != null) {
            element = renderElement(parent, elementName);
            element.addContent(new CDATA(data));
        }
        return element;
    }

}
