/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.uif.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.container.InquiryView;
import org.kuali.rice.kns.uif.container.LookupView;
import org.kuali.rice.kns.uif.container.MaintenanceView;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewDictionaryService;

/**
 * Implementation of <code>ViewDictionaryService</code>
 * 
 * <p>
 * Pulls view entries from the data dictionary to implement the various query
 * methods
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewDictionaryServiceImpl implements ViewDictionaryService {

    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.rice.kns.uif.service.ViewDictionaryService#getInquirable(java.lang.Class,
     *      java.lang.String)
     */
    public Inquirable getInquirable(Class<?> dataObjectClass, String viewName) {
        List<View> inquiryViews = getDataDictionary().getViewsForType(UifConstants.ViewType.INQUIRY);

        Inquirable inquirable = null;
        for (View view : inquiryViews) {
            InquiryView inquiryView = (InquiryView) view;

            if (StringUtils.equals(inquiryView.getDataObjectClassName().getName(), dataObjectClass.getName())) {
                if (StringUtils.equals(inquiryView.getViewName(), viewName)
                        || (StringUtils.isBlank(viewName) && StringUtils.equals(inquiryView.getViewName(),
                                UifConstants.DEFAULT_VIEW_NAME))) {
                    inquirable = (Inquirable) inquiryView.getViewHelperService();
                    break;
                }
            }
        }

        return inquirable;
    }

    /**
     * @see org.kuali.rice.kns.uif.service.ViewDictionaryService#isInquirable(java.lang.Class)
     */
    public boolean isInquirable(Class<?> dataObjectClass) {
        boolean inquirable = false;

        List<View> inquiryViews = getDataDictionary().getViewsForType(UifConstants.ViewType.INQUIRY);
        for (View view : inquiryViews) {
            InquiryView inquiryView = (InquiryView) view;

            if (StringUtils.equals(inquiryView.getDataObjectClassName().getName(), dataObjectClass.getName())) {
                inquirable = true;
                break;
            }
        }

        return inquirable;
    }

    /**
     * @see org.kuali.rice.kns.uif.service.ViewDictionaryService#isLookupable(java.lang.Class)
     */
    public boolean isLookupable(Class<?> dataObjectClass) {
        boolean lookupable = false;

        List<View> lookupViews = getDataDictionary().getViewsForType(UifConstants.ViewType.LOOKUP);
        for (View view : lookupViews) {
            LookupView lookupView = (LookupView) view;

            if (StringUtils.equals(lookupView.getDataObjectClassName().getName(), dataObjectClass.getName())) {
                lookupable = true;
                break;
            }
        }

        return lookupable;
    }

    /**
     * @see org.kuali.rice.kns.uif.service.ViewDictionaryService#isMaintainable(java.lang.Class)
     */
    public boolean isMaintainable(Class<?> dataObjectClass) {
        boolean maintainable = false;

        List<View> maintenanceViews = getDataDictionary().getViewsForType(UifConstants.ViewType.MAINTENANCE);
        for (View view : maintenanceViews) {
            MaintenanceView maintenanceView = (MaintenanceView) view;

            if (StringUtils.equals(maintenanceView.getDataObjectClassName().getName(), dataObjectClass.getName())) {
                maintainable = true;
                break;
            }
        }

        return maintainable;
    }

    protected DataDictionary getDataDictionary() {
        return getDataDictionaryService().getDataDictionary();
    }

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
