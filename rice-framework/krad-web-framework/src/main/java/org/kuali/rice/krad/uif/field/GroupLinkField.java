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
import org.kuali.rice.kim.api.group.Group;
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
 * Field that encloses a link to a group element.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "groupLinkField", parent = "Uif-GroupLinkField")
public class GroupLinkField extends LinkField {
    private static final long serialVersionUID = 6304287260087690284L;
    private static final Logger LOG = Logger.getLogger(GroupLinkField.class);

    private String groupId;
    private String groupName;
    private String groupNamespaceCode;
    private String dataObjectClassName;
    private String baseInquiryUrl;
    private boolean namespaceInLinkText;
    private boolean disableLink;
    private Map<String, String> additionalInquiryParameters;

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's
     * content/
     *
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (StringUtils.isNotBlank(groupId)) {
            setHref(buildInquiryUrl());
            setLinkText(getGroupNameByGroupId(groupId));
        } else {
            if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(groupNamespaceCode) ) {
                setHref(buildInquiryUrl());
                setLinkText(getGroupNameByNamespaceAndName(groupNamespaceCode, groupName));
            }

            if (StringUtils.isBlank(groupName) && StringUtils.isNotBlank(groupNamespaceCode) ) {
                setDisableLink(true);

                if (isNamespaceInLinkText()){
                    setLinkText(groupNamespaceCode);
                }
            }

            if (StringUtils.isNotBlank(groupName) && StringUtils.isBlank(groupNamespaceCode) ) {
                setDisableLink(true);
                setLinkText(groupName);
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

        if (StringUtils.isNotBlank(groupId)) {
            urlParameters.setProperty(UifConstants.KimLink.GROUP_ID, groupId);
        } else {
            if (StringUtils.isNotBlank(groupName)) {
                urlParameters.setProperty(UifConstants.KimLink.GROUP_NAME, groupName);
                urlParameters.setProperty(UifConstants.KimLink.GROUP_NAMESPACE_CODE, groupNamespaceCode);
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

    protected String getGroupNameByGroupId(String groupId) {
        Group group = KimApiServiceLocator.getGroupService().getGroup(groupId);
        if (group == null) {
            // disable link and display groupId
            setDisableLink(true);
            return groupId;
        }

        if (isNamespaceInLinkText()){
            return group.getNamespaceCode() + " " + group.getName();
        }

        return group.getName();
    }

    protected String getGroupNameByNamespaceAndName(String namespaceCode, String groupName) {
        Group group = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(namespaceCode, groupName);
        if (group == null) {
            // disable link
            setDisableLink(true);

            if (isNamespaceInLinkText()){
                return this.groupNamespaceCode + " " + this.groupName;
            } else {
                return this.groupName;
            }
        }

        if (isNamespaceInLinkText()){
            return group.getNamespaceCode() + " " + group.getName();
        }

        return group.getName();
    }

    /**
     * GroupName key used as a parameter of generated inquiry link url
     *
     * @return value of GroupName key parameter
     */
    @BeanTagAttribute
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * GroupNamespaceCode key used as a parameter of generated inquiry link url
     *
     * @return value of GroupNamespaceCode key parameter
     */
    @BeanTagAttribute
    public String getGroupNamespaceCode() {
        return groupNamespaceCode;
    }

    public void setGroupNamespaceCode(String groupNamespaceCode) {
        this.groupNamespaceCode = groupNamespaceCode;
    }

    /**
     * GroupId key used as a parameter of generated inquiry link url
     *
     * @return value of GroupId key parameter
     */
    @BeanTagAttribute
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * DataObjectClassName used as a parameter of generated inquiry link url
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
     * BaseInquiryUrl is the foundation of generated inquiry link url
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
