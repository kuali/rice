/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
// Created on Mar 21, 2007

package org.kuali.rice.kew.mail.service.impl;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.mail.CustomEmailAttribute;
import org.kuali.rice.kew.mail.service.EmailContentService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Base EmailContentService implementation with a default email from address that can be
 * configured via Spring property injection
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class BaseEmailContentServiceImpl implements EmailContentService {
    protected String defaultEmailFromAddress = "workflow@indiana.edu";
    protected String deploymentEnvironment;

    public void setDefaultEmailFromAddress(String defaultEmailFromAddress) {
        this.defaultEmailFromAddress = defaultEmailFromAddress;
    }

    public String getApplicationEmailAddress() {
        // first check the configured value
        String fromAddress = Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.MAILER_DETAIL_TYPE, KEWConstants.EMAIL_REMINDER_FROM_ADDRESS);
        // if there's no value configured, use the default
        if (Utilities.isEmpty(fromAddress)) {
            fromAddress = defaultEmailFromAddress;
        }
        return fromAddress;
    }

    public String getDocumentTypeEmailAddress(DocumentType documentType) {
        String fromAddress = (documentType == null ? null : documentType.getNotificationFromAddress());
        if (Utilities.isEmpty(fromAddress)) {
            fromAddress = getApplicationEmailAddress();
        }
        return fromAddress;
    }

    public String getDeploymentEnvironment() {
        return deploymentEnvironment;
    }

    public void setDeploymentEnvironment(String deploymentEnvironment) {
        this.deploymentEnvironment = deploymentEnvironment;
    }

    protected static CustomEmailAttribute getCustomEmailAttribute(WorkflowUser user, ActionItem actionItem) throws KEWUserNotFoundException, WorkflowException {
	if (actionItem.getRouteHeader() == null) {
	    actionItem.setRouteHeader(KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId()));
	}
        CustomEmailAttribute customEmailAttribute = actionItem.getRouteHeader().getCustomEmailAttribute();
        if (customEmailAttribute != null) {
            RouteHeaderDTO routeHeaderVO = DTOConverter.convertRouteHeader(actionItem.getRouteHeader(), user);
            ActionRequestValue actionRequest = KEWServiceLocator.getActionRequestService().findByActionRequestId(actionItem.getActionRequestId());
            ActionRequestDTO actionRequestVO = DTOConverter.convertActionRequest(actionRequest);
            customEmailAttribute.setRouteHeaderVO(routeHeaderVO);
            customEmailAttribute.setActionRequestVO(actionRequestVO);
        }
        return customEmailAttribute;
    }

    protected String getActionListUrl() {
        return ConfigContext.getCurrentContextConfig().getBaseUrl() + Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KEWConstants.APPLICATION_CONTEXT) + "/" + "ActionList.do";
    }

    protected String getPreferencesUrl() {
        return ConfigContext.getCurrentContextConfig().getBaseUrl() + Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KEWConstants.APPLICATION_CONTEXT) + "/" + "Preferences.do";
    }
}