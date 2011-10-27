package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Field that renders data from the application, such as the value of a data object property
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataField extends FieldBase {
    private static final long serialVersionUID = -4129678891948564724L;

    // binding
    private String propertyName;
    private BindingInfo bindingInfo;

    private String dictionaryAttributeName;
    private String dictionaryObjectEntry;

    private Formatter formatter;
    private AttributeSecurity attributeSecurity;

    private boolean readOnlyHidden;

    // alternate and additional display properties
    protected String alternateDisplayPropertyName;
    protected String additionalDisplayPropertyName;

    private String alternateDisplayValue;
    private String additionalDisplayValue;

    private List<String> hiddenPropertyNames;
    private List<String> informationalDisplayPropertyNames;

    private boolean escapeHtmlInPropertyValue;

    private String helpSummary;
    private String helpDescription;

    // widgets
    private Inquiry fieldInquiry;

    public DataField() {
        super();

        readOnlyHidden = false;
        hiddenPropertyNames = new ArrayList<String>();
        informationalDisplayPropertyNames = new ArrayList<String>();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set defaults for binding</li>
     * <li>Default the model path if not set</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>If readOnlyHidden set to true, set field to readonly and add to hidden property names</li>
     * </ul>
     */
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (isReadOnlyHidden()) {
            setReadOnly(true);
            getHiddenPropertyNames().add(getPropertyName());
        }
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Set the ids for the various attribute components</li>
     * <li>Sets up the client side validation for constraints on this field. In
     * addition, it sets up the messages applied to this field</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // adjust the path for hidden fields
        // TODO: should this check the view#readOnly?
        List<String> hiddenPropertyPaths = new ArrayList<String>();
        for (String hiddenPropertyName : getHiddenPropertyNames()) {
            String hiddenPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(hiddenPropertyName);
            hiddenPropertyPaths.add(hiddenPropertyPath);
        }
        this.hiddenPropertyNames = hiddenPropertyPaths;

        // adjust paths on informational property names
        List<String> informationalPropertyPaths = new ArrayList<String>();
        for (String infoPropertyName : getInformationalDisplayPropertyNames()) {
            String infoPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(infoPropertyName);
            informationalPropertyPaths.add(infoPropertyPath);
        }
        this.informationalDisplayPropertyNames = informationalPropertyPaths;

        // Additional and Alternate display value
        setAlternateAndAdditionalDisplayValue(view, model);
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
     * present,
     * check whether this field is has <code>MultiValueControlBase</code>. If yes, get the Label for the value and
     * set it as additional display value.
     * </p>
     *
     * @param view - the current view instance
     * @param model - model instance
     */
    protected void setAlternateAndAdditionalDisplayValue(View view, Object model) {
        // if alternate or additional display values set don't use property names
        if (StringUtils.isNotBlank(alternateDisplayValue) || StringUtils.isNotBlank(additionalDisplayValue)) {
            return;
        }

        // check whether field value needs to be masked, and if so apply masking as alternateDisplayValue
        if (getAttributeSecurity() != null) {
            //TODO: Check authorization
            // if (attributeSecurity.isMask() && !boAuthzService.canFullyUnmaskField(user,dataObjectClass, field.getPropertyName(), null)) {
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());
            alternateDisplayValue = getSecuredFieldValue(attributeSecurity, fieldValue);

            setReadOnly(true);
            return;
        }

        // if not read only, return without trying to set alternate and additional values
        if (!isReadOnly()) {
            return;
        }

        // if field is not secure, check for alternate and additional display properties
        if (StringUtils.isNotBlank(getAlternateDisplayPropertyName())) {
            String alternateDisplayPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(
                    getAlternateDisplayPropertyName());

            Object alternateFieldValue = ObjectPropertyUtils.getPropertyValue(model, alternateDisplayPropertyPath);
            if (alternateFieldValue != null) {
                // TODO: format by type
                alternateDisplayValue = alternateFieldValue.toString();
            }
        }

        // perform automatic translation for code references if enabled on view
        if (StringUtils.isBlank(getAdditionalDisplayPropertyName()) && view.isTranslateCodes()) {
            // check for any relationship present for this field and it's of type KualiCode
            Class<?> parentObjectClass = ViewModelUtils.getParentObjectClassForMetadata(view, model, this);
            DataObjectRelationship relationship =
                    KRADServiceLocatorWeb.getDataObjectMetaDataService().getDataObjectRelationship(null,
                            parentObjectClass, getBindingInfo().getBindingName(), "", true, false, false);

            if (relationship != null
                    && getPropertyName().startsWith(relationship.getParentAttributeName())
                    && KualiCode.class.isAssignableFrom(relationship.getRelatedClass())) {
                additionalDisplayPropertyName =
                        relationship.getParentAttributeName() + "." + KRADPropertyConstants.NAME;
            }
        }

        // now check for an additional display property and if set get the value
        if (StringUtils.isNotBlank(getAdditionalDisplayPropertyName())) {
            String additionalDisplayPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(
                    getAdditionalDisplayPropertyName());

            Object additionalFieldValue = ObjectPropertyUtils.getPropertyValue(model, additionalDisplayPropertyPath);
            if (additionalFieldValue != null) {
                // TODO: format by type
                additionalDisplayValue = additionalFieldValue.toString();
            }
        }
    }

    /**
     * Helper method which returns the masked value based on the <code>AttributeSecurity</code>
     *
     * @param attributeSecurity attribute security to check
     * @param fieldValue field value
     * @return masked value
     */
    protected String getSecuredFieldValue(AttributeSecurity attributeSecurity, Object fieldValue) {
        if (attributeSecurity.isMask()) {
            return attributeSecurity.getMaskFormatter().maskValue(fieldValue);
        } else if (getAttributeSecurity().isPartialMask()) {
            return attributeSecurity.getPartialMaskFormatter().maskValue(fieldValue);
        } else {
            throw new RuntimeException("Encountered unsupported Attribute Security..");
        }
    }

    /**
     * Defaults the properties of the <code>DataField</code> to the
     * corresponding properties of its <code>AttributeDefinition</code>
     * retrieved from the dictionary (if such an entry exists). If the field
     * already contains a value for a property, the definitions value is not
     * used.
     *
     * @param view - view instance the field belongs to
     * @param attributeDefinition - AttributeDefinition instance the property values should be
     * copied from
     */
    public void copyFromAttributeDefinition(View view, AttributeDefinition attributeDefinition) {
        // label
        if (StringUtils.isEmpty(getLabel())) {
            setLabel(attributeDefinition.getLabel());
        }

        // short label
        if (StringUtils.isEmpty(getShortLabel())) {
            setShortLabel(attributeDefinition.getShortLabel());
        }

        // summary
        if (StringUtils.isEmpty(getHelpSummary())) {
            setHelpSummary(attributeDefinition.getSummary());
        }

        // description
        if (StringUtils.isEmpty(getHelpDescription())) {
            setHelpDescription(attributeDefinition.getDescription());
        }

        // security
        if (getAttributeSecurity() == null) {
            attributeSecurity = attributeDefinition.getAttributeSecurity();
        }

        // alternate property name
        if (getAlternateDisplayPropertyName() == null && StringUtils.isNotBlank(
                attributeDefinition.getAlternateDisplayAttributeName())) {
            setAlternateDisplayPropertyName(attributeDefinition.getAlternateDisplayAttributeName());
        }

        // additional property display name
        if (getAdditionalDisplayPropertyName() == null && StringUtils.isNotBlank(
                attributeDefinition.getAdditionalDisplayAttributeName())) {
            setAdditionalDisplayPropertyName(attributeDefinition.getAdditionalDisplayAttributeName());
        }

        if (getFormatter() == null && StringUtils.isNotBlank(attributeDefinition.getFormatterClass())) {
            Class clazz = null;
            try {
                clazz = Class.forName(attributeDefinition.getFormatterClass());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to get class from name: " + attributeDefinition.getFormatterClass(),
                        e);
            }

            Formatter formatter = (Formatter) ObjectUtils.newInstance(clazz);
            setFormatter(formatter);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(fieldInquiry);

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.DataBinding#getPropertyName()
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Setter for the component's property name
     *
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * <code>Formatter</code> instance that should be used when displaying and
     * accepting the field's value in the user interface
     *
     * <p>
     * Formatters can provide conversion between datatypes in addition to
     * special string formatting such as currency display
     * </p>
     *
     * @return Formatter instance
     * @see org.kuali.rice.core.web.format.Formatter
     */
    public Formatter getFormatter() {
        return this.formatter;
    }

    /**
     * Setter for the field's formatter
     *
     * @param formatter
     */
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.DataBinding#getBindingInfo()
     */
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * Setter for the field's binding info
     *
     * @param bindingInfo
     */
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * Name of the attribute within the data dictionary the attribute field is
     * associated with
     *
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * propertyName and parent object class the framework attempts will
     * determine the attribute definition that is associated with the field and
     * set this property. However this property can also be set in the fields
     * configuration to use another dictionary attribute.
     * </p>
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     *
     * @return String attribute name
     */
    public String getDictionaryAttributeName() {
        return this.dictionaryAttributeName;
    }

    /**
     * Setter for the dictionary attribute name
     *
     * @param dictionaryAttributeName
     */
    public void setDictionaryAttributeName(String dictionaryAttributeName) {
        this.dictionaryAttributeName = dictionaryAttributeName;
    }

    /**
     * Object entry name in the data dictionary the associated attribute is
     * apart of
     *
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * parent object class the framework will determine the object entry for the
     * associated attribute. However the object entry can be set in the field's
     * configuration to use another object entry for the attribute
     * </p>
     *
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     *
     * @return
     */
    public String getDictionaryObjectEntry() {
        return this.dictionaryObjectEntry;
    }

    /**
     * Setter for the dictionary object entry
     *
     * @param dictionaryObjectEntry
     */
    public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
        this.dictionaryObjectEntry = dictionaryObjectEntry;
    }

    /**
     * Summary help text for the field
     *
     * @return String summary help text
     */
    public String getHelpSummary() {
        return helpSummary;
    }

    /**
     * Setter for the summary help text
     *
     * @param helpSummary
     */
    public void setHelpSummary(String helpSummary) {
        this.helpSummary = helpSummary;
    }

    /**
     * Full help information text for the field
     *
     * @return String help description text
     */
    public String getHelpDescription() {
        return this.helpDescription;
    }

    /**
     * Setter for the help description text
     *
     * @param helpDescription
     */
    public void setHelpDescription(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    /**
     * Holds security configuration for the attribute field. This triggers
     * corresponding permission checks in KIM and can result in an update to the
     * field state (such as read-only or hidden) and masking of the value
     *
     * @return AttributeSecurity instance configured for field or Null if no
     *         restrictions are defined
     */
    public AttributeSecurity getAttributeSecurity() {
        return this.attributeSecurity;
    }

    /**
     * Setter for the AttributeSecurity instance that defines restrictions for
     * the field
     *
     * @param attributeSecurity
     */
    public void setAttributeSecurity(AttributeSecurity attributeSecurity) {
        this.attributeSecurity = attributeSecurity;
    }

    /**
     * Indicates the field should be read-only but also a hidden should be generated for the field
     *
     * <p>
     * Useful for when a value is just displayed but is needed by script
     * </p>
     *
     * @return boolean true if field should be readOnly hidden, false if not
     */
    public boolean isReadOnlyHidden() {
        return readOnlyHidden;
    }

    /**
     * Setter for the read-only hidden indicator
     *
     * @param readOnlyHidden
     */
    public void setReadOnlyHidden(boolean readOnlyHidden) {
        this.readOnlyHidden = readOnlyHidden;
    }

    /**
     * Inquiry widget for the field
     *
     * <p>
     * The inquiry widget will render a link for the field value when read-only
     * that points to the associated inquiry view for the field. The inquiry can
     * be configured to point to a certain <code>InquiryView</code>, or the
     * framework will attempt to associate the field with a inquiry based on its
     * metadata (in particular its relationships in the model)
     * </p>
     *
     * @return Inquiry field inquiry
     */
    public Inquiry getFieldInquiry() {
        return this.fieldInquiry;
    }

    /**
     * Setter for the inquiry widget
     *
     * @param fieldInquiry
     */
    public void setFieldInquiry(Inquiry fieldInquiry) {
        this.fieldInquiry = fieldInquiry;
    }

    /**
     * Additional display attribute name, which will be displayed next to the actual field value
     * when the field is readonly with hypen inbetween like PropertyValue - AdditionalPropertyValue
     *
     * @param additionalDisplayPropertyName - Name of the additional display property
     */
    public void setAdditionalDisplayPropertyName(String additionalDisplayPropertyName) {
        this.additionalDisplayPropertyName = additionalDisplayPropertyName;
    }

    /**
     * Returns the additional display attribute name to be displayed when the field is readonly
     *
     * @return Additional Display Attribute Name
     */
    public String getAdditionalDisplayPropertyName() {
        return this.additionalDisplayPropertyName;
    }

    /**
     * Sets the alternate display attribute name to be displayed when the field is readonly.
     * This properties value will be displayed instead of actual fields value when the field is readonly.
     *
     * @param alternateDisplayPropertyName - alternate display property name
     */
    public void setAlternateDisplayPropertyName(String alternateDisplayPropertyName) {
        this.alternateDisplayPropertyName = alternateDisplayPropertyName;
    }

    /**
     * Returns the alternate display attribute name to be displayed when the field is readonly.
     *
     * @return alternate Display Property Name
     */
    public String getAlternateDisplayPropertyName() {
        return this.alternateDisplayPropertyName;
    }

    /**
     * Returns the alternate display value
     *
     * @return the alternate display value set for this field
     */
    public String getAlternateDisplayValue() {
        return alternateDisplayValue;
    }

    /**
     * Setter for the alternative display value
     *
     * @param value
     */
    public void setAlternateDisplayValue(String value) {
        this.alternateDisplayValue = value;
    }

    /**
     * Returns the additional display value.
     *
     * @return the additional display value set for this field
     */
    public String getAdditionalDisplayValue() {
        return additionalDisplayValue;
    }

    /**
     * Setter for the additional display value
     *
     * @param value
     */
    public void setAdditionalDisplayValue(String value) {
        this.additionalDisplayValue = value;
    }

    /**
     * Allows specifying hidden property names without having to specify as a
     * field in the group config (that might impact layout)
     *
     * @return List<String> hidden property names
     */
    public List<String> getHiddenPropertyNames() {
        return hiddenPropertyNames;
    }

    /**
     * Setter for the hidden property names
     *
     * @param hiddenPropertyNames
     */
    public void setHiddenPropertyNames(List<String> hiddenPropertyNames) {
        this.hiddenPropertyNames = hiddenPropertyNames;
    }

    /**
     * List of property names whose values should be displayed read-only under this field
     *
     * <p>
     * In the attribute field template for each information property name given its values is
     * outputted read-only. Informational property values can also be updated dynamically with
     * the use of field attribute query
     * </p>
     *
     * <p>
     * Simple property names can be given if the property has the same binding parent as this
     * field, in which case the binding path will be adjusted by the framework. If the property
     * names starts with org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX, no binding
     * prefix will be added.
     * </p>
     *
     * @return List<String> informational property names
     */
    public List<String> getInformationalDisplayPropertyNames() {
        return informationalDisplayPropertyNames;
    }

    /**
     * Setter for the list of informational property names
     *
     * @param informationalDisplayPropertyNames
     */
    public void setInformationalDisplayPropertyNames(List<String> informationalDisplayPropertyNames) {
        this.informationalDisplayPropertyNames = informationalDisplayPropertyNames;
    }

    /**
     * Sets HTML escaping for this property value. HTML escaping will be handled in alternate and additional fields
     * also.
     */
    public void setEscapeHtmlInPropertyValue(boolean escapeHtmlInPropertyValue) {
        this.escapeHtmlInPropertyValue = escapeHtmlInPropertyValue;
    }

    /**
     * Returns true if HTML escape allowed for this field
     *
     * @return true if escaping allowed
     */
    public boolean isEscapeHtmlInPropertyValue() {
        return this.escapeHtmlInPropertyValue;
    }

}
