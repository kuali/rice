/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.inquiry;

import org.kuali.rice.core.impl.namespace.NamespaceBo;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.lookup.RoleLookupableHelperServiceImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleInquirableImpl extends KualiInquirableImpl {

	protected final String ROLE_NAME = "roleName";
	protected final String ROLE_ID = "roleId";
	protected final String NAMESPACE_CODE = "namespaceCode";
	
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
		if(ROLE_NAME.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add(ROLE_ID);
		    //((AnchorHtmlData)inqUrl).setHref("../kim/identityManagementRoleDocument.do?methodToCall=inquiry&command=initiate&docTypeName=IdentityManagementRoleDocument"+href.substring(idx1, idx2));
		    String href = (getInquiryUrlForPrimaryKeys(RoleImpl.class, businessObject, primaryKeys, null)).getHref();
		    AnchorHtmlData htmlData = new AnchorHtmlData();
		    htmlData.setHref(RoleLookupableHelperServiceImpl.getCustomRoleInquiryHref(href));
			return htmlData;
		} else if(NAMESPACE_CODE.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add("code");
			NamespaceBo parameterNamespace = new NamespaceBo();
			parameterNamespace.setCode((String)ObjectUtils.getPropertyValue(businessObject, attributeName));
			return getInquiryUrlForPrimaryKeys(NamespaceBo.class, parameterNamespace, primaryKeys, null);
		} else if("kimRoleType.name".equals(attributeName)){
			KimTypeBo kimType = new KimTypeBo();
			kimType.setId( ((RoleImpl)businessObject).getKimTypeId() );
			return getInquiryUrlForPrimaryKeys(KimTypeBo.class, kimType, Collections.singletonList( KimConstants.PrimaryKeyConstants.KIM_TYPE_ID ), null);
        }
		
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}
