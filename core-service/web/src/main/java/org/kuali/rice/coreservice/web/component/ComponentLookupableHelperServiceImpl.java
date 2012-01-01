/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.coreservice.web.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.coreservice.impl.component.ComponentBo;
import org.kuali.rice.coreservice.impl.component.DerivedComponentBo;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ComponentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final long serialVersionUID = -3978422770535345525L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ComponentLookupableHelperServiceImpl.class);
    
    private static final String ACTIVE = "active";
    private static final String CODE = "code";
    private static final String NAMESPACE_CODE = "namespaceCode";
    private static final String NAME = "name";

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {

        List<BusinessObject> baseLookup = (List<BusinessObject>) super.getSearchResults(fieldValues);

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

            // now search for derived components
            Map<String, Object> derivedComponentCriteria = new HashMap<String, Object>();
            if (fieldValues.containsKey(NAMESPACE_CODE) && StringUtils.isNotBlank(fieldValues.get(NAMESPACE_CODE))) {
                derivedComponentCriteria.put(NAMESPACE_CODE, fieldValues.get(NAMESPACE_CODE));
            }
            if (fieldValues.containsKey(CODE) && StringUtils.isNotBlank(fieldValues.get(CODE))) {
                derivedComponentCriteria.put(CODE, fieldValues.get(CODE));
            }
            if (fieldValues.containsKey(NAME) && StringUtils.isNotBlank(fieldValues.get(NAME))) {
                derivedComponentCriteria.put(NAME, fieldValues.get(NAME));
            }
            Collection<DerivedComponentBo> derivedComponentBos = null;
            if (derivedComponentCriteria.isEmpty()) {
                derivedComponentBos = KRADServiceLocator.getBusinessObjectService().findAll(DerivedComponentBo.class);
            } else {
                derivedComponentBos = KRADServiceLocator.getBusinessObjectService().findMatching(DerivedComponentBo.class, derivedComponentCriteria);
            }
            if (CollectionUtils.isNotEmpty(derivedComponentBos)) {
                for (DerivedComponentBo derivedComponentBo : derivedComponentBos) {
                    if (totalCount++ < maxResultsCount) {
                        baseLookup.add(DerivedComponentBo.toComponentBo(derivedComponentBo));
                    } else {
                        break;
                    }
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
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if ( ((ComponentBo)businessObject).getObjectId() == null ) {
            return super.getEmptyActionUrls();
        }
        return super.getCustomActionUrls(businessObject, pkNames);
    }

}
