package org.kuali.rice.krad.service.impl;

import org.kuali.rice.core.api.criteria.QueryByCriteria;

import java.util.Map;

/**
 * Handles generating QueryByCriteria for lookups from a given Map of the properties submitted on the lookup form.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupCriteriaGenerator {

    QueryByCriteria.Builder generateCriteria(Class<?> type, Map<String, String> formProps, boolean usePrimaryKeysOnly);

    QueryByCriteria.Builder createObjectCriteriaFromMap(Object example, Map<String, String> formProps);

}
