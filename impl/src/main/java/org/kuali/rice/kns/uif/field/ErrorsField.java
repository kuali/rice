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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.ContainerBase;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;

/**
 * Field that displays error messages or counts
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ErrorsField extends FieldBase {
	private static final long serialVersionUID = 780940788435330077L;

	private List<String> additionalKeysToMatch;
	private String errorTitle;
	private String warningTitle;
	private String infoTitle;
	
	private Component parentComponent;

	private boolean displayErrorTitle;
	private boolean displayWarningTitle;
	private boolean displayInfoTitle;
	private boolean displayFieldLabelWithMessages;
	
	private boolean displayNestedMessages;
	private boolean highlightOnError;
	private boolean combineMessages;
	private boolean allowMessageRepeat;
	
	private boolean displayMessages;
	private boolean displayErrorMessages;
	private boolean displayInfoMessages;
	private boolean displayWarningMessages;
	private boolean displayCounts;
	private boolean alternateContainer;
	
	private List<String> errors;
	private List<String> warnings;
	private List<String> infos;
	
	private int errorCount;
	private int warningCount;
	private int infoCount;
	
	private int tempCount;
	
	//not used
	private boolean displayLockMessages;
	

	public ErrorsField() {
	}
	
	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#performFinalize(org.kuali.rice.kns.uif.container.View, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void performFinalize(View view, Object model) {
		super.performFinalize(view, model);
		
		List<String> masterKeyList = getKeys();
		errors = new ArrayList<String>();
		warnings = new ArrayList<String>();
		infos = new ArrayList<String>();
		errorCount = 0;
		warningCount = 0;
		infoCount = 0;
		
		if(displayMessages){
			if(displayNestedMessages){
				this.addNestedKeys(masterKeyList, parentComponent);
			}
			MessageMap messageMap = GlobalVariables.getMessageMap();
			for(String key: masterKeyList){
				if(displayErrorMessages){
					errors.addAll(getMessages(view, key, messageMap.getErrorMessagesForProperty(key)));
					errorCount = errorCount + tempCount;
				}
				if(displayWarningMessages){
					warnings.addAll(getMessages(view, key, messageMap.getWarningMessagesForProperty(key)));
					warningCount = warningCount + tempCount;
				}
				if(displayInfoMessages){
					infos.addAll(getMessages(view, key, messageMap.getInfoMessagesForProperty(key)));
					infoCount = infoCount + tempCount;
				}
			}
		}
		
		if(errorCount + warningCount + infoCount == 0){
			this.setStyle("display: none;");
		}
		else{
			this.setStyle("display: visible");
		}
	}
	
	private List<String> getMessages(View view, String key, List<ErrorMessage> errorList){
		List<String> result = new ArrayList<String>();
		tempCount = 0;
		if(errorList != null && StringUtils.isNotBlank(key)){
			KualiConfigurationService configService = KNSServiceLocator.getKualiConfigurationService();
			String comboMessage = "";
			String label = "";
			for(ErrorMessage e: errorList){
				tempCount++;
				String message = configService.getPropertyString(e.getErrorKey());
				if(e.getMessageParameters() != null){
					message = MessageFormat.format(message, (Object[])e.getMessageParameters());
				}
				if(displayFieldLabelWithMessages){
					AttributeField field = view.getViewIndex().getAttributeFieldByPath(key);
					if(field != null && field.getLabel() != null){
						label = field.getLabel();
					}
				}
				//adding them to combo string instead of the list
				if(combineMessages){
					if(comboMessage.isEmpty()){
						comboMessage = message;
					}
					else{
						comboMessage = comboMessage + ", " + message;
					}
				}
				else{
					//add it directly to the list - non combined messages
					if(StringUtils.isNotEmpty(label)){
						result.add(label + " - " + message);
					}
					else{
						result.add(message);
					}
					
				}
			}
			//add the single combo string to the returned list
			//combineMessages will also be checked in the template to further combine them
			if(StringUtils.isNotEmpty(comboMessage)){
				if(StringUtils.isNotEmpty(label)){
					result.add(label + " - " + comboMessage);
				}
				else{
					result.add(comboMessage);
				}
			}
		}
		return result;
	}
	
	protected List<String> getKeys(){
		List<String> keyList = new ArrayList<String>();
		if(additionalKeysToMatch != null){
			keyList.addAll(additionalKeysToMatch);
		}
		if(StringUtils.isNotBlank(parentComponent.getId())){
			keyList.add(parentComponent.getId());
		}
		if(parentComponent instanceof AttributeField){
			if(((AttributeField) parentComponent).getBindingInfo() != null && 
					StringUtils.isNotEmpty(((AttributeField) parentComponent).getBindingInfo().getBindingPath())){
				keyList.add(((AttributeField) parentComponent).getBindingInfo().getBindingPath());
			}
		}
		//Will there be additional components to check beyond AttributeField?
		
		return keyList;
	}
	
	private void addNestedKeys(List<String> keyList, Component component){
		for(Component c: component.getNestedComponents()){
			ErrorsField ef = null;
			if(c instanceof AttributeField){
				ef = ((AttributeField) c).getErrorsField();
			}
			else if(c instanceof ContainerBase){
				ef = ((ContainerBase) c).getErrorsField();
			}
			if(ef != null){
				if(!allowMessageRepeat){
					ef.setDisplayMessages(false);
				}
				keyList.addAll(ef.getKeys());
				addNestedKeys(keyList, c);
			}
		}
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
		return this.displayCounts;
	}

	public void setDisplayErrorCount(boolean displayErrorCount) {
		this.displayCounts = displayErrorCount;
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

	/**
	 * @param parentComponent the parentComponent to set
	 */
	public void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	/**
	 * @return the additionalKeysToMatch
	 */
	public List<String> getAdditionalKeysToMatch() {
		return this.additionalKeysToMatch;
	}

	/**
	 * @param additionalKeysToMatch the additionalKeysToMatch to set
	 */
	public void setAdditionalKeysToMatch(List<String> additionalKeysToMatch) {
		this.additionalKeysToMatch = additionalKeysToMatch;
	}

	/**
	 * @return the displayErrorTitle
	 */
	public boolean isDisplayErrorTitle() {
		return this.displayErrorTitle;
	}

	/**
	 * @param displayErrorTitle the displayErrorTitle to set
	 */
	public void setDisplayErrorTitle(boolean displayErrorTitle) {
		this.displayErrorTitle = displayErrorTitle;
	}

	/**
	 * @return the displayWarningTitle
	 */
	public boolean isDisplayWarningTitle() {
		return this.displayWarningTitle;
	}

	/**
	 * @param displayWarningTitle the displayWarningTitle to set
	 */
	public void setDisplayWarningTitle(boolean displayWarningTitle) {
		this.displayWarningTitle = displayWarningTitle;
	}

	/**
	 * @return the displayInfoTitle
	 */
	public boolean isDisplayInfoTitle() {
		return this.displayInfoTitle;
	}

	/**
	 * @param displayInfoTitle the displayInfoTitle to set
	 */
	public void setDisplayInfoTitle(boolean displayInfoTitle) {
		this.displayInfoTitle = displayInfoTitle;
	}

	/**
	 * @return the displayFieldLabelWithMessages
	 */
	public boolean isDisplayFieldLabelWithMessages() {
		return this.displayFieldLabelWithMessages;
	}

	/**
	 * @param displayFieldLabelWithMessages the displayFieldLabelWithMessages to set
	 */
	public void setDisplayFieldLabelWithMessages(
			boolean displayFieldLabelWithMessages) {
		this.displayFieldLabelWithMessages = displayFieldLabelWithMessages;
	}

	/**
	 * @return the displayMessages
	 */
	public boolean isDisplayMessages() {
		return this.displayMessages;
	}

	/**
	 * @param displayMessages the displayMessages to set
	 */
	public void setDisplayMessages(boolean displayMessages) {
		this.displayMessages = displayMessages;
	}

	/**
	 * @return the displayNestedMessages
	 */
	public boolean isDisplayNestedMessages() {
		return this.displayNestedMessages;
	}

	/**
	 * @param displayNestedMessages the displayNestedMessages to set
	 */
	public void setDisplayNestedMessages(boolean displayNestedMessages) {
		this.displayNestedMessages = displayNestedMessages;
	}

	/**
	 * @return the highlightOnError
	 */
	public boolean isHighlightOnError() {
		return this.highlightOnError;
	}

	/**
	 * @param highlightOnError the highlightOnError to set
	 */
	public void setHighlightOnError(boolean highlightOnError) {
		this.highlightOnError = highlightOnError;
	}

	/**
	 * @return the combineMessages
	 */
	public boolean isCombineMessages() {
		return this.combineMessages;
	}

	/**
	 * @param combineMessages the combineMessages to set
	 */
	public void setCombineMessages(boolean combineMessages) {
		this.combineMessages = combineMessages;
	}

	/**
	 * @return the allowMessageRepeat
	 */
	public boolean isAllowMessageRepeat() {
		return this.allowMessageRepeat;
	}

	/**
	 * @param allowMessageRepeat the allowMessageRepeat to set
	 */
	public void setAllowMessageRepeat(boolean allowMessageRepeat) {
		this.allowMessageRepeat = allowMessageRepeat;
	}

	/**
	 * @return the displayCounts
	 */
	public boolean isDisplayCounts() {
		return this.displayCounts;
	}

	/**
	 * @param displayCounts the displayCounts to set
	 */
	public void setDisplayCounts(boolean displayCounts) {
		this.displayCounts = displayCounts;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return this.errors;
	}

	/**
	 * @return the warnings
	 */
	public List<String> getWarnings() {
		return this.warnings;
	}

	/**
	 * @return the infos
	 */
	public List<String> getInfos() {
		return this.infos;
	}

	/**
	 * @return the errorCount
	 */
	public int getErrorCount() {
		return this.errorCount;
	}

	/**
	 * @return the warningCount
	 */
	public int getWarningCount() {
		return this.warningCount;
	}

	/**
	 * @return the infoCount
	 */
	public int getInfoCount() {
		return this.infoCount;
	}

	/**
	 * @return the alternateContainer
	 */
	public boolean isAlternateContainer() {
		return this.alternateContainer;
	}

	/**
	 * @param alternateContainer the alternateContainer to set
	 */
	public void setAlternateContainer(boolean alternateContainer) {
		this.alternateContainer = alternateContainer;
	}

	
}
