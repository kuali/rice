/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.uif.field;


/**
 * Field that displays error messages or counts
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ErrorsField extends FieldBase {
	private static final long serialVersionUID = 780940788435330077L;

	private String keyMatch;
	private String errorStyleClass;
	private String warningStyleClass;
	private String infoStyleClass;
	private String errorTitle;
	private String warningTitle;
	private String infoTitle;

	private boolean displayErrorCount;
	private boolean displayErrorMessages;
	private boolean displayInfoMessages;
	private boolean displayLockMessages;
	private boolean displayWarningMessages;
	

	public ErrorsField() {

	}

	public String getKeyMatch() {
		return this.keyMatch;
	}

	public void setKeyMatch(String keyMatch) {
		this.keyMatch = keyMatch;
	}

	public String getErrorStyleClass() {
		return this.errorStyleClass;
	}

	public void setErrorStyleClass(String errorStyleClass) {
		this.errorStyleClass = errorStyleClass;
	}

	public String getWarningStyleClass() {
		return this.warningStyleClass;
	}

	public void setWarningStyleClass(String warningStyleClass) {
		this.warningStyleClass = warningStyleClass;
	}

	public String getInfoStyleClass() {
		return this.infoStyleClass;
	}

	public void setInfoStyleClass(String infoStyleClass) {
		this.infoStyleClass = infoStyleClass;
	}

	public String getErrorTitle() {
		return this.errorTitle;
	}

	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	public String getWarningTitle() {
		return this.warningTitle;
	}

	public void setWarningTitle(String warningTitle) {
		this.warningTitle = warningTitle;
	}

	public String getInfoTitle() {
		return this.infoTitle;
	}

	public void setInfoTitle(String infoTitle) {
		this.infoTitle = infoTitle;
	}

	public boolean isDisplayErrorCount() {
		return this.displayErrorCount;
	}

	public void setDisplayErrorCount(boolean displayErrorCount) {
		this.displayErrorCount = displayErrorCount;
	}

	public boolean isDisplayErrorMessages() {
		return this.displayErrorMessages;
	}

	public void setDisplayErrorMessages(boolean displayErrorMessages) {
		this.displayErrorMessages = displayErrorMessages;
	}

	public boolean isDisplayInfoMessages() {
		return this.displayInfoMessages;
	}

	public void setDisplayInfoMessages(boolean displayInfoMessages) {
		this.displayInfoMessages = displayInfoMessages;
	}

	public boolean isDisplayLockMessages() {
		return this.displayLockMessages;
	}

	public void setDisplayLockMessages(boolean displayLockMessages) {
		this.displayLockMessages = displayLockMessages;
	}

	public boolean isDisplayWarningMessages() {
		return this.displayWarningMessages;
	}

	public void setDisplayWarningMessages(boolean displayWarningMessages) {
		this.displayWarningMessages = displayWarningMessages;
	}

}
