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
package org.kuali.rice.kim.inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.lookup.GroupLookupableHelperServiceImpl;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.Namespace;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupInquirableImpl extends KualiInquirableImpl {


	protected final String GROUP_NAME = "groupName";
	protected final String GROUP_ID = "groupId";
	protected final String NAMESPACE_CODE = "namespaceCode";
	
    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
		if(GROUP_NAME.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add(GROUP_ID);
		    String href = (getInquiryUrlForPrimaryKeys(GroupImpl.class, businessObject, primaryKeys, null)).getHref();
		    AnchorHtmlData htmlData = new AnchorHtmlData();
		    htmlData.setHref(getCustomGroupInquiryHref(href));
			return htmlData;
		} else if(NAMESPACE_CODE.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add("code");
			Namespace parameterNamespace = new Namespace();
			parameterNamespace.setCode((String)ObjectUtils.getPropertyValue(businessObject, attributeName));
			return getInquiryUrlForPrimaryKeys(Namespace.class, parameterNamespace, primaryKeys, null);
        }
		
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

	public String getCustomGroupInquiryHref(String href){
        Properties parameters = new Properties();
        String hrefPart = "";
		if (StringUtils.isNotBlank(href) && href.indexOf("&"+KimConstants.PrimaryKeyConstants.GROUP_ID+"=")!=-1) {
			int idx1 = href.indexOf("&"+KimConstants.PrimaryKeyConstants.GROUP_ID+"=");
		    int idx2 = href.indexOf("&", idx1+1);
		    if (idx2 < 0) {
		    	idx2 = href.length();
		    }
	        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
	        hrefPart = href.substring(idx1, idx2);
	    }
		return UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+
				KimConstants.KimUIConstants.KIM_GROUP_INQUIRY_ACTION, parameters)+hrefPart;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
	    BusinessObject bo = super.getBusinessObject(fieldValues);
	    ((GroupImpl)bo).setMemberPersonsAndGroups();
	    return bo;
	}

}
