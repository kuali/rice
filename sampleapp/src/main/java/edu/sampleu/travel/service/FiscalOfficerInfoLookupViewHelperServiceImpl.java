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

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.uif.service.impl.LookupViewHelperServiceImpl;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerInfoLookupViewHelperServiceImpl extends LookupViewHelperServiceImpl {

    @Override
    protected List<?> getSearchResultsWithBounding(Map<String, String> fieldValues, boolean unbounded) {
        FiscalOfficerService service = GlobalResourceLoader.getService("fiscalOfficerService");
        return service.lookupFiscalOfficer(fieldValues);
    }

}
