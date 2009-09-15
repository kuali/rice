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

/**
 * This interface defines the methods for inquirables.
 */
public interface Inquirable {
    public String getHtmlMenuBar();

    public String getTitle();

    public BusinessObject getBusinessObject(Map fieldValues);

    public List getSections(BusinessObject bo);

    public void setBusinessObjectClass(Class businessObjectClass);

    public void addAdditionalSections(List columns, BusinessObject bo);
    
    /**
     * Indicates whether inactive records for the given collection should be display.
     * 
     * @param collectionName - name of the collection (or sub-collection) to check inactive record display setting
     * @return true if inactive records should be displayed, false otherwise
     */
    public boolean getShowInactiveRecords(String collectionName);
    
    /**
     * Returns the Map used to control the state of inactive record collection display. Exposed for setting from the
     * maintenance jsp.
     */
    public Map<String, Boolean> getInactiveRecordDisplay();
    
    /**
     * Indicates to maintainble whether or not inactive records should be displayed for the given collection name.
     * 
     * @param collectionName - name of the collection (or sub-collection) to set inactive record display setting
     * @param showInactive - true to display inactive, false to not display inactive records
     */
    public void setShowInactiveRecords(String collectionName, boolean showInactive);
    
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry);
}
