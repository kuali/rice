/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.lifecycle.LifecycleTaskFactory;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleAwareList;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Help;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Field that renders data from the application, such as the value of a data object property
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "dataField-bean", parent = "Uif-DataField"),
        @BeanTag(name = "dataField-labelTop-bean", parent = "Uif-DataField-LabelTop"),
        @BeanTag(name = "dataField-labelRight-bean", parent = "Uif-DataField-LabelRight"),
        @BeanTag(name = "dataField-withoutLabel-bean", parent = "Uif-DataField-withoutLabel")})
public class DataFieldBase extends FieldBase implements DataField {
    private static final long serialVersionUID = -4129678891948564724L;

    // binding
    private String propertyName;
    private BindingInfo bindingInfo;

    private String dictionaryAttributeName;
    private String dictionaryObjectEntry;

    // value props
    private String defaultValue;
    private Class<? extends ValueFinder> defaultValueFinderClass;
    private Object[] defaultValues;
    private String forcedValue;

    private PropertyEditor propertyEditor;

    private boolean addHiddenWhenReadOnly;

    // read only display properties
    protected String readOnlyDisplayReplacementPropertyName;
    protected String readOnlyDisplaySuffixPropertyName;

    private String readOnlyDisplayReplacement;
    private String readOnlyDisplaySuffix;

    private String readOnlyListDisplayType;
    private String readOnlyListDelimiter;

    private boolean applyMask;
    private MaskFormatter maskFormatter;

    private List<String> additionalHiddenPropertyNames;
    private List<String> propertyNamesForAdditionalDisplay;

    private boolean escapeHtmlInPropertyValue;
    private boolean multiLineReadOnlyDisplay;

    // widgets
    private Inquiry inquiry;
    private boolean enableAutoInquiry;

    private Help help;

    // Optional span render flags
    private boolean renderInfoMessageSpan;
    private boolean renderMarkerIconSpan;

