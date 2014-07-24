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
package org.kuali.rice.krad.uif.widget;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Widget for rendering an Inquiry link or DirectInquiry action field
 *
 * <p>
 * The inquiry widget will render a button for the field value when
 * that field is editable. When read only the widget will create a link on the display value.
 * It points to the associated inquiry view for the field. The inquiry can be configured to point to a certain
 * {@code InquiryView}, or the framework will attempt to associate the field with a inquiry based on
 * its metadata (in particular its relationships in the model).
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "inquiry", parent = "Uif-Inquiry")
public class Inquiry extends WidgetBase {
    private static final long serialVersionUID = -2154388007867302901L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Inquiry.class);

    public static final String INQUIRY_TITLE_PREFIX = "title.inquiry.url.actiontext";
    public static final String INQUIRY_TITLE_POSTFIX = "title.inquiry.url.value.prependtext";

    private String baseInquiryUrl;

    private String dataObjectClassName;
    private String viewName;

    private Map<String, String> inquiryParameters;

    private Link inquiryLink;

    private Action directInquiryAction;
    private boolean enableDirectInquiry;

    private boolean adjustInquiryParameters;
    private BindingInfo fieldBindingInfo;

    private boolean parentReadOnly;

    public Inquiry() {
        super();

        inquiryParameters = new HashMap<String, String>();
    }

    /**
     * Inherits readOnly from parent if not explicitly populated.
     * 
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        super.afterEvaluateExpression();
        
        if (getReadOnly() == null) {
            Component parent = ViewLifecycle.getPhase().getParent();
            setReadOnly(parent == null ? null : parent.getReadOnly());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (!isRender()) {
            return;
        }

        // set render to false until we find an inquiry class
        setRender(false);

        // used to determine whether a normal or direct inquiry should be enabled
        if (parent instanceof Component) {
            setParentReadOnly(((Component) parent).getReadOnly());
        }

        // Do checks for inquiry when read only
        if (isParentReadOnly()) {
            if (StringUtils.isBlank(((DataField) parent).getBindingInfo().getBindingPath()) || ((DataField) parent)
                    .getBindingInfo().getBindingPath().equals("null")) {
                return;
            }

            // check if field value is null, if so no inquiry
            try {
                Object propertyValue = ObjectPropertyUtils.getPropertyValue(model,
                        ((DataField) parent).getBindingInfo().getBindingPath());

                if ((propertyValue == null) || StringUtils.isBlank(propertyValue.toString())) {
                    return;
                }
            } catch (Exception e) {
                // if we can't get the value just swallow the exception and don't set an inquiry
                return;
            }

            View view = ViewLifecycle.getActiveLifecycle().getView();
            // skips creating inquiry link if same as parent
            if (view.getViewTypeName() == UifConstants.ViewType.INQUIRY) {
                DataField dataField = (DataField)parent;
                InquiryForm inquiryForm = (InquiryForm)model;

                // value of field
                Object fieldValue = ObjectPropertyUtils.getPropertyValue(ViewModelUtils.getParentObjectForMetadata(
                        view, model, dataField), dataField.getPropertyName());

                // value of field in request parameter
                Object parameterValue = inquiryForm.getInitialRequestParameters().get(dataField.getPropertyName());

                // if data classes and field values are equal
                if (inquiryForm.getDataObjectClassName().equals(dataField.getDictionaryObjectEntry())
                        && parameterValue != null && fieldValue.equals(parameterValue))  {
                    return ;
                }
            }
        }

        // Do checks for direct inquiry when editable
        if (!isParentReadOnly() && parent instanceof InputField) {
            if (!enableDirectInquiry) {
                return;
            }

            // determine whether inquiry parameters will need adjusted
            if (StringUtils.isBlank(getDataObjectClassName())
                    || (getInquiryParameters() == null)
                    || getInquiryParameters().isEmpty()) {
                // if inquiry parameters not given, they will not be adjusted by super
                adjustInquiryParameters = true;
                fieldBindingInfo = ((InputField) parent).getBindingInfo();
            }
        }

        setupLink(model, (DataField) parent);

        if (isRender() && !isParentReadOnly() && enableDirectInquiry) {
            ((InputField) parent).addPostInputAddon(directInquiryAction);
        }
    }

    /**
     * Get parent object and field name and build the inquiry link
     *
     * <p>
     * This was moved from the performFinalize because overlapping and to be used
     * by DirectInquiry.
     * </p>
     *
     * @param model model
     * @param field The parent Attribute field
     */
    private void setupLink(Object model, DataField field) {
        String propertyName = field.getBindingInfo().getBindingName();

        // if class and parameters configured, build link from those
        if (StringUtils.isNotBlank(getDataObjectClassName()) && (getInquiryParameters() != null) &&
                !getInquiryParameters().isEmpty()) {
            Class<?> inquiryObjectClass;
            try {
                inquiryObjectClass = Class.forName(getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                LOG.error("Unable to get class for: " + getDataObjectClassName());
                throw new RuntimeException(e);
            }

            updateInquiryParameters(field.getBindingInfo());

            buildInquiryLink(model, propertyName, inquiryObjectClass, getInquiryParameters());
        }
        // get inquiry class and parameters from view helper
        else {
            // get parent object for inquiry metadata
            ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();

            Object parentObject = ViewModelUtils.getParentObjectForMetadata(viewLifecycle.getView(), model, field);
            if (parentObject != null) {
                viewLifecycle.getHelper().buildInquiryLink(parentObject, propertyName, this);
            }
        }
    }

    /**
     * Adjusts the path on the inquiry parameter property to match the binding
     * path prefix of the given {@code BindingInfo}
     *
     * @param bindingInfo binding info instance to copy binding path prefix from
     */
    public void updateInquiryParameters(BindingInfo bindingInfo) {
        Map<String, String> adjustedInquiryParameters = new HashMap<String, String>();
        for (Entry<String, String> stringEntry : inquiryParameters.entrySet()) {
            String toField = stringEntry.getValue();
            String adjustedFromFieldPath = bindingInfo.getPropertyAdjustedBindingPath(stringEntry.getKey());

            adjustedInquiryParameters.put(adjustedFromFieldPath, toField);
        }

        this.inquiryParameters = adjustedInquiryParameters;
    }

    /**
     * Builds the inquiry link based on the given inquiry class and parameters
     *
     * @param dataObject parent object that contains the data (used to pull inquiry
     * parameters)
     * @param propertyName name of the property the inquiry is set on
     * @param inquiryObjectClass class of the object the inquiry should point to
     * @param inquiryParams map of key field mappings for the inquiry
     */
    @SuppressWarnings("deprecation")
    public void buildInquiryLink(Object dataObject, String propertyName, Class<?> inquiryObjectClass,
            Map<String, String> inquiryParams) {
        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        Properties urlParameters = new Properties();
        Map<String,String> inquiryKeyValues = new HashMap<String, String>();

        urlParameters.setProperty(UifParameters.DATA_OBJECT_CLASS_NAME, inquiryObjectClass.getName());
        urlParameters.setProperty(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);
        if (StringUtils.isNotBlank(this.viewName)) {
            urlParameters.setProperty(UifParameters.VIEW_NAME, this.viewName);
        }

        // configure inquiry when read only
        if (isParentReadOnly()) {
            for (Entry<String, String> inquiryParameter : inquiryParams.entrySet()) {
                String parameterName = inquiryParameter.getKey();

                Object parameterValue = ObjectPropertyUtils.getPropertyValue(dataObject, parameterName);

                // TODO: need general format util that uses spring
                if (parameterValue == null) {
                    parameterValue = "";
                } else if (parameterValue instanceof java.sql.Date) {
                    if (org.kuali.rice.core.web.format.Formatter.findFormatter(parameterValue.getClass()) != null) {
                        org.kuali.rice.core.web.format.Formatter formatter =
                                org.kuali.rice.core.web.format.Formatter.getFormatter(parameterValue.getClass());
                        parameterValue = formatter.format(parameterValue);
                    }
                } else {
                    parameterValue = ObjectPropertyUtils.getPropertyValueAsText(dataObject, parameterName);
                }

                // Encrypt value if it is a field that has restriction that prevents a value from being shown to
                // user, because we don't want the browser history to store the restricted attributes value in the URL
                if (KRADServiceLocatorWeb.getDataObjectAuthorizationService()
                        .attributeValueNeedsToBeEncryptedOnFormsAndLinks(inquiryObjectClass,
                                inquiryParameter.getValue())) {
                    try {
                        parameterValue = CoreApiServiceLocator.getEncryptionService().encrypt(parameterValue);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException("Exception while trying to encrypted value for inquiry framework.",
                                e);
                    }
                }

                // add inquiry parameter to URL
                urlParameters.put(inquiryParameter.getValue(), parameterValue);

                inquiryKeyValues.put(inquiryParameter.getValue(), parameterValue.toString());
            }

            /* build inquiry URL */
            String inquiryUrl;

            // check for EBOs for an alternate inquiry URL
            ModuleService responsibleModuleService =
                    KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(inquiryObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(inquiryObjectClass)) {
                inquiryUrl = responsibleModuleService.getExternalizableDataObjectInquiryUrl(inquiryObjectClass,
                        urlParameters);
            } else {
                inquiryUrl = UrlFactory.parameterizeUrl(getBaseInquiryUrl(), urlParameters);
            }

            getInquiryLink().setHref(inquiryUrl);

            // set inquiry title
            getInquiryLink().setTitle(createTitleText(inquiryObjectClass, inquiryKeyValues));

            setRender(true);
        }
        // configure direct inquiry when editable
        else {
            // Direct inquiry
            String inquiryUrl = UrlFactory.parameterizeUrl(getBaseInquiryUrl(), urlParameters);

            StringBuilder paramMapStringBuilder = new StringBuilder();

            // Build parameter string using the actual names of the fields as on the html page
            for (Entry<String, String> inquiryParameter : inquiryParams.entrySet()) {
                String inquiryParameterFrom = inquiryParameter.getKey();

                if (adjustInquiryParameters && (fieldBindingInfo != null)) {
                    inquiryParameterFrom = fieldBindingInfo.getPropertyAdjustedBindingPath(inquiryParameterFrom);
                }

                ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();

                // Make sure our inquiry parameters are included as a rendered property path
                if(!viewLifecycle.getViewPostMetadata().getAllRenderedPropertyPaths().contains(inquiryParameterFrom.toString())){
                    setRender(false);
                    return;
                }

                paramMapStringBuilder.append(inquiryParameterFrom);
                paramMapStringBuilder.append(":");
                paramMapStringBuilder.append(inquiryParameter.getValue());
                paramMapStringBuilder.append(",");

            }
            String paramMapString = StringUtils.removeEnd(paramMapStringBuilder.toString(), ",");

            // Check if showing in dialog
            if (!getInquiryLink().isOpenInDialog()) {
                String title = this.getTitle();
                if (StringUtils.isNotBlank(title)) {
                    this.setTitle(title + " - " + messageService.getMessageText("accessibility.link.opensTab"));
                }
                else{
                    this.setTitle(messageService.getMessageText("accessibility.link.opensTab"));
                }
            }

            // Create onlick script to open the inquiry window on the click event
            // of the direct inquiry
            StringBuilder onClickScript = new StringBuilder("showDirectInquiry(\"");
            onClickScript.append(inquiryUrl);
            onClickScript.append("\", \"");
            onClickScript.append(paramMapString);
            onClickScript.append("\", ");
            onClickScript.append(getInquiryLink().isOpenInDialog());
            onClickScript.append(", \"");
            onClickScript.append(getInquiryLink().getLinkDialogId());
            onClickScript.append("\");");

            directInquiryAction.setPerformDirtyValidation(false);
            directInquiryAction.setActionScript(onClickScript.toString());

            setRender(true);
        }
    }

    /**
     * Gets text to prepend to the inquiry link title
     *
     * @param dataObjectClass data object class being inquired into
     * @return inquiry link title
     */
    public String createTitleText(Class<?> dataObjectClass, Map<String,String> inquiryKeyValues) {
        // use manually configured title if exists
        if (StringUtils.isNotBlank(getTitle())) {
            return getTitle();
        }

        String titleText = "";

        String titlePrefix = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                INQUIRY_TITLE_PREFIX);
        if (StringUtils.isNotBlank(titlePrefix)) {
            titleText += titlePrefix + " ";
        }

        String objectLabel = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDataObjectEntry(
                dataObjectClass.getName()).getObjectLabel();
        if (StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        if (StringUtils.isNotBlank(titleText)){
            String titlePostfix = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                    INQUIRY_TITLE_POSTFIX);
            if (StringUtils.isNotBlank(titlePostfix)) {
                titleText += titlePostfix + " ";
            }
        }

        return KRADUtils.buildAttributeTitleString(titleText, dataObjectClass, inquiryKeyValues);
   }

    /**
     * Returns the URL for the inquiry for which parameters will be added
     *
     * <p>
     * The base URL includes the domain, context, and controller mapping for the inquiry invocation. Parameters are
     * then added based on configuration to complete the URL. This is generally defaulted to the application URL and
     * internal KRAD servlet mapping, but can be changed to invoke another application such as the Rice standalone
     * server
     * </p>
     *
     * @return inquiry base URL
     */
    @BeanTagAttribute
    public String getBaseInquiryUrl() {
        return this.baseInquiryUrl;
    }

    /**
     * Setter for the inquiry base url (domain, context, and controller)
     *
     * @param baseInquiryUrl
     */
    public void setBaseInquiryUrl(String baseInquiryUrl) {
        this.baseInquiryUrl = baseInquiryUrl;
    }

    /**
     * Full class name the inquiry should be provided for
     *
     * <p>
     * This is passed on to the inquiry request for the data object the lookup should be rendered for. This is then
     * used by the inquiry framework to select the lookup view (if more than one inquiry view exists for the same
     * data object class name, the {@link #getViewName()} property should be specified to select the view to render).
     * </p>
     *
     * @return inquiry class name
     */
    @BeanTagAttribute
    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for the class name that inquiry should be provided for
     *
     * @param dataObjectClassName
     */
    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * When multiple target inquiry views exists for the same data object class, the view name can be set to
     * determine which one to use
     *
     * <p>
     * When creating multiple inquiry views for the same data object class, the view name can be specified for the
     * different versions (for example 'simple' and 'advanced'). When multiple inquiry views exist the view name must
     * be sent with the data object class for the request. Note the view id can be alternatively used to uniquely
     * identify the inquiry view
     * </p>
     * @return view name
     */
    @BeanTagAttribute
    public String getViewName() {
        return this.viewName;
    }

    /**
     * Setter for the view name configured on the inquiry view that should be invoked by the inquiry widget
     *
     * @param viewName
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Map that determines what properties from a calling view will be sent to properties on the inquiry data object
     *
     * <p>
     * When invoking an inquiry view, a query is done against the inquiries configured data object and the resulting
     * record is display. The values for the properties configured within the inquiry parameters Map will be
     * pulled and passed along as values for the inquiry data object properties (thus they form the criteria for
     * the inquiry)
     * </p>
     *
     * @return mapping of calling view properties to inquiry data object properties
     */
    @BeanTagAttribute
    public Map<String, String> getInquiryParameters() {
        return this.inquiryParameters;
    }

    /**
     * Setter for the map that determines what property values on the calling view will be sent to properties on the
     * inquiry data object
     *
     * @param inquiryParameters
     */
    public void setInquiryParameters(Map<String, String> inquiryParameters) {
        this.inquiryParameters = inquiryParameters;
    }

    /**
     * {@code Link} that will be rendered for an inquiry
     *
     * @return the inquiry link
     */
    @BeanTagAttribute
    public Link getInquiryLink() {
        return this.inquiryLink;
    }

    /**
     * Setter for the inquiry {@code Link}
     *
     * @param inquiryLink the inquiry {@link Link} object
     */
    public void setInquiryLink(Link inquiryLink) {
        this.inquiryLink = inquiryLink;
    }

    /**
     * {@code Action} that will be rendered next to the field for a direct inquiry
     *
     * @return the directInquiryAction
     */
    @BeanTagAttribute
    public Action getDirectInquiryAction() {
        return this.directInquiryAction;
    }

    /**
     * Setter for the direct inquiry {@code Action}
     *
     * @param directInquiryAction the direct inquiry {@link Action}
     */
    public void setDirectInquiryAction(Action directInquiryAction) {
        this.directInquiryAction = directInquiryAction;
    }

    /**
     * Indicates that the direct inquiry will not be rendered
     *
     * @return true if the direct inquiry should be rendered, false if not
     */
    @BeanTagAttribute
    public boolean isEnableDirectInquiry() {
        return enableDirectInquiry;
    }

    /**
     * Setter for the hideDirectInquiry flag
     *
     * @param enableDirectInquiry
     */
    public void setEnableDirectInquiry(boolean enableDirectInquiry) {
        this.enableDirectInquiry = enableDirectInquiry;
    }

    /**
     * Determines whether a normal or direct inquiry should be enabled
     *
     * @return true if parent component is read only, false otherwise
     */
    protected boolean isParentReadOnly() {
        return parentReadOnly;
    }

    /**
     * Determines whether a normal or direct inquiry should be enabled
     *
     * <p>
     * Used by unit tests and internally
     * </p>
     *
     * @param parentReadOnly true if parent component is read only, false otherwise
     */
    protected void setParentReadOnly(boolean parentReadOnly) {
        this.parentReadOnly = parentReadOnly;
    }

    /**
     * Determines whether inquiry parameters adjusted
     *
     * @return true if adjusted
     */
    public boolean isAdjustInquiryParameters() {
        return adjustInquiryParameters;
    }

    /**
     * Determines whether inquiry parameters adjusted
     *
     * <p>
     * Used internally
     * </p>
     *
     * @param adjustInquiryParameters
     */
    protected void setAdjustInquiryParameters(boolean adjustInquiryParameters) {
        this.adjustInquiryParameters = adjustInquiryParameters;
    }

    /**
     * Sets the field binding information
     *
     * <p>
     * Sets the field binding information
     * </p>
     *
     * @param fieldBindingInfo
     */
    protected void setFieldBindingInfo(BindingInfo fieldBindingInfo) {
        this.fieldBindingInfo = fieldBindingInfo;
    }
}
