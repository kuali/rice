package org.kuali.rice.kns.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Constraint extends BaseConstraint {
	public static final String UNBOUNDED = "unbounded";
	public static final String SINGLE = "1";
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
	protected String maxLength;	
	@XmlElement
	protected ValidCharsConstraint validChars;	
	@XmlElement
	protected Integer minOccurs;
	@XmlElement
	protected String maxOccurs;
	
	@XmlElement
    protected CaseConstraint caseConstraint;
	
	@XmlElement
    protected List<RequiredConstraint> requireConstraint;

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

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
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

	public String getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public List<RequiredConstraint> getRequireConstraint() {
        if(null == requireConstraint) {
            this.requireConstraint = new ArrayList<RequiredConstraint>();
        }

	    return requireConstraint;
	}

	public void setRequireConstraint(List<RequiredConstraint> requireConstraint) {
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
}
