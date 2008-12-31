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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonLookupableHelperServiceImpl  extends KualiLookupableHelperServiceImpl {
	
	@Override
	public List<? extends BusinessObject> getSearchResults(
			Map<String, String> fieldValues) {
		if (fieldValues != null && StringUtils.isNotEmpty(fieldValues.get("principalName"))) {
			fieldValues.put("principalName", fieldValues.get("principalName").toLowerCase());
		}
		return super.getSearchResults(fieldValues);
	}

	@Override
	public boolean allowsMaintenanceNewOrCopyAction() {
		// TODO : to let it rendering 'create new' and 'edit'/'copy' button
		return true;
	}

	@Override
	public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
    	List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
    	    AnchorHtmlData htmlData = getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames);
    	    String href = htmlData.getHref();
    	    int idx1 = href.indexOf("&principalId=");
    	    int idx2 = href.indexOf("&", idx1+1);
    	    if (idx2 < 0) {
    	    	idx2 = href.length();
    	    }
    	    htmlData.setHref("../kim/identityManagementPersonDocument.do?methodToCall=docHandler&command=initiate&docTypeName=IdentityManagementPersonDocument"+href.substring(idx1, idx2));
    	    htmlDataList.add(htmlData);
        	//htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        
        return htmlDataList;
	}

	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
		// TODO shyu - THIS METHOD NEEDS JAVADOCS
		HtmlData inqUrl = super.getInquiryUrl(bo, propertyName);
	    String href = ((AnchorHtmlData)inqUrl).getHref();
	    if (StringUtils.isNotBlank(href)) {
		    int idx1 = href.indexOf("&principalId=");
		    int idx2 = href.indexOf("&", idx1+1);
		    if (idx2 < 0) {
		    	idx2 = href.length();
		    }
		    ((AnchorHtmlData)inqUrl).setHref("../kim/identityManagementPersonDocument.do?command=initiate&docTypeName=IdentityManagementPersonDocument"+href.substring(idx1, idx2));
	    }
	    return inqUrl;
	}
	
	
}
