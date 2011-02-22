package org.kuali.rice.kns.datadictionary.validation.constraint;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class is a direct copy of one that was in Kuali Student. Look up constraints are currently not implemented. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LookupConstraintParamMapping {
	@XmlElement
    protected String fieldPath;
	@XmlElement
	protected String paramKey;
	@XmlElement
	protected List<String> defaultValueList;
	@XmlElement
	protected String defaultValueString;

	public String getFieldPath() {
		return fieldPath;
	}

	public void setFieldPath(String fieldPath) {
		this.fieldPath = fieldPath;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public List<String> getDefaultValueList() {
		return defaultValueList;
	}

	public void setDefaultValueList(List<String> defaultValueList) {
		this.defaultValueList = defaultValueList;
	}

	public String getDefaultValueString() {
		return defaultValueString;
	}

	public void setDefaultValueString(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}
}