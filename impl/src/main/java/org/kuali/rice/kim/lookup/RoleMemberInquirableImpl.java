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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleMemberInquirableImpl extends KualiInquirableImpl {

	protected final String ROLE_ID = "roleId";
	protected final String NAME = "name";
	protected final String NAMESPACE_CODE = "namespaceCode";
	protected final String DETAIL_OBJECTS = "detailObjects";
	protected final String ASSIGNED_TO_ROLES = "assignedToRoles";
	protected final String REQUIRED_ROLE_QUALIFIER_ATTRIBUTES = "requiredRoleQualifierAttributes";
	protected final String ATTRIBUTE_DATA_ID = "attributeDataId";
	
    protected AnchorHtmlData getHyperLink(Class inquiryClass, Map<String,String> fieldList, String inquiryUrl, String displayText){
    	AnchorHtmlData a = new AnchorHtmlData(inquiryUrl, KNSConstants.EMPTY_STRING, displayText);
    	a.setTitle(AnchorHtmlData.getTitleText(
                getKualiConfigurationService().getPropertyString(
                        INQUIRY_TITLE_PREFIX) + " " + 
                        getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(inquiryClass.getName()).getObjectLabel()+
                        " ", inquiryClass, fieldList));
    	return a;
    }

    protected AnchorHtmlData getInquiryUrlForPrimaryKeys(
    		Class clazz, BusinessObject businessObject, List<String> primaryKeys, String displayText){
    	if(businessObject==null)
    		return new AnchorHtmlData(KNSConstants.EMPTY_STRING, KNSConstants.EMPTY_STRING);

        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.START_METHOD);
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, clazz.getName());
        
        String titleAttributeValue;
        Map<String, String> fieldList = new HashMap<String, String>();
        for(String primaryKey: primaryKeys){
        	titleAttributeValue = (String)ObjectUtils.getPropertyValue(businessObject, primaryKey);
            parameters.put(primaryKey, titleAttributeValue);
            fieldList.put(primaryKey, titleAttributeValue);
        }
        if(StringUtils.isEmpty(displayText))
        	return getHyperLink(clazz, fieldList, UrlFactory.parameterizeUrl(KNSConstants.INQUIRY_ACTION, parameters));
        else
        	return getHyperLink(clazz, fieldList, UrlFactory.parameterizeUrl(KNSConstants.INQUIRY_ACTION, parameters), displayText);
    }

    protected String getKimAttributeLabelFromDD(String attributeName){
    	return KNSServiceLocator.getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName);
    }

}