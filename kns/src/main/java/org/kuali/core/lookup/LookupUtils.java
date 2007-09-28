/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.RelationshipDefinition;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.dbplatform.KualiDBPlatform;
import org.kuali.core.exceptions.ClassNotPersistableException;
import org.kuali.core.exceptions.UnknownBusinessClassAttributeException;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.comparator.NullValueComparator;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.rice.KNSServiceLocator;

/**
 * This is a static utility class for Lookup related utilities and helper methods.
 * 
 * 
 */
public class LookupUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupUtils.class);

    private static DataDictionaryService dataDictionaryService;
    private static PersistenceStructureService persistenceStructureService;
    private static BusinessObjectDictionaryService businessObjectDictionaryService;
    private static BusinessObjectMetaDataService businessObjectMetaDataService;

    public LookupUtils() {
        // default constructor for Spring to call to start up initialization process
    }

    public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
        LookupUtils.businessObjectDictionaryService = businessObjectDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService ddService) {
        LookupUtils.dataDictionaryService = ddService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        LookupUtils.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the businessObjectMetaDataService attribute value.
     * @param businessObjectMetaDataService The businessObjectMetaDataService to set.
     */
    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        LookupUtils.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    /**
     * 
     * This method uses the DataDictionary to determine whether to force uppercase the value, and if it should, then it does the
     * uppercase, and returns the upper-cased value.
     * 
     * @param boClass Parent BO class that the fieldName is a member of.
     * @param fieldName Name of the field to be forced to uppercase.
     * @param fieldValue Value of the field that may be uppercased.
     * @return The correctly uppercased fieldValue if it should be uppercased, otherwise fieldValue is returned unchanged.
     * 
     */
    public static String forceUppercase(Class boClass, String fieldName, String fieldValue) {

        // short-circuit to exit if there isnt enough information to do the forceUppercase
        if (StringUtils.isBlank(fieldValue)) {
            return fieldValue;
        }

        // parameter validation
        if (boClass == null) {
            throw new IllegalArgumentException("Parameter boClass passed in with null value.");
        }
        else if (!BusinessObject.class.isAssignableFrom(boClass)) {
            throw new IllegalArgumentException("Parameter boClass value passed in [" + boClass.getName() + "] " + "was not a descendent of BusinessObject.");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("Parameter fieldName passed in with empty value.");
        }

        if (!dataDictionaryService.isAttributeDefined(boClass, fieldName)) {
            return fieldValue;
        }


        boolean forceUpperCase = false;
        try {
            forceUpperCase = dataDictionaryService.getAttributeForceUppercase(boClass, fieldName).booleanValue();
        }
        catch (UnknownBusinessClassAttributeException ubae) {
            // do nothing, dont alter the fieldValue
        }
        if (forceUpperCase) {
            return fieldValue.toUpperCase();
        }
        return fieldValue;
    }

    /**
     * 
     * This method uses the DataDictionary to determine whether to force uppercase the values, and if it should, then it does the
     * uppercase, and returns the upper-cased Map of fieldname/fieldValue pairs.
     * 
     * @param boClass Parent BO class that the fieldName is a member of.
     * @param fieldValues A Map<String,String> where the key is the fieldName and the value is the fieldValue.
     * @return The same Map is returned, with the appropriate values uppercased (if any).
     * 
     */
    public static Map<String, String> forceUppercase(Class boClass, Map<String, String> fieldValues) {
        if (boClass == null) {
            throw new IllegalArgumentException("Parameter boClass passed in with null value.");
        }
        else if (!PersistableBusinessObject.class.isAssignableFrom(boClass)) {
            throw new IllegalArgumentException("Parameter boClass value passed in [" + boClass.getName() + "] " + "was not a descendent of BusinessObject.");
        }
        if (fieldValues == null) {
            throw new IllegalArgumentException("Parameter fieldValues passed in with null value.");
        }

        for (String fieldName : fieldValues.keySet()) {
            fieldValues.put(fieldName, LookupUtils.forceUppercase(boClass, fieldName, (String) fieldValues.get(fieldName)));
        }
        return fieldValues;
    }

    public static void applySearchResultsLimit(Criteria criteria, KualiDBPlatform platform) {
        Integer limit = getApplicationSearchResultsLimit();
        if (limit != null) {
            platform.applyLimit(limit, criteria);
        }
    }

    public static Integer getApplicationSearchResultsLimit() {
        String limitString = KNSServiceLocator.getKualiConfigurationService().getParameterValue(RiceConstants.KNS_NAMESPACE, RiceConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE, RiceConstants.SystemGroupParameterNames.LOOKUP_RESULTS_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return null;
    }
    
    /**
     * This method the maximum rows per page in a multiple value lookup
     * 
     * @see org.kuali.RiceConstants.SystemGroupParameterNames#MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE
     * @return
     */
    public static Integer getApplicationMaximumSearchResulsPerPageForMultipleValueLookups() {
        String limitString = KNSServiceLocator.getKualiConfigurationService().getParameterValue(RiceConstants.KNS_NAMESPACE, RiceConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE, RiceConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return null;
    }
    
    /**
     * This makes a , delimited String list of fields separated by a , into a List of target --> lookup readOnlyFieldsList.
     * 
     * @param readOnlyFields
     * @return the List representation of the readOnlyFields  String provided.
     */
    public static List<String> translateReadOnlyFieldsToList(String readOnlyFieldsString) {
        List<String> readOnlyFieldsList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(readOnlyFieldsString)) {
            if (readOnlyFieldsString.indexOf(",") > 0) {
                StringTokenizer token = new StringTokenizer(readOnlyFieldsString, ",");
                while (token.hasMoreTokens()) {
                    String element = token.nextToken();
                    readOnlyFieldsList.add(element);
                }
            }
            else {
                readOnlyFieldsList.add(readOnlyFieldsString);
            }
        }
      return readOnlyFieldsList;  
    }

    /**
     * This translates a , delimited list of pairs separated by a : into a Map of target --> lookup field conversions.
     * 
     * @param conversionFields
     * @return the Map representation of the fieldConversions String provided.
     */
    public static Map translateFieldConversions(String fieldConversionsString) {
        Map fieldConversionsMap = new HashMap();
        if (StringUtils.isNotEmpty(fieldConversionsString)) {
            if (fieldConversionsString.indexOf(",") > 0) {
                StringTokenizer token = new StringTokenizer(fieldConversionsString, ",");
                while (token.hasMoreTokens()) {
                    String element = token.nextToken();
                    fieldConversionsMap.put(element.substring(0, element.indexOf(":")), element.substring(element.indexOf(":") + 1));
                }
            }
            else {
                fieldConversionsMap.put(fieldConversionsString.substring(0, fieldConversionsString.indexOf(":")), fieldConversionsString.substring(fieldConversionsString.indexOf(":") + 1));
            }
        }
        return fieldConversionsMap;
    }

    public static Field setFieldQuickfinder(BusinessObject businessObject, 
            String attributeName, Field field, List displayedFieldNames) {
        return setFieldQuickfinder( businessObject, (String)null, false, 0, attributeName, field, displayedFieldNames );
    }
    
    public static Field setFieldQuickfinder(BusinessObject businessObject, 
            String attributeName, Field field, List displayedFieldNames, SelectiveReferenceRefresher srr) {
        return setFieldQuickfinder( businessObject, (String)null, false, 0, attributeName, field, displayedFieldNames, srr );
    }
    
    /**
     * Sets a fields quickfinder class and field conversions for an attribute.
     */
    public static Field setFieldQuickfinder(BusinessObject businessObject, String collectionName, boolean addLine, int index,
            String attributeName, Field field, List displayedFieldNames, SelectiveReferenceRefresher srr) {
        field = setFieldQuickfinder(businessObject, collectionName, addLine, index, attributeName, field, displayedFieldNames);
        if (srr != null) {
            String collectionPrefix = "";
            if ( collectionName != null ) {
                if (addLine) {
                    collectionPrefix = RiceConstants.MAINTENANCE_ADD_PREFIX + collectionName + ".";
                }
                else {
                    collectionPrefix = collectionName + "[" + index + "].";
                }
            }
            field.setReferencesToRefresh(convertReferencesToSelectCollectionToString(
                    srr.getAffectedReferencesFromLookup(businessObject, attributeName, collectionPrefix)));
        }
        return field;
    }
    
    /**
     * Sets a fields quickfinder class and field conversions for an attribute.
     */
    public static Field setFieldQuickfinder(BusinessObject businessObject, String collectionName, boolean addLine, int index,
            String attributeName, Field field, List displayedFieldNames) {
        if (businessObject == null) {
            return field;
        }
        
        Boolean noLookupField = businessObjectDictionaryService.noLookupFieldLookup(businessObject.getClass(), attributeName);
        if (noLookupField != null && noLookupField.booleanValue()) {
            return field;
        }
        
        BusinessObjectRelationship relationship = null;
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "setFieldQuickfinder("+businessObject.getClass().getName()+","+attributeName+","+field+","+displayedFieldNames+")" );
        }

        relationship = businessObjectMetaDataService.getBusinessObjectRelationship(businessObject, businessObject.getClass(), attributeName, "", false);

        String collectionPrefix = "";
        if ( collectionName != null ) {
            if (addLine) {
                collectionPrefix = RiceConstants.MAINTENANCE_ADD_PREFIX + collectionName + ".";
            }
            else {
                collectionPrefix = collectionName + "[" + index + "].";
            }
        }
       
        if (relationship == null) {
            Class c = ObjectUtils.getPropertyType(businessObject, attributeName, persistenceStructureService);

            if(c!=null) {
                if (attributeName.contains(".")) {
                    attributeName = StringUtils.substringBeforeLast( attributeName, "." );
                }
                
                RelationshipDefinition ddReference = businessObjectMetaDataService.getBusinessObjectRelationshipDefinition(businessObject, attributeName);
                relationship = businessObjectMetaDataService.getBusinessObjectRelationship(ddReference, businessObject, businessObject.getClass(), attributeName, "", false);
                if(relationship!=null) {
                    field.setQuickFinderClassNameImpl(relationship.getRelatedClass().getName());
                    field.setFieldConversions(generateFieldConversions( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null));
                    field.setLookupParameters(generateLookupParameters( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null));
                }
            }
            
            return field;
        }
        if (ObjectUtils.isNestedAttribute(attributeName)) {
            //first determine the prefix and the attribute we are referring to
            String nestedAttributePrefix = StringUtils.substringBeforeLast(attributeName, ".");
            PersistableBusinessObject childBO = (PersistableBusinessObject)getNestedBusinessObject(businessObject, attributeName);
            
            field.setQuickFinderClassNameImpl(relationship.getRelatedClass().getName());
            field.setFieldConversions( generateFieldConversions( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, nestedAttributePrefix ) );
            field.setLookupParameters( generateLookupParameters( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, nestedAttributePrefix ) );
        } else {
            field.setQuickFinderClassNameImpl(relationship.getRelatedClass().getName());
            field.setFieldConversions( generateFieldConversions( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null ) );
            field.setLookupParameters( generateLookupParameters( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null ) );
        }

        return field;
    }

    public static Map getPrimitiveReference(BusinessObject businessObject, String attributeName) {
        Map chosenReferenceByKeySize = new HashMap();
        Map chosenReferenceByFieldName = new HashMap();
    
        Map referenceClasses = null;
        
        try {
            referenceClasses = persistenceStructureService.getReferencesForForeignKey(businessObject.getClass(), attributeName);
        } catch ( ClassNotPersistableException ex ) {
            // do nothing, there is no quickfinder
        }
    
        // if field is not fk to any reference class, return field object w no quickfinder
        if (referenceClasses == null || referenceClasses.isEmpty()) {
            return chosenReferenceByKeySize;
        }
    
        /*
         * if field is fk to more than one reference, take the class with the least # of pk fields, this should give the correct
         * grain for the attribute
         */
        int minKeys = Integer.MAX_VALUE;
        for (Iterator iter = referenceClasses.keySet().iterator(); iter.hasNext();) {
            String attr = (String) iter.next();
            Class clazz = (Class) referenceClasses.get(attr);
            List pkNames = persistenceStructureService.listPrimaryKeyFieldNames(clazz);
    
            // Compare based on key size.
            if (pkNames.size() < minKeys) {
                minKeys = pkNames.size();
                chosenReferenceByKeySize.clear();
                chosenReferenceByKeySize.put(attr, clazz);
            }
    
            // Compare based on field name.
            if (attributeName.startsWith(attr)) {
                chosenReferenceByFieldName.clear();
                chosenReferenceByFieldName.put(attr, clazz);
            }
        }
    
        // If a compatible key was found based on field names, prefer it, otherwise use choice by key size.
        return chosenReferenceByFieldName.isEmpty() ? chosenReferenceByKeySize : chosenReferenceByFieldName;
    }

    /**
     * 
     * This method walks through the nested attribute and finds the last business object in the chain and returns it (excluding the
     * last parameter which is the actual attribute)
     * 
     * @param attributeName
     * @return
     */
    public static BusinessObject getNestedBusinessObject(BusinessObject bo, String attributeName) {
        String[] nestedAttributes = StringUtils.split(attributeName, ".");
    
        BusinessObject childBO = null;
        String attributeRefName = "";
        Class clazz = null;
        if (nestedAttributes.length > 1) {
            String attributeStringSoFar = "";
            for (int i = 0; i < nestedAttributes.length - 1; i++) {
                // we need to build a string of the attribute names depending on which iteration we're in.
                // so if the original attributeName string we're using is "a.b.c.d.e", then first iteration would use
                // "a", 2nd "a.b", 3rd "a.b.c", etc.
                if (i != 0) {
                    attributeStringSoFar = attributeStringSoFar + ".";  
                }
                attributeStringSoFar = attributeStringSoFar + nestedAttributes[i];
                
                clazz = ObjectUtils.getPropertyType( bo, attributeStringSoFar, persistenceStructureService );

                if (clazz != null && BusinessObject.class.isAssignableFrom(clazz)) {
                    try {
                        childBO = (BusinessObject) clazz.newInstance();
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return childBO;
    }

    public static Class getNestedReferenceClass(BusinessObject businessObject, String attributeName) {    
        BusinessObject bo = getNestedBusinessObject(businessObject, attributeName);
        return null == bo ? null : bo.getClass();
    }

    private static String generateFieldConversions(BusinessObject businessObject, String collectionName, BusinessObjectRelationship relationship, String propertyPrefix, List displayedFieldNames, String nestedObjectPrefix) {
        String fieldConversions = "";
    
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "generateFieldConversions(" + businessObject.getClass().getName() + "," + collectionName + ",\n" + relationship + "\n," + propertyPrefix + "," + displayedFieldNames + "," + nestedObjectPrefix + ")" );
        }
        
        // get the references for the given property
        for ( Map.Entry<String,String> entry : relationship.getParentToChildReferences().entrySet() ) {
            String fromField = entry.getValue();
            String toField = entry.getKey();
    
            // find the displayed to field mapping
            if (!displayedFieldNames.contains(toField)) {
                toField = translateToDisplayedField(businessObject.getClass(), toField, displayedFieldNames);
            }
    
            if (StringUtils.isNotBlank(fieldConversions)) {
                fieldConversions += ",";
            }
    
            if ( StringUtils.isNotEmpty( propertyPrefix ) ) {
                toField = propertyPrefix + "." + toField;
            }

            if ( StringUtils.isNotEmpty( collectionName ) ) {
                toField = collectionName + toField;
            }
    
            fieldConversions += fromField + ":" + toField;
        }
    
        return fieldConversions;
    }

    private static String generateLookupParameters(BusinessObject businessObject, String collectionName, BusinessObjectRelationship relationship, String propertyPrefix, List displayedFieldNames, String nestedObjectPrefix) {
        
        String lookupParameters = "";
    
        List displayedQFFieldNames = businessObjectDictionaryService.getLookupFieldNames(relationship.getRelatedClass());
        for ( Map.Entry<String,String> entry : relationship.getParentToChildReferences().entrySet() ) {
            String fromField = entry.getKey();
            String toField = entry.getValue();
    
            if ( relationship.getUserVisibleIdentifierKey() == null || relationship.getUserVisibleIdentifierKey().equals( fromField ) ) {
	            // find the displayed from field mapping
	            if (!displayedFieldNames.contains(fromField)) {
	                fromField = translateToDisplayedField(businessObject.getClass(), fromField, displayedFieldNames);
	            }
	    
	            // translate to field
	            if (displayedQFFieldNames != null && !displayedQFFieldNames.contains(toField)) {
	                toField = translateToDisplayedField(relationship.getRelatedClass(), toField, displayedQFFieldNames);
	            }
	    
	            if (StringUtils.isNotBlank(lookupParameters)) {
	                lookupParameters += ",";
	            }
	    
	            if (propertyPrefix != null && !propertyPrefix.equals("")) {
	                fromField = propertyPrefix + "." + fromField;
	            }
	            
	            if ( StringUtils.isNotEmpty( collectionName ) ) {
	                fromField = collectionName + fromField;
	            }
	            
	            lookupParameters += fromField + ":" + toField;
            }
        }
    
        return lookupParameters;
    }
    

    private static String translateToDisplayedField(Class businessObjectClass, String fieldName, List displayedFieldNames) {
        if ( PersistableBusinessObject.class.isAssignableFrom( businessObjectClass ) ) {
            Map nestedFkMap = persistenceStructureService.getNestedForeignKeyMap(businessObjectClass);
        
            // translate to primitive fk if nested
            /*
             * if (ObjectUtils.isNestedAttribute(fieldName) && nestedFkMap.containsKey(fieldName)) { fieldName = (String)
             * nestedFkMap.get(fieldName); }
             */
        
            if (!displayedFieldNames.contains(fieldName)) {
                for (Iterator iterator = displayedFieldNames.iterator(); iterator.hasNext();) {
                    String dispField = (String) iterator.next();
        
                    if (nestedFkMap.containsKey(dispField) && nestedFkMap.get(dispField).equals(fieldName)) {
                        fieldName = dispField;
                    }
                }
            }
        }
    
        return fieldName;
    }

    public static String convertReferencesToSelectCollectionToString(Collection<String> referencesToRefresh) {
        StringBuilder buf = new StringBuilder();
        for (String reference : referencesToRefresh) {
            buf.append(reference).append(RiceConstants.REFERENCES_TO_REFRESH_SEPARATOR);
        }
        if (!referencesToRefresh.isEmpty()) {
            // we appended one too many separators, remove it
            buf.delete(buf.length() - RiceConstants.REFERENCES_TO_REFRESH_SEPARATOR.length(), buf.length());
        }
        return buf.toString();
    }
    
    public static String convertSetOfObjectIdsToString(Set<String> objectIds) {
        if (objectIds.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (String objectId : objectIds) {
            if (objectId.contains(RiceConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR)) {
                throw new RuntimeException("object ID " + objectId + " contains the selected obj ID separator");
            }
            buf.append(objectId).append(RiceConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR);
        }
        // added one extra separator, remove it
        buf.delete(buf.length() - RiceConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR.length(), buf.length());
        
        return buf.toString();
    }
    
    public static Set<String> convertStringOfObjectIdsToSet(String objectIdsString) {
        Set<String> set = new HashSet<String>();
        
        if (StringUtils.isNotBlank(objectIdsString)) {
            String[] objectIds = StringUtils.splitByWholeSeparator(objectIdsString, RiceConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR);
            for (String objectId : objectIds) {
                set.add(objectId);
            }
        }
        return set;
    }
    
    /**
     * Given a list of results from a lookup, determines the best comparator to use on the String values of each of these columns
     * 
     * This method exists because each cell (represented by the Column object) lists the comparator that should be used within it based on the property value class,
     * so we gotta go thru the whole list and determine the best comparator to use
     * 
     * @param resultsTable
     * @param column
     * @return
     */
    public static Comparator findBestValueComparatorForColumn(List<ResultRow> resultTable, int column) {
        // BIG HACK
        Comparator comp = NullValueComparator.getInstance();
        for (ResultRow row : resultTable) {
            Comparator tempComp = row.getColumns().get(column).getValueComparator();
            if (tempComp != null && !NullValueComparator.class.equals(tempComp.getClass())) {
                return tempComp;
            }
        }
        return comp;
    }
    
    /**
     * Given 3 sets of object IDs: the set of selected object IDs before rendering the current page,
     * the set of object IDs rendered on the page, and the set of object IDs selected on the page, computes
     * the total set of selected object IDs.
     * 
     * Instead of storing it in a set, returns it in a map with the selected object ID as both the key and value
     * @param previouslySelectedObjectIds
     * @param displayedObjectIds
     * @param selectedObjectIds
     * @return
     */
    public static Map<String, String> generateCompositeSelectedObjectIds(Set<String> previouslySelectedObjectIds, Set<String> displayedObjectIds, Set<String> selectedObjectIds) {
        Map<String, String> tempMap = new HashMap<String, String>();
        // Equivalent to the set operation:
        // (P - D) union C, where - is the set difference operator
        // P is the list of object IDs previously passed in, D is the set of displayed object IDs, and C is the set of checked obj IDs
        // since HTML does not pass a value for non-selected dcheckboxes
        
        // first build a map w/ all the previouslySelectedObjectIds as keys
        for (String previouslySelectedObjectId : previouslySelectedObjectIds) {
            tempMap.put(previouslySelectedObjectId, previouslySelectedObjectId);
        }
        // then remove all the displayed elements (any selected displayed elements will be added back in the next loop)
        for (String displayedObjectId : displayedObjectIds) {
            tempMap.remove(displayedObjectId);
        }
        // put back the selected IDs
        for (String selectedObjectId : selectedObjectIds) {
            tempMap.put(selectedObjectId, selectedObjectId);
        }
        return tempMap;
    }
    
    /**
     * Removes fields idenfied in the data dictionary as hidden from the lookup field values.
     * (This will remove Universal User ID and Person name from search requests when a user ID is entered.)
     * 
     * @param fieldValues
     */
    public static void removeHiddenCriteriaFields( Class businessObjectClass, Map fieldValues ) {
        List<String> lookupFieldAttributeList = businessObjectMetaDataService.getLookupableFieldNames(businessObjectClass);
        if (lookupFieldAttributeList != null) {
	        for (Iterator iter = lookupFieldAttributeList.iterator(); iter.hasNext();) {
	            String attributeName = (String) iter.next();
	            if (fieldValues.containsKey(attributeName)) {
	            	ControlDefinition controlDef = dataDictionaryService.getAttributeControlDefinition(businessObjectClass, attributeName);
	            	if ( controlDef.isHidden() ) {
	            		fieldValues.remove(attributeName);
	            	}
	            }
	        }
        }
    }
}
