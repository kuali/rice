/*
 * Copyright 2011 The Kuali Foundation
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
package edu.sampleu.travel.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.uif.service.impl.ViewHelperServiceImpl;

import edu.sampleu.travel.dto.FiscalOfficerInfo;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerInfoInquiryViewHelperServiceImpl extends ViewHelperServiceImpl implements Inquirable<FiscalOfficerInfo> {
    
    @Override
    public FiscalOfficerInfo getBusinessObject(Map fieldValues) {
        FiscalOfficerService service = GlobalResourceLoader.getService("fiscalOfficerService");
        
        return service.retrieveFiscalOfficer(new Long((String)fieldValues.get("id")));
    }

    @Override
    public HtmlData getInquiryUrl(FiscalOfficerInfo businessObject, String attributeName, boolean forceInquiry) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#addAdditionalSections(java.util.List, java.lang.Object)
     */
    @Override
    public void addAdditionalSections(List columns, FiscalOfficerInfo bo) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#getHtmlMenuBar()
     */
    @Override
    public String getHtmlMenuBar() {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#getInactiveRecordDisplay()
     */
    @Override
    public Map<String, Boolean> getInactiveRecordDisplay() {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#getSections(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public List getSections(BusinessObject bo) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#getShowInactiveRecords(java.lang.String)
     */
    @Override
    public boolean getShowInactiveRecords(String collectionName) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return false;
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#getTitle()
     */
    @Override
    public String getTitle() {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#setBusinessObjectClass(java.lang.Class)
     */
    @Override
    public void setBusinessObjectClass(Class businessObjectClass) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        
    }

    /**
     * @see org.kuali.rice.kns.inquiry.Inquirable#setShowInactiveRecords(java.lang.String, boolean)
     */
    @Override
    public void setShowInactiveRecords(String collectionName, boolean showInactive) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        
    }

}
