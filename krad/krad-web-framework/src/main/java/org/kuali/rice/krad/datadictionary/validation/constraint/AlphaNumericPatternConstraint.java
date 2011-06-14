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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;

/**
 * A ValidCharactersConstraint based on AlphaNumericValidationPattern.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AlphaNumericPatternConstraint extends ValidCharactersPatternConstraint{
    protected boolean allowWhitespace = false;
    protected boolean allowUnderscore = false;
    protected boolean allowPeriod = false;
    protected boolean allowParenthesis = false;
    protected boolean allowDollar = false;
    protected boolean allowForwardSlash = false;
    protected boolean lowerCase = false;
    
    /**
     * A label key is auto generated for this bean if none is set.  This generated message can be overridden
     * through setLabelKey, but the generated message should cover most cases.
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
    	if(StringUtils.isEmpty(labelKey)){
	    	StringBuilder key = new StringBuilder("");
	    	if(lowerCase){
	    		key.append("alphanumericPatternLowerCase,");
	    	}
	    	else{
	    		key.append("alphanumericPattern,");
	    	}
	        if (allowWhitespace) {
	        	key.append("whitespace,");
	        }
	        if (allowUnderscore) {
	            key.append("underscore,");
	        }
	        if (allowPeriod) {
	            key.append("period,");
	        }
	        if(allowParenthesis) {
	        	key.append("parenthesis,");
	        }
	        if(allowDollar) {
	        	key.append("dollar,");
	        }
	        if(allowForwardSlash) {
	        	key.append("forwardSlash");
	        }
	        
	    	return key.toString();
    	}
    	return labelKey;
    }
    
    /**
     * The labelKey should only be set if the auto generated message by this class needs to be overridden
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#setLabelKey(java.lang.String)
     */
    @Override
    public void setLabelKey(String labelKey) {
    	super.setLabelKey(labelKey);
    }
    
	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersPatternConstraint#getRegexString()
	 */
	@Override
	protected String getRegexString() {
		//Exact same logic is used here as old KS AlphaNumericValidationPattern for server side value
    	StringBuilder regexString = new StringBuilder("[A-Za-z0-9");
    	/*
    	 * This check must be first because we are removing the base 'A-Z' if lowerCase == true
    	 */
    	if(lowerCase){
    		regexString = new StringBuilder("[a-z0-9");
    	}

        if (allowWhitespace) {
            regexString.append("\\s");
        }
        if (allowUnderscore) {
            regexString.append("_");
        }
        if (allowPeriod) {
            regexString.append(".");
        }
        if(allowParenthesis) {
        	regexString.append("(");
        	regexString.append(")");
        }
        if(allowDollar) {
        	regexString.append("$");
        }
        if(allowForwardSlash) {
        	regexString.append("/");
        }
        regexString.append("]");

        return regexString.toString();
	}

	/**
	 * @return the allowWhitespace
	 */
	public boolean isAllowWhitespace() {
		return this.allowWhitespace;
	}
	/**
	 * @param allowWhitespace the allowWhitespace to set
	 */
	public void setAllowWhitespace(boolean allowWhitespace) {
		this.allowWhitespace = allowWhitespace;
	}
	/**
	 * @return the allowUnderscore
	 */
	public boolean isAllowUnderscore() {
		return this.allowUnderscore;
	}
	/**
	 * @param allowUnderscore the allowUnderscore to set
	 */
	public void setAllowUnderscore(boolean allowUnderscore) {
		this.allowUnderscore = allowUnderscore;
	}
	/**
	 * @return the allowPeriod
	 */
	public boolean isAllowPeriod() {
		return this.allowPeriod;
	}
	/**
	 * @param allowPeriod the allowPeriod to set
	 */
	public void setAllowPeriod(boolean allowPeriod) {
		this.allowPeriod = allowPeriod;
	}
	/**
	 * @return the allowParenthesis
	 */
	public boolean isAllowParenthesis() {
		return this.allowParenthesis;
	}
	/**
	 * @param allowParenthesis the allowParenthesis to set
	 */
	public void setAllowParenthesis(boolean allowParenthesis) {
		this.allowParenthesis = allowParenthesis;
	}
	/**
	 * @return the allowDollar
	 */
	public boolean isAllowDollar() {
		return this.allowDollar;
	}
	/**
	 * @param allowDollar the allowDollar to set
	 */
	public void setAllowDollar(boolean allowDollar) {
		this.allowDollar = allowDollar;
	}
	/**
	 * @return the allowForwardSlash
	 */
	public boolean isAllowForwardSlash() {
		return this.allowForwardSlash;
	}
	/**
	 * @param allowForwardSlash the allowForwardSlash to set
	 */
	public void setAllowForwardSlash(boolean allowForwardSlash) {
		this.allowForwardSlash = allowForwardSlash;
	}
	/**
	 * @return the lowerCase
	 */
	public boolean isLowerCase() {
		return this.lowerCase;
	}
	/**
	 * @param lowerCase the lowerCase to set
	 */
	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}



}
