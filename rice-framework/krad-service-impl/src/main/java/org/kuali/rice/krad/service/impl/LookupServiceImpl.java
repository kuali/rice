/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.LookupService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for the Lookup structure. It Provides a generic search
 * mechanism against Business Objects. This is the default implementation, that
 * is delivered with Kuali.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupServiceImpl implements LookupService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupServiceImpl.class);

    private ConfigurationService kualiConfigurationService;
    private LegacyDataAdapter legacyDataAdapter;

    @Override
    @Deprecated
    public <T> Collection<T> findCollectionBySearchUnbounded(Class<T> example,
            Map<String, String> formProps) {
        return findCollectionBySearchHelper(example, formProps, true);
    }

    @Override
    @Deprecated
    public <T> Collection<T> findCollectionBySearch(Class<T> type, Map<String, String> formProps) {
        return findCollectionBySearchHelper(type, formProps, false);
    }

    @Override
    @Deprecated
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> type,
            Map<String, String> formProps, boolean unbounded) {
        return findCollectionBySearchHelper(type, formProps, unbounded, null);
    }

    @Override
    @Deprecated
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> type,
            Map<String, String> formProps, boolean unbounded, Integer searchResultsLimit) {
        return getLegacyDataAdapter().findCollectionBySearchHelper(type, formProps, unbounded,
                allPrimaryKeyValuesPresentAndNotWildcard(type, formProps), searchResultsLimit);
    }

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> type,Map<String, String> formProps,
           List<String> wildcardAsLiteralPropertyNames, boolean unbounded, Integer searchResultsLimit) {
        return getLegacyDataAdapter().findCollectionBySearchHelper(type, formProps, wildcardAsLiteralPropertyNames,
                unbounded, allPrimaryKeyValuesPresentAndNotWildcard(type, formProps), searchResultsLimit);
    }

    @Override
    public <T> T findObjectBySearch(Class<T> type, Map<String, String> formProps) {
        if (type == null || formProps == null) {
            throw new IllegalArgumentException("Object and Map must not be null");
        }
        return getLegacyDataAdapter().findObjectBySearch(type, formProps);
    }

    @Override
    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps) {
        return getLegacyDataAdapter().allPrimaryKeyValuesPresentAndNotWildcard(boClass, formProps);
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public LegacyDataAdapter getLegacyDataAdapter() {
        return legacyDataAdapter;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

}
