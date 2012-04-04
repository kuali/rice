/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.layout.StackedLayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.util.AutoPopulatingList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

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
public class ValidationMessages extends FieldBase {
    private static final long serialVersionUID = 780940788435330077L;

    private List<String> additionalKeysToMatch;

    private boolean fireGrowlsForMessages;
    private String growlScript = "";

    // Message construction variables
    private boolean displayFieldLabelWithMessages;

    private boolean collapseAdditionalFieldLinkMessages;

    private boolean displayMessages;

    private boolean useTooltip;

    // Error messages
    private List<String> errors;
    private List<String> warnings;
    private List<String> infos;

    // Counts
    private int errorCount;
    private int warningCount;
    private int infoCount;

    // not used
    private boolean displayLockMessages;

    public ValidationMessages() {
        super();
    }

    /**
     * PerformFinalize will generate the messages and counts used by the
     * errorsField based on the keys that were matched from the MessageMap for
     * this ValidationMessages. It will also set up nestedComponents of its
     * parentComponent correctly based on the flags that were chosen for this
     * ValidationMessages.
     *
     * @see org.kuali.rice.krad.uif.field.FieldBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        generateMessages(true, view, model, parent);
    }

    public void generateMessages(boolean reset, View view, Object model, Component parent) {
        if (reset) {
            errors = new ArrayList<String>();
            warnings = new ArrayList<String>();
            infos = new ArrayList<String>();
            errorCount = 0;
            warningCount = 0;
            infoCount = 0;
        }

        List<String> masterKeyList = getKeys(parent);
        MessageMap messageMap = GlobalVariables.getMessageMap();
        
        String parentContainerId = "";
        Object parentContainer = parent.getContext().get("parent");
        if(parentContainer != null && (parentContainer instanceof Container || parentContainer instanceof FieldGroup)){
            parentContainerId = ((Component)parentContainer).getId();
        }
        
        if(parentContainer != null && parentContainer instanceof FieldGroup){
            masterKeyList.add(((Component)parentContainer).getId());
        }

        // TODO: need constants
        if (!displayFieldLabelWithMessages) {
            this.addStyleClass("uif-noLabels");
        }

        for (String key : masterKeyList) {

            errors.addAll(getMessages(view, key, messageMap.getErrorMessagesForProperty(key, true)));

            warnings.addAll(getMessages(view, key, messageMap.getWarningMessagesForProperty(key, true)));

            infos.addAll(getMessages(view, key, messageMap.getInfoMessagesForProperty(key, true)));
        }

        //Check for errors that are not matched on the page(only applies when parent is page)
        if (parent instanceof PageGroup) {
            //TODO add logic to check for keys not matched on anything
        }

        if (fireGrowlsForMessages) {
            //set up growl script
            growlScript = getGrowlScript(view);
        }

        //Remove any textual duplicates that may have snuck in, by converting to set and back to list
/*        errors = new ArrayList<String>(new LinkedHashSet<String>(errors));
        warnings = new ArrayList<String>(new LinkedHashSet<String>(warnings));
        infos = new ArrayList<String>(new LinkedHashSet<String>(infos));*/

        this.addDataAttribute("messagesFor", parent.getId());

        if(parent instanceof InputField){
            
            parent.addDataAttribute("parent", parentContainerId);
            parent.addDataAttribute("validationMessages", "{"
                    + "displayMessages:" + displayMessages + ","
                    + "useTooltip:"+ useTooltip + ","
                    + "serverErrors:" + convertStringListToJsArray(errors) + ","
                    + "serverWarnings:" + convertStringListToJsArray(warnings) + ","
                    + "serverInfo:" + convertStringListToJsArray(infos)
                    + "}");
        }
        else if(parent instanceof Container){

            List<? extends Component> items = ((Container)parent).getItems();
            boolean skipSections = false;
            if(parent instanceof CollectionGroup){
                if(((CollectionGroup)parent).getLayoutManager() instanceof StackedLayoutManager){
                    items = ((StackedLayoutManager)((CollectionGroup)parent).getLayoutManager()).getStackedGroups();
                }
                else if(((CollectionGroup)parent).getLayoutManager() instanceof TableLayoutManager){
                    items = ((TableLayoutManager)((CollectionGroup)parent).getLayoutManager()).getDataFields();
                    skipSections = true;
                }
            }

            List<String> sectionIds = new ArrayList<String>();
            List<String> fieldOrder = new ArrayList<String>();
            collectIdsFromItems(items, sectionIds, fieldOrder, skipSections);
            
            parent.addDataAttribute("parent", parentContainerId);

            boolean pageLevel = false;
            boolean forceShow = false;
            if(parent instanceof PageGroup){
                pageLevel = true;
                forceShow = true;
            }
            else if(parentContainer instanceof FieldGroup) {
                //note this means container of the parent is a FieldGroup
                forceShow = true;
            }

            parent.addDataAttribute("validationMessages", "{"
                    + "summarize:" + true + ","
                    + "displayMessages:" + displayMessages + ","
                    + "collapseFieldMessages:" + collapseAdditionalFieldLinkMessages + ","
                    + "displayLabel:" + displayFieldLabelWithMessages + ","
                    + "pageLevel:" + pageLevel + ","
                    + "forceShow:" + forceShow + ","
                    + "sections:" + convertStringListToJsArray(sectionIds) + ","
                    + "order:" + convertStringListToJsArray(fieldOrder) + ","
                    + "serverErrors:" + convertStringListToJsArray(errors) + ","
                    + "serverWarnings:" + convertStringListToJsArray(warnings) + ","
                    + "serverInfo:" + convertStringListToJsArray(infos)
                    + "}");
        }

