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

import org.kuali.rice.core.api.criteria.QueryByCriteria;

import java.util.List;
import java.util.Map;

/**
 * Handles generating QueryByCriteria for lookups from a given Map of the properties submitted on the lookup form.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupCriteriaGenerator {

    /**
     * Generates QueryByCriteria for lookup search criteria obtained from the lookup form.
     *
     * @param type
     * @param formProps
     * @param usePrimaryKeysOnly
     * @return
     * @deprecated please use {@link #generateCriteria(Class, java.util.Map, java.util.List, boolean)} instead
     */
    @Deprecated
    QueryByCriteria.Builder generateCriteria(Class<?> type, Map<String, String> formProps, boolean usePrimaryKeysOnly);

    /**
     * Generates QueryByCriteria for lookup search criteria obtained from the lookup form.
     *
     * <p>
     *     This implementation better isolates the UIFramework from the lookup service.
     * </p>
     *
     * @param type the class name of the object on which the lookup is performed.
     * @param formProps a Map containing the form properties to be used as search criteria.
     * @param wildcardAsLiteralPropertyNames  list of properties that have wildcards disabled, any wildcard characters
     *      are treated as literals.
     * @param usePrimaryKeysOnly determines whether only primary keys are used in search
     * @return QueryByCriteria.Builder
     */
    QueryByCriteria.Builder generateCriteria(Class<?> type, Map<String, String> formProps,
            List<String> wildcardAsLiteralPropertyNames, boolean usePrimaryKeysOnly);

    QueryByCriteria.Builder createObjectCriteriaFromMap(Object example, Map<String, String> formProps);
}
