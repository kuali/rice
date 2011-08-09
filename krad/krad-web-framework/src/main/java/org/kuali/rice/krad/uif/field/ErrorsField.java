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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.util.AutoPopulatingList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Field that displays error, warning, and info messages for the keys that are
 * matched. By default, an ErrorsField will match on id and bindingPath (if this
 * ErrorsField is for an AttributeField), but can be set to match on
 * additionalKeys and nested components keys (of the its parentComponent).
 * 
 * In addition, there are a variety of options which can be toggled to effect
 * the display of these messages during both client and server side validation
 * display. See documentation on each get method for more details on the effect
 * of each option.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ErrorsField extends FieldBase {
	private static final long serialVersionUID = 780940788435330077L;

	private List<String> additionalKeysToMatch;

	// Title variables
	private String errorTitle;
	private String warningTitle;
	private String infoTitle;

	private boolean displayErrorTitle;
	private boolean displayWarningTitle;
	private boolean displayInfoTitle;

	// Field variables
	private boolean highlightOnError;
	private boolean displayFieldErrorIcon;

	// Message construction variables
	private boolean displayFieldLabelWithMessages;
	private boolean combineMessages;

	// Message display flags
	private boolean displayNestedMessages;
	private boolean allowMessageRepeat;

	private boolean displayMessages;
	private boolean displayErrorMessages;
	private boolean displayInfoMessages;
	private boolean displayWarningMessages;
	private boolean displayCounts;
	private boolean alternateContainer;

	// Error messages
	private List<String> errors;
	private List<String> warnings;
	private List<String> infos;

	// Counts
	private int errorCount;
	private int warningCount;
	private int infoCount;

	// internal
	private int tempCount;

	// not used
	private boolean displayLockMessages;

	public ErrorsField() {
		super();
	}

	/**
	 * PerformFinalize will generate the messages and counts used by the
	 * errorsField based on the keys that were matched from the MessageMap for
	 * this ErrorsField. It will also set up nestedComponents of its
	 * parentComponent correctly based on the flags that were chosen for this
	 * ErrorsField.
	 * 
	 * @see org.kuali.rice.krad.uif.field.FieldBase#performFinalize(org.kuali.rice.krad.uif.view.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
	 */
	@Override
	public void performFinalize(View view, Object model, Component parent) {
		super.performFinalize(view, model, parent);

		List<String> masterKeyList = getKeys(parent);
		errors = new ArrayList<String>();
		warnings = new ArrayList<String>();
		infos = new ArrayList<String>();
		errorCount = 0;
		warningCount = 0;
		infoCount = 0;
		MessageMap messageMap = GlobalVariables.getMessageMap();

		if (!displayFieldLabelWithMessages) {
			this.addStyleClass("noLabels");
		}
		if (!highlightOnError) {
			this.addStyleClass("noHighlight");
		}
		if (displayFieldErrorIcon) {
			this.addStyleClass("addFieldIcon");
		}

		if (displayMessages) {
			if (displayNestedMessages) {
				this.addNestedKeys(masterKeyList, parent);
			}

			for (String key : masterKeyList) {
				if (displayErrorMessages) {
					errors.addAll(getMessages(view, key,
							messageMap.getErrorMessagesForProperty(key, true)));
					errorCount = errorCount + tempCount;
				}
				if (displayWarningMessages) {
					warnings.addAll(getMessages(view, key,
							messageMap.getWarningMessagesForProperty(key, true)));
					warningCount = warningCount + tempCount;
				}
				if (displayInfoMessages) {
					infos.addAll(getMessages(view, key,
							messageMap.getInfoMessagesForProperty(key, true)));
					infoCount = infoCount + tempCount;
				}
			}
		} else if (displayFieldErrorIcon) {
			// Checks to see if any errors exist for this field, if they do set
			// errorCount as positive
			// so the jsp will call the corresponding js to show the icon
			// messages do not need to be generated because they are not being shown
			for (String key : masterKeyList) {
				if (!messageMap.getErrorMessagesForProperty(key, true)
						.isEmpty()) {
					errorCount = 1;
					break;
				}
			}
		}
		
		//Check for errors that are not matched on the page(only applies when parent is page)
		if(parent instanceof PageGroup){
			if(errorCount < messageMap.getErrorCount()){
				List<String> diff = messageMap.getPropertiesWithErrors();
				diff.removeAll(masterKeyList);
				for (String key : diff) {
    				errors.addAll(getMessages(view, key,
                            messageMap.getErrorMessagesForProperty(key, true)));
    				errorCount = errorCount+ tempCount;
				}
				
			}
			if(warningCount < messageMap.getWarningCount()){
				List<String> diff = messageMap.getPropertiesWithWarnings();
				diff.removeAll(masterKeyList);
				for (String key : diff) {
				    warnings.addAll(getMessages(view, key,
                            messageMap.getWarningMessagesForProperty(key, true)));
                    warningCount = warningCount + tempCount;
                }
			}
			if(infoCount < messageMap.getInfoCount()){
				List<String> diff = messageMap.getPropertiesWithInfo();
				diff.removeAll(masterKeyList);
                for (String key : diff) {
                    infos.addAll(getMessages(view, key,
                            messageMap.getInfoMessagesForProperty(key, true)));
                    infoCount = infoCount + tempCount;
                }
			}
		}
		
		// dont display anything if there are no messages
		if (errorCount + warningCount + infoCount == 0 || !displayMessages) {
			this.setStyle("display: none;");
		} else {
			this.setStyle("display: visible");
		}
	}

	/**
	 * Gets all the messages from the list of lists passed in (which are
	 * lists of ErrorMessages associated to the key) and uses the configuration
	 * service to get the message String associated. This will also combine
	 * error messages per a field if that option is turned on. If
	 * displayFieldLabelWithMessages is turned on, it will also find the label
	 * by key passed in.
	 * 
	 * @param view
	 * @param key
	 * @param lists
	 * @return
	 */
	private List<String> getMessages(View view, String key,
			List<AutoPopulatingList<ErrorMessage>> lists) {
		List<String> result = new ArrayList<String>();
		tempCount = 0;
		for (List<ErrorMessage> errorList : lists) {
			if (errorList != null && StringUtils.isNotBlank(key)) {
				ConfigurationService configService = KRADServiceLocator
						.getKualiConfigurationService();
				String comboMessage = "";
				String label = "";

				for (ErrorMessage e : errorList) {
					tempCount++;
					String message = configService.getPropertyValueAsString(e.getErrorKey());
					if (e.getMessageParameters() != null) {
						message = message.replace("'", "''");
						message = MessageFormat.format(message,
								(Object[]) e.getMessageParameters());
					}
					if (displayFieldLabelWithMessages) {
						AttributeField field = view.getViewIndex()
								.getAttributeFieldByPath(key);
						if (field != null && field.getLabel() != null) {
							label = field.getLabel();
						}
						else{
						    label = key;
						}
					}

					// adding them to combo string instead of the list
					if (combineMessages) {
						if (comboMessage.isEmpty()) {
							comboMessage = message;
						} else {
							comboMessage = comboMessage + ", " + message;
						}
					} else {
						// add it directly to the list - non combined messages
						if (StringUtils.isNotEmpty(label)) {
							result.add(label + " - " + message);
						} else {
							result.add(message);
						}

					}
				}
				// add the single combo string to the returned list
				// combineMessages will also be checked in the template to
				// further
				// combine them
				if (StringUtils.isNotEmpty(comboMessage)) {
					if (StringUtils.isNotEmpty(label)) {
						result.add(label + " - " + comboMessage);
					} else {
						result.add(comboMessage);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Gets all the keys associated to this ErrorsField. This includes the id of
	 * the parent component, additional keys to match, and the bindingPath if
	 * this is an ErrorsField for an AttributeField. These are the keys that are
	 * used to match errors with their component and display them as part of its
	 * ErrorsField.
	 * 
	 * @return
	 */
	protected List<String> getKeys(Component parent) {
		List<String> keyList = new ArrayList<String>();
		if (additionalKeysToMatch != null) {
			keyList.addAll(additionalKeysToMatch);
		}
		if (StringUtils.isNotBlank(parent.getId())) {
			keyList.add(parent.getId());
		}
		if (parent instanceof AttributeField) {
			if (((AttributeField) parent).getBindingInfo() != null
					&& StringUtils.isNotEmpty(((AttributeField) parent)
							.getBindingInfo().getBindingPath())) {
				keyList.add(((AttributeField) parent).getBindingInfo()
						.getBindingPath());
			}
		}
		// Will there be additional components to check beyond AttributeField?

		return keyList;
	}

	/**
	 * Adds all the nestedKeys of this component by calling getKeys on each of
	 * its nestedComponents' ErrorsFields and adding them to the list. If
	 * allowMessageRepeat is false, it will also turn off error display for its
	 * parent's nestedComponents' ErrorsFields.
	 * 
	 * @param keyList
	 * @param component
	 */
	private void addNestedKeys(List<String> keyList, Component component) {
		for (Component c : component.getNestedComponents()) {
			ErrorsField ef = null;
			if (c instanceof AttributeField) {
				ef = ((AttributeField) c).getErrorsField();
			} else if (c instanceof ContainerBase) {
				ef = ((ContainerBase) c).getErrorsField();
			}
			if (ef != null) {
				if (!allowMessageRepeat) {
					ef.setDisplayMessages(false);
				}
				keyList.addAll(ef.getKeys(c));
				addNestedKeys(keyList, c);
			}
		}
	}

	/**
	 * ErrorTitle is the title that will be shown before any error
	 * messages/error counts are displayed
	 * 
	 * @return
	 */
	public String getErrorTitle() {
		return this.errorTitle;
	}

	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	/**
	 * WarningTitle is the title that will be shown before any warning
	 * messages/warning counts are displayed
	 * 
	 * @return
	 */
	public String getWarningTitle() {
		return this.warningTitle;
	}

	public void setWarningTitle(String warningTitle) {
		this.warningTitle = warningTitle;
	}

	/**
	 * InfoTitle is the title that will be shown before any info messages/info
	 * counts are displayed
	 * 
	 * @return
	 */
	public String getInfoTitle() {
		return this.infoTitle;
	}

	public void setInfoTitle(String infoTitle) {
		this.infoTitle = infoTitle;
	}

	/**
	 * If displayErrorMessages is true, error messages will be displayed,
	 * otherwise they will not. Unlike many of the options contained on
	 * ErrorsField, this will not effect client side validations; ie this will
	 * not turn off errorMessage display for client side validation, as it may
	 * prevent a user from completing a form. To turn off client side validation
	 * AND its messaging use the applyClientSide flag on the Constraint itself.
	 * 
	 * TODO this may be changed to: if this is set on a field it will attempt
	 * show client side validation errors in the closest parent container error
	 * container
	 * 
	 * @return
	 */
	public boolean isDisplayErrorMessages() {
		return this.displayErrorMessages;
	}

	public void setDisplayErrorMessages(boolean displayErrorMessages) {
		this.displayErrorMessages = displayErrorMessages;
	}

	/**
	 * If displayInfoMessages is true, info messages will be displayed,
	 * otherwise they will not. Client side validation has no concept of warning
	 * or info messages, so this will not effect client side functionality.
	 * 
	 * @return
	 */
	public boolean isDisplayInfoMessages() {
		return this.displayInfoMessages;
	}

	public void setDisplayInfoMessages(boolean displayInfoMessages) {
		this.displayInfoMessages = displayInfoMessages;
	}

	public boolean isDisplayLockMessages() {
		return this.displayLockMessages;
	}

	/**
	 * This has no use - needs to be removed(?)
	 * 
	 * @param displayLockMessages
	 */
	public void setDisplayLockMessages(boolean displayLockMessages) {
		this.displayLockMessages = displayLockMessages;
	}

	/**
	 * If displayWarningMessages is true, warning messages will be displayed,
	 * otherwise they will not. Client side validation has no concept of warning
	 * or info messages, so this will not effect client side functionality.
	 * 
	 * @return
	 */
	public boolean isDisplayWarningMessages() {
		return this.displayWarningMessages;
	}

	public void setDisplayWarningMessages(boolean displayWarningMessages) {
		this.displayWarningMessages = displayWarningMessages;
	}

	/**
	 * AdditionalKeysToMatch is an additional list of keys outside of the
	 * default keys that will be matched when messages are returned after a form
	 * is submitted. These keys are only used for displaying messages generated
	 * by the server and have no effect on client side validation error display.
	 * 
	 * @return the additionalKeysToMatch
	 */
	public List<String> getAdditionalKeysToMatch() {
		return this.additionalKeysToMatch;
	}
	
    /**
     * Convenience setter for additional keys to match that takes a string argument and
     * splits on comma to build the list
     * 
     * @param additionalKeysToMatch String to parse
     */
    public void setAdditionalKeysToMatch(String additionalKeysToMatch) {
        if (StringUtils.isNotBlank(additionalKeysToMatch)) {
            this.additionalKeysToMatch = Arrays.asList(StringUtils.split(additionalKeysToMatch, ","));
        }
    }

	/**
	 * @param additionalKeysToMatch
	 *            the additionalKeysToMatch to set
	 */
	public void setAdditionalKeysToMatch(List<String> additionalKeysToMatch) {
		this.additionalKeysToMatch = additionalKeysToMatch;
	}

	/**
	 * If true, the errorTitle set on this ErrorsField will be displayed along
	 * with the error messages. Otherwise, the title will not be displayed.
	 * 
	 * @return the displayErrorTitle
	 */
	public boolean isDisplayErrorTitle() {
		return this.displayErrorTitle;
	}

	/**
	 * @param displayErrorTitle
	 *            the displayErrorTitle to set
	 */
	public void setDisplayErrorTitle(boolean displayErrorTitle) {
		this.displayErrorTitle = displayErrorTitle;
	}

	/**
	 * If true, the warningTitle set on this ErrorsField will be displayed along
	 * with the warning messages. Otherwise, the title will not be displayed.
	 * 
	 * @return the displayWarningTitle
	 */
	public boolean isDisplayWarningTitle() {
		return this.displayWarningTitle;
	}

	/**
	 * @param displayWarningTitle
	 *            the displayWarningTitle to set
	 */
	public void setDisplayWarningTitle(boolean displayWarningTitle) {
		this.displayWarningTitle = displayWarningTitle;
	}

	/**
	 * If true, the infoTitle set on this ErrorsField will be displayed along
	 * with the info messages. Otherwise, the title will not be displayed.
	 * 
	 * @return the displayInfoTitle
	 */
	public boolean isDisplayInfoTitle() {
		return this.displayInfoTitle;
	}

	/**
	 * @param displayInfoTitle
	 *            the displayInfoTitle to set
	 */
	public void setDisplayInfoTitle(boolean displayInfoTitle) {
		this.displayInfoTitle = displayInfoTitle;
	}

	/**
	 * If true, the error messages will display the an AttributeField's title
	 * alongside the error, warning, and info messages related to it. This
	 * setting has no effect on messages which do not relate directly to a
	 * single AttributeField.
	 * 
	 * @return the displayFieldLabelWithMessages
	 */
	public boolean isDisplayFieldLabelWithMessages() {
		return this.displayFieldLabelWithMessages;
	}

	/**
	 * @param displayFieldLabelWithMessages
	 *            the displayFieldLabelWithMessages to set
	 */
	public void setDisplayFieldLabelWithMessages(
			boolean displayFieldLabelWithMessages) {
		this.displayFieldLabelWithMessages = displayFieldLabelWithMessages;
	}

	/**
	 * If true, error, warning, and info messages will be displayed (provided
	 * they are also set to display). Otherwise, no messages for this
	 * ErrorsField container will be displayed (including ones set to display).
	 * This is a global display on/off switch for all messages.
	 * 
	 * @return the displayMessages
	 */
	public boolean isDisplayMessages() {
		return this.displayMessages;
	}

	/**
	 * @param displayMessages
	 *            the displayMessages to set
	 */
	public void setDisplayMessages(boolean displayMessages) {
		this.displayMessages = displayMessages;
	}

	/**
	 * If true, this ErrorsField will show messages related to the nested
	 * components of its parent component, and not just those related only to
	 * its parent component. Otherwise, it will be up to the individual
	 * components to display their messages, if any, in their ErrorsField.
	 * 
	 * @return the displayNestedMessages
	 */
	public boolean isDisplayNestedMessages() {
		return this.displayNestedMessages;
	}

	/**
	 * @param displayNestedMessages
	 *            the displayNestedMessages to set
	 */
	public void setDisplayNestedMessages(boolean displayNestedMessages) {
		this.displayNestedMessages = displayNestedMessages;
	}

	/**
	 * Combines the messages for a single key into one concatenated message per
	 * key being matched, seperated by a comma
	 * 
	 * @return the combineMessages
	 */
	public boolean isCombineMessages() {
		return this.combineMessages;
	}

	/**
	 * @param combineMessages
	 *            the combineMessages to set
	 */
	public void setCombineMessages(boolean combineMessages) {
		this.combineMessages = combineMessages;
	}

	/**
	 * If true, when this is set on an ErrorsField whose parentComponent has
	 * nested Containers or AttributeFields, it will allow those fields to also
	 * show their ErrorsField messages. Otherwise, it will turn off the the
	 * display of those messages. This can be used to avoid repeating
	 * information to the user per field, if errors are already being displayed
	 * at the parent's level. This flag has no effect if displayNestedMessages
	 * is false on this ErrorsField.
	 * 
	 * @return the allowMessageRepeat
	 */
	public boolean isAllowMessageRepeat() {
		return this.allowMessageRepeat;
	}

	/**
	 * 
	 * @param allowMessageRepeat
	 *            the allowMessageRepeat to set
	 */
	public void setAllowMessageRepeat(boolean allowMessageRepeat) {
		this.allowMessageRepeat = allowMessageRepeat;
	}

	/**
	 * displayCounts is true if the counts of errors, warning, and info messages
	 * within this ErrorsField should be displayed (includes count of nested
	 * messages if displayNestedMessages is true).
	 * 
	 * @return
	 */
	public boolean isDisplayCounts() {
		return this.displayCounts;
	}

	/**
	 * @param displayCounts
	 *            the displayCounts to set
	 */
	public void setDisplayCounts(boolean displayCounts) {
		this.displayCounts = displayCounts;
	}

	/**
	 * The list of error messages found for the keys that were matched on this
	 * ErrorsField This is generated and cannot be set
	 * 
	 * @return the errors
	 */
	public List<String> getErrors() {
		return this.errors;
	}

	/**
	 * The list of warning messages found for the keys that were matched on this
	 * ErrorsField This is generated and cannot be set
	 * 
	 * @return the warnings
	 */
	public List<String> getWarnings() {
		return this.warnings;
	}

	/**
	 * The list of info messages found for the keys that were matched on this
	 * ErrorsField This is generated and cannot be set
	 * 
	 * @return the infos
	 */
	public List<String> getInfos() {
		return this.infos;
	}

	/**
	 * The count of error messages found for the keys that were matched on this
	 * ErrorsField This is generated and cannot be set
	 * 
	 * @return the errorCount
	 */
	public int getErrorCount() {
		return this.errorCount;
	}

	/**
	 * The count of warning messages found for the keys that were matched on
	 * this ErrorsField This is generated and cannot be set
	 * 
	 * @return the warningCount
	 */
	public int getWarningCount() {
		return this.warningCount;
	}

	/**
	 * The count of info messages found for the keys that were matched on this
	 * ErrorsField This is generated and cannot be set
	 * 
	 * @return the infoCount
	 */
	public int getInfoCount() {
		return this.infoCount;
	}

	/**
	 * If this is true, the display of messages is being handled by another
	 * container. The ErrorsField html generated by the jsp will still be used,
	 * but it will be placed in different location within the page than the
	 * default to accommodate an alternate layout. This flag is used by
	 * BoxLayoutManager.
	 * 
	 * This flag only applies to ErrorsFields whose parentComponents are
	 * AttributeFields.
	 * 
	 * @return the alternateContainer
	 */
	public boolean isAlternateContainer() {
		return this.alternateContainer;
	}

	/**
	 * @param alternateContainer
	 *            the alternateContainer to set
	 */
	public void setAlternateContainer(boolean alternateContainer) {
		this.alternateContainer = alternateContainer;
	}

	/**
	 * If true, displays an icon next to each field that has an error (default
	 * KNS look). Otherwise, this icon will not be displayed. Note that any icon
	 * set through css for the message containers will still appear and this
	 * only relates to the icon directly to the right of an input field.
	 * 
	 * This flag should only be set on AttributeField ErrorsFields.
	 * 
	 * @return the displayFieldErrorIcon
	 */
	public boolean isDisplayFieldErrorIcon() {
		return this.displayFieldErrorIcon;
	}

	/**
	 * @param displayFieldErrorIcon
	 *            the displayFieldErrorIcon to set
	 */
	public void setDisplayFieldErrorIcon(boolean displayFieldErrorIcon) {
		this.displayFieldErrorIcon = displayFieldErrorIcon;
	}

	/**
	 * If true, highlights the parentComponent's container when it has an
	 * error/warning/info. Otherwise, this highlighting will not be displayed.
	 * Note that the css can be changed per a type of highlighting, if showing a
	 * different color or no color per type of message is desired.
	 * 
	 * @return the highlightOnError
	 */
	public void setHighlightOnError(boolean highlightOnError) {
		this.highlightOnError = highlightOnError;
	}

	/**
	 * @return the highlightOnError
	 */
	public boolean isHighlightOnError() {
		return highlightOnError;
	}

}
