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
package org.kuali.rice.ken.web.spring;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.ken.exception.ErrorList;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Base class for KEN controllers for sending notifications
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class BaseSendNotificationController extends MultiActionController {
    private static final Logger LOG = Logger.getLogger(BaseSendNotificationController.class);

    private static final String USER_RECIPS_PARAM = "userRecipients";
    private static final String WORKGROUP_RECIPS_PARAM = "workgroupRecipients";
    private static final String SPLIT_REGEX = "(%2C|,)";
    
    private static IdentityManagementService identityManagementService;

    protected static IdentityManagementService getIdentityManagementService() {
        if ( identityManagementService == null ) {
            identityManagementService = KIMServiceLocator.getIdentityManagementService();
        }
        return identityManagementService;
    }
    
    protected String[] parseUserRecipients(HttpServletRequest request) {
        return parseCommaSeparatedValues(request, USER_RECIPS_PARAM);
    }

    protected String[] parseWorkgroupRecipients(HttpServletRequest request) {
        return parseCommaSeparatedValues(request, WORKGROUP_RECIPS_PARAM);
    }

    protected String[] parseCommaSeparatedValues(HttpServletRequest request, String param) {
        String vals = request.getParameter(param);
        if (vals != null) {
            String[] split = vals.split(SPLIT_REGEX);
            List<String> strs = new ArrayList<String>();
            for (String component: split) {
                if (StringUtils.isNotBlank(component)) {
                    strs.add(component.trim());
                }
            }
            return strs.toArray(new String[strs.size()]);
        } else {
            return new String[0];
        }
    }

    protected boolean isUserRecipientValid(String user, ErrorList errors) {
        boolean valid = true;
        KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(user);
        if (principal == null) {
        	valid = false;
        	errors.addError("'" + user + "' is not a valid principal name");
        }

        return valid;
    }

    protected boolean isWorkgroupRecipientValid(String group, ErrorList errors) {
        // FIXME: this will probably need to split the given group name
        // on some delimiter to separate the namespace and group name
        // or switch to using the group ID number
        KimGroup i = getIdentityManagementService().getGroupByName("", group);
        if (i == null) {
            errors.addError( group + " is not a valid group name");
            return false;
        } else {
            return true;
        }
    }
}