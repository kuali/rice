/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonLookupableHelperServiceImpl  extends KimLookupableHelperServiceImpl {
	
	private static final long serialVersionUID = 1971744785776844579L;
	
	@Override
	public List<? extends BusinessObject> getSearchResults(
			Map<String, String> fieldValues) {
		if (fieldValues != null && StringUtils.isNotEmpty(fieldValues.get(KIMPropertyConstants.Person.PRINCIPAL_NAME))) {
			fieldValues.put(KIMPropertyConstants.Person.PRINCIPAL_NAME, fieldValues.get(KIMPropertyConstants.Person.PRINCIPAL_NAME).toLowerCase());
		}
		return super.getSearchResults(fieldValues);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
		if(allowsNewOrCopyAction(KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_TYPE_NAME)){
			String href = "";
			Properties parameters = new Properties();
	        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
	        parameters.put(KNSConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
	        parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_TYPE_NAME);
	        parameters.put(KimConstants.PrimaryKeyConstants.PRINCIPAL_ID, ((PersonImpl)bo).getPrincipalId());
	        href = UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_ACTION, parameters);
	
	        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, 
	        		KNSConstants.DOC_HANDLER_METHOD, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
	
	    	anchorHtmlDataList.add(anchorHtmlData);
		}
    	return anchorHtmlDataList;
	}

	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
		HtmlData inqUrl = super.getInquiryUrl(bo, propertyName);
		Properties parameters = new Properties();
        parameters.put(KEWConstants.COMMAND_PARAMETER, KEWConstants.INITIATE_COMMAND);
        parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_TYPE_NAME);
        parameters.put(KimConstants.PrimaryKeyConstants.PRINCIPAL_ID, ((Person)bo).getPrincipalId());
        String href = UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+KimConstants.KimUIConstants.KIM_PERSON_INQUIRY_ACTION, parameters);
	    ((AnchorHtmlData)inqUrl).setHref(href);
	    return inqUrl;
	}
	
	/**
	 * Checks for the special role lookup parameters and removes/marks read-only the fields in the search criteria.
	 * If present, this method also has a side-effect of updating the title with the role name.
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
	 */
	@Override
	public List<Row> getRows() {
		title.remove(); 
		List<Row> rows = super.getRows();
		Iterator<Row> i = rows.iterator();
		String roleName = null;
		String namespaceCode = null;
		while ( i.hasNext() ) {
			Row row = i.next();
			Field field = row.getField(0);
			String propertyName = field.getPropertyName();
			if ( propertyName.equals("lookupRoleName") ) {
				Object val = getParameters().get( propertyName );
				String propVal = null;
				if ( val != null ) {
					propVal = (val instanceof String)?(String)val:((String[])val)[0];
				}
				if ( StringUtils.isBlank( propVal ) ) {
					i.remove();
				} else {
					field.setReadOnly(true);
					field.setDefaultValue( propVal );
					roleName = propVal;
				}
			} else if ( propertyName.equals("lookupRoleNamespaceCode") ) {
				Object val = getParameters().get( propertyName );
				String propVal = null;
				if ( val != null ) {
					propVal = (val instanceof String)?(String)val:((String[])val)[0];
				}
				if ( StringUtils.isBlank( propVal ) ) {
					i.remove();
				} else {
					field.setReadOnly(true);
					field.setDefaultValue( propVal );
					namespaceCode = propVal;
				}				
			}
		}
		if ( roleName != null && namespaceCode != null ) {
			title.set( namespaceCode + " " + roleName + " Lookup" );
		}
		return rows;
	}
	
	private ThreadLocal<String> title = new ThreadLocal<String>();
	public String getTitle() {
		if ( title.get() == null ) {
			return super.getTitle();
		}
		return title.get();
	}
}
