/**
 * Copyright 2010 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.rice.kns.datadictionary.validator;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationResultInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	public enum ErrorLevel {
        OK(0), WARN(1), ERROR(2);

        int level;

        private ErrorLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
        
        public static ErrorLevel min(ErrorLevel e1, ErrorLevel e2) {
        	return e1.ordinal() < e2.ordinal() ? e1 : e2;
        }
        public static ErrorLevel max(ErrorLevel e1, ErrorLevel e2) {
        	return e1.ordinal() > e2.ordinal() ? e1 : e2;
        }
    }	

	
	@XmlElement
	protected String element;
	
	@XmlElement
	protected ErrorLevel level = ErrorLevel.OK;

	@XmlElement
	protected String message;
	
	private String entryName;
	private String attributeName;
	private String errorKey;
	private String[] errorParameters;
	
	
	public ValidationResultInfo() {
		super();
		this.level = ErrorLevel.OK;
	}
	
	public ValidationResultInfo(String entryName, String attributeName) {
		this();
		this.entryName = entryName;
		this.attributeName = attributeName;
	}
	
	public ValidationResultInfo(String entryName, String attributeName, String errorKey, String... errorParameters) {
		this(entryName, attributeName);
		this.errorKey = errorKey;
		this.errorParameters = errorParameters;
	}
	
	/**
	 * @return the level
	 */
	public ErrorLevel getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}
	
    /**
     * Returns the ValidationResult's error level
     *
     * @return
     */
    public ErrorLevel getErrorLevel() {
        return level;
    }

    /**
     * Convenience method. Adds a message with an error level of WARN
     *
     * @param message
     *            the message to add
     */
    public void setWarning(String message) {
    	this.level = ErrorLevel.WARN;
    	this.message = message;
    }

    /**
     * Convenience method. Adds a message with an error level of ERROR
     *
     * @param message
     *            the message to add
     */
//    public void setError(String message) {
//    	this.level = ErrorLevel.ERROR;
//    	this.message = message;
//    }
    
    public void setError(String errorKey, String... errorParameters) {
    	this.level = ErrorLevel.ERROR;
    	this.errorKey = errorKey;
    	this.errorParameters = errorParameters;
    }

    /**
     * Convenience method. Returns true if getErrorLevel() == ErrorLevel.OK
     *
     * @return true if getErrorLevel() == ErrorLevel.OK
     */
    public boolean isOk() {
        return getErrorLevel() == ErrorLevel.OK;
    }

    /**
     * Convenience method. Returns true if getErrorLevel() == ErrorLevel.WARN
     *
     * @return true if getErrorLevel() == ErrorLevel.WARN
     */
    public boolean isWarn() {
        return getErrorLevel() == ErrorLevel.WARN;
    }

    /**
     * Convenience method. Returns true if getErrorLevel() == ErrorLevel.ERROR
     *
     * @return true if getErrorLevel() == ErrorLevel.ERROR
     */
    public boolean isError() {
        return getErrorLevel() == ErrorLevel.ERROR;
    }

    public String toString(){
    	return "Entry: [" + entryName + "] Attribute: [" + attributeName + "] - " + errorKey + " data=[" + errorParameters + "]";
    }

	/**
	 * @return the entryName
	 */
	public String getEntryName() {
		return this.entryName;
	}

	/**
	 * @param entryName the entryName to set
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return this.attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the errorKey
	 */
	public String getErrorKey() {
		return this.errorKey;
	}

	/**
	 * @param errorKey the errorKey to set
	 */
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * @return the errorParameters
	 */
	public String[] getErrorParameters() {
		return this.errorParameters;
	}

	/**
	 * @param errorParameters the errorParameters to set
	 */
	public void setErrorParameters(String[] errorParameters) {
		this.errorParameters = errorParameters;
	}
	
}
