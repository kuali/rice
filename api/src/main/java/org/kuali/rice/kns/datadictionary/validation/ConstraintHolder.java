package org.kuali.rice.kns.datadictionary.validation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConstraintHolder implements Validatable {
	public static final String UNBOUNDED = "unbounded";
	public static final String SINGLE = "1";
	
	private String name;
	private String label;
	private String childEntryName;
	private DataType dataType;
	
	// Constraints
	@XmlElement
	protected boolean serverSide;
	@XmlElement
	protected String customValidatorClass;
	@XmlElement
	protected String locale; // What is locale for?
	@XmlElement
	protected String exclusiveMin;
	@XmlElement
	protected String inclusiveMax;
	@XmlElement
	protected Integer minLength;
	@XmlElement
	protected Integer maxLength;	
	@XmlElement
	protected ValidCharsConstraint validChars;	
	@XmlElement
	protected Integer minOccurs;
	@XmlElement
	protected Integer maxOccurs;
	
	@XmlElement
    protected CaseConstraint caseConstraint;
	
	@XmlElement
    protected List<DependencyConstraint> requireConstraint;

	@XmlElement
	protected List<MustOccurConstraint> occursConstraint;

	// LookupConstraints
	protected LookupConstraint lookupDefinition;// If the user wants to match
	// against two searches, that
	// search must be defined as
	// well
	protected String lookupContextPath;// The idea here is to reuse a

	// lookupConstraint with fields relative
	// to the contextPath. We might not need
	// this
	public boolean isServerSide() {
		return serverSide;
	}

	public void setServerSide(boolean serverSide) {
		this.serverSide = serverSide;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getExclusiveMin() {
		return exclusiveMin;
	}

	public void setExclusiveMin(String exclusiveMin) {
		this.exclusiveMin = exclusiveMin;
	}

	public String getInclusiveMax() {
		return inclusiveMax;
	}

	public void setInclusiveMax(String inclusiveMax) {
		this.inclusiveMax = inclusiveMax;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public ValidCharsConstraint getValidChars() {
		return validChars;
	}

	public void setValidChars(ValidCharsConstraint validChars) {
		this.validChars = validChars;
	}

	public Integer getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(Integer minOccurs) {
		this.minOccurs = minOccurs;
	}

	public Integer getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public List<DependencyConstraint> getRequireConstraint() {
        if(null == requireConstraint) {
            this.requireConstraint = new ArrayList<DependencyConstraint>();
        }

	    return requireConstraint;
	}

	public void setRequireConstraint(List<DependencyConstraint> requireConstraint) {
	    this.requireConstraint = requireConstraint;
	}

    public CaseConstraint getCaseConstraint() {
        return caseConstraint;
    }

    public void setCaseConstraint(CaseConstraint caseConstraint) {
        this.caseConstraint = caseConstraint;
    }

    public List<MustOccurConstraint> getOccursConstraint() {
        if(null == occursConstraint) {
            this.occursConstraint = new ArrayList<MustOccurConstraint>();
        }
		return occursConstraint;
	}

	public void setOccursConstraint(List<MustOccurConstraint> occursConstraint) {
		this.occursConstraint = occursConstraint;
	}

	public LookupConstraint getLookupDefinition() {
		return lookupDefinition;
	}

	public void setLookupDefinition(LookupConstraint lookupDefinition) {
		this.lookupDefinition = lookupDefinition;
	}

	public String getLookupContextPath() {
		return lookupContextPath;
	}

	public void setLookupContextPath(String lookupContextPath) {
		this.lookupContextPath = lookupContextPath;
	}

	public String getCustomValidatorClass() {
		return customValidatorClass;
	}

	public void setCustomValidatorClass(String customValidatorClass) {
		this.customValidatorClass = customValidatorClass;
	}
	
	public Boolean isRequired() {
		return Boolean.valueOf(getMinOccurs() != null && getMinOccurs().intValue() > 0);
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
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the childEntryName
	 */
	public String getChildEntryName() {
		return this.childEntryName;
	}

	/**
	 * @param childEntryName the childEntryName to set
	 */
	public void setChildEntryName(String childEntryName) {
		this.childEntryName = childEntryName;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return this.dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
}
