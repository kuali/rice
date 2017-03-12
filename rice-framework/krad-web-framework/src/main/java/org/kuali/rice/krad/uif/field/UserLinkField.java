/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Field that encloses a link to a person element.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "userLinkField", parent = "Uif-UserLinkField")
public class UserLinkField extends LinkField {
    private static final long serialVersionUID = -6328858502087834L;
    private static final Logger LOG = Logger.getLogger(UserLinkField.class);

    private String principalId;
    private String principalName;
    private String dataObjectClassName;
    private String baseInquiryUrl;
    private boolean disableLink;
    private Map<String, String> additionalInquiryParameters;

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the linkLabel if blank to the Field label</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);
    }

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's
     * content
     *
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (StringUtils.isNotBlank(principalId)) {
            setHref(buildInquiryUrl());
            setLinkText(getPersonNameByPrincipalId(principalId));
        } else {
            if (StringUtils.isNotBlank(principalName)) {
                setHref(buildInquiryUrl());
                setLinkText(getPersonNameByPrincipalName(principalName));
            }
        }

        if (StringUtils.isBlank(getHref())) {
            setDisableLink(true);
        }

        if (StringUtils.isBlank(getLinkText())){
            setLinkText(UifConstants.KimLink.BLANK_LINK);
        }
    }

    protected String buildInquiryUrl() {
        Class<?> inquiryObjectClass;
        try {
            inquiryObjectClass = Class.forName(getDataObjectClassName());
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to get class for: " + getDataObjectClassName());
            throw new RuntimeException(e);
        }

        Properties urlParameters = new Properties();
        urlParameters.setProperty(UifParameters.DATA_OBJECT_CLASS_NAME, inquiryObjectClass.getName());
        urlParameters.setProperty(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);

        if (StringUtils.isNotBlank(principalId)) {
            urlParameters.setProperty(UifConstants.KimLink.PRINCIPAL_ID, principalId);
        } else {
            if (StringUtils.isNotBlank(principalName)) {
                urlParameters.setProperty(UifConstants.KimLink.PRINCIPAL_NAME, principalName);
            }
        }

        for (Map.Entry<String, String> inquiryParameter : additionalInquiryParameters.entrySet()) {
            // add inquiry parameter to URL
            urlParameters.setProperty(inquiryParameter.getKey(), inquiryParameter.getValue());
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

        return inquiryUrl;
    }

    protected String getPersonNameByPrincipalId(String principalId) {
        Person person = KimApiServiceLocator.getPersonService().getPerson(principalId);
        if (person == null) {
            // disable link and display principalId
            setDisableLink(true);
            return principalId;
        }

        return person.getName();
    }

    protected String getPersonNameByPrincipalName(String principalName) {
        Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
        if (person == null) {
            // disable link and display principalName
            setDisableLink(true);
            return principalName;
        }

        return person.getName();
    }

    /**
     * PrincipalName key used as a parameter of generated inquiry link url.
     *
     * @return value of PrincipalName key parameter
     */
    @BeanTagAttribute
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    /**
     * PrincipalId key used as a parameter of generated inquiry link url.
     *
     * @return value of PrincipalId key parameter
     */
    @BeanTagAttribute
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * DataObjectClassName used as a parameter of generated inquiry link url.
     *
     * @return value of DataObjectClassName parameter
     */
    @BeanTagAttribute
    public String getDataObjectClassName() {
        return dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * BaseInquiryUrl is the foundation of generated inquiry link url.
     *
     * @return value of BaseInquiryUrl part of inquiry link url
     */
    @BeanTagAttribute
    public String getBaseInquiryUrl() {
        return baseInquiryUrl;
    }

    public void setBaseInquiryUrl(String baseInquiryUrl) {
        this.baseInquiryUrl = baseInquiryUrl;
    }

    /**
     * True if this fields link is disabled, false otherwise.
     *
     * @return true if link disabled
     */
    @BeanTagAttribute
    public boolean isDisableLink() {
        return disableLink;
    }

    public void setDisableLink(boolean disableLink) {
        this.disableLink = disableLink;
    }

    /**
     * Map that determines what additional properties from a calling view will be sent to properties on the inquiry data object.
     *
     * <p>
     * When invoking an inquiry view, a query is done against the inquiries configured data object and the resulting
     * record is display. The values for the properties configured within the inquiry parameters Map, in addition to the
     * inquiry key parameters, will be passed along as values for the inquiry data object properties (thus they form the
     * criteria for the inquiry)
     * </p>
     *
     * @return mapping of calling additional view properties to inquiry data object properties
     */
    @BeanTagAttribute
    public Map<String, String> getAdditionalInquiryParameters() {
        return additionalInquiryParameters;
    }

    public void setAdditionalInquiryParameters(Map<String, String> additionalInquiryParameters) {
        this.additionalInquiryParameters = additionalInquiryParameters;
    }

}