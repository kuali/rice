/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.core.web.component;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.impl.component.ComponentBo;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.DataDictionaryException;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ComponentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ComponentLookupableHelperServiceImpl.class);
    private static final String ACTIVE = "active";
    private static final String CODE = "code";
    private static final String NAMESPACE_CODE = "namespaceCode";
    private static final String NAME = "name";
    private ParameterService parameterService;

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {

        List<BusinessObject> baseLookup = (List<BusinessObject>) super.getSearchResults(fieldValues);

        // all step beans
        // all BO beans
        // all trans doc beans

        List<Component> components;
        try {
        	components = KNSServiceLocatorWeb.getRiceApplicationConfigurationMediationService().getNonDatabaseComponents();
        }
        catch (DataDictionaryException ex) {
            throw new RuntimeException("Problem parsing data dictionary during full load required for lookup to function: " + ex.getMessage(), ex);
        }

        String activeCheck = fieldValues.get(ACTIVE);
        if (activeCheck == null) {
            activeCheck = "";
        }
        int maxResultsCount = LookupUtils.getSearchResultsLimit(ComponentBo.class);
        // only bother with the component lookup if returning active components
        if (baseLookup instanceof CollectionIncomplete && !activeCheck.equals("N")) {
            long originalCount = Math.max(baseLookup.size(), ((CollectionIncomplete) baseLookup).getActualSizeIfTruncated());
            long totalCount = originalCount;
            Pattern detailTypeRegex = null;
            Pattern namespaceRegex = null;
            Pattern nameRegex = null;

            if (StringUtils.isNotBlank(fieldValues.get(CODE))) {
                String patternStr = fieldValues.get(CODE).replace("*", ".*").toUpperCase();
                try {
                    detailTypeRegex = Pattern.compile(patternStr);
                }
                catch (PatternSyntaxException ex) {
                    LOG.error("Unable to parse code pattern, ignoring.", ex);
                }
            }
            if (StringUtils.isNotBlank(fieldValues.get(NAMESPACE_CODE))) {
                String patternStr = fieldValues.get(NAMESPACE_CODE).replace("*", ".*").toUpperCase();
                try {
                    namespaceRegex = Pattern.compile(patternStr);
                }
                catch (PatternSyntaxException ex) {
                    LOG.error("Unable to parse namespaceCode pattern, ignoring.", ex);
                }
            }
            if (StringUtils.isNotBlank(fieldValues.get(NAME))) {
                String patternStr = fieldValues.get(NAME).replace("*", ".*").toUpperCase();
                try {
                    nameRegex = Pattern.compile(patternStr);
                }
                catch (PatternSyntaxException ex) {
                    LOG.error("Unable to parse name pattern, ignoring.", ex);
                }
            }
            for (Component pdt : components) {
                boolean includeType = true;
                if (detailTypeRegex != null) {
                    includeType = detailTypeRegex.matcher(pdt.getCode().toUpperCase()).matches();
                }
                if (includeType && namespaceRegex != null) {
                    includeType = namespaceRegex.matcher(pdt.getNamespaceCode().toUpperCase()).matches();
                }
                if (includeType && nameRegex != null) {
                    includeType = nameRegex.matcher(pdt.getName().toUpperCase()).matches();
                }
                if (includeType) {
                    if (totalCount < maxResultsCount) {
                        baseLookup.add(ComponentBo.from(pdt));
                    }
                    totalCount++;
                }
            }
            if (totalCount > maxResultsCount) {
                ((CollectionIncomplete) baseLookup).setActualSizeIfTruncated(totalCount);
            }
            else {
                ((CollectionIncomplete) baseLookup).setActualSizeIfTruncated(0L);
            }
        }

        return baseLookup;
    }

    /**
     * Suppress the edit/copy links on synthetic detail types.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if ( ((ComponentBo)businessObject).getObjectId() == null ) {
            return super.getEmptyActionUrls();
        }
        return super.getCustomActionUrls(businessObject, pkNames);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
