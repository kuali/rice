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
package org.kuali.rice.kns.lookup;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ParameterLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * Hides the edit/copy links when not valid for the current user.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.lang.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        return super.getCustomActionUrls(businessObject, pkNames);
    	//Parameter parm = (Parameter)businessObject;
        
        //if ( KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE, parm.getParameterWorkgroupName() ) ) {
        //    return super.getCustomActionUrls(businessObject, pkNames);
        //} else {
        //    return super.getEmptyActionUrls();
        //}
    }
    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
        boolean allowsEdit = false;
        Parameter parm = (Parameter)businessObject;
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.NAMESPACE_CODE, parm.getParameterNamespaceCode());
        permissionDetails.put(KimAttributes.COMPONENT_NAME, parm.getParameterDetailTypeCode());
        permissionDetails.put(KimAttributes.PARAMETER_NAME, parm.getParameterName());
        KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(
        		GlobalVariables.getUserSession().getPerson().getPrincipalId(),
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.MAINAIN_SYSTEM_PARAMETER,
				permissionDetails, null);
        return allowsEdit;
    }
}

