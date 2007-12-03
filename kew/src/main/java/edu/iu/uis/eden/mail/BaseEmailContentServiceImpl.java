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

package edu.iu.uis.eden.mail;

import org.kuali.rice.core.Core;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.server.BeanConverter;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

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
        String fromAddress = Utilities.getApplicationConstant(EdenConstants.EMAIL_REMINDER_FROM_ADDRESS_KEY);
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

    protected static CustomEmailAttribute getCustomEmailAttribute(WorkflowUser user, ActionItem actionItem) throws EdenUserNotFoundException, WorkflowException {
	if (actionItem.getRouteHeader() == null) {
	    actionItem.setRouteHeader(KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId()));
	}
        CustomEmailAttribute customEmailAttribute = actionItem.getRouteHeader().getCustomEmailAttribute();
        if (customEmailAttribute != null) {
            RouteHeaderVO routeHeaderVO = BeanConverter.convertRouteHeader(actionItem.getRouteHeader(), user);
            ActionRequestValue actionRequest = KEWServiceLocator.getActionRequestService().findByActionRequestId(actionItem.getActionRequestId());
            ActionRequestVO actionRequestVO = BeanConverter.convertActionRequest(actionRequest);
            customEmailAttribute.setRouteHeaderVO(routeHeaderVO);
            customEmailAttribute.setActionRequestVO(actionRequestVO);
        }
        return customEmailAttribute;
    }

    protected String getActionListUrl() {
        return Core.getCurrentContextConfig().getBaseUrl() + Utilities.getApplicationConstant(EdenConstants.APPLICATION_CONTEXT_KEY) + "/" + "ActionList.do";
    }

    protected String getPreferencesUrl() {
        return Core.getCurrentContextConfig().getBaseUrl() + Utilities.getApplicationConstant(EdenConstants.APPLICATION_CONTEXT_KEY) + "/" + "Preferences.do";
    }
}