/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;

/**
 * This is a generic dictionary entry for an object that does not have to implement BusinessObject. It provides support
 * for general objects. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class DataObjectEntry extends DataDictionaryEntryBase implements MustOccurConstrainable {

	protected String name;
	protected Class<?> objectClass;

    protected String titleAttribute;
	protected String objectLabel;
    protected String objectDescription;

    protected List<String> primaryKeys;
    protected Class<? extends Exporter> exporterClass;
    
	protected List<MustOccurConstraint> mustOccurConstraints;
	
    
    protected HelpDefinition helpDefinition;
	
    
	@Override
    public void completeValidation() {
	    //KFSMI-1340 - Object label should never be blank
        if (StringUtils.isBlank(getObjectLabel())) {
            throw new AttributeValidationException("Object label cannot be blank for class " + objectClass.getName());
        }
        
        super.completeValidation();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getJstlKey()
     */
	@Override
	public String getJstlKey() {
        if (objectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: objectClass is null");
        }

        return (objectClass != null) ? objectClass.getSimpleName() : objectClass.getSimpleName();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    @Override
    public String getFullClassName() {
        return objectClass.getName();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    @Override
    public Class<?> getEntryClass() {
        return objectClass;
    }

	/**
	 * @return the objectClass
	 */
	public Class<?> getObjectClass() {
		return this.objectClass;
	}

	/**
	 * @param objectClass the objectClass to set
	 */
	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
    /**
     * @return Returns the objectLabel.
     */
    public String getObjectLabel() {
        return objectLabel;
    }

    /**
           The objectLabel provides a short name of the business
           object for use on help screens.
     *
     * @param objectLabel The objectLabel to set.
     */
    public void setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
    }

    /**
     * @return Returns the description.
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
           The objectDescription provides a brief description
           of the business object for use on help screens.
     *
     * @param description The description to set.
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }
	
    

    
	
    /**
     * Gets the helpDefinition attribute.
     *
     * @return Returns the helpDefinition.
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Sets the helpDefinition attribute value.
     *
           The objectHelp element provides the keys to
           obtain a help description from the system parameters table.

           parameterNamespace the namespace of the parameter containing help information
           parameterName the name of the parameter containing help information
           parameterDetailType the detail type of the parameter containing help information
     *
     * @param helpDefinition The helpDefinition to set.
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }    

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DataObjectEntry for " + getObjectClass();
    }

	/**
	 * @return the mustOccurConstraints
	 */
	public List<MustOccurConstraint> getMustOccurConstraints() {
		return this.mustOccurConstraints;
	}

	/**
	 * @param mustOccurConstraints the mustOccurConstraints to set
	 */
	public void setMustOccurConstraints(
			List<MustOccurConstraint> mustOccurConstraints) {
		this.mustOccurConstraints = mustOccurConstraints;
	}

    /**
	 * @return the titleAttribute
	 */
	public String getTitleAttribute() {
		return this.titleAttribute;
	}

    /**
	The titleAttribute element is the name of the attribute that
     will be used as an inquiry field when the lookup search results
     fields are displayed.

     For some business objects, there is no obvious field to serve
     as the inquiry field. in that case a special field may be required
     for inquiry purposes.
     */
	public void setTitleAttribute(String titleAttribute) {
		this.titleAttribute = titleAttribute;
	}

    /**
	 * @return the primaryKeys
	 */
	public List<String> getPrimaryKeys() {
		return this.primaryKeys;
	}

	/**
	 * @param primaryKeys the primaryKeys to set
	 */
	public void setPrimaryKeys(List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
	
	public Class<? extends Exporter> getExporterClass() {
        return this.exporterClass;
    }

    public void setExporterClass(Class<? extends Exporter> exporterClass) {
        this.exporterClass = exporterClass;
    }
}
