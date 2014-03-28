/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.metadata.impl;

import com.google.common.annotations.Beta;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.Set;

/**
 * Base implementation class for attribute metadata for data object classes.
 *
 * <p>
 * This implementation supports "chaining" for most attributes. That is, if the value for a property is defined locally,
 * it will me used. If unset (null) it will, if there is an {@link #embeddedAttribute}, request it from that
 * DataObjectAttribute. (This could be a recursive operation if multiple metadata providers are chained.)
 * </p>
 * <p>
 * If the value is unset and there is no embedded attribute, most methods will return a non-null default value.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectAttributeImpl extends MetadataCommonBase implements DataObjectAttributeInternal {
	private static final long serialVersionUID = -5241499559388935579L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataObjectAttributeImpl.class);

	protected DataObjectAttribute embeddedAttribute;

	protected Class<?> owningType;

	// These are temporary placeholders for the source property from which a property was inherited when
	// it "lives" on a related object. E.g., accountType.codeAndDescription
	// After all metadata has been imported, this information will be used to "embed" the parent
	// DataObjectAttribute so that properties (E.g., label) are inherited from there
	protected Class<?> inheritedFromType;
	protected String inheritedFromAttributeName;
	protected String inheritedFromParentAttributeName;
	protected String displayAttributeName;
	protected Boolean caseInsensitive;
	protected Boolean forceUppercase;
	protected Boolean required;
	protected Boolean persisted;
	protected Boolean sensitive;
	protected Long maxLength;
	protected Long minLength;
	protected String validCharactersConstraintBeanName;

	protected PropertyEditor propertyEditor;
	protected KeyValuesFinder validValues;
	protected DataType dataType;
	protected Class<?> type;
	
	protected Set<UifDisplayHint> displayHints;

    /**
     * {@inheritDoc}
     */
	@Override
	public String getDisplayAttributeName() {
		if (displayAttributeName != null) {
			return displayAttributeName;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getDisplayAttributeName();
		}
		return getName();
	}

    /**
    * Sets the attribute display name.
    *
    * @param displayAttributeName the attribute name.
    */
	public void setDisplayAttributeName(String displayAttributeName) {
		if (StringUtils.isBlank(displayAttributeName)) {
			displayAttributeName = null;
		}
		this.displayAttributeName = displayAttributeName;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isCaseInsensitive() {
		if (caseInsensitive != null) {
			return caseInsensitive;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.isCaseInsensitive();
		}
		return false;
	}

    /**
    * Sets value that determines whether attribute is case insensitive.
    *
    * @param caseInsensitive whether attribute is case insensitive.
    */
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isForceUppercase() {
		if (forceUppercase != null) {
			return forceUppercase;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.isForceUppercase();
		}
		return false;
	}


    /**
    * Determines if attribute should be forced to upper case.
    *
    * @param forceUppercase whether attribute should be forced to upper.
    */
	public void setForceUppercase(boolean forceUppercase) {
		this.forceUppercase = forceUppercase;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public PropertyEditor getPropertyEditor() {
		if (propertyEditor != null) {
			return propertyEditor;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getPropertyEditor();
		}
		return null;
	}

    /**
     * Sets the property editor used when loading data.
     *
     * @param propertyEditor determines formats when loading data.
     */
	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public KeyValuesFinder getValidValues() {
		if (validValues != null) {
			return validValues;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getValidValues();
		}
		return null;
	}

    /**
    * Sets keyValueFinder used for dropdown.
    *
    * @param validValues dropdown keyValueFinder.
    */
	public void setValidValues(KeyValuesFinder validValues) {
		this.validValues = validValues;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public DataType getDataType() {
		if (dataType != null) {
			return dataType;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getDataType();
		}
		return DataType.STRING;
	}

    /**
    * Sets KRAD data type.
    *
    * @param dataType KRAD derived data type.
    */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataObjectAttribute [");
		builder.append("name=").append(name);
		if (label != null) {
			builder.append(", ").append("label=").append(label);
		}
		if (backingObjectName != null) {
			builder.append(", ").append("backingObjectName=").append(backingObjectName);
		}
		if (dataType != null) {
			builder.append(", ").append("dataType=").append(dataType);
		}
		if (type != null) {
			builder.append(", ").append("type=").append(type.getName());
		}
		if (caseInsensitive != null) {
			builder.append(", ").append("caseInsensitive=").append(caseInsensitive);
		}
		if (propertyEditor != null) {
			builder.append(", ").append("propertyEditor=").append(propertyEditor);
		}
		if (sensitive != null && sensitive) {
			builder.append(", ").append("sensitive=").append(sensitive);
		}
		if (validValues != null) {
			builder.append(", ").append("validValues=").append(validValues);
		}
		if (inheritedFromType != null) {
			builder.append(", ").append("inheritedFromType=").append(inheritedFromType);
		}
		if (inheritedFromAttributeName != null) {
			builder.append(", ").append("inheritedFromAttributeName=").append(inheritedFromAttributeName);
		}
		builder.append(", ").append("mergeAction=").append(mergeAction);
		builder.append("]");
		return builder.toString();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Long getMaxLength() {
		if (maxLength != null) {
			return maxLength;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getMaxLength();
		}
		return null;
	}

    /**
    * Sets max length of attribute.
    *
    * @param maxLength attribute max length.
    */
	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public DataObjectAttribute getEmbeddedAttribute() {
		return embeddedAttribute;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void setEmbeddedAttribute(DataObjectAttribute embeddedAttribute) {
		// protect against embedding itself
		if (embeddedAttribute == this) {
			LOG.warn(
					"ERROR!!!!  Attempt to embed a DataObjectAttribute into itself.  You must really want a stack overflow!  Trace: ",
					new Throwable("Throw-away Throwable for tracing purposes."));
			return;
		}
		this.embeddedAttribute = embeddedAttribute;
		setEmbeddedCommonMetadata(embeddedAttribute);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isRequired() {
		if (required != null) {
			return required;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.isRequired();
		}
		return false;
	}

    /**
    * Set whether attribute is required.
    *
    * @param required attribute required flag.
    */
	public void setRequired(boolean required) {
		this.required = required;
	}

    /**
     * {@inheritDoc}
     */
	@Override
    @Beta
	public String getValidCharactersConstraintBeanName() {
		if (validCharactersConstraintBeanName != null) {
			return validCharactersConstraintBeanName;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getValidCharactersConstraintBeanName();
		}
		return validCharactersConstraintBeanName;
	}

    /**
     * BETA: Sets valid character constraint bean name.
     *
     * @param validCharactersConstraintBeanName character constraint bean name.
     */
    @Beta
	public void setValidCharactersConstraintBeanName(String validCharactersConstraintBeanName) {
		this.validCharactersConstraintBeanName = validCharactersConstraintBeanName;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Class<?> getOwningType() {
		if (owningType != null) {
			return owningType;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getOwningType();
		}
		return null;
	}

    /**
    * Sets the data object type to which this attribute belongs.
    *
    * @param owningType data object type to which this attribute belongs.
    */
	public void setOwningType(Class<?> owningType) {
		this.owningType = owningType;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isPersisted() {
		if (persisted != null) {
			return persisted;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.isPersisted();
		}
		return true;
	}

    /**
    * Sets flag whether object is persisted.
    *
    * @param persisted flag whether object is persisted.
    */
	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

    /**
    * Determines type of class.
    *
    */
	public Class<?> getType() {
		if (type != null) {
            return type;
        }
        return String.class;
	}

    /**
    * Sets unknown class in order to determine type.
    *
    * @param javaType unknown class.
    */
	public void setType(Class<?> javaType) {
		this.type = javaType;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Class<?> getInheritedFromType() {
		if (inheritedFromType != null) {
			return inheritedFromType;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getInheritedFromType();
		}
		return null;
	}

    /**
    * Sets unknown class to determine if inherited.
    *
    * @param inheritedFromType unknown class.
    */
	public void setInheritedFromType(Class<?> inheritedFromType) {
		this.inheritedFromType = inheritedFromType;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getInheritedFromAttributeName() {
		if (inheritedFromAttributeName != null) {
			return inheritedFromAttributeName;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getInheritedFromAttributeName();
		}
		return null;
	}

    /**
    * Sets data object name to determine if inherited.
    *
    * @param inheritedFromAttributeName name of attribute.
    */
	public void setInheritedFromAttributeName(String inheritedFromAttributeName) {
		this.inheritedFromAttributeName = inheritedFromAttributeName;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getInheritedFromParentAttributeName() {
		if (inheritedFromParentAttributeName != null) {
			return inheritedFromParentAttributeName;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getInheritedFromParentAttributeName();
		}
		return null;
	}

    /**
    * Sets parent data object name to determine if inherited.
    *
    * @param inheritedFromParentAttributeName name of attribute.
    */
	public void setInheritedFromParentAttributeName(String inheritedFromParentAttributeName) {
		this.inheritedFromParentAttributeName = inheritedFromParentAttributeName;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isInherited() {
		return getInheritedFromAttributeName() != null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public DataObjectAttribute getOriginalDataObjectAttribute() {
		if (embeddedAttribute == null) {
			return this;
		}
		return embeddedAttribute.getOriginalDataObjectAttribute();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Long getMinLength() {
		if (minLength != null) {
			return minLength;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getMinLength();
		}
		return null;
	}

    /**
    * Sets minimum length of attribute.
    *
    * @param minLength minimum length value.
    */
	public void setMinLength(Long minLength) {
		this.minLength = minLength;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isSensitive() {
		if (sensitive != null) {
			return sensitive;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.isSensitive();
		}
		return false;
	}

    /**
    * Sets whether sensitive.
    *
    * @param sensitive whether attribute is sensitive.
    */
	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    @Beta
	public Set<UifDisplayHint> getDisplayHints() {
		if (displayHints != null) {
			return displayHints;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getDisplayHints();
		}
		return Collections.emptySet();
	}

    /**
    * BETA: Sets UIF display hints.
    *
    * @param displayHints UIF display hints.
    */
    @Beta
	public void setDisplayHints(Set<UifDisplayHint> displayHints) {
		this.displayHints = displayHints;
	}
}