    public DataFieldBase() {
        super();

        enableAutoInquiry = true;
        escapeHtmlInPropertyValue = true;

        additionalHiddenPropertyNames = Collections.emptyList();
        propertyNamesForAdditionalDisplay = Collections.emptyList();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#performInitialization(java.lang.Object)
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(ViewLifecycle.getView(), getPropertyName());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#performApplyModel(java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(Object model, Component parent) {
        super.performApplyModel(model, parent);

        if (this.enableAutoInquiry && (this.inquiry == null) && isReadOnly()) {
            buildAutomaticInquiry(model, false);
        }

        if (isAddHiddenWhenReadOnly()) {
            setReadOnly(true);
            getAdditionalHiddenPropertyNames().add(getPropertyName());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#performFinalize(java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(Object model, Component parent) {
        super.performFinalize(model, parent);

        // adjust the path for hidden fields
        // TODO: should this check the view#readOnly?
        List<String> hiddenPropertyPaths = new ArrayList<String>();
        for (String hiddenPropertyName : getAdditionalHiddenPropertyNames()) {
            String hiddenPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(hiddenPropertyName);
            hiddenPropertyPaths.add(hiddenPropertyPath);
        }
        this.additionalHiddenPropertyNames = hiddenPropertyPaths;

        // adjust paths on informational property names
        List<String> informationalPropertyPaths = new ArrayList<String>();
        for (String infoPropertyName : getPropertyNamesForAdditionalDisplay()) {
            String infoPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(infoPropertyName);
            informationalPropertyPaths.add(infoPropertyPath);
        }
        this.propertyNamesForAdditionalDisplay = informationalPropertyPaths;

        //Special processing for List<?> readOnly
        String bindingPath = getBindingInfo().getBindingPath();
        Class<?> type = StringUtils.isNotEmpty(bindingPath)
                ? ObjectPropertyUtils.getPropertyType(model, bindingPath)
                : null;
        if (this.isReadOnly() && type != null && List.class.isAssignableFrom(type) && StringUtils.isBlank(
                getReadOnlyDisplayReplacement()) && StringUtils.isBlank(getReadOnlyDisplayReplacementPropertyName())) {
            //get the list
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());

            //check for null, empty or non-simple type content (not supported by DataField)
            if (fieldValue != null && fieldValue instanceof List<?> && !((List) fieldValue).isEmpty()) {
                List<?> list = (List<?>) fieldValue;
                processReadOnlyListDisplay(model, list);
            } else {
                this.setReadOnlyDisplayReplacement("&nbsp;");
            }

        } else {
            // Additional and Alternate display value
            setAlternateAndAdditionalDisplayValue(ViewLifecycle.getView(), model);
        }

        if (this.getFieldLabel() != null && StringUtils.isNotBlank(this.getId())) {
            this.getFieldLabel().setLabelForComponentId(this.getId() + UifConstants.IdSuffixes.CONTROL);
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#initializePendingTasks(org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase, java.util.Queue)
     */
    @Override
    public void initializePendingTasks(ViewLifecyclePhase phase, Queue<ViewLifecycleTask> pendingTasks) {
        if (phase.getViewPhase().equals(UifConstants.ViewPhases.INITIALIZE)) {
            pendingTasks.offer(LifecycleTaskFactory.getTask(InitializeDataFieldFromDictionaryTask.class, phase));
        }
        
        super.initializePendingTasks(phase, pendingTasks);
    }

    /**
     * Creates a new {@link org.kuali.rice.krad.uif.widget.Inquiry} and then invokes the lifecycle process for
     * the inquiry to determine if a relationship was found, if so the inquiry is assigned to the field
     *
     * @param view view instance being processed
     * @param model object containing the view data
     * @param enableDirectInquiry whether direct inquiry should be enabled if an inquiry is found
     */
    protected void buildAutomaticInquiry(Object model, boolean enableDirectInquiry) {
        Inquiry autoInquiry = ComponentFactory.getInquiry();

        ViewLifecycle.spawnSubLifecyle(model, autoInquiry, this);

        // if render flag is true, that means the inquiry was able to find a relationship
        if (autoInquiry.isRender()) {
            this.inquiry = autoInquiry;
        }
    }

    /**
     * This method is called when the list is readOnly as determined in DataField's performFinalize method.  This
     * method
     * should be overridden to perform any additional processing to the values before calling
     * generateReadOnlyListDisplayReplacement.  The default implementation calls it directly with the originalList.
     *
     * @param model the model
     * @param originalList originalList of values
     */
    protected void processReadOnlyListDisplay(Object model, List<?> originalList) {
        this.setReadOnlyDisplayReplacement(generateReadOnlyListDisplayReplacement(originalList));
    }

    /**
     * Generates the html to be used and sets the readOnlyDisplayReplacement for DataFields that contain lists and
     * do not have their own readOnlyDisplayReplacement defined.  The type of html generated is based on the options
     * set on readOnlyListDisplayType and readOnlyListDelimiter.
     *
     * @param list the list to be converted to readOnly html
     */
    protected String generateReadOnlyListDisplayReplacement(List<?> list) {
        String generatedHtml = "";

        //Default to delimited if nothing is set
        if (getReadOnlyListDisplayType() == null) {
            this.setReadOnlyListDisplayType(UifConstants.ReadOnlyListTypes.DELIMITED.name());
        }

        //begin generation setup
        if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.UL.name())) {
            generatedHtml = "<ul class='uif-readOnlyStringList'>";
        } else if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.OL.name())) {
            generatedHtml = "<ol class='uif-readOnlyStringList'>";
        } else if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.BREAK.name())) {
            setReadOnlyListDelimiter("<br/>");
        } else if (this.getReadOnlyListDelimiter() == null) {
            setReadOnlyListDelimiter(", ");
        }

        //iterate through each value
        for (Object value : list) {
            //if blank skip
            if (!TypeUtils.isSimpleType(value.getClass()) || StringUtils.isBlank(value.toString())) {
                continue;
            }

            //handle mask if any
            if (isApplyMask()) {
                value = getMaskFormatter().maskValue(value);
            }

            //TODO the value should use the formatted text property value we would expect to see instead of toString
            //two types - delimited and html list
            if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.UL.name())
                    || getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.OL.name())) {
                generatedHtml = generatedHtml + "<li>" + StringEscapeUtils.escapeHtml(value.toString()) + "</li>";
            } else {
                //no matching needed - delimited is always the fallback and break uses same logic
                generatedHtml = generatedHtml + StringEscapeUtils.escapeHtml(value.toString())
                        + this.getReadOnlyListDelimiter();
            }
        }

        //end the generation
        if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.UL.name())) {
            generatedHtml = generatedHtml + "</ul>";
        } else if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.OL.name())) {
            generatedHtml = generatedHtml + "</ol>";
        } else {
            generatedHtml = StringUtils.removeEnd(generatedHtml, this.getReadOnlyListDelimiter());
        }

        if (StringUtils.isNotBlank(generatedHtml)) {
            return generatedHtml;
        } else {
            //this must be done or the ftl will skip and throw error
            return "&nbsp;";
        }
    }

    /**
     * Sets alternate and additional property value for this field.
     *
     * <p>
     * If <code>AttributeSecurity</code> present in this field, make sure the current user has permission to view the
     * field value. If user doesn't have permission to view the value, mask the value as configured and set it
     * as alternate value for display. If security doesn't exists for this field but
     * <code>alternateDisplayPropertyName</code> present, get its value and format it based on that
     * fields formatting and set for display.
     * </p>
     *
     * <p>
     * For additional display value, if <code>AttributeSecurity</code> not present, sets the value if
     * <code>additionalDisplayPropertyName</code> present. If not present, check whether this field is a
     * <code>KualiCode</code> and get the relationship configured in the datadictionary file and set the name
     * additional display value which will be displayed along with the code. If additional display property not
     * present, check whether this field is has <code>MultiValueControlBase</code>. If yes, get the Label
     * for the value and set it as additional display value.
     * </p>
     *
     * @param view the current view instance
     * @param model model instance
     */
    protected void setAlternateAndAdditionalDisplayValue(View view, Object model) {
        // if alternate or additional display values set don't use property names
        if (StringUtils.isNotBlank(readOnlyDisplayReplacement) || StringUtils.isNotBlank(readOnlyDisplaySuffix)) {
            return;
        }

        // check whether field value needs to be masked, and if so apply masking as alternateDisplayValue
        if (isApplyMask()) {
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());
            if (getMaskFormatter() != null) {
                readOnlyDisplayReplacement = getMaskFormatter().maskValue(fieldValue);
            }

            return;
        }

        // if not read only, return without trying to set alternate and additional values
        if (!isReadOnly()) {
            return;
        }

        // if field is not secure, check for alternate and additional display properties
        if (StringUtils.isNotBlank(getReadOnlyDisplayReplacementPropertyName())) {
            String alternateDisplayPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(
                    getReadOnlyDisplayReplacementPropertyName());

            Object alternateFieldValue = ObjectPropertyUtils.getPropertyValue(model, alternateDisplayPropertyPath);
            if (alternateFieldValue != null) {
                // TODO: format by type
                readOnlyDisplayReplacement = alternateFieldValue.toString();
            }
        }

        // perform automatic translation for code references if enabled on view
        if (StringUtils.isBlank(getReadOnlyDisplaySuffixPropertyName()) && view.isTranslateCodesOnReadOnlyDisplay()) {
            // check for any relationship present for this field and it's of type KualiCode
            Class<?> parentObjectClass = ViewModelUtils.getParentObjectClassForMetadata(view, model, this);
            DataObjectRelationship relationship =
                    KRADServiceLocatorWeb.getLegacyDataAdapter().getDataObjectRelationship(null,
                            parentObjectClass, getBindingInfo().getBindingName(), "", true, false, false);

            if (relationship != null
                    && getPropertyName().startsWith(relationship.getParentAttributeName())
                    && KualiCode.class.isAssignableFrom(relationship.getRelatedClass())) {
                readOnlyDisplaySuffixPropertyName =
                        relationship.getParentAttributeName() + "." + KRADPropertyConstants.NAME;
            }
        }

        // now check for an additional display property and if set get the value
        if (StringUtils.isNotBlank(getReadOnlyDisplaySuffixPropertyName())) {
            String additionalDisplayPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(
                    getReadOnlyDisplaySuffixPropertyName());

            Object additionalFieldValue = ObjectPropertyUtils.getPropertyValue(model, additionalDisplayPropertyPath);
            if (additionalFieldValue != null) {
                // TODO: format by type
                readOnlyDisplaySuffix = additionalFieldValue.toString();
            }
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#copyFromAttributeDefinition(org.kuali.rice.krad.datadictionary.AttributeDefinition)
     */
    @Override
    public void copyFromAttributeDefinition(AttributeDefinition attributeDefinition) {
        // label
        if (StringUtils.isEmpty(getLabel())) {
            setLabel(attributeDefinition.getLabel());
        }

        // short label
        if (StringUtils.isEmpty(getShortLabel())) {
            setShortLabel(attributeDefinition.getShortLabel());
        }

        // security
        if ((attributeDefinition.getAttributeSecurity() != null) && ((getDataFieldSecurity() == null) || (
                getDataFieldSecurity().getAttributeSecurity() == null))) {
            initializeComponentSecurity();

            getDataFieldSecurity().setAttributeSecurity(attributeDefinition.getAttributeSecurity());
        }

        // alternate property name
        if (getReadOnlyDisplayReplacementPropertyName() == null && StringUtils.isNotBlank(
                attributeDefinition.getAlternateDisplayAttributeName())) {
            setReadOnlyDisplayReplacementPropertyName(attributeDefinition.getAlternateDisplayAttributeName());
        }

        // additional property display name
        if (getReadOnlyDisplaySuffixPropertyName() == null && StringUtils.isNotBlank(
                attributeDefinition.getAdditionalDisplayAttributeName())) {
            setReadOnlyDisplaySuffixPropertyName(attributeDefinition.getAdditionalDisplayAttributeName());
        }

        // property editor
        if (getPropertyEditor() == null) {
            setPropertyEditor(attributeDefinition.getPropertyEditor());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isInputAllowed()
     */
    @Override
    public boolean isInputAllowed() {
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getPropertyName()
     */
    @Override
    @BeanTagAttribute(name = "propertyName")
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setPropertyName(java.lang.String)
     */
    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getPropertyEditor()
     */
    @Override
    @BeanTagAttribute(name = "propertyEditor", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setPropertyEditor(java.beans.PropertyEditor)
     */
    @Override
    public void setPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setPropertyEditorClass(java.lang.Class)
     */
    @Override
    public void setPropertyEditorClass(Class<? extends PropertyEditor> propertyEditorClass) {
        this.propertyEditor = DataObjectUtils.newInstance(propertyEditorClass);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getBindingInfo()
     */
    @Override
    @BeanTagAttribute(name = "bindingInfo", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setBindingInfo(org.kuali.rice.krad.uif.component.BindingInfo)
     */
    @Override
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getName()
     */
    @Override
    public String getName() {
        return this.getBindingInfo().getBindingPath();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getDictionaryAttributeName()
     */
    @Override
    @BeanTagAttribute(name = "dictionaryAttributeName")
    public String getDictionaryAttributeName() {
        return this.dictionaryAttributeName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setDictionaryAttributeName(java.lang.String)
     */
    @Override
    public void setDictionaryAttributeName(String dictionaryAttributeName) {
        this.dictionaryAttributeName = dictionaryAttributeName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getDictionaryObjectEntry()
     */
    @Override
    @BeanTagAttribute(name = "dictionaryObjectEntry")
    public String getDictionaryObjectEntry() {
        return this.dictionaryObjectEntry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setDictionaryObjectEntry(java.lang.String)
     */
    @Override
    public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
        this.dictionaryObjectEntry = dictionaryObjectEntry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getDefaultValue()
     */
    @Override
    @BeanTagAttribute(name = "defaultValue")
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setDefaultValue(java.lang.String)
     */
    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getDefaultValueFinderClass()
     */
    @Override
    @BeanTagAttribute(name = "defaultValueFinderClass")
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return this.defaultValueFinderClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setDefaultValueFinderClass(java.lang.Class)
     */
    @Override
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        this.defaultValueFinderClass = defaultValueFinderClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getDefaultValues()
     */
    @Override
    @BeanTagAttribute(name = "defaultValues", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public Object[] getDefaultValues() {
        return this.defaultValues;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setDefaultValues(java.lang.Object[])
     */
    @Override
    public void setDefaultValues(Object[] defaultValues) {
        this.defaultValues = defaultValues;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getForcedValue()
     */
    @Override
    public String getForcedValue() {
        return forcedValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setForcedValue(java.lang.String)
     */
    @Override
    public void setForcedValue(String forcedValue) {
        this.forcedValue = forcedValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getHelpSummary()
     */
    @Override
    @BeanTagAttribute(name = "helpSummary")
    public String getHelpSummary() {
        return this.help.getTooltipHelpContent();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setHelpSummary(java.lang.String)
     */
    @Override
    public void setHelpSummary(String helpSummary) {
        this.help.setTooltipHelpContent(helpSummary);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getDataFieldSecurity()
     */
    @Override
    public DataFieldSecurity getDataFieldSecurity() {
        return (DataFieldSecurity) super.getComponentSecurity();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setComponentSecurity(org.kuali.rice.krad.uif.component.ComponentSecurity)
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if ((componentSecurity != null) && !(componentSecurity instanceof DataFieldSecurity)) {
            throw new RiceRuntimeException("Component security for DataField should be instance of DataFieldSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#initializeComponentSecurity()
     */
    @Override
    protected void initializeComponentSecurity() {
        if (getComponentSecurity() == null) {
            setComponentSecurity(DataObjectUtils.newInstance(DataFieldSecurity.class));
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isAddHiddenWhenReadOnly()
     */
    @Override
    @BeanTagAttribute(name = "addHiddenWhenReadOnly")
    public boolean isAddHiddenWhenReadOnly() {
        return addHiddenWhenReadOnly;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setAddHiddenWhenReadOnly(boolean)
     */
    @Override
    public void setAddHiddenWhenReadOnly(boolean addHiddenWhenReadOnly) {
        this.addHiddenWhenReadOnly = addHiddenWhenReadOnly;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getInquiry()
     */
    @Override
    @BeanTagAttribute(name = "inquiry", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Inquiry getInquiry() {
        return this.inquiry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setInquiry(org.kuali.rice.krad.uif.widget.Inquiry)
     */
    @Override
    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isEnableAutoInquiry()
     */
    @Override
    public boolean isEnableAutoInquiry() {
        return enableAutoInquiry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setEnableAutoInquiry(boolean)
     */
    @Override
    public void setEnableAutoInquiry(boolean enableAutoInquiry) {
        this.enableAutoInquiry = enableAutoInquiry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getHelp()
     */
    @Override
    @BeanTagAttribute(name = "help", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Help getHelp() {
        return this.help;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setHelp(org.kuali.rice.krad.uif.widget.Help)
     */
    @Override
    public void setHelp(Help help) {
        this.help = help;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isRenderInfoMessageSpan()
     */
    @Override
    public boolean isRenderInfoMessageSpan() {
        return renderInfoMessageSpan;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setRenderInfoMessageSpan(boolean)
     */
    @Override
    public void setRenderInfoMessageSpan(boolean renderInfoMessageSpan) {
        this.renderInfoMessageSpan = renderInfoMessageSpan;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isRenderMarkerIconSpan()
     */
    @Override
    public boolean isRenderMarkerIconSpan() {
        return renderMarkerIconSpan;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setRenderMarkerIconSpan(boolean)
     */
    @Override
    public void setRenderMarkerIconSpan(boolean renderMarkerIconSpan) {
        this.renderMarkerIconSpan = renderMarkerIconSpan;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setTooltipOfComponent(org.kuali.rice.krad.uif.widget.Tooltip)
     */
    @Override
    @BeanTagAttribute(name = "tooltipOfComponent", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public void setTooltipOfComponent(Tooltip tooltip) {
        getFieldLabel().setToolTip(tooltip);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getHelpTitle()
     */
    @Override
    public String getHelpTitle() {
        return this.getLabel();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setReadOnlyDisplaySuffixPropertyName(java.lang.String)
     */
    @Override
    public void setReadOnlyDisplaySuffixPropertyName(String readOnlyDisplaySuffixPropertyName) {
        this.readOnlyDisplaySuffixPropertyName = readOnlyDisplaySuffixPropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getReadOnlyDisplaySuffixPropertyName()
     */
    @Override
    @BeanTagAttribute(name = "readOnlyDisplaceSuffixPropertyName")
    public String getReadOnlyDisplaySuffixPropertyName() {
        return this.readOnlyDisplaySuffixPropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setReadOnlyDisplayReplacementPropertyName(java.lang.String)
     */
    @Override
    public void setReadOnlyDisplayReplacementPropertyName(String readOnlyDisplayReplacementPropertyName) {
        this.readOnlyDisplayReplacementPropertyName = readOnlyDisplayReplacementPropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getReadOnlyDisplayReplacementPropertyName()
     */
    @Override
    @BeanTagAttribute(name = "readOnlyDisplayReplacementPropertyName")
    public String getReadOnlyDisplayReplacementPropertyName() {
        return this.readOnlyDisplayReplacementPropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getReadOnlyDisplayReplacement()
     */
    @Override
    @BeanTagAttribute(name = "readOnlyDisplayReplacement")
    public String getReadOnlyDisplayReplacement() {
        return readOnlyDisplayReplacement;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setReadOnlyDisplayReplacement(java.lang.String)
     */
    @Override
    public void setReadOnlyDisplayReplacement(String value) {
        this.readOnlyDisplayReplacement = value;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getReadOnlyDisplaySuffix()
     */
    @Override
    @BeanTagAttribute(name = "readOnlyDispalySuffix")
    public String getReadOnlyDisplaySuffix() {
        return readOnlyDisplaySuffix;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setReadOnlyDisplaySuffix(java.lang.String)
     */
    @Override
    public void setReadOnlyDisplaySuffix(String value) {
        this.readOnlyDisplaySuffix = value;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getReadOnlyListDisplayType()
     */
    @Override
    public String getReadOnlyListDisplayType() {
        return readOnlyListDisplayType;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setReadOnlyListDisplayType(java.lang.String)
     */
    @Override
    public void setReadOnlyListDisplayType(String readOnlyListDisplayType) {
        this.readOnlyListDisplayType = readOnlyListDisplayType;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getReadOnlyListDelimiter()
     */
    @Override
    public String getReadOnlyListDelimiter() {
        return readOnlyListDelimiter;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setReadOnlyListDelimiter(java.lang.String)
     */
    @Override
    public void setReadOnlyListDelimiter(String readOnlyListDelimiter) {
        this.readOnlyListDelimiter = readOnlyListDelimiter;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isApplyMask()
     */
    @Override
    @BeanTagAttribute(name = "applyMask")
    public boolean isApplyMask() {
        return applyMask;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setApplyMask(boolean)
     */
    @Override
    public void setApplyMask(boolean applyMask) {
        this.applyMask = applyMask;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getMaskFormatter()
     */
    @Override
    @BeanTagAttribute(name = "maskFormatter", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public MaskFormatter getMaskFormatter() {
        return maskFormatter;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setMaskFormatter(org.kuali.rice.krad.datadictionary.mask.MaskFormatter)
     */
    @Override
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getAdditionalHiddenPropertyNames()
     */
    @Override
    @BeanTagAttribute(name = "additionalHiddenPropertyNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalHiddenPropertyNames() {
        if (additionalHiddenPropertyNames == Collections.EMPTY_LIST && isMutable(true)) {
            additionalHiddenPropertyNames = new LifecycleAwareList<String>(this);
        }
        
        return additionalHiddenPropertyNames;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setAdditionalHiddenPropertyNames(java.util.List)
     */
    @Override
    public void setAdditionalHiddenPropertyNames(List<String> additionalHiddenPropertyNames) {
        if (additionalHiddenPropertyNames == null) {
            this.additionalHiddenPropertyNames = Collections.emptyList();
        } else {
            this.additionalHiddenPropertyNames =
                    new LifecycleAwareList<String>(this, additionalHiddenPropertyNames);
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#getPropertyNamesForAdditionalDisplay()
     */
    @Override
    @BeanTagAttribute(name = "propertyNamesForAdditionalDisplay", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getPropertyNamesForAdditionalDisplay() {
        if (propertyNamesForAdditionalDisplay == Collections.EMPTY_LIST && isMutable(true)) {
            propertyNamesForAdditionalDisplay = new LifecycleAwareList<String>(this);
        }
        
        return propertyNamesForAdditionalDisplay;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setPropertyNamesForAdditionalDisplay(java.util.List)
     */
    @Override
    public void setPropertyNamesForAdditionalDisplay(List<String> propertyNamesForAdditionalDisplay) {
        if (propertyNamesForAdditionalDisplay == null) {
            this.propertyNamesForAdditionalDisplay = Collections.emptyList();
        } else {
            this.propertyNamesForAdditionalDisplay =
                    new LifecycleAwareList<String>(this, propertyNamesForAdditionalDisplay);
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setEscapeHtmlInPropertyValue(boolean)
     */
    @Override
    public void setEscapeHtmlInPropertyValue(boolean escapeHtmlInPropertyValue) {
        this.escapeHtmlInPropertyValue = escapeHtmlInPropertyValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isEscapeHtmlInPropertyValue()
     */
    @Override
    @BeanTagAttribute(name = "escapeHtmlInPropertyValue")
    public boolean isEscapeHtmlInPropertyValue() {
        return this.escapeHtmlInPropertyValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isMultiLineReadOnlyDisplay()
     */
    @Override
    public boolean isMultiLineReadOnlyDisplay() {
        return multiLineReadOnlyDisplay;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#setMultiLineReadOnlyDisplay(boolean)
     */
    @Override
    public void setMultiLineReadOnlyDisplay(boolean multiLineReadOnlyDisplay) {
        this.multiLineReadOnlyDisplay = multiLineReadOnlyDisplay;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#hasSecureValue()
     */
    @Override
    public boolean hasSecureValue() {
        boolean hasHideAuthz = false;

        if (getDataFieldSecurity() != null) {
            boolean isViewAuthz = false;
            boolean isViewInLineAuthz = false;
            boolean isHide = false;

            if (getDataFieldSecurity().isViewAuthz() != null) {
                isViewAuthz = getDataFieldSecurity().isViewAuthz().booleanValue();
            }

            if (getDataFieldSecurity().isViewInLineAuthz() != null) {
                isViewInLineAuthz = getDataFieldSecurity().isViewInLineAuthz().booleanValue();
            }

            if (getDataFieldSecurity().getAttributeSecurity() != null) {
                isHide = getDataFieldSecurity().getAttributeSecurity().isHide();
            }

            hasHideAuthz = isViewAuthz || isViewInLineAuthz || isHide;
        }

        return isApplyMask() || (hasHideAuthz && isHidden());
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#isRenderFieldset()
     */
    @Override
    public boolean isRenderFieldset() {
        return (!this.isReadOnly()
                && inquiry != null
                && inquiry.isRender()
                && inquiry.getInquiryLink() != null
                && inquiry.getInquiryLink().isRender()) || (help != null
                && help.isRender()
                && help.getHelpAction() != null
                && help.getHelpAction().isRender());
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#copy()
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        DataFieldBase dataFieldCopy = (DataFieldBase) component;

        dataFieldCopy.setAddHiddenWhenReadOnly(this.addHiddenWhenReadOnly);
        dataFieldCopy.setAdditionalHiddenPropertyNames(new ArrayList<String>(this.additionalHiddenPropertyNames));
        dataFieldCopy.setApplyMask(this.applyMask);
        dataFieldCopy.setMaskFormatter(this.maskFormatter);

        if (this.bindingInfo != null) {
            dataFieldCopy.setBindingInfo((BindingInfo) this.bindingInfo.copy());
        }

        dataFieldCopy.setDefaultValue(this.defaultValue);
        dataFieldCopy.setDefaultValues(this.defaultValues);
        dataFieldCopy.setDictionaryAttributeName(this.dictionaryAttributeName);
        dataFieldCopy.setDictionaryObjectEntry(this.dictionaryObjectEntry);
        dataFieldCopy.setEnableAutoInquiry(this.enableAutoInquiry);
        dataFieldCopy.setEscapeHtmlInPropertyValue(this.escapeHtmlInPropertyValue);
        dataFieldCopy.setForcedValue(this.forcedValue);
        dataFieldCopy.setMultiLineReadOnlyDisplay(this.multiLineReadOnlyDisplay);
        dataFieldCopy.setPropertyEditor(this.propertyEditor);
        dataFieldCopy.setPropertyName(this.propertyName);

        if (this.propertyNamesForAdditionalDisplay != null) {
            dataFieldCopy.setPropertyNamesForAdditionalDisplay(new ArrayList<String>(
                    this.propertyNamesForAdditionalDisplay));
        }

        dataFieldCopy.setReadOnlyDisplayReplacement(this.readOnlyDisplayReplacement);
        dataFieldCopy.setReadOnlyDisplayReplacementPropertyName(this.readOnlyDisplayReplacementPropertyName);
        dataFieldCopy.setReadOnlyDisplaySuffix(this.readOnlyDisplaySuffix);
        dataFieldCopy.setReadOnlyDisplaySuffixPropertyName(this.readOnlyDisplaySuffixPropertyName);
        dataFieldCopy.setReadOnlyListDelimiter(this.readOnlyListDelimiter);
        dataFieldCopy.setReadOnlyListDisplayType(this.readOnlyListDisplayType);
        dataFieldCopy.setDefaultValueFinderClass(this.defaultValueFinderClass);

        if (this.help != null) {
            dataFieldCopy.setHelp((Help) this.help.copy());
        }

        if (this.inquiry != null) {
            dataFieldCopy.setInquiry((Inquiry) this.inquiry.copy());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.DataField#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that the property is connected to the field
        if (getPropertyName() == null) {
            if (!Validator.checkExpressions(this, "propertyName")) {
                String currentValues[] = {"propertyName = " + getPropertyName()};
                tracer.createError("Property name not set", currentValues);
            }
        }

        // Checks that the default values  present
/*        if (getDefaultValue() != null && getDefaultValues() != null) {
            String currentValues[] =
                    {"defaultValue =" + getDefaultValue(), "defaultValues Size =" + getDefaultValues().length};
            tracer.createWarning("Both Default Value and Default Values set", currentValues);
        }*/

        // Checks that a mask formatter is set if the data field is to be masked
        if (isApplyMask()) {
            if (maskFormatter == null) {
                String currentValues[] = {"applyMask =" + isApplyMask(), "maskFormatter =" + maskFormatter};
                tracer.createWarning("Apply mask is true, but no value is set for maskFormatter", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
