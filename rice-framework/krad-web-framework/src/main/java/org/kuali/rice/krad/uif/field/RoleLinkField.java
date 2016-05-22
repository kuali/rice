/**
 * Copyright 2005-2016 The Kuali Foundation
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
import org.kuali.rice.kim.api.role.Role;
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
 * Field that encloses a link to a role element.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "roleLinkField", parent = "Uif-RoleLinkField")
public class RoleLinkField extends LinkField {
    private static final long serialVersionUID = -7175947391547723712L;
    private static final Logger LOG = Logger.getLogger(RoleLinkField.class);

    private String roleId;
    private String roleName;
    private String roleNamespaceCode;
    private String dataObjectClassName;
    private String baseInquiryUrl;
    private boolean namespaceInLinkText;
    private boolean disableLink;
    private Map<String, String> additionalInquiryParameters;

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's
     * content.
     *
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (StringUtils.isNotBlank(roleId)) {
            setHref(buildInquiryUrl());
            setLinkText(getRoleNameByRoleId(roleId));
        } else {
            if (StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(roleNamespaceCode) ) {
                setHref(buildInquiryUrl());
                setLinkText(getRoleNameByNamespaceAndName(roleNamespaceCode, roleName));
            }

            if (StringUtils.isBlank(roleName) && StringUtils.isNotBlank(roleNamespaceCode) ) {
                setDisableLink(true);

                if (isNamespaceInLinkText()){
                    setLinkText(roleNamespaceCode);
                }
            }

            if (StringUtils.isNotBlank(roleName) && StringUtils.isBlank(roleNamespaceCode) ) {
                setDisableLink(true);
                setLinkText(roleName);
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

        if (StringUtils.isNotBlank(roleId)) {
            urlParameters.setProperty(UifConstants.KimLink.ROLE_ID, roleId);
        } else {
            if (StringUtils.isNotBlank(roleName)) {
                urlParameters.setProperty(UifConstants.KimLink.ROLE_NAME, roleName);
                urlParameters.setProperty(UifConstants.KimLink.ROLE_NAMESPACE_CODE, roleNamespaceCode);
            }
        }

        for (Map.Entry<String, String> inquiryParameter : additionalInquiryParameters.entrySet()) {
            // add additional inquiry parameter to URL
            urlParameters.setProperty(inquiryParameter.getKey(), inquiryParameter.getValue());
        }

        // build inquiry URL
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

    protected String getRoleNameByRoleId(String roleId) {
        Role role = KimApiServiceLocator.getRoleService().getRole(roleId);
        if (role == null) {
            // disable link and display roleId
            setDisableLink(true);
            return roleId;
        }

        if (isNamespaceInLinkText()){
            return role.getNamespaceCode() + " " + role.getName();
        }

        return role.getName();
    }

    protected String getRoleNameByNamespaceAndName(String namespaceCode, String roleName) {
        Role role = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName(namespaceCode, roleName);
        if (role == null) {
            // disable link
            setDisableLink(true);

            if (isNamespaceInLinkText()){
                return this.roleNamespaceCode + " " + this.roleName;
            } else {
                return this.roleName;
            }
        }

        if (isNamespaceInLinkText()){
            return role.getNamespaceCode() + " " + role.getName();
        }

        return role.getName();
    }

    /**
     * RoleName key used as a parameter of generated inquiry link url.
     *
     * @return value of RoleName key parameter
     */
    @BeanTagAttribute
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * RoleNamespaceCode key used as a parameter of generated inquiry link url.
     *
     * @return value of RoleNamespaceCode key parameter
     */
    @BeanTagAttribute
    public String getRoleNamespaceCode() {
        return roleNamespaceCode;
    }

    public void setRoleNamespaceCode(String roleNamespaceCode) {
        this.roleNamespaceCode = roleNamespaceCode;
    }

    /**
     * RoleId key used as a parameter of generated inquiry link url.
     *
     * @return value of RoleId key parameter
     */
    @BeanTagAttribute
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
     * True if namespaceCode is to be included in this links linkText, false otherwise.
     *
     * @return true if namespaceCode is to be included in linkText
     */
    @BeanTagAttribute
    public boolean isNamespaceInLinkText() {
        return namespaceInLinkText;
    }

    public void setNamespaceInLinkText(boolean namespaceInLinkText) {
        this.namespaceInLinkText = namespaceInLinkText;
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
