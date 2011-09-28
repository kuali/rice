/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup;
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
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.BlankField;
import org.kuali.rice.krad.uif.field.ErrorsField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.GenericField;
import org.kuali.rice.krad.uif.field.HeaderField;
import org.kuali.rice.krad.uif.field.IframeField;
import org.kuali.rice.krad.uif.field.ImageField;
import org.kuali.rice.krad.uif.field.LabelField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

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

    protected static final String TEXT_CONTROL = "TextControl";
    protected static final String CHECKBOX_CONTROL = "CheckboxControl";
    protected static final String HIDDEN_CONTROL = "HiddenControl";
    protected static final String TEXTAREA_CONTROL = "TextAreaControl";
    protected static final String SELECT_CONTROL = "SelectControl";
    protected static final String CHECKBOX_GROUP_CONTROL = "CheckboxGroupControl";
    protected static final String CHECKBOX_GROUP_CONTROL_HORIZONTAL = "CheckboxGroupControlHorizontal";
    protected static final String RADIO_GROUP_CONTROL = "RadioGroupControl";
    protected static final String RADIO_GROUP_CONTROL_HORIZONTAL = "RadioGroupControlHorizontal";
    protected static final String FILE_CONTROL = "FileControl";
    protected static final String DATE_CONTROL = "DateControl";
    protected static final String USER_CONTROL = "UserControl";
    protected static final String GROUP_CONTROL = "GroupControl";

    protected static final String ATTRIBUTE_FIELD = "AttributeField";
    protected static final String ERRORS_FIELD = "ErrorsField";
    protected static final String ACTION_FIELD = "ActionField";
    protected static final String ACTION_LINK_FIELD = "ActionLinkField";
    protected static final String LINK_FIELD = "LinkField";
    protected static final String IFRAME_FIELD = "IframeField";
    protected static final String IMAGE_FIELD = "ImageField";
    protected static final String BLANK_FIELD = "BlankField";
    protected static final String GENERIC_FIELD = "GenericField";
    protected static final String LABEL_FIELD = "LabelField";
    protected static final String MESSAGE_FIELD = "MessageField";
    protected static final String FIELD_GROUP = "FieldGroup";
    protected static final String HORIZONTAL_FIELD_GROUP = "HorizontalFieldGroup";

    protected static final String GROUP = "Group";
    protected static final String PAGE_GROUP = "PageGroup";
    protected static final String GROUP_GRID_LAYOUT = "GroupGridLayout";
    protected static final String GROUP_BODY_ONLY = "GroupBodyOnly";
    protected static final String GROUP_GRID_BODY_ONLY = "GroupGridBodyOnly";
    protected static final String TAB_GROUP = "TabGroup";
    protected static final String NAVIGATION_GROUP = "NavigationGroup";
    protected static final String TREE_GROUP = "TreeGroup";
    protected static final String LINK_GROUP = "LinkGroup";
    protected static final String COLLECTION_GROUP = "CollectionGroup";
    protected static final String COLLECTION_GROUP_TABLE_LAYOUT = "CollectionGroupTableLayout";
    protected static final String LIST_GROUP = "ListGroup";

    protected static final String HEADER = "Header";
    protected static final String FOOTER = "Footer";
    protected static final String FOOTER_SAVECLOSECANCEL = "Footer_SaveCloseCancel";

    /**
     * Returns a new <code>Component</code> instance from the given component id initialized by the
     * corresponding initial configuration
     *
     * @param factoryId - factory id for the component to retrieve
     * @return new component instance or null if no such component definition was found
     * @see org.kuali.rice.krad.uif.component.Component#getFactoryId()
     */
    public static Component getNewComponentInstance(View view, String factoryId) {
        Component component = null;

        // first check if the view index contains an initial configuration for this id
        if (view.getViewIndex().getInitialComponentStates().containsKey(factoryId)) {
            component = view.getViewIndex().getInitialComponentStates().get(factoryId);
            component = ComponentUtils.copyObject(component);
        } else {
            // attempt to get an instance from the spring bean factory
            component = (Component) KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryObject(factoryId);
        }

        // clear id before returning so duplicates do not occur
        component.setId(null);

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

    public static AttributeField getAttributeField() {
        return (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);
    }

    public static AttributeField getAttributeField(String propertyName, String label) {
        AttributeField field = (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);

        return field;
    }

    public static AttributeField getAttributeField(String propertyName, String label,
            UifConstants.ControlType controlType) {
        AttributeField field = (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);
        field.setControl(getControl(controlType));

        return field;
    }

    public static AttributeField getAttributeField(String propertyName, String label,
            UifConstants.ControlType controlType, String defaultValue) {
        AttributeField field = (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);
        field.setControl(getControl(controlType));
        field.setDefaultValue(defaultValue);

        return field;
    }

    public static AttributeField getAttributeField(String propertyName, String label,
            UifConstants.ControlType controlType, Class<? extends KeyValuesFinder> optionsFinderClass) {
        AttributeField field = (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);

        field.setPropertyName(propertyName);
        field.setLabel(label);
        field.setControl(getControl(controlType));
        field.setOptionsFinderClass(optionsFinderClass);

        return field;
    }

    public static AttributeField getAttributeField(String propertyName, String label,
            UifConstants.ControlType controlType, List<KeyValue> options) {
        AttributeField field = (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);

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

    public static AttributeField getAttributeField(String propertyName, String label,
            UifConstants.ControlType controlType, int size, int maxLength, int minLength) {
        AttributeField field = (AttributeField) getNewComponentInstance(ATTRIBUTE_FIELD);

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
     * Builds a new <code>AttributeField</code> from the properties set on the
     * given <code>RemotableAttributeField</code>
     *
     * <p>
     * Note the returned AttributeField will not be initialized yet. Its state will be that of the initial
     * object returned from the UIF dictionary with the properties set from the remotable attribute field, thus it
     * is really just a more configuration complete field
     * </p>
     *
     * @return AttributeField instance built from remotable field
     */
    public static AttributeField translateRemotableField(RemotableAttributeField remotableField) {
        AttributeField attributeField = getAttributeField();

        attributeField.setPropertyName(remotableField.getName());
        attributeField.setShortLabel(remotableField.getShortLabel());
        attributeField.setLabel(remotableField.getLongLabel());
        attributeField.setHelpSummary(remotableField.getHelpSummary());
        attributeField.setHelpDescription(remotableField.getHelpDescription());
        attributeField.setConstraintText(remotableField.getHelpConstraint());
        attributeField.setPerformUppercase(remotableField.isForceUpperCase());
        attributeField.setMinLength(remotableField.getMinLength());
        attributeField.setMaxLength(remotableField.getMaxLength());

        // why are exclusive min and max strings?
        if (remotableField.getMinValue() != null) {
            attributeField.setExclusiveMin(remotableField.getMinValue().toString());
        }
        if (remotableField.getMaxValue() != null) {
            attributeField.setInclusiveMax(remotableField.getMaxValue().toString());
        }
        attributeField.setRequired(remotableField.isRequired());

        if ((remotableField.getDefaultValues() != null) && !remotableField.getDefaultValues().isEmpty()) {
           attributeField.setDefaultValue(remotableField.getDefaultValues().iterator().next());
        }

        if (StringUtils.isNotBlank(remotableField.getRegexConstraint())) {
            ValidCharactersConstraint constraint = new ValidCharactersConstraint();
            constraint.setValue(remotableField.getRegexConstraint());
            attributeField.setValidCharactersConstraint(constraint);
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
            attributeField.getFieldLookup().setBaseLookupUrl(remotableQuickFinder.getBaseLookupUrl());
            attributeField.getFieldLookup().setDataObjectClassName(remotableQuickFinder.getDataObjectClass());
            attributeField.getFieldLookup().setLookupParameters(remotableQuickFinder.getLookupParameters());
            attributeField.getFieldLookup().setFieldConversions(remotableQuickFinder.getFieldConversions());
        }

        if (remotableField.getControl() != null) {
            Control control = null;

            org.kuali.rice.core.api.uif.Control remotableControl = remotableField.getControl();
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

            attributeField.setControl(control);
        }

        return attributeField;
    }

    /**
     * For each remotable field in the given list creates a new {@link AttributeField} instance and sets the
     * corresponding properties from the remotable instance
     *
     * @param remotableFields - list of remotable fields to translate
     * @return List<AttributeField> list of attribute fields built from the remotable field properties
     */
    public static List<AttributeField> translateRemotableFields(List<RemotableAttributeField> remotableFields) {
        List<AttributeField> attributeFields = new ArrayList<AttributeField>();

        for (RemotableAttributeField remotableField : remotableFields) {
            attributeFields.add(translateRemotableField(remotableField));
        }

        return attributeFields;
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

    /**
     * Gets a fresh copy of the component by the id passed in which used to look up the component in
     * the view index, then retrieve a new instance with initial state configured using the factory id
     *
     * @param id - id for the component to retrieve
     * @return Component new instance
     */
    public static Component getComponentById(UifFormBase form, String id) {
        Component origComponent = form.getView().getViewIndex().getComponentById(id);
        Component component = getNewComponentInstance(form.getView(), origComponent.getFactoryId());

        return component;
    }

    /**
     * Gets a fresh copy of the component by the id passed in with its lifecycle performed upon it,
     * using the form data passed in
     *
     * @param form - object containing the view instance and data
     * @param id - id for the component to retrieve
     * @return Component instance that has been run through the lifecycle
     */
    public static Component getComponentByIdWithLifecycle(UifFormBase form, String id) {
        Component origComponent = form.getView().getViewIndex().getComponentById(id);
        Component component = getComponentById(form, id);

        form.getView().getViewHelperService().performComponentLifecycle(form, component, id);

        if (component instanceof Field) {
            ((Field) component).setLabelFieldRendered(((Field) origComponent).isLabelFieldRendered());
        }

        if (component instanceof AttributeField) {
            ((AttributeField) component).setBindingInfo(((AttributeField) origComponent).getBindingInfo());
        }

        if (component instanceof CollectionGroup) {
            ((CollectionGroup) component).setBindingInfo(((CollectionGroup) origComponent).getBindingInfo());
        }

        if (component instanceof Group || component instanceof FieldGroup) {
            List<AttributeField> fields = ComponentUtils.getComponentsOfTypeDeep(component, AttributeField.class);
            String suffix = StringUtils.replaceOnce(component.getId(), component.getFactoryId(), "");
            for (AttributeField field : fields) {
                AttributeField origField = (AttributeField) form.getView().getViewIndex().getComponentById(
                        StringUtils.replaceOnce(field.getId(), field.getFactoryId(), field.getFactoryId() + suffix));
                if (origField != null) {
                    field.setBindingInfo(origField.getBindingInfo());
                    field.setLabelFieldRendered(origField.isLabelFieldRendered());
                }
            }

            List<CollectionGroup> collections = ComponentUtils.getComponentsOfTypeDeep(component,
                    CollectionGroup.class);
            for (CollectionGroup collection : collections) {
                CollectionGroup origField = (CollectionGroup) form.getView().getViewIndex().getComponentById(
                        StringUtils.replaceOnce(collection.getId(), collection.getFactoryId(),
                                collection.getFactoryId() + suffix));
                if (origField != null) {
                    collection.setBindingInfo(origField.getBindingInfo());
                }
            }
        }

        form.getView().getViewIndex().indexComponent(component);

        return component;
    }

}
