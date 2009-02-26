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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ParameterNamespace;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleInquirableImpl extends KualiInquirableImpl {

	protected final String ROLE_NAME = "roleName";
	protected final String ROLE_ID = "roleId";
	protected final String NAMESPACE_CODE = "namespaceCode";
	
    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
		if(ROLE_NAME.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add(ROLE_ID);
		    //((AnchorHtmlData)inqUrl).setHref("../kim/identityManagementRoleDocument.do?methodToCall=inquiry&command=initiate&docTypeName=IdentityManagementRoleDocument"+href.substring(idx1, idx2));
		    String href = (getInquiryUrlForPrimaryKeys(KimRoleImpl.class, businessObject, primaryKeys, null)).getHref();
		    AnchorHtmlData htmlData = new AnchorHtmlData();
		    htmlData.setHref(RoleLookupableHelperServiceImpl.getCustomRoleInquiryHref(href));
			return htmlData;
		} else if(NAMESPACE_CODE.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add("code");
			ParameterNamespace parameterNamespace = new ParameterNamespace();
			parameterNamespace.setCode((String)ObjectUtils.getPropertyValue(businessObject, attributeName));
			return getInquiryUrlForPrimaryKeys(ParameterNamespace.class, parameterNamespace, primaryKeys, null);
        }
		
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}