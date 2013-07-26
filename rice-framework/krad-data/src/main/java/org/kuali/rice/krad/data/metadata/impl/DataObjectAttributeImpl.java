package org.kuali.rice.krad.data.metadata.impl;

import java.beans.PropertyEditor;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeSecurity;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

/**
 * Base implementation class for attributes on data object classes.
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

	protected String displayAttributeName;
	protected Boolean caseInsensitive;
	protected Boolean required;
	protected Boolean persisted;
	protected Long maxLength;
	protected Long minLength;
	protected String validCharactersConstraintBeanName;

	protected PropertyEditor propertyEditor;
	protected DataObjectAttributeSecurity attributeSecurity;
	protected KeyValuesFinder optionsFinder;
	protected DataType dataType = DataType.STRING;
	protected Class<?> type = String.class;
	
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

	public void setDisplayAttributeName(String displayAttributeName) {
		if (StringUtils.isBlank(displayAttributeName)) {
			displayAttributeName = null;
		}
		this.displayAttributeName = displayAttributeName;
	}

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

	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}
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

	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}
	@Override
	public DataObjectAttributeSecurity getAttributeSecurity() {
		if (attributeSecurity != null) {
			return attributeSecurity;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getAttributeSecurity();
		}
		return null;
	}
	public void setAttributeSecurity(DataObjectAttributeSecurity attributeSecurity) {
		this.attributeSecurity = attributeSecurity;
	}
	@Override
	public KeyValuesFinder getOptionsFinder() {
		if (optionsFinder != null) {
			return optionsFinder;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getOptionsFinder();
		}
		return null;
	}
	public void setOptionsFinder(KeyValuesFinder optionsFinder) {
		this.optionsFinder = optionsFinder;
	}
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
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

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
		if (attributeSecurity != null) {
			builder.append(", ").append("attributeSecurity=").append(attributeSecurity);
		}
		if (optionsFinder != null) {
			builder.append(", ").append("optionsFinder=").append(optionsFinder);
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

	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public DataObjectAttribute getEmbeddedAttribute() {
		return embeddedAttribute;
	}

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

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public String getValidCharactersConstraintBeanName() {
		if (validCharactersConstraintBeanName != null) {
			return validCharactersConstraintBeanName;
		}
		if (embeddedAttribute != null) {
			return embeddedAttribute.getValidCharactersConstraintBeanName();
		}
		return validCharactersConstraintBeanName;
	}

	public void setValidCharactersConstraintBeanName(String validCharactersConstraintBeanName) {
		this.validCharactersConstraintBeanName = validCharactersConstraintBeanName;
	}

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

	public void setOwningType(Class<?> owningType) {
		this.owningType = owningType;
	}

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

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> javaType) {
		this.type = javaType;
	}

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

	public void setInheritedFromType(Class<?> inheritedFromType) {
		this.inheritedFromType = inheritedFromType;
	}

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

	public void setInheritedFromAttributeName(String inheritedFromAttributeName) {
		this.inheritedFromAttributeName = inheritedFromAttributeName;
	}

	@Override
	public boolean isInherited() {
		return getInheritedFromAttributeName() != null;
	}

	@Override
	public DataObjectAttribute getOriginalDataObjectAttribute() {
		if (embeddedAttribute == null) {
			return this;
		}
		return embeddedAttribute.getOriginalDataObjectAttribute();
	}

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

	public void setMinLength(Long minLength) {
		this.minLength = minLength;
	}
}
