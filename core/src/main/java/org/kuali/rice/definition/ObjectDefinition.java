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
package org.kuali.rice.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A marker interface for object definitions.
 * 
 * @author ewestfal
 */
public class ObjectDefinition implements Serializable {

	private static final long serialVersionUID = 835423601196288352L;

	private String className;
	private String messageEntity;
	private boolean atRemotingLayer;
	private final List constructorParameters = new ArrayList();
	private final Map properties = new HashMap();

	public ObjectDefinition(Class objectClass) {
		this(objectClass.getName());
	}
	
	public ObjectDefinition(String className, String messageEntity) {
		this.className = className;
		this.messageEntity = messageEntity;
	}
	
	public ObjectDefinition(String className) {
		if (className == null) {
			throw new IllegalArgumentException("Extension class name cannot be null");
		}
		this.className = className;
	}

	public String getClassName() {
		return this.className;
	}

	public void addConstructorParameter(DataDefinition parameter) {
	    this.constructorParameters.add(parameter);
	}

	public void removeConstructorParameter(DataDefinition parameter) {
	    this.constructorParameters.remove(parameter);
	}

	public void setConstructorParameters(List parameters) {
	    this.constructorParameters.clear();
	    this.constructorParameters.addAll(parameters);
	}

	public List getConstructorParameters() {
		return this.constructorParameters;
	}

	public void addProperty(PropertyDefinition property) {
		if (property == null) {
			return;
		}
		if (property.getName() == null) {
			throw new IllegalArgumentException("PropertyDefinition cannot have a null name.");
		}
		this.properties.put(property.getName(), property);
	}

	public PropertyDefinition getProperty(String name) {
		return (PropertyDefinition) this.properties.get(name);
	}

	public Collection getProperties() {
		return this.properties.values();
	}

	public void setProperties(Collection properties) {
		this.properties.clear();
		if (properties == null) {
			return;
		}
		for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
			addProperty((PropertyDefinition) iterator.next());
		}
	}

	public String getMessageEntity() {
		return this.messageEntity;
	}

	public void setMessageEntity(String messageEntity) {
		this.messageEntity = messageEntity;
	}

    public String toString() {
        return "[ObjectDefinition: className: " + getClassName()
               + ", messageEntity: " + getMessageEntity()
               + "]";
    }

	public boolean isAtRemotingLayer() {
		return this.atRemotingLayer;
	}

	public void setAtRemotingLayer(boolean atRemotingLayer) {
		this.atRemotingLayer = atRemotingLayer;
	}
}
