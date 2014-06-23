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
package org.kuali.rice.krad.uif.field;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleAwareList;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.Help;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.krad.web.form.InquiryForm;

/**
 * Field that renders data from the application, such as the value of a data object property
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "data", parent = "Uif-DataField"),
        @BeanTag(name = "dataLabelTop", parent = "Uif-DataField-LabelTop"),
        @BeanTag(name = "dataLabelRight", parent = "Uif-DataField-LabelRight"),
        @BeanTag(name = "dataNoLabel", parent = "Uif-DataField-withoutLabel")})
public class DataFieldBase extends FieldBase implements DataField {
    private static final long serialVersionUID = -4129678891948564724L;

    // binding
    private String propertyName;
    private BindingInfo bindingInfo;

    private String dictionaryAttributeName;
    private String dictionaryObjectEntry;

    // value props
    private Object defaultValue;
    private Class<? extends ValueFinder> defaultValueFinderClass;
    private List<Object> defaultValues;
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

    private String sortAs;

    public DataFieldBase() {
        super();

        enableAutoInquiry = true;
        escapeHtmlInPropertyValue = true;

        additionalHiddenPropertyNames = Collections.emptyList();
        propertyNamesForAdditionalDisplay = Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(ViewLifecycle.getView(), getPropertyName());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        // set to true before calling super.
        if (getReadOnly() == null) {
            setReadOnly(true);
        }
        
        super.afterEvaluateExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (enableAutoInquiry && this.inquiry == null && getReadOnly() && hasAutoInquiryRelationship()) {
            this.inquiry = ComponentFactory.getInquiry();
        }

        if (isAddHiddenWhenReadOnly()) {
            setReadOnly(true);
            getAdditionalHiddenPropertyNames().add(getPropertyName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        // adjust the path for hidden fields and add as accessible paths
        List<String> hiddenPropertyPaths = new ArrayList<String>();
        for (String hiddenPropertyName : getAdditionalHiddenPropertyNames()) {
            String hiddenPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(hiddenPropertyName);
            hiddenPropertyPaths.add(hiddenPropertyPath);

            if (isRender() || StringUtils.isNotBlank(getProgressiveRender())) {
                ViewLifecycle.getViewPostMetadata().addAccessibleBindingPath(hiddenPropertyPath);
            }
        }
        this.additionalHiddenPropertyNames = hiddenPropertyPaths;

        // adjust paths on informational property names
        List<String> informationalPropertyPaths = new ArrayList<String>();
        for (String infoPropertyName : getPropertyNamesForAdditionalDisplay()) {
            String infoPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(infoPropertyName);
            informationalPropertyPaths.add(infoPropertyPath);
        }
        this.propertyNamesForAdditionalDisplay = informationalPropertyPaths;

        // process read-only lists and additional and alternate display values
        boolean hasPropertyEditor = getPropertyEditor() != null;
        boolean hasReadOnlyDisplayReplacement = StringUtils.isNotBlank(getReadOnlyDisplayReplacement());
        boolean hasReadOnlyDisplayReplacementPropertyName = StringUtils.isNotBlank(
                getReadOnlyDisplayReplacementPropertyName());
        String bindingPath = getBindingInfo().getBindingPath();
        Class<?> type = StringUtils.isNotEmpty(bindingPath) ? ObjectPropertyUtils.getPropertyType(model, bindingPath) : null;
        boolean isReadOnlyList = Boolean.TRUE.equals(getReadOnly()) && type != null && List.class.isAssignableFrom(type);

        if (!hasPropertyEditor && !hasReadOnlyDisplayReplacement && !hasReadOnlyDisplayReplacementPropertyName && isReadOnlyList) {
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, bindingPath);
            List<?> list = fieldValue != null ? (List<?>) fieldValue : Collections.emptyList();

            processReadOnlyListDisplay(model, list);
        } else {
            setAlternateAndAdditionalDisplayValue(ViewLifecycle.getView(), model);
        }

        if (this.getFieldLabel() != null && StringUtils.isNotBlank(this.getId())) {
            this.getFieldLabel().setLabelForComponentId(this.getId() + UifConstants.IdSuffixes.CONTROL);
        }

        if (model instanceof ViewModel) {
            View view = ViewLifecycle.getView();
            if(((ViewModel) model).isApplyDefaultValues()) {

                // apply default field values to view
                view.getViewHelperService().populateDefaultValueForField(model, this,
                        this.getBindingInfo().getBindingPath());

                // ensure default values are only applied once
                ((ViewModel) model).setApplyDefaultValues(false);
            }
        }
        
        ViewPostMetadata viewPostMetadata = ViewLifecycle.getViewPostMetadata();
        if (isRender() && viewPostMetadata != null) {
            viewPostMetadata.addRenderedPropertyPath(getBindingInfo().getBindingPath());
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
        //Default to delimited if nothing is set
        if (getReadOnlyListDisplayType() == null) {
            this.setReadOnlyListDisplayType(UifConstants.ReadOnlyListTypes.DELIMITED.name());
        }

        String generatedHtml = "";

        //begin generation setup
        if (!list.isEmpty()) {
            if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.UL.name())) {
                generatedHtml = "<ul class='uif-readOnlyStringList'>";
            } else if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.OL.name())) {
                generatedHtml = "<ol class='uif-readOnlyStringList'>";
            } else if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.BREAK.name())) {
                setReadOnlyListDelimiter("<br/>");
            } else if (this.getReadOnlyListDelimiter() == null) {
                setReadOnlyListDelimiter(", ");
            }
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
        if (!list.isEmpty()) {
            if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.UL.name())) {
                generatedHtml = generatedHtml + "</ul>";
            } else if (getReadOnlyListDisplayType().equalsIgnoreCase(UifConstants.ReadOnlyListTypes.OL.name())) {
                generatedHtml = generatedHtml + "</ol>";
            } else {
                generatedHtml = StringUtils.removeEnd(generatedHtml, this.getReadOnlyListDelimiter());
            }
        }

        if (StringUtils.isBlank(generatedHtml)) {
            generatedHtml = "&nbsp;";
        }

        return generatedHtml;
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
        if (!Boolean.TRUE.equals(getReadOnly())) {
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public boolean isInputAllowed() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyEditorClass(Class<? extends PropertyEditor> propertyEditorClass) {
        this.propertyEditor = KRADUtils.createNewObjectFromClass(propertyEditorClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.getBindingInfo().getBindingPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getDictionaryAttributeName() {
        return this.dictionaryAttributeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDictionaryAttributeName(String dictionaryAttributeName) {
        this.dictionaryAttributeName = dictionaryAttributeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getDictionaryObjectEntry() {
        return this.dictionaryObjectEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
        this.dictionaryObjectEntry = dictionaryObjectEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return this.defaultValueFinderClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        this.defaultValueFinderClass = defaultValueFinderClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<Object> getDefaultValues() {
        return this.defaultValues;
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultValues(List<Object> defaultValues) {
        this.defaultValues = defaultValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getForcedValue() {
        return forcedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForcedValue(String forcedValue) {
        this.forcedValue = forcedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getHelpSummary() {
        return this.help.getTooltipHelpContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHelpSummary(String helpSummary) {
        this.help.setTooltipHelpContent(helpSummary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataFieldSecurity getDataFieldSecurity() {
        return (DataFieldSecurity) super.getComponentSecurity();
    }

    /**
     * {@inheritDoc}
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
            setComponentSecurity(KRADUtils.createNewObjectFromClass(DataFieldSecurity.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isAddHiddenWhenReadOnly() {
        return addHiddenWhenReadOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddHiddenWhenReadOnly(boolean addHiddenWhenReadOnly) {
        this.addHiddenWhenReadOnly = addHiddenWhenReadOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Inquiry getInquiry() {
        return this.inquiry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnableAutoInquiry() {
        return enableAutoInquiry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableAutoInquiry(boolean enableAutoInquiry) {
        this.enableAutoInquiry = enableAutoInquiry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Help getHelp() {
        return this.help;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHelp(Help help) {
        this.help = help;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderInfoMessageSpan() {
        return renderInfoMessageSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderInfoMessageSpan(boolean renderInfoMessageSpan) {
        this.renderInfoMessageSpan = renderInfoMessageSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderMarkerIconSpan() {
        return renderMarkerIconSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderMarkerIconSpan(boolean renderMarkerIconSpan) {
        this.renderMarkerIconSpan = renderMarkerIconSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTooltipOfComponent(Tooltip tooltip) {
        getFieldLabel().setToolTip(tooltip);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelpTitle() {
        return this.getLabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getReadOnlyDisplaySuffixPropertyName() {
        return this.readOnlyDisplaySuffixPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnlyDisplaySuffixPropertyName(String readOnlyDisplaySuffixPropertyName) {
        this.readOnlyDisplaySuffixPropertyName = readOnlyDisplaySuffixPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getReadOnlyDisplayReplacementPropertyName() {
        return this.readOnlyDisplayReplacementPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnlyDisplayReplacementPropertyName(String readOnlyDisplayReplacementPropertyName) {
        this.readOnlyDisplayReplacementPropertyName = readOnlyDisplayReplacementPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getReadOnlyDisplayReplacement() {
        return readOnlyDisplayReplacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnlyDisplayReplacement(String value) {
        this.readOnlyDisplayReplacement = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getReadOnlyDisplaySuffix() {
        return readOnlyDisplaySuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnlyDisplaySuffix(String value) {
        this.readOnlyDisplaySuffix = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getReadOnlyListDisplayType() {
        return readOnlyListDisplayType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnlyListDisplayType(String readOnlyListDisplayType) {
        this.readOnlyListDisplayType = readOnlyListDisplayType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getReadOnlyListDelimiter() {
        return readOnlyListDelimiter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnlyListDelimiter(String readOnlyListDelimiter) {
        this.readOnlyListDelimiter = readOnlyListDelimiter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isApplyMask() {
        return applyMask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplyMask(boolean applyMask) {
        this.applyMask = applyMask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public MaskFormatter getMaskFormatter() {
        return maskFormatter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getAdditionalHiddenPropertyNames() {
        if (additionalHiddenPropertyNames == Collections.EMPTY_LIST && isMutable(true)) {
            additionalHiddenPropertyNames = new LifecycleAwareList<String>(this);
        }
        
        return additionalHiddenPropertyNames;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getPropertyNamesForAdditionalDisplay() {
        if (propertyNamesForAdditionalDisplay == Collections.EMPTY_LIST && isMutable(true)) {
            propertyNamesForAdditionalDisplay = new LifecycleAwareList<String>(this);
        }
        
        return propertyNamesForAdditionalDisplay;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isEscapeHtmlInPropertyValue() {
        return this.escapeHtmlInPropertyValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEscapeHtmlInPropertyValue(boolean escapeHtmlInPropertyValue) {
        this.escapeHtmlInPropertyValue = escapeHtmlInPropertyValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isMultiLineReadOnlyDisplay() {
        return multiLineReadOnlyDisplay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMultiLineReadOnlyDisplay(boolean multiLineReadOnlyDisplay) {
        this.multiLineReadOnlyDisplay = multiLineReadOnlyDisplay;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public boolean isRenderFieldset() {
        return (!Boolean.TRUE.equals(this.getReadOnly())
                && inquiry != null
                && inquiry.isRender()
                && inquiry.getInquiryLink() != null
                && inquiry.getInquiryLink().isRender()) || (help != null
                && help.isRender()
                && help.getHelpAction() != null
                && help.getHelpAction().isRender());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "sortAs")
    public String getSortAs() {
        return sortAs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSortAs(String sortAs) {
        if (!(sortAs.equals(UifConstants.TableToolsValues.CURRENCY) || sortAs.equals(UifConstants.TableToolsValues.DATE) || sortAs.equals(UifConstants.TableToolsValues.NUMERIC) || sortAs.equals(UifConstants.TableToolsValues.STRING))) {
            throw new IllegalArgumentException("invalid sortAs value of " + sortAs + ", allowed: " + UifConstants.TableToolsValues.CURRENCY + "|" + UifConstants.TableToolsValues.DATE + "|" + UifConstants.TableToolsValues.NUMERIC + "|" + UifConstants.TableToolsValues.STRING);
        }
        this.sortAs = sortAs;
    }

    /**
     * {@inheritDoc}
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

    /**
     * Determines wheter or not to create an automatic inqury widget for this field within the current lifecycle.
     * 
     * @return True if an automatic inquiry widget should be created for this field on the current lifecycle.
     */
    protected boolean hasAutoInquiryRelationship() {
        if (getBindingInfo() == null) {
            return false;
        }

        View view = ViewLifecycle.getView();
        Object model = ViewLifecycle.getModel();

        // Do checks for inquiry when read only
        if (getReadOnly()) {

            String bindingPath = getBindingInfo().getBindingPath();
            if (StringUtils.isBlank(bindingPath) || bindingPath.equals("null")) {
                return false;
            }

            // check if field value is null, if so no inquiry
            try {
                Object propertyValue = ObjectPropertyUtils.getPropertyValue(model, bindingPath);

                if ((propertyValue == null) || StringUtils.isBlank(propertyValue.toString())) {
                    return false;
                }
            } catch (Exception e) {
                // if we can't get the value just swallow the exception and don't set an inquiry
                return false;
            }

            // skips creating inquiry link if same as parent
            if (view.getViewTypeName() == UifConstants.ViewType.INQUIRY) {
                InquiryForm inquiryForm = (InquiryForm) model;
                String propertyName = getPropertyName();

                // value of field
                Object fieldValue = ObjectPropertyUtils.getPropertyValue(ViewModelUtils.getParentObjectForMetadata(
                        view, model, this), propertyName);

                // value of field in request parameter
                Object parameterValue = inquiryForm.getInitialRequestParameters().get(propertyName);

                // if data classes and field values are equal
                if (inquiryForm.getDataObjectClassName().equals(getDictionaryObjectEntry())
                        && parameterValue != null && fieldValue.equals(parameterValue)) {
                    return false;
                }
            }
        }

        // get parent object for inquiry metadata
        Object parentObject = ViewModelUtils.getParentObjectForMetadata(view, model, this);
        return parentObject == null ? false : KRADServiceLocatorWeb.getViewDictionaryService().isInquirable(
                parentObject.getClass());
    }

}
