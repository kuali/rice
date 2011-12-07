/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.impl.document.search;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.RouteNodeLookupLogic;
import org.kuali.rice.kew.docsearch.DocumentSearchInternalUtils;
import org.kuali.rice.kew.api.KewApiConstants;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reference implementation of {@code DocumentSearchCriteriaTranslator}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchCriteriaTranslatorImpl implements DocumentSearchCriteriaTranslator {

    private static final Logger LOG = Logger.getLogger(DocumentSearchCriteriaTranslatorImpl.class);

    private static final String DOCUMENT_STATUSES = "documentStatuses";
    private static final String ROUTE_NODE_LOOKUP_LOGIC = "routeNodeLookupLogic";

    /**
     * Fields which translate directory from criteria strings to properties on the DocumentSearchCriteria.
     */
    private static final String[] DIRECT_TRANSLATE_FIELD_NAMES = {
            "documentId",
            "applicationDocumentId",
            "applicationDocumentStatus",
            "initiatorPrincipalName",
            "viewerPrincipalName",
            "approverPrincipalName",
            "routeNodeName",
            "documentTypeName",
            "saveName",
            "isAdvancedSearch"
    };
    private static final Set<String> DIRECT_TRANSLATE_FIELD_NAMES_SET =
            new HashSet<String>(Arrays.asList(DIRECT_TRANSLATE_FIELD_NAMES));

    private static final String[] DATE_RANGE_TRANSLATE_FIELD_NAMES = {
            "dateCreated",
            "dateLastModified",
            "dateApproved",
            "dateFinalized"
    };
    private static final Set<String> DATE_RANGE_TRANSLATE_FIELD_NAMES_SET =
            new HashSet<String>(Arrays.asList(DATE_RANGE_TRANSLATE_FIELD_NAMES));

    @Override
    public DocumentSearchCriteria translateFieldsToCriteria(Map<String, String> fieldValues) {

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        for (Map.Entry<String, String> field : fieldValues.entrySet()) {
            try {
                if (StringUtils.isNotBlank(field.getValue())) {
                    if (DIRECT_TRANSLATE_FIELD_NAMES_SET.contains(field.getKey())) {
                        PropertyUtils.setNestedProperty(criteria, field.getKey(), field.getValue());
                    } else if (DATE_RANGE_TRANSLATE_FIELD_NAMES_SET.contains(field.getKey())) {
                        applyDateRangeField(criteria, field.getKey(), field.getValue());
                    } else if (field.getKey().startsWith(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX)) {
                        String documentAttributeName = field.getKey().substring(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX.length());
                        applyDocumentAttribute(criteria, documentAttributeName, field.getValue());
                    }

                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to set document search criteria field: " + field.getKey(), e);
            }
        }

        String routeNodeLookupLogic = fieldValues.get(ROUTE_NODE_LOOKUP_LOGIC);
        if (StringUtils.isNotBlank(routeNodeLookupLogic)) {
            criteria.setRouteNodeLookupLogic(RouteNodeLookupLogic.valueOf(routeNodeLookupLogic));
        }

        String documentStatusesValue = fieldValues.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE);
        if (StringUtils.isNotBlank(documentStatusesValue)) {
            String[] documentStatuses = documentStatusesValue.split(",");
            for (String documentStatus : documentStatuses) {
                if (documentStatus.startsWith("category:")) {
                    String categoryCode = StringUtils.remove(documentStatus, "category:");
                    criteria.getDocumentStatusCategories().add(DocumentStatusCategory.fromCode(categoryCode));
                } else {
                    criteria.getDocumentStatuses().add(DocumentStatus.fromCode(documentStatus));
                }
            }
        }

        return criteria.build();
    }

    /**
     * Converts the DocumentSearchCriteria to a Map of values that can be applied to the Lookup form fields.
     * @param criteria the criteria to translate
     * @return a Map of values that can be applied to the Lookup form fields.
     */
    public Map<String, String[]> translateCriteriaToFields(DocumentSearchCriteria criteria) {
        Map<String, String[]> values = new HashMap<String, String[]>();

        for (String property: DIRECT_TRANSLATE_FIELD_NAMES) {
            convertCriteriaPropertyToField(criteria, property, values);
        }

        for (String property: DATE_RANGE_TRANSLATE_FIELD_NAMES) {
            convertCriteriaPropertyToField(criteria, property + "From", values);
            convertCriteriaPropertyToField(criteria, property + "To", values);
        }

        Map<String, List<String>> docAttrValues = criteria.getDocumentAttributeValues();
        for (Map.Entry<String, List<String>> entry: docAttrValues.entrySet()) {
            values.put(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + entry.getKey(), entry.getValue().toArray(new String[0]));
        }

        RouteNodeLookupLogic lookupLogic = criteria.getRouteNodeLookupLogic();
        if (lookupLogic != null) {
            values.put(ROUTE_NODE_LOOKUP_LOGIC, new String[]{lookupLogic.name()});
        }

        Collection<String> statuses = new ArrayList<String>();
        for (DocumentStatus status: criteria.getDocumentStatuses()) {
            statuses.add(status.getCode());
        }
        for (DocumentStatusCategory category: criteria.getDocumentStatusCategories()) {
            statuses.add("category:" + category.getCode());
        }
        values.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE, statuses.toArray(new String[0]));

        return values;
    }

    /**
     * Looks up a property on the criteria object and sets it as a key/value pair in the values Map
     * @param criteria the DocumentSearchCriteria
     * @param property the DocumentSearchCriteria property name
     * @param values the map of values to update
     */
    protected static void convertCriteriaPropertyToField(DocumentSearchCriteria criteria, String property, Map<String, String[]> values) {
        try {
            Object val = PropertyUtils.getProperty(criteria, property);
            if (val != null) {
                values.put(property, new String[] { ObjectUtils.toString(val) });
            }
        } catch (NoSuchMethodException nsme) {
            LOG.error("Error reading property '" + property + "' of criteria", nsme);
        } catch (InvocationTargetException ite) {
            LOG.error("Error reading property '" + property + "' of criteria", ite);
        } catch (IllegalAccessException iae) {
            LOG.error("Error reading property '" + property + "' of criteria", iae);

        }
    }

    protected void applyDateRangeField(DocumentSearchCriteria.Builder criteria, String fieldName, String fieldValue) throws Exception {
        DateTime lowerDateTime = DocumentSearchInternalUtils.getLowerDateTimeBound(fieldValue);
        DateTime upperDateTime = DocumentSearchInternalUtils.getUpperDateTimeBound(fieldValue);
        if (lowerDateTime != null) {
            PropertyUtils.setNestedProperty(criteria, fieldName + "From", lowerDateTime);
        }
        if (upperDateTime != null) {
            PropertyUtils.setNestedProperty(criteria, fieldName + "To", upperDateTime);
        }
    }

    protected void applyDocumentAttribute(DocumentSearchCriteria.Builder criteria, String documentAttributeName, String attributeValue) {
        criteria.addDocumentAttributeValue(documentAttributeName, attributeValue);
    }

}
