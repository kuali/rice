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
package edu.iu.uis.eden.clientapp.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a remote WorkflowAttribute and how to construct it.  This is to be used when 
 * the attributes are being hosted remotely inside the Workflow runtime.
 * 
 * The attribute definition must be constructed with the fully qualified class name of the
 * target attribute.  It can be constructed one of two ways, through the constructor of the
 * attribute or by setting java bean properties.  If both are specified, then the attribute
 * will be constructed using both the constructor and properties.  If no constructor
 * parameters are specified the target attribute must have a no-argument public constructor.
 * 
 * The "properties" represented by the PropertyDefinitionVOs will be set as bean properties
 * (setters) on the target attribute class.  The standard KSB resource/object-definition loading
 * mechanism implements this functionality.  If the attribute is a GenericXMLRuleAttribute
 * or GenerixXMLSearchableAttribute, then the properties will also be set explicitly in the <code>paramMap</code>
 * by the workflow server. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class WorkflowAttributeDefinitionVO implements java.io.Serializable {
    
    static final long serialVersionUID = 1000;
    private String attributeName;
    private List<String> constructorParameters = new ArrayList<String>();
    private Map<String, PropertyDefinitionVO> properties = new HashMap<String, PropertyDefinitionVO>();
    
    public WorkflowAttributeDefinitionVO() {}
    
    public WorkflowAttributeDefinitionVO(String attributeName) {
        setAttributeName(attributeName);
    }
    
    public String getAttributeName() {
        return attributeName;
    }
    
    public void setAttributeName(String attributeName) {
        if (attributeName == null) throw new IllegalArgumentException("Attribute name cannot be null");
        this.attributeName = attributeName;
    }
        
    public void addConstructorParameter(String parameter) {
        constructorParameters.add(parameter);
    }
    
    public void removeConstructorParameter(String parameter) {
        constructorParameters.remove(parameter);
    }
    
    public void setConstructorParameters(String[] parameters) {
        constructorParameters = Arrays.asList(parameters);
    }
    
    public String[] getConstructorParameters() {
        return (String[])constructorParameters.toArray(new String[0]);
    }
    
    public void addProperty(PropertyDefinitionVO property) {
        if (property == null) return;
        if (property.getName() == null) {
            throw new IllegalArgumentException("PropertyDefinition cannot have a null name.");
        }
        properties.put(property.getName(), property);
    }
    
    public PropertyDefinitionVO getProperty(String name) {
        return (PropertyDefinitionVO)properties.get(name);
    }
    
    public PropertyDefinitionVO[] getProperties() {
        return (PropertyDefinitionVO[])properties.values().toArray(new PropertyDefinitionVO[0]);
    }
    
    public void setProperties(PropertyDefinitionVO[] properties) {
        this.properties.clear();
        if (properties == null) return;
        for (int index = 0; index < properties.length; index++) {
            addProperty(properties[index]);
        }
    }   
    
    public void addProperty(String name, String value) {
        addProperty(new PropertyDefinitionVO(name, value));
    }
}