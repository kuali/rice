/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.inquiry;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.uif.widget.Inquiry;

/**
 * Defines business logic methods that support the Inquiry framework
 */
public interface Inquirable {

	/**
	 * Responsible for retrieving the data object from its data source
	 * (database, service call, etc).
	 * 
	 * @param fieldValues
	 *            a map of string field names and values
	 * @return the data object or null if not found
	 */
	public Object getDataObject(Map<String, String> fieldValues);

	/**
	 * This method will be called before getDataObject(Map). This is useful for
	 * implementing inquirables that can handle multiple data object types.
	 * 
	 * @param dataObjectClass
	 *            the class of the dataObject that this inquirable should
	 *            retrieve
	 */
	public void setDataObjectClass(Class<?> dataObjectClass);

	/**
	 * Invoked by the <code>ViewHelperService</code> to build a link to the
	 * inquiry
	 * 
	 * <p>
	 * Note this is used primarily for custom <code>Inquirable</code>
	 * implementations to customize the inquiry class or parameters for an
	 * inquiry. Instead of building the full inquiry link, implementations can
	 * make a callback to
	 * org.kuali.rice.kns.uif.widget.Inquiry.buildInquiryLink(Object, String,
	 * Class<?>, Map<String, String>) given an inquiry class and parameters to
	 * build the link field.
	 * </p>
	 * 
	 * @param dataObject
	 *            - parent object for the inquiry property
	 * @param propertyName
	 *            - name of the property the inquiry is being built for
	 * @param inquiry
	 *            - instance of the inquiry widget being built for the property
	 */
	public void buildInquirableLink(Object dataObject, String propertyName,
			Inquiry inquiry);

	@Deprecated
	public void setBusinessObjectClass(Class businessObjectClass);

	@Deprecated
	public BusinessObject getBusinessObject(Map fieldValues);

	@Deprecated
	public HtmlData getInquiryUrl(BusinessObject businessObject,
			String attributeName, boolean forceInquiry);

	@Deprecated
	public String getHtmlMenuBar();

	@Deprecated
	public String getTitle();

	@Deprecated
	public List getSections(BusinessObject bo);

	@Deprecated
	public void addAdditionalSections(List columns, BusinessObject bo);

	/**
	 * Indicates whether inactive records for the given collection should be
	 * display.
	 * 
	 * @param collectionName
	 *            - name of the collection (or sub-collection) to check inactive
	 *            record display setting
	 * @return true if inactive records should be displayed, false otherwise
	 */
	@Deprecated
	public boolean getShowInactiveRecords(String collectionName);

	/**
	 * Returns the Map used to control the state of inactive record collection
	 * display. Exposed for setting from the maintenance jsp.
	 */
	@Deprecated
	public Map<String, Boolean> getInactiveRecordDisplay();

	/**
	 * Indicates to maintainble whether or not inactive records should be
	 * displayed for the given collection name.
	 * 
	 * @param collectionName
	 *            - name of the collection (or sub-collection) to set inactive
	 *            record display setting
	 * @param showInactive
	 *            - true to display inactive, false to not display inactive
	 *            records
	 */
	@Deprecated
	public void setShowInactiveRecords(String collectionName,
			boolean showInactive);
}
