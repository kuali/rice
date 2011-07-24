/*
 * Copyright 2005-2009, 2011 The Kuali Foundation
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
package org.kuali.rice.kew.api.document.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.kew.api.document.PropertyDefinition;
import org.w3c.dom.Element;

@XmlRootElement(name = WorkflowAttributeDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = WorkflowAttributeDefinition.Constants.TYPE_NAME, propOrder = {
		WorkflowAttributeDefinition.Elements.ATTRIBUTE_NAME,
		WorkflowAttributeDefinition.Elements.PARAMETERS,
		WorkflowAttributeDefinition.Elements.PROPERTY_DEFINITIONS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class WorkflowAttributeDefinition extends AbstractDataTransferObject {
    
	@XmlElement(name = Elements.ATTRIBUTE_NAME, required = true)
    private final String attributeName;
	
	@XmlElementWrapper(name = Elements.PARAMETERS, required = false)
	@XmlElement(name = Elements.PARAMETER, required = false)
    private final List<String> parameters;
	
	@XmlElementWrapper(name = Elements.PROPERTY_DEFINITIONS, required = false)
	@XmlElement(name = Elements.PROPERTY_DEFINITION, required = false)
    private final List<PropertyDefinition> propertyDefinitions;
    
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
	/**
     * Private constructor used only by JAXB.
     */
	private WorkflowAttributeDefinition() {
	    this.attributeName = null;
	    this.parameters = null;
	    this.propertyDefinitions = null;
	}
	
	private WorkflowAttributeDefinition(Builder builder) {
		this.attributeName = builder.getAttributeName();
		if (builder.getParameters() == null) {
			this.parameters = Collections.emptyList();
		} else {
			this.parameters = new ArrayList<String>(builder.getParameters());
		}
		if (builder.getPropertyDefinitions() == null) {
			this.propertyDefinitions = Collections.emptyList();
		} else {
			this.propertyDefinitions = new ArrayList<PropertyDefinition>(builder.getPropertyDefinitions());
		}
	}
	    
    public String getAttributeName() {
        return attributeName;
    }
    
    public List<String> getParameters() {
    	return Collections.unmodifiableList(parameters);
    }
    
    public List<PropertyDefinition> getPropertyDefinitions() {
    	return Collections.unmodifiableList(propertyDefinitions);
    }
    
    public final static class Builder implements Serializable {

		private static final long serialVersionUID = 7549637048594326790L;

		private String attributeName;
		private List<String> parameters;
		private List<PropertyDefinition> propertyDefinitions;

		private Builder(String attributeName) {
			setAttributeName(attributeName);
			setParameters(new ArrayList<String>());
			setPropertyDefinitions(new ArrayList<PropertyDefinition>());
		}
		
		private Builder(WorkflowAttributeDefinition definition) {
		    setAttributeName(definition.getAttributeName());
		    setParameters(definition.getParameters());
		    setPropertyDefinitions(definition.getPropertyDefinitions());
		}
		
		public static Builder create(WorkflowAttributeDefinition definition) {
		    if (definition == null) {
		        throw new IllegalArgumentException("definition was null");
		    }
		    return new Builder(definition);
		}
		
		public static Builder create(String attributeName) {
			return new Builder(attributeName);
			
		}
		
		public WorkflowAttributeDefinition build() {
			return new WorkflowAttributeDefinition(this);
		}
    
		public String getAttributeName() {
			return attributeName;
		}
		
		public List<String> getParameters() {
        	return parameters;
		}
		
		public List<PropertyDefinition> getPropertyDefinitions() {
			return propertyDefinitions;
		}
		
		public void setAttributeName(String attributeName) {
			if (StringUtils.isBlank(attributeName)) {
				throw new IllegalArgumentException("attributeName was null or blank");
			}
			this.attributeName = attributeName;
		}
		
		public void addParameter(String parameter) {
			parameters.add(parameter);
		}
    
		public void removeParameter(String parameter) {
			parameters.remove(parameter);
		}
		
		public void setParameters(List<String> parameters) {
			this.parameters = new ArrayList<String>(parameters);
		}
    
		public void addPropertyDefinition(PropertyDefinition property) {
			if (property == null) {
				throw new IllegalArgumentException("Property definition must be non-null.");
			}
			propertyDefinitions.add(property);
		}
        
        
		public void setPropertyDefinitions(List<PropertyDefinition> propertyDefinitions) {
			if (propertyDefinitions == null) {
				throw new IllegalArgumentException("propertyDefinitions must not be null.");
			}
			this.propertyDefinitions = new ArrayList<PropertyDefinition>(propertyDefinitions);
			
		}   
    
		public void addPropertyDefinition(String name, String value) {
			addPropertyDefinition(PropertyDefinition.create(name, value));
		}
		
	    public PropertyDefinition getPropertyDefinition(String name) {
	        if (StringUtils.isBlank(name)) {
	            throw new IllegalArgumentException("name was null or blank");
	        }
	        for (PropertyDefinition propertyDefinition : propertyDefinitions) {
	            if (propertyDefinition.equals(name)) {
	                return propertyDefinition;
	            }
	        }
	        return null;
	    }

		
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "workflowAttributeDefinition";
        final static String TYPE_NAME = "WorkflowAttributeDefinitionType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String ATTRIBUTE_NAME = "attributeName";
        final static String PARAMETERS = "parameters";
        final static String PARAMETER = "parameter";
        final static String PROPERTY_DEFINITIONS = "propertyDefinitions";
        final static String PROPERTY_DEFINITION = "propertyDefinition";
    }
    
}
