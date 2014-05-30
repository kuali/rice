/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.MessageStructureUtils;
import org.kuali.rice.krad.uif.util.RecycleUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Field that displays error, warning, and info messages for the keys that are
 * matched. By default, an ValidationMessages will match on id and bindingPath (if this
 * ValidationMessages is for an InputField), but can be set to match on
 * additionalKeys and nested components keys (of the its parentComponent).
 *
 * In addition, there are a variety of options which can be toggled to effect
 * the display of these messages during both client and server side validation
 * display. See documentation on each get method for more details on the effect
 * of each option.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "validationMessages", parent = "Uif-ValidationMessagesBase")
public class ValidationMessages extends UifDictionaryBeanBase {
    private static final long serialVersionUID = 780940788435330077L;

    private List<String> additionalKeysToMatch;

    private boolean displayMessages;

    // Error messages
    private List<String> errors;
    private List<String> warnings;
    private List<String> infos;

    /**
     * Generates the messages based on the content in the messageMap
     *
     * @param view the current View
     * @param model the current model
     * @param parent the parent of this ValidationMessages
     */
    public void generateMessages(View view, Object model, Component parent) {
        errors = new ArrayList<String>();
        warnings = new ArrayList<String>();
        infos = new ArrayList<String>();

        List<String> masterKeyList = getKeys(parent);
        MessageMap messageMap = GlobalVariables.getMessageMap();

        String parentContainerId = "";

        Map<String, Object> parentContext = parent.getContext();
        Object parentContainer = parentContext == null ? null : parentContext
                .get(UifConstants.ContextVariableNames.PARENT);

        if (parentContainer != null && (parentContainer instanceof Container
                || parentContainer instanceof FieldGroup)) {
            parentContainerId = ((Component) parentContainer).getId();
        }

        // special message component case
        if (parentContainer != null && parentContainer instanceof Message && ((Message) parentContainer)
                .isRenderWrapperTag()) {
            parentContainerId = ((Component) parentContainer).getId();
        }

        // special case for nested contentElement with no parent
        if (parentContainer != null && parentContainer instanceof Component && StringUtils.isBlank(parentContainerId)) {
            parentContext = ((Component) parentContainer).getContext();
            parentContainer = parentContext == null ? null : parentContext
                    .get(UifConstants.ContextVariableNames.PARENT);
            if (parentContainer != null && (parentContainer instanceof Container
                    || parentContainer instanceof FieldGroup)) {
                parentContainerId = ((Component) parentContainer).getId();
            }
        }

        if ((parent.getDataAttributes() == null) || (parent.getDataAttributes().get(UifConstants.DataAttributes.PARENT)
                == null)) {
            parent.addDataAttribute(UifConstants.DataAttributes.PARENT, parentContainerId);
        }

        //Handle the special FieldGroup case - adds the FieldGroup itself to ids handled by this group (this must
        //be a group if its parent is FieldGroup)
        if (parentContainer != null && parentContainer instanceof FieldGroup) {
            masterKeyList.add(parentContainerId);
        }

        for (String key : masterKeyList) {
            errors.addAll(getMessages(view, key, messageMap.getErrorMessagesForProperty(key, true)));
            warnings.addAll(getMessages(view, key, messageMap.getWarningMessagesForProperty(key, true)));
            infos.addAll(getMessages(view, key, messageMap.getInfoMessagesForProperty(key, true)));
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
     * @return list of messages
     */
    protected List<String> getMessages(View view, String key, List<List<ErrorMessage>> lists) {
        List<String> result = new ArrayList<String>();
        for (List<ErrorMessage> errorList : lists) {
            if (errorList != null && StringUtils.isNotBlank(key)) {
                for (ErrorMessage e : errorList) {
                    String message = KRADUtils.getMessageText(e, true);
                    message = MessageStructureUtils.translateStringMessage(message);

                    result.add(message);
                }
            }
        }

        return result;
    }

    /**
     * Gets all the keys associated to this ValidationMessages. This includes the id of
     * the parent component, additional keys to match, and the bindingPath if
     * this is an ValidationMessages for an InputField. These are the keys that are
     * used to match errors with their component and display them as part of its
     * ValidationMessages.
     *
     * @return list of keys
     */
    protected List<String> getKeys(Component parent) {
        List<String> keyList = new ArrayList<String>();

        if (additionalKeysToMatch != null) {
            keyList.addAll(additionalKeysToMatch);
        }

        if (StringUtils.isNotBlank(parent.getId())) {
            keyList.add(parent.getId());
        }

        if (parent instanceof InputField) {
            if (((InputField) parent).getBindingInfo() != null && StringUtils.isNotEmpty(
                    ((InputField) parent).getBindingInfo().getBindingPath())) {
                keyList.add(((InputField) parent).getBindingInfo().getBindingPath());
            }
        }

        return keyList;
    }

    /**
     * Adds all group keys of this component (starting from this component itself) by calling getKeys on each of
     * its nested group's ValidationMessages and adding them to the list.
     *
     * @param keyList
     * @param component
     */
    protected void addNestedGroupKeys(Collection<String> keyList, Component component) {
        @SuppressWarnings("unchecked")
        Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(LinkedList.class);
        try {
            elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(component).values());
            while (!elementQueue.isEmpty()) {
                LifecycleElement element = elementQueue.poll();

                ValidationMessages ef = null;
                if (element instanceof ContainerBase) {
                    ef = ((ContainerBase) element).getValidationMessages();
                } else if (element instanceof FieldGroup) {
                    ef = ((FieldGroup) element).getGroup().getValidationMessages();
                }
                
                if (ef != null) {
                    keyList.addAll(ef.getKeys((Component) element));
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(element).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    /**
     * AdditionalKeysToMatch is an additional list of keys outside of the
     * default keys that will be matched when messages are returned after a form
     * is submitted. These keys are only used for displaying messages generated
     * by the server and have no effect on client side validation error display.
     *
     * @return the additionalKeysToMatch
     */
    @BeanTagAttribute
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
     * @param additionalKeysToMatch the additionalKeysToMatch to set
     */
    public void setAdditionalKeysToMatch(List<String> additionalKeysToMatch) {
        this.additionalKeysToMatch = additionalKeysToMatch;
    }

    /**
     * <p>If true, error, warning, and info messages will be displayed (provided
     * they are also set to display). Otherwise, no messages for this
     * ValidationMessages container will be displayed (including ones set to display).
     * This is a global display on/off switch for all messages.</p>
     *
     * <p>Other areas of the screen react to
     * a display flag being turned off at a certain level, if display is off for a field, the next
     * level up will display that fields full message text, and if display is off at a section the
     * next section up will display those messages nested in a sublist.</p>
     *
     * @return the displayMessages
     */
    @BeanTagAttribute
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
     * The list of error messages found for the keys that were matched on this
     * ValidationMessages This is generated and cannot be set
     *
     * @return the errors
     */
    @BeanTagAttribute
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * @see ValidationMessages#getErrors()
     */
    protected void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * The list of warning messages found for the keys that were matched on this
     * ValidationMessages This is generated and cannot be set
     *
     * @return the warnings
     */
    @BeanTagAttribute
    public List<String> getWarnings() {
        return this.warnings;
    }

    /**
     * @see ValidationMessages#getWarnings()
     */
    protected void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    /**
     * The list of info messages found for the keys that were matched on this
     * ValidationMessages This is generated and cannot be set
     *
     * @return the infos
     */
    @BeanTagAttribute
    public List<String> getInfos() {
        return this.infos;
    }

    /**
     * @see ValidationMessages#getInfos()
     */
    protected void setInfos(List<String> infos) {
        this.infos = infos;
    }

    /**
     * Adds the value passed to the valueMap with the key specified, if the value does not match the
     * value which already exists in defaults (to avoid having to write out extra data that can later
     * be derived from the defaults in the js)
     *
     * @param valueMap the data map being constructed
     * @param defaults defaults for validation messages
     * @param key the variable name being added
     * @param value the value set on this object
     */
    protected void addValidationDataSettingsValue(Map<String, Object> valueMap, Map<String, String> defaults,
            String key, Object value) {
        String defaultValue = defaults.get(key);
        if ((defaultValue != null && !value.toString().equals(defaultValue)) || (defaultValue != null && defaultValue
                .equals("[]") && value instanceof List && !((List) value).isEmpty()) || defaultValue == null) {
            valueMap.put(key, value);
        }
    }

}