        this.setStyle("display: none;");
    }

    private void collectIdsFromItems(List<? extends Component> items, List<String> sectionIds, List<String> order, boolean skipSections){

        if(items != null){
            for(Component c: items){
                if(c instanceof Container || c instanceof FieldGroup){
                    if(c instanceof FieldGroup){
                        if(!skipSections &&
                            ((FieldGroup)c).getFieldLabel().isRender() &&
                            !((FieldGroup)c).getFieldLabel().isHidden() &&
                            (StringUtils.isNotEmpty(((FieldGroup)c).getLabel()) ||
                            StringUtils.isNotEmpty(((FieldGroup)c).getFieldLabel().getLabelText()))){
                            sectionIds.add(c.getId());
                            order.add("f$" + c.getId());
                            continue;
                        }
                        else{
                            c = ((FieldGroup) c).getGroup();
                            if(c == null){
                                continue;
                            }
                        }
                    }
                    //TODO possibly find a better way to identify a section/subsection but this may work
                    if(!skipSections && ((Container) c).getHeader() != null && ((Container) c).getHeader().isRender() &&
                            (StringUtils.isNotBlank(((Container) c).getHeader().getHeaderText())
                            || StringUtils.isNotBlank(c.getTitle()))){
                        sectionIds.add(c.getId());
                        //TODO make constant for section token
                        order.add("s$" + c.getId());
                    }
                    else{
                        collectIdsFromItems(((Container) c).getItems(), sectionIds, order, skipSections);
                    }
                }
                else if(c instanceof InputField){
                    order.add(c.getId());    
                }
            }
        }
    }
    
    private String convertStringListToJsArray(List<String> list){
        String array = "[";
        for(String s: list){
            array = array + "'" + s + "',";
        }
        array = StringUtils.removeEnd(array, ",");
        array = array + "]";
        return array;
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
    private List<String> getMessages(View view, String key, List<AutoPopulatingList<ErrorMessage>> lists) {
        List<String> result = new ArrayList<String>();
        for (List<ErrorMessage> errorList : lists) {
            if (errorList != null && StringUtils.isNotBlank(key)) {
                ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();

                for (ErrorMessage e : errorList) {
                    String message = configService.getPropertyValueAsString(e.getErrorKey());
                    if (message == null) {
                        message = "Intended message with key: " + e.getErrorKey() + " not found.";
                    }
                    if (e.getMessageParameters() != null) {
                        message = message.replace("'", "''");
                        message = MessageFormat.format(message, (Object[]) e.getMessageParameters());
                    }

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
        if (parent instanceof InputField) {
            if (((InputField) parent).getBindingInfo() != null && StringUtils.isNotEmpty(
                    ((InputField) parent).getBindingInfo().getBindingPath())) {
                keyList.add(((InputField) parent).getBindingInfo().getBindingPath());
            }
        }

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
        for (Component c : component.getComponentsForLifecycle()) {
            ValidationMessages ef = null;
            if (c instanceof InputField) {
                ef = ((InputField) c).getValidationMessages();
            } else if (c instanceof ContainerBase) {
                ef = ((ContainerBase) c).getValidationMessages();
            }
            if (ef != null) {
                keyList.addAll(ef.getKeys(c));
                addNestedKeys(keyList, c);
            }
        }
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
     * @param additionalKeysToMatch the additionalKeysToMatch to set
     */
    public void setAdditionalKeysToMatch(List<String> additionalKeysToMatch) {
        this.additionalKeysToMatch = additionalKeysToMatch;
    }

    /**
     * If true, the error messages will display the an InputField's title
     * alongside the error, warning, and info messages related to it. This
     * setting has no effect on messages which do not relate directly to a
     * single InputField.
     *
     * @return the displayFieldLabelWithMessages
     */
    public boolean isDisplayFieldLabelWithMessages() {
        return this.displayFieldLabelWithMessages;
    }

    /**
     * @param displayFieldLabelWithMessages the displayFieldLabelWithMessages to set
     */
    public void setDisplayFieldLabelWithMessages(boolean displayFieldLabelWithMessages) {
        this.displayFieldLabelWithMessages = displayFieldLabelWithMessages;
    }

    /**
     * If true, error, warning, and info messages will be displayed (provided
     * they are also set to display). Otherwise, no messages for this
     * ValidationMessages container will be displayed (including ones set to display).
     * This is a global display on/off switch for all messages.
     *
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
     * The list of error messages found for the keys that were matched on this
     * ValidationMessages This is generated and cannot be set
     *
     * @return the errors
     */
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * The list of warning messages found for the keys that were matched on this
     * ValidationMessages This is generated and cannot be set
     *
     * @return the warnings
     */
    public List<String> getWarnings() {
        return this.warnings;
    }

    /**
     * The list of info messages found for the keys that were matched on this
     * ValidationMessages This is generated and cannot be set
     *
     * @return the infos
     */
    public List<String> getInfos() {
        return this.infos;
    }

    private String getGrowlScript(View view) {
        // growls are setup here because they are relevant to the current page, but their
        // settings are global to the view
        String growlScript = "";
        if (view.isGrowlMessagingEnabled()) {
            ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
            MessageMap messageMap = GlobalVariables.getMessageMap();
            if (messageMap.hasErrors()) {
                String message = configService.getPropertyValueAsString("growl.hasErrors");
                if (StringUtils.isNotBlank(message)) {
                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + configService.getPropertyValueAsString(
                                    "general.error") + "', 'errorGrowl');";
                }
            }

            if (messageMap.hasWarnings()) {
                String message = configService.getPropertyValueAsString("growl.hasWarnings");
                if (StringUtils.isNotBlank(message)) {
                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + configService.getPropertyValueAsString(
                                    "general.warning") + "', 'warningGrowl');";
                }
            }

            if (messageMap.hasInfo()) {
                List<String> properties = messageMap.getPropertiesWithInfo();
                String message = "";
                for (String property : properties) {
                    List<AutoPopulatingList<ErrorMessage>> lists = messageMap.getInfoMessagesForProperty(property,
                            true);
                    for (List<ErrorMessage> errorList : lists) {
                        if (errorList != null) {
                            for (ErrorMessage e : errorList) {
                                if (StringUtils.isBlank(message)) {
                                    message = configService.getPropertyValueAsString(e.getErrorKey());
                                } else {
                                    message = message + "<br/>" + configService.getPropertyValueAsString(
                                            e.getErrorKey());
                                }
                                if (e.getMessageParameters() != null) {
                                    message = message.replace("'", "''");
                                    message = MessageFormat.format(message, (Object[]) e.getMessageParameters());
                                }
                            }
                        }
                    }
                }

                if (StringUtils.isNotBlank(message)) {
                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + configService.getPropertyValueAsString(
                                    "general.info") + "', 'infoGrowl');";
                }
            }
        }
        return growlScript;
    }

    public boolean isFireGrowlsForMessages() {
        return fireGrowlsForMessages;
    }

    public void setFireGrowlsForMessages(boolean fireGrowlsForMessages) {
        this.fireGrowlsForMessages = fireGrowlsForMessages;
    }

    public String getGrowlScript() {
        return growlScript;
    }

    /**
     * When collapseAdditionalFieldLinkMessages is set to true, the messages generated on field links will be
     * summarized to limit the space they take up with an appendage similar to [+n message type] appended for additional
     * messages that are omitted.  When this flag is false, all messages will be part of the link separated by
     * a comma.
     * @return if field link messages are being collapsed
     */
    public boolean isCollapseAdditionalFieldLinkMessages() {
        return collapseAdditionalFieldLinkMessages;
    }

    /**
     * Set collapseAdditionalFieldLinkMessages
     * @param collapseAdditionalFieldLinkMessages - true if field link messages are being collapsed
     */
    public void setCollapseAdditionalFieldLinkMessages(boolean collapseAdditionalFieldLinkMessages) {
        this.collapseAdditionalFieldLinkMessages = collapseAdditionalFieldLinkMessages;
    }

    /**
     * When true, use the tooltip on fields to display their relevant messages.  When false, these messages
     * will appear directly below the control.
     * @return true if using tooltips for messages, false to display below control
     */
    public boolean isUseTooltip() {
        return useTooltip;
    }

    /**
     * Set the useTooltip flag
     * @param useTooltip - if true show tooltip, otherwise show messages below field control
     */
    public void setUseTooltip(boolean useTooltip) {
        this.useTooltip = useTooltip;
    }
}
