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
package edu.iu.uis.eden.edl.components;

/**
 * Convenience class for representing a request param with it's value and any associated errors.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MatchingParam {
	
	private String paramName;
	private String paramValue;
	private Boolean error = Boolean.FALSE;
	private String errorMessage;
	
	public MatchingParam() {
	}
	
	public MatchingParam(String paramName, String paramValue, Boolean error, String errorMessage) {
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.error = error;
		this.errorMessage = errorMessage;
	}
	
	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
