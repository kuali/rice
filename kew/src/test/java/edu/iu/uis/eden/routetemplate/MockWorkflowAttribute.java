/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.routetemplate;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MockWorkflowAttribute implements WorkflowAttribute {
    
    private static final String MOCK_VALUE_ELEMENT = "mockValue";
    //private static final String VALUE_KEY = "value";
    
    private String value;
    
    public MockWorkflowAttribute() {}
    
    public MockWorkflowAttribute(String value) {
        setValue(value);
    }

    public String getDocContent() {
        if (value == null) return "";
        return "<"+MOCK_VALUE_ELEMENT+">"+value+"</"+MOCK_VALUE_ELEMENT+">";
    }
    
    public List parseDocContent(String docContent) {
        try {
            Document doc = XmlHelper.buildJDocument(new StringReader(docContent));
            Element mockValueElement = XmlHelper.findElement(doc.getRootElement(), MOCK_VALUE_ELEMENT);
            List attributes = new ArrayList();
            if (mockValueElement != null) {
                attributes.add(new MockWorkflowAttribute(mockValueElement.getText()));
            }
            return attributes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getIdFieldName() {
        return null;
    }
    public String getLockFieldName() {
        return null;
    }
    public List getRoutingDataRows() {
        return null;
    }
    public List getRuleExtensionValues() {
        return null;
    }
    public List getRuleRows() {
        return null;
    }
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return false;
    }
    public boolean isRequired() {
        return false;
    }
    
    public void setRequired(boolean required) {
    }
    public List validateRoutingData(Map paramMap) {
        return null;
    }
    public List validateRuleData(Map paramMap) {
        return null;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
