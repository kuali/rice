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
package org.kuali.rice.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.util.LookupInquiryUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.util.UrlFactory;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Widget for rendering an Inquiry link on a field's value
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Inquiry extends WidgetBase {
    private static final long serialVersionUID = -2154388007867302901L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Inquiry.class);

    public static final String INQUIRY_TITLE_PREFIX = "title.inquiry.url.value.prependtext";

    private String baseInquiryUrl;

    private String dataObjectClassName;
    private String viewName;

    private Map<String, String> inquiryParameters;

    private boolean forceInquiry;

    private LinkField inquiryLinkField;

    public Inquiry() {
        super();

        forceInquiry = false;
        inquiryParameters = new HashMap<String, String>();
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.WidgetBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // only set inquiry if enabled
        if (!isRender() || !isReadOnly()) {
            return;
        }

        // set render to false until we find an inquiry class
        setRender(false);

        AttributeField field = (AttributeField) parent;

        // check if field value is null, if so no inquiry
        Object propertyValue = ObjectPropertyUtils.getPropertyValue(model, field.getBindingInfo().getBindingPath());
        if ((propertyValue == null) || StringUtils.isBlank(propertyValue.toString())) {
            return;
        }

        setupLink(view, model, field);
    }

    /**
     * Get parent object and field name and build the inquiry link
     * This was moved from the performFinalize because overlapping and to be used
     * by DirectInquiry
     *
     * @param view - Container View
     * @param model - model
     * @param field - The parent Attribute field
     */
    public void setupLink(View view, Object model, AttributeField field) {
        String propertyName = field.getBindingInfo().getBindingName();

        // if class and parameters configured, build link from those
        if (StringUtils.isNotBlank(getDataObjectClassName()) && (getInquiryParameters() != null) &&
                !getInquiryParameters().isEmpty()) {
            Class<?> inquiryObjectClass = null;
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
            Object parentObject = ViewModelUtils.getParentObjectForMetadata(view, model, field);
            view.getViewHelperService().buildInquiryLink(parentObject, propertyName, this);
        }
    }

    /**
     * Adjusts the path on the inquiry parameter property to match the binding
     * path prefix of the given <code>BindingInfo</code>
     *
     * @param bindingInfo - binding info instance to copy binding path prefix from
     */
    public void updateInquiryParameters(BindingInfo bindingInfo) {
        Map<String, String> adjustedInquiryParameters = new HashMap<String, String>();
        for (String fromField : inquiryParameters.keySet()) {
            String toField = inquiryParameters.get(fromField);
            String adjustedFromFieldPath = bindingInfo.getPropertyAdjustedBindingPath(fromField);

            adjustedInquiryParameters.put(adjustedFromFieldPath, toField);
        }

        this.inquiryParameters = adjustedInquiryParameters;
    }

    /**
     * Builds the inquiry link based on the given inquiry class and parameters
     *
     * @param dataObject - parent object that contains the data (used to pull inquiry
     * parameters)
     * @param propertyName - name of the property the inquiry is set on
     * @param inquiryObjectClass - class of the object the inquiry should point to
     * @param inquiryParms - map of key field mappings for the inquiry
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Class<?> inquiryObjectClass,
            Map<String, String> inquiryParms) {
        Properties urlParameters = new Properties();

        urlParameters.put(UifParameters.DATA_OBJECT_CLASS_NAME, inquiryObjectClass.getName());
        urlParameters.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);

        for (Entry<String, String> inquiryParameter : inquiryParms.entrySet()) {
            String parameterName = inquiryParameter.getKey();

            Object parameterValue = ObjectPropertyUtils.getPropertyValue(dataObject, parameterName);

            // TODO: need general format util that uses spring
            if (parameterValue == null) {
                parameterValue = "";
            } else if (parameterValue instanceof java.sql.Date) {
                if (Formatter.findFormatter(parameterValue.getClass()) != null) {
                    Formatter formatter = Formatter.getFormatter(parameterValue.getClass());
                    parameterValue = formatter.format(parameterValue);
                }
            } else {
                parameterValue = parameterValue.toString();
            }

            // Encrypt value if it is a field that has restriction that prevents
            // a value from being shown to user, because we don't want the
            // browser history to store the restricted
            // attribute's value in the URL
            if (KRADServiceLocatorWeb.getDataObjectAuthorizationService()
                    .attributeValueNeedsToBeEncryptedOnFormsAndLinks(inquiryObjectClass, inquiryParameter.getValue())) {
                try {
                    parameterValue = CoreApiServiceLocator.getEncryptionService().encrypt(parameterValue);
                } catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }
            }

            // add inquiry parameter to URL
            urlParameters.put(inquiryParameter.getValue(), parameterValue);
        }

        // check for EBOs and adjust inquiry
        ModuleService responsibleModuleService =
                KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(inquiryObjectClass);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(inquiryObjectClass)) {
            Class<? extends ExternalizableBusinessObject> implLookupObjectClass =
                    responsibleModuleService.getExternalizableBusinessObjectImplementation(
                            inquiryObjectClass.asSubclass(ExternalizableBusinessObject.class));

            // TODO: this should set base inquiry URL from module service as well, but right now set for KNS
            if (implLookupObjectClass != null) {
                urlParameters.put(UifParameters.DATA_OBJECT_CLASS_NAME, implLookupObjectClass.getName());
            } else {
                throw new RuntimeException(
                        "Unable to find implementation class for EBO: " + inquiryObjectClass.getName());
            }
        }

        String inquiryUrl = UrlFactory.parameterizeUrl(getBaseInquiryUrl(), urlParameters);
        getInquiryLinkField().setHrefText(inquiryUrl);

        // set inquiry title
        String linkTitle = createTitleText(inquiryObjectClass);
        linkTitle = LookupInquiryUtils.getLinkTitleText(linkTitle, inquiryObjectClass, getInquiryParameters());
        getInquiryLinkField().setTitle(linkTitle);

        setRender(true);
    }

    /**
     * Gets text to prepend to the inquiry link title
     *
     * @param dataObjectClass - data object class being inquired into
     * @return String title prepend text
     */
    public String createTitleText(Class<?> dataObjectClass) {
        String titleText = "";

        String titlePrefixProp =
                KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(INQUIRY_TITLE_PREFIX);
        if (StringUtils.isNotBlank(titlePrefixProp)) {
            titleText += titlePrefixProp + " ";
        }

        String objectLabel = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary()
                .getDataObjectEntry(dataObjectClass.getName()).getObjectLabel();
        if (StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        return titleText;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(getInquiryLinkField());

        return components;
    }

    public String getBaseInquiryUrl() {
        return this.baseInquiryUrl;
    }

    public void setBaseInquiryUrl(String baseInquiryUrl) {
        this.baseInquiryUrl = baseInquiryUrl;
    }

    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    public String getViewName() {
        return this.viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public boolean isForceInquiry() {
        return this.forceInquiry;
    }

    public void setForceInquiry(boolean forceInquiry) {
        this.forceInquiry = forceInquiry;
    }

    public Map<String, String> getInquiryParameters() {
        return this.inquiryParameters;
    }

    public void setInquiryParameters(Map<String, String> inquiryParameters) {
        this.inquiryParameters = inquiryParameters;
    }

    public LinkField getInquiryLinkField() {
        return this.inquiryLinkField;
    }

    public void setInquiryLinkField(LinkField inquiryLinkField) {
        this.inquiryLinkField = inquiryLinkField;
    }
}
