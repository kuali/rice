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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableCheckbox;
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup;
import org.kuali.rice.core.api.uif.RemotableControlContract;
import org.kuali.rice.core.api.uif.RemotableDatepicker;
import org.kuali.rice.core.api.uif.RemotableHiddenInput;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.uif.RemotableRadioButtonGroup;
import org.kuali.rice.core.api.uif.RemotableSelect;
import org.kuali.rice.core.api.uif.RemotableSelectGroup;
import org.kuali.rice.core.api.uif.RemotableTextExpand;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.uif.RemotableTextarea;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.LinkGroup;
import org.kuali.rice.krad.uif.container.NavigationGroup;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.container.TabGroup;
import org.kuali.rice.krad.uif.container.TreeGroup;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.CheckboxGroupControl;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.FileControl;
import org.kuali.rice.krad.uif.control.HiddenControl;
import org.kuali.rice.krad.uif.control.MultiValueControl;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.control.SizedControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.BlankField;
import org.kuali.rice.krad.uif.field.ErrorsField;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.GenericField;
import org.kuali.rice.krad.uif.field.HeaderField;
import org.kuali.rice.krad.uif.field.IframeField;
import org.kuali.rice.krad.uif.field.ImageField;
import org.kuali.rice.krad.uif.field.LabelField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating new UIF components from their base definitions
 * in the dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFactory {

    protected static final String TEXT_CONTROL = "Uif-TextControl";
    protected static final String CHECKBOX_CONTROL = "Uif-CheckboxControl";
    protected static final String HIDDEN_CONTROL = "Uif-HiddenControl";
    protected static final String TEXTAREA_CONTROL = "Uif-TextAreaControl";
    protected static final String SELECT_CONTROL = "Uif-DropdownControl";
    protected static final String CHECKBOX_GROUP_CONTROL = "Uif-VerticalCheckboxesControl";
    protected static final String CHECKBOX_GROUP_CONTROL_HORIZONTAL = "Uif-HorizontalCheckboxesControl";
    protected static final String RADIO_GROUP_CONTROL = "Uif-VerticalRadioControl";
    protected static final String RADIO_GROUP_CONTROL_HORIZONTAL = "Uif-HorizontalRadioControl";
    protected static final String FILE_CONTROL = "Uif-FileControl";
    protected static final String DATE_CONTROL = "Uif-DateControl";
    protected static final String USER_CONTROL = "Uif-KimPersonControl";
    protected static final String GROUP_CONTROL = "Uif-KimGroupControl";

    protected static final String DATA_FIELD = "Uif-DataField";
    protected static final String INPUT_FIELD = "Uif-InputField";
    protected static final String ERRORS_FIELD = "Uif-FieldValidationMessages";
    protected static final String ACTION_FIELD = "Uif-PrimaryActionButton";
    protected static final String ACTION_LINK_FIELD = "Uif-ActionLink";
    protected static final String LINK_FIELD = "Uif-Link";
    protected static final String IFRAME_FIELD = "Uif-IframeField";
    protected static final String IMAGE_FIELD = "Uif-ImageField";
    protected static final String BLANK_FIELD = "Uif-EmptyField";
    protected static final String GENERIC_FIELD = "Uif-CustomTemplateField";
    protected static final String LABEL_FIELD = "Uif-Label";
    protected static final String MESSAGE_FIELD = "Uif-MessageField";
    protected static final String FIELD_GROUP = "Uif-VerticalFieldGroup";
    protected static final String HORIZONTAL_FIELD_GROUP = "Uif-HorizontalFieldGroup";

    protected static final String GROUP = "Uif-BoxSection";
    protected static final String PAGE_GROUP = "Uif-Page";
    protected static final String GROUP_GRID_LAYOUT = "Uif-GridSection";
    protected static final String GROUP_BODY_ONLY = "Uif-BoxGroup";
    protected static final String GROUP_GRID_BODY_ONLY = "Uif-GridGroup";
    protected static final String TAB_GROUP = "Uif-TabSection";
    protected static final String NAVIGATION_GROUP = "Uif-NavigationGroupBase";
    protected static final String TREE_GROUP = "Uif-TreeSection";
    protected static final String LINK_GROUP = "Uif-LinkGroup";
    protected static final String COLLECTION_GROUP = "Uif-StackedCollectionSection";
    protected static final String COLLECTION_GROUP_TABLE_LAYOUT = "Uif-TableCollectionSection";
    protected static final String LIST_GROUP = "Uif-ListCollectionSection";

    protected static final String HEADER = "Uif-HeaderFieldBase";
    protected static final String FOOTER = "Uif-FooterBase";
    protected static final String FOOTER_SAVECLOSECANCEL = "Uif-FormFooter";

    /**
     * Gets a fresh copy of the component by the id passed in which used to look up the component in
     * the view index, then retrieve a new instance with initial state configured using the factory id
     *
     * @param id - id for the component in the view index
     * @return Component new instance
     */
    public static Component getNewInstanceForRefresh(View view, String id) {
        Component component = null;
        Component origComponent = view.getViewIndex().getComponentById(id);

        if (origComponent == null) {
            throw new RuntimeException(id + " not found in view index try setting p:persistInSession=\"true\" in xml");
        }

        if (view.getViewIndex().getInitialComponentStates().containsKey(origComponent.getFactoryId())) {
            component = view.getViewIndex().getInitialComponentStates().get(origComponent.getFactoryId());
        } else {
            component = (Component) KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryObject(
                    origComponent.getFactoryId());
        }

        if (component != null) {
            component = ComponentUtils.copyObject(component);
            component.setId(origComponent.getFactoryId());
        }

        return component;
    }

    /**
     * Returns a new <code>Component</code> instance for the given bean id from the spring factory
     *
     * @param beanId - id of the bean definition
     * @return new component instance or null if no such component definition was found
     */
    public static Component getNewComponentInstance(String beanId) {
        Component component = (Component) KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryObject(beanId);

        // clear id before returning so duplicates do not occur
        component.setId(null);
        component.setFactoryId(null);

        return component;
    }

    public static TextControl getTextControl() {
        return (TextControl) getNewComponentInstance(TEXT_CONTROL);
    }

    public static TextAreaControl getTextAreaControl() {
        return (TextAreaControl) getNewComponentInstance(TEXTAREA_CONTROL);
    }

    public static CheckboxControl getCheckboxControl() {
        return (CheckboxControl) getNewComponentInstance(CHECKBOX_CONTROL);
    }

    public static HiddenControl getHiddenControl() {
        return (HiddenControl) getNewComponentInstance(HIDDEN_CONTROL);
    }

    public static SelectControl getSelectControl() {
        return (SelectControl) getNewComponentInstance(SELECT_CONTROL);
    }

    public static CheckboxGroupControl getCheckboxGroupControl() {
        return (CheckboxGroupControl) getNewComponentInstance(CHECKBOX_GROUP_CONTROL);
    }

    public static CheckboxGroupControl getCheckboxGroupControlHorizontal() {
        return (CheckboxGroupControl) getNewComponentInstance(CHECKBOX_GROUP_CONTROL_HORIZONTAL);
    }

    public static RadioGroupControl getRadioGroupControl() {
        return (RadioGroupControl) getNewComponentInstance(RADIO_GROUP_CONTROL);
    }

    public static RadioGroupControl getRadioGroupControlHorizontal() {
        return (RadioGroupControl) getNewComponentInstance(RADIO_GROUP_CONTROL_HORIZONTAL);
    }

    public static FileControl getFileControl() {
        return (FileControl) getNewComponentInstance(FILE_CONTROL);
    }

    public static TextControl getDateControl() {
        return (TextControl) getNewComponentInstance(DATE_CONTROL);
    }

    public static TextControl getUserControl() {
        return (TextControl) getNewComponentInstance(USER_CONTROL);
    }

    public static TextControl getGroupControl() {
        return (TextControl) getNewComponentInstance(GROUP_CONTROL);
    }

    public static DataField getDataField() {
        return (DataField) getNewComponentInstance(DATA_FIELD);
    }

    public static DataField getDataField(String propertyName, String label) {
        DataField field = (DataField) getNewComponentInstance(DATA_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);

        return field;
    }

    public static InputField getInputField() {
        return (InputField) getNewComponentInstance(INPUT_FIELD);
    }

    public static InputField getInputField(String propertyName, String label) {
        InputField field = (InputField) getNewComponentInstance(INPUT_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);

        return field;
    }

    public static InputField getInputField(String propertyName, String label, UifConstants.ControlType controlType) {
        InputField field = (InputField) getNewComponentInstance(INPUT_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);
        field.setControl(getControl(controlType));

        return field;
    }

    public static InputField getInputField(String propertyName, String label, UifConstants.ControlType controlType,
            String defaultValue) {
        InputField field = (InputField) getNewComponentInstance(INPUT_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);
        field.setControl(getControl(controlType));
        field.setDefaultValue(defaultValue);

        return field;
    }

    public static InputField getInputField(String propertyName, String label, UifConstants.ControlType controlType,
            Class<? extends KeyValuesFinder> optionsFinderClass) {
        InputField field = (InputField) getNewComponentInstance(INPUT_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);
        field.setControl(getControl(controlType));
        field.setOptionsFinderClass(optionsFinderClass);

        return field;
    }

    public static InputField getInputField(String propertyName, String label, UifConstants.ControlType controlType,
            List<KeyValue> options) {
        InputField field = (InputField) getNewComponentInstance(INPUT_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);

        Control control = getControl(controlType);
        if (control instanceof MultiValueControl) {
            ((MultiValueControl) control).setOptions(options);
        } else {
            throw new RuntimeException("Control is not instance of multi-value control, cannot set options");
        }

        return field;
    }

    public static InputField getInputField(String propertyName, String label, UifConstants.ControlType controlType,
            int size, int maxLength, int minLength) {
        InputField field = (InputField) getNewComponentInstance(INPUT_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);

        Control control = getControl(controlType);
        if (control instanceof SizedControl) {
            ((SizedControl) control).setSize(size);
        } else {
            throw new RuntimeException("Control does not support the size property");
        }

        field.setMaxLength(maxLength);
        field.setMinLength(minLength);

        return field;
    }

    /**
     * Builds a new <code>InputField</code> from the properties set on the
     * given <code>RemotableAttributeField</code>
     *
     * <p>
     * Note the returned InputField will not be initialized yet. Its state will be that of the initial
     * object returned from the UIF dictionary with the properties set from the remotable attribute field, thus it
     * is really just a more configuration complete field
     * </p>
     *
     * @return AttributeField instance built from remotable field
     */
    public static InputField translateRemotableField(RemotableAttributeField remotableField) {
        InputField inputField = getInputField();

        inputField.setPropertyName(remotableField.getName());
        inputField.setShortLabel(remotableField.getShortLabel());
        inputField.setLabel(remotableField.getLongLabel());
        inputField.setHelpSummary(remotableField.getHelpSummary());
        inputField.setHelpDescription(remotableField.getHelpDescription());
        inputField.setConstraintText(remotableField.getConstraintText());
        inputField.setPerformUppercase(remotableField.isForceUpperCase());
        inputField.setMinLength(remotableField.getMinLength());
        inputField.setMaxLength(remotableField.getMaxLength());

        // why are exclusive min and max strings?
        if (remotableField.getMinValue() != null) {
            inputField.setExclusiveMin(remotableField.getMinValue().toString());
        }
        if (remotableField.getMaxValue() != null) {
            inputField.setInclusiveMax(remotableField.getMaxValue().toString());
        }
        inputField.setRequired(remotableField.isRequired());

        if ((remotableField.getDefaultValues() != null) && !remotableField.getDefaultValues().isEmpty()) {
            inputField.setDefaultValue(remotableField.getDefaultValues().iterator().next());
        }

        if (StringUtils.isNotBlank(remotableField.getRegexConstraint())) {
            ValidCharactersConstraint constraint = new ValidCharactersConstraint();
            constraint.setValue(remotableField.getRegexConstraint());
            inputField.setValidCharactersConstraint(constraint);
            // TODO: how to deal with remotable field regexContraintMsg?
        }

        RemotableDatepicker remotableDatepicker = null;
        RemotableTextExpand remotableTextExpand = null;
        RemotableQuickFinder remotableQuickFinder = null;
        for (RemotableAbstractWidget remoteWidget : remotableField.getWidgets()) {
            if (remoteWidget instanceof RemotableDatepicker) {
                remotableDatepicker = (RemotableDatepicker) remoteWidget;
            } else if (remoteWidget instanceof RemotableTextExpand) {
                remotableTextExpand = (RemotableTextExpand) remoteWidget;
            } else if (remoteWidget instanceof RemotableQuickFinder) {
                remotableQuickFinder = (RemotableQuickFinder) remoteWidget;
            }
        }

        if (remotableQuickFinder != null) {
            inputField.getFieldLookup().setBaseLookupUrl(remotableQuickFinder.getBaseLookupUrl());
            inputField.getFieldLookup().setDataObjectClassName(remotableQuickFinder.getDataObjectClass());
            inputField.getFieldLookup().setLookupParameters(remotableQuickFinder.getLookupParameters());
            inputField.getFieldLookup().setFieldConversions(remotableQuickFinder.getFieldConversions());
        }

        if (remotableField.getControl() != null) {
            Control control = null;

            RemotableControlContract remotableControl = remotableField.getControl();
            if (remotableControl instanceof RemotableHiddenInput) {
                control = getHiddenControl();
            } else if (remotableControl instanceof RemotableRadioButtonGroup) {
                RemotableRadioButtonGroup remotableRadioButtonGroup = (RemotableRadioButtonGroup) remotableControl;
                control = getRadioGroupControl();
                ((RadioGroupControl) control).setOptions(buildKeyValuePairs(remotableRadioButtonGroup.getKeyLabels()));
            } else if (remotableControl instanceof RemotableSelect) {
                RemotableSelect remotableSelect = (RemotableSelect) remotableControl;
                control = getSelectControl();

                Map<String, String> keyLabels = new HashMap<String, String>();
                if ((remotableSelect.getGroups() != null) && (!remotableSelect.getGroups().isEmpty())) {
                    for (RemotableSelectGroup remotableSelectGroup : remotableSelect.getGroups()) {
                        keyLabels.putAll(remotableSelectGroup.getKeyLabels());
                    }
                } else {
                    keyLabels = remotableSelect.getKeyLabels();
                }

                ((SelectControl) control).setOptions(buildKeyValuePairs(keyLabels));
                if (remotableSelect.getSize() != null) {
                    ((SelectControl) control).setSize(remotableSelect.getSize());
                }
                ((SelectControl) control).setMultiple(remotableSelect.isMultiple());
            } else if (remotableControl instanceof RemotableCheckboxGroup) {
                RemotableCheckboxGroup remotableCheckboxGroup = (RemotableCheckboxGroup) remotableControl;
                control = getCheckboxGroupControl();
                ((CheckboxGroupControl) control).setOptions(buildKeyValuePairs(remotableCheckboxGroup.getKeyLabels()));
            } else if (remotableControl instanceof RemotableCheckbox) {
                control = getCheckboxControl();
            } else if (remotableControl instanceof RemotableTextarea) {
                RemotableTextarea remotableTextarea = (RemotableTextarea) remotableControl;
                control = getTextAreaControl();

                if (remotableTextExpand != null) {
                    ((TextAreaControl) control).setTextExpand(true);
                }
                ((TextAreaControl) control).setRows(remotableTextarea.getRows());
                ((TextAreaControl) control).setCols(remotableTextarea.getCols());
                ((TextAreaControl) control).setWatermarkText(remotableTextarea.getWatermark());

            } else if (remotableControl instanceof RemotableTextInput) {
                RemotableTextInput remotableTextInput = (RemotableTextInput) remotableControl;

                if (remotableDatepicker != null) {
                    control = getDateControl();
                } else {
                    control = getTextControl();
                }

                if (remotableTextExpand != null) {
                    ((TextAreaControl) control).setTextExpand(true);
                }
                ((TextControl) control).setSize(remotableTextInput.getSize());
                ((TextControl) control).setWatermarkText(remotableTextInput.getWatermark());
            }

            inputField.setControl(control);
        }

        return inputField;
    }

    /**
     * For each remotable field in the given list creates a new {@link org.kuali.rice.krad.uif.field.InputField}
     * instance and sets the
     * corresponding properties from the remotable instance
     *
     * @param remotableFields - list of remotable fields to translate
     * @return List<AttributeField> list of attribute fields built from the remotable field properties
     */
    public static List<InputField> translateRemotableFields(List<RemotableAttributeField> remotableFields) {
        List<InputField> inputFields = new ArrayList<InputField>();

        for (RemotableAttributeField remotableField : remotableFields) {
            inputFields.add(translateRemotableField(remotableField));
        }

        return inputFields;
    }

    protected static List<KeyValue> buildKeyValuePairs(Map<String, String> optionsMap) {
        List<KeyValue> options = new ArrayList<KeyValue>();

        for (Map.Entry<String, String> optionEntry : optionsMap.entrySet()) {
            KeyValue keyValue = new ConcreteKeyValue(optionEntry.getKey(), optionEntry.getValue());
            options.add(keyValue);
        }

        return options;
    }

    protected static Control getControl(UifConstants.ControlType controlType) {
        Control control = null;
        switch (controlType) {
            case CHECKBOX:
                control = getCheckboxControl();
            case CHECKBOXGROUP:
                control = getCheckboxGroupControl();
            case FILE:
                control = getFileControl();
            case GROUP:
                control = getGroupControl();
            case HIDDEN:
                control = getHiddenControl();
            case RADIOGROUP:
                control = getRadioGroupControl();
            case SELECT:
                control = getSelectControl();
            case TEXTAREA:
                control = getTextAreaControl();
            case TEXT:
                control = getTextControl();
            case USER:
                control = getUserControl();
        }

        return control;
    }

    public static ErrorsField getErrorsField() {
        return (ErrorsField) getNewComponentInstance(ERRORS_FIELD);
    }

    public static ActionField getActionField() {
        return (ActionField) getNewComponentInstance(ACTION_FIELD);
    }

    public static ActionField getActionLinkField() {
        return (ActionField) getNewComponentInstance(ACTION_LINK_FIELD);
    }

    public static LinkField getLinkField() {
        return (LinkField) getNewComponentInstance(LINK_FIELD);
    }

    public static IframeField getIframeField() {
        return (IframeField) getNewComponentInstance(IFRAME_FIELD);
    }

    public static ImageField getImageField() {
        return (ImageField) getNewComponentInstance(IMAGE_FIELD);
    }

    public static BlankField getBlankField() {
        return (BlankField) getNewComponentInstance(BLANK_FIELD);
    }

    public static GenericField getGenericField() {
        return (GenericField) getNewComponentInstance(GENERIC_FIELD);
    }

    public static LabelField getLabelField() {
        return (LabelField) getNewComponentInstance(LABEL_FIELD);
    }

    public static MessageField getMessageField() {
        return (MessageField) getNewComponentInstance(MESSAGE_FIELD);
    }

    public static FieldGroup getFieldGroup() {
        return (FieldGroup) getNewComponentInstance(FIELD_GROUP);
    }

    public static FieldGroup getHorizontalFieldGroup() {
        return (FieldGroup) getNewComponentInstance(HORIZONTAL_FIELD_GROUP);
    }

    public static Group getGroup() {
        return (Group) getNewComponentInstance(GROUP);
    }

    public static PageGroup getPageGroup() {
        return (PageGroup) getNewComponentInstance(PAGE_GROUP);
    }

    public static Group getGroupGridLayout() {
        return (Group) getNewComponentInstance(GROUP_GRID_LAYOUT);
    }

    public static Group getGroupBodyOnly() {
        return (Group) getNewComponentInstance(GROUP_BODY_ONLY);
    }

    public static Group getGroupGridBodyOnly() {
        return (Group) getNewComponentInstance(GROUP_GRID_BODY_ONLY);
    }

    public static TabGroup getTabGroup() {
        return (TabGroup) getNewComponentInstance(TAB_GROUP);
    }

    public static NavigationGroup getNavigationGroup() {
        return (NavigationGroup) getNewComponentInstance(NAVIGATION_GROUP);
    }

    public static TreeGroup getTreeGroup() {
        return (TreeGroup) getNewComponentInstance(TREE_GROUP);
    }

    public static LinkGroup getLinkGroup() {
        return (LinkGroup) getNewComponentInstance(LINK_GROUP);
    }

    public static CollectionGroup getCollectionGroup() {
        return (CollectionGroup) getNewComponentInstance(COLLECTION_GROUP);
    }

    public static CollectionGroup getCollectionGroupTableLayout() {
        return (CollectionGroup) getNewComponentInstance(COLLECTION_GROUP_TABLE_LAYOUT);
    }

    public static CollectionGroup getListGroup() {
        return (CollectionGroup) getNewComponentInstance(LIST_GROUP);
    }

    public static HeaderField getHeader() {
        return (HeaderField) getNewComponentInstance(HEADER);
    }

    public static Group getFooter() {
        return (Group) getNewComponentInstance(FOOTER);
    }

    public static Group getFooterSaveCloseCancel() {
        return (Group) getNewComponentInstance(FOOTER_SAVECLOSECANCEL);
    }

}
