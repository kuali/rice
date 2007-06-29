/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.util;

import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.authorization.FieldAuthorization;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.control.ApcSelectControlDefinition;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.datadictionary.control.CurrencyControlDefinition;
import org.kuali.core.datadictionary.control.KualiUserControlDefinition;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.exceptions.UnknownBusinessClassAttributeException;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.keyvalues.ApcValuesFinder;
import org.kuali.core.lookup.keyvalues.IndicatorValuesFinder;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.web.format.FormatException;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.rice.KNSServiceLocator;


/**
 * This class is used to build Field objects from underlying data dictionary and general utility methods for handling fields.
 */
public class FieldUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FieldUtils.class);
    private static DataDictionaryService dictionaryService = KNSServiceLocator.getDataDictionaryService();
    private static BusinessObjectMetaDataService businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
    private static BusinessObjectDictionaryService businessObjectDictionaryService = KNSServiceLocator.getBusinessObjectDictionaryService();

    public static void setInquiryURL(Field field, BusinessObject bo, String propertyName) {
        String inquiryUrl = "";

        Boolean b = businessObjectDictionaryService.noInquiryFieldInquiry(bo.getClass(), propertyName);
        if (b == null || !b.booleanValue()) {
            Boolean b2 = businessObjectDictionaryService.forceLookupResultFieldInquiry(bo.getClass(), propertyName);
            inquiryUrl = KualiInquirableImpl.getInquiryUrl(bo, propertyName, null == b2 ? false : b2.booleanValue());
        }

        field.setInquiryURL(inquiryUrl);
    }

    /**
     * Builds up a Field object based on the propertyName and business object class.
     * 
     * @param propertyName
     * @return Field
     */
    public static Field getPropertyField(Class businessObjectClass, String attributeName, boolean translateCheckboxes) {
        Field field = new Field();
        field.setPropertyName(attributeName);
        field.setFieldLabel(dictionaryService.getAttributeLabel(businessObjectClass, attributeName));

        // get control type for ui, depending on type set other field properties
        ControlDefinition control = dictionaryService.getAttributeControlDefinition(businessObjectClass, attributeName);
        String fieldType = Field.TEXT;

        if (control != null) {
            if (control.isSelect()) {
                if (control.getScript() != null && control.getScript().length() > 0) {
                    fieldType = Field.DROPDOWN_SCRIPT;
                    field.setScript(control.getScript());
                }
                else {
                    fieldType = Field.DROPDOWN;
                }
            }


            if (control.isApcSelect()) {
                fieldType = Field.DROPDOWN_APC;
            }

            if (control.isCheckbox()) {
                fieldType = Field.CHECKBOX;
            }

            if (control.isRadio()) {
                fieldType = Field.RADIO;
            }

            if (control.isHidden()) {
                fieldType = Field.HIDDEN;
            }

            if (control.isKualiUser()) {
                fieldType = Field.KUALIUSER;
                KualiUserControlDefinition kualiUserControl = (KualiUserControlDefinition) control;
                field.setUniversalIdAttributeName(kualiUserControl.getUniversalIdAttributeName());
                field.setUserIdAttributeName(kualiUserControl.getUserIdAttributeName());
                field.setPersonNameAttributeName(kualiUserControl.getPersonNameAttributeName());
            }

            if (control.isWorkflowWorkgroup()) {
                fieldType = Field.WORKFLOW_WORKGROUP;
            }

            if (control.isTextarea()) {
                fieldType = Field.TEXT_AREA;
            }

            if (control.isLookupHidden()) {
                fieldType = Field.LOOKUP_HIDDEN;
            }

            if (control.isLookupReadonly()) {
                fieldType = Field.LOOKUP_READONLY;
            }

            if (control.isCurrency()) {
                fieldType = Field.CURRENCY;
            }

            if (Field.CURRENCY.equals(fieldType) && control instanceof CurrencyControlDefinition) {
                CurrencyControlDefinition currencyControl = (CurrencyControlDefinition) control;
                field.setStyleClass("amount");
                field.setSize(currencyControl.getSize());
                field.setFormattedMaxLength(currencyControl.getFormattedMaxLength());
            }

            // for text controls, set size attribute
            if (Field.TEXT.equals(fieldType)) {
                Integer size = control.getSize();
                if (size != null) {
                    field.setSize(size.intValue());
                }
                else {
                    field.setSize(30);
                }
                field.setDatePicker(control.isDatePicker());

            }

            if (Field.WORKFLOW_WORKGROUP.equals(fieldType)) {
                Integer size = control.getSize();
                if (size != null) {
                    field.setSize(size.intValue());
                }
                else {
                    field.setSize(30);
                }
            }

            // for text area controls, set rows and cols attributes
            if (Field.TEXT_AREA.equals(fieldType)) {
                Integer rows = control.getRows();
                if (rows != null) {
                    field.setRows(rows.intValue());
                }
                else {
                    field.setRows(3);
                }

                Integer cols = control.getCols();
                if (cols != null) {
                    field.setCols(cols.intValue());
                }
                else {
                    field.setCols(40);
                }
            }

            // for dropdown and radio, get instance of specified KeyValuesFinder and set field values
            if (Field.DROPDOWN.equals(fieldType) || Field.RADIO.equals(fieldType) || Field.DROPDOWN_SCRIPT.equals(fieldType) || Field.DROPDOWN_APC.equals(fieldType)) {
                Class keyFinderClassName = control.getValuesFinderClass();

                if (keyFinderClassName != null) {
                    try {
                        KeyValuesFinder finder = (KeyValuesFinder) keyFinderClassName.newInstance();

                        if (finder != null) {
                            if (finder instanceof ApcValuesFinder && control instanceof ApcSelectControlDefinition) {
                                ((ApcValuesFinder) finder).setGroup(((ApcSelectControlDefinition) control).getGroup());
                                ((ApcValuesFinder) finder).setParameterName(((ApcSelectControlDefinition) control).getParameterName());
                            }
                            field.setFieldValidValues(finder.getKeyValues());
                        }
                    }
                    catch (InstantiationException e) {
                        LOG.error("Unable to get new instance of finder class: " + keyFinderClassName);
                        throw new RuntimeException("Unable to get new instance of finder class: " + keyFinderClassName);
                    }
                    catch (IllegalAccessException e) {
                        LOG.error("Unable to get new instance of finder class: " + keyFinderClassName);
                        throw new RuntimeException("Unable to get new instance of finder class: " + keyFinderClassName);
                    }
                }
            }

            if (Field.CHECKBOX.equals(fieldType) && translateCheckboxes) {
                fieldType = Field.RADIO;
                field.setFieldValidValues((new IndicatorValuesFinder()).getKeyValues());
            }
        }

        field.setFieldType(fieldType);

        Boolean fieldRequired = KNSServiceLocator.getBusinessObjectDictionaryService().getLookupAttributeRequired(businessObjectClass, attributeName);
        if (fieldRequired != null) {
            field.setFieldRequired(fieldRequired.booleanValue());
        }

        Integer maxLength = dictionaryService.getAttributeMaxLength(businessObjectClass, attributeName);
        if (maxLength != null) {
            field.setMaxLength(maxLength.intValue());
        }

        Boolean upperCase = null;
        try {
            upperCase = dictionaryService.getAttributeForceUppercase(businessObjectClass, attributeName);
        }
        catch (UnknownBusinessClassAttributeException t) {
            boolean catchme = true;
            // throw t;
        }
        if (upperCase != null) {
            field.setUpperCase(upperCase.booleanValue());
        }

        Class formatterClass = dictionaryService.getAttributeFormatter(businessObjectClass, attributeName);
        if (formatterClass != null) {
            try {
                field.setFormatter((Formatter) formatterClass.newInstance());
            }
            catch (InstantiationException e) {
                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
            catch (IllegalAccessException e) {
                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
        }

        // set Field help properties
        field.setBusinessObjectClassName(businessObjectClass.getName());
        field.setFieldHelpName(attributeName);
        field.setFieldHelpSummary(dictionaryService.getAttributeSummary(businessObjectClass, attributeName));

        return field;
    }


    /**
     * Wraps each Field in the list into a Row.
     * 
     * @param fields
     * @return List of Row objects
     */
    public static List wrapFields(List fields) {
        return wrapFields(fields, Constants.DEFAULT_NUM_OF_COLUMNS);
    }

    /**
     * This method is to implement multiple columns where the numberOfColumns is obtained from data dictionary.
     * 
     * @param fields
     * @param numberOfColumns
     * @return
     */
    public static List<Row> wrapFields(List<Field> fields, int numberOfColumns) {

        List<Row> rows = new ArrayList();
        List<Field> fieldOnlyList = new ArrayList();
        int fieldsPosition = 0;
        for (Field element : fields) {
            if (Field.SUB_SECTION_SEPARATOR.equals(element.getFieldType())) {
                fieldsPosition = createBlankSpace(fieldOnlyList, rows, numberOfColumns, fieldsPosition);
                List fieldList = new ArrayList();
                fieldList.add(element);
                rows.add(new Row(fieldList));
            }
            else {
                if (fieldsPosition < numberOfColumns) {
                    fieldOnlyList.add(element);
                    fieldsPosition++;
                }
                else {
                    rows.add(new Row(new ArrayList(fieldOnlyList)));
                    fieldOnlyList.clear();
                    fieldOnlyList.add(element);
                    fieldsPosition = 1;
                }
            }
        }
        createBlankSpace(fieldOnlyList, rows, numberOfColumns, fieldsPosition);
        return rows;
    }

    /**
     * This is a helper method to create and add a blank space to the fieldOnly List.
     * 
     * @param fieldOnlyList
     * @param rows
     * @param numberOfColumns
     * @return fieldsPosition
     */
    private static int createBlankSpace(List<Field> fieldOnlyList, List<Row> rows, int numberOfColumns, int fieldsPosition) {
        int fieldOnlySize = fieldOnlyList.size();
        if (fieldOnlySize > 0) {
            for (int i = 0; i < (numberOfColumns - fieldOnlySize); i++) {
                Field empty = new Field();
                empty.setFieldType(Field.BLANK_SPACE);
                fieldOnlyList.add(empty);
            }
            rows.add(new Row(new ArrayList(fieldOnlyList)));
            fieldOnlyList.clear();
            fieldsPosition = 0;
        }
        return fieldsPosition;
    }

    /**
     * Wraps list of fields into a Field of type CONTAINER
     * 
     * @param name name for the field
     * @param label label for the field
     * @param fields list of fields that should be contained in the container
     * @return Field of type CONTAINER
     */
    public static Field constructContainerField(String name, String label, List fields) {
        return constructContainerField(name, label, fields, Constants.DEFAULT_NUM_OF_COLUMNS);
    }

    /**
     * Wraps list of fields into a Field of type CONTAINER and arrange them into multiple columns.
     * 
     * @param name name for the field
     * @param label label for the field
     * @param fields list of fields that should be contained in the container
     * @param numberOfColumns the number of columns for each row that the fields should be arranged into
     * @return Field of type CONTAINER
     */
    public static Field constructContainerField(String name, String label, List fields, int numberOfColumns) {
        Field containerField = new Field();
        containerField.setPropertyName(name);
        containerField.setFieldLabel(label);
        containerField.setFieldType(Field.CONTAINER);
        containerField.setNumberOfColumnsForCollection(numberOfColumns);

        List rows = wrapFields(fields, numberOfColumns);
        containerField.setContainerRows(rows);

        return containerField;
    }

    /**
     * Uses reflection to get the property names of the business object, then checks for a matching field property name. If found,
     * takes the value of the business object property and populates the field value. Iterates through for all fields in the list.
     * 
     * @param fields list of Field object to populate
     * @param bo business object to get field values from
     * @return List of fields with values populated from business object.
     */
    public static List populateFieldsFromBusinessObject(List fields, BusinessObject bo) {
        List populatedFields = new ArrayList();

        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            Field element = (Field) iter.next();
            if (element.containsBOData()) {
                String propertyName = element.getPropertyName();

                // See: https://test.kuali.org/jira/browse/KULCOA-1185
                // Properties that could not possibly be set by the BusinessObject should be ignored.
                // (https://test.kuali.org/jira/browse/KULRNE-4354; this code was killing the src attribute of IMAGE_SUBMITs).
                if (isPropertyNested(propertyName) && !isObjectTreeNonNullAllTheWayDown(bo, propertyName) && ((!element.getFieldType().equals(Field.IMAGE_SUBMIT)) && !(element.getFieldType().equals(Field.CONTAINER)) && (!element.getFieldType().equals(Field.QUICKFINDER)))) {
                    element.setPropertyValue(null);
                }
                else if (PropertyUtils.isReadable(bo, propertyName)) {
                    Object obj = ObjectUtils.getNestedValue(bo, element.getPropertyName());
                    if (obj != null) {
                        element.setPropertyValue(obj);
                    }

                    // set encrypted & masked value if user does not have permission to see real value in UI
                    if (element.isSecure()) {
                        try {
                            if (obj != null && obj.toString().endsWith(EncryptionService.HASH_POST_PREFIX)) {
                                element.setEncryptedValue(obj.toString());
                            }
                            else {
                                element.setEncryptedValue(KNSServiceLocator.getEncryptionService().encrypt(obj) + EncryptionService.ENCRYPTION_POST_PREFIX);
                            }
                        }
                        catch (GeneralSecurityException e) {
                            throw new RuntimeException("Unable to encrypt secure field " + e.getMessage());
                        }
                        element.setDisplayMaskValue(element.getDisplayMask().maskValue(obj));
                    }
                }
            }
            populatedFields.add(element);
        }

        return populatedFields;
    }

    /**
     * This method indicates whether or not propertyName refers to a nested attribute.
     * 
     * @param propertyName
     * @return true if propertyName refers to a nested property (e.g. "x.y")
     */
    static private boolean isPropertyNested(String propertyName) {
        return -1 != propertyName.indexOf('.');
    }

    /**
     * This method verifies that all of the parent objects of propertyName are non-null.
     * 
     * @param bo
     * @param propertyName
     * @return true if all parents are non-null, otherwise false
     */

    static private boolean isObjectTreeNonNullAllTheWayDown(BusinessObject bo, String propertyName) {
        String[] propertyParts = propertyName.split("\\.");

        StringBuffer property = new StringBuffer();
        for (int i = 0; i < propertyParts.length - 1; i++) {

            property.append((0 == property.length()) ? "" : ".").append(propertyParts[i]);
            try {
                if (null == PropertyUtils.getNestedProperty(bo, property.toString())) {
                    return false;
                }
            }
            catch (Throwable t) {
                LOG.debug("Either getter or setter not specified for property \"" + property.toString() + "\"", t);
                return false;
            }
        }

        return true;

    }

    /**
     * @param bo
     * @param propertyName
     * @return true if one (or more) of the intermediate objects in the given propertyName is null
     */
    private static boolean containsIntermediateNull(Object bo, String propertyName) {
        boolean containsNull = false;

        if (StringUtils.contains(propertyName, ".")) {
            String prefix = StringUtils.substringBefore(propertyName, ".");
            Object propertyValue = ObjectUtils.getPropertyValue(bo, prefix);

            if (propertyValue == null) {
                containsNull = true;
            }
            else {
                String suffix = StringUtils.substringAfter(propertyName, ".");
                containsNull = containsIntermediateNull(propertyValue, suffix);
            }
        }

        return containsNull;
    }

    /**
     * Uses reflection to get the property names of the business object, then checks for the property name as a key in the passed
     * map. If found, takes the value from the map and sets the business object property.
     * 
     * @param bo
     * @param fieldValues
     * @return Cached Values from any formatting failures
     */
    public static Map populateBusinessObjectFromMap(BusinessObject bo, Map fieldValues) {
        return populateBusinessObjectFromMap(bo, fieldValues, "");
    }

    /**
     * Uses reflection to get the property names of the business object, then checks for the property name as a key in the passed
     * map. If found, takes the value from the map and sets the business object property.
     * 
     * @param bo
     * @param fieldValues
     * @param propertyNamePrefix this value will be prepended to all property names in the returned unformattable values map
     * @return Cached Values from any formatting failures
     */
    public static Map populateBusinessObjectFromMap(BusinessObject bo, Map fieldValues, String propertyNamePrefix) {
        Map cachedValues = new HashMap();
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        try {
            for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
                String propertyName = (String) iter.next();

                if (propertyName.endsWith(Constants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION)) {
                    // since checkboxes do not post values when unchecked, this code detects whether a checkbox was unchecked, and
                    // sets the value to false.
                    if (StringUtils.isNotBlank((String) fieldValues.get(propertyName))) {
                        String checkboxName = StringUtils.removeEnd(propertyName, Constants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION);
                        String checkboxValue = (String) fieldValues.get(checkboxName);
                        if (checkboxValue == null) {
                            // didn't find a checkbox value, assume that it is unchecked
                            if (PropertyUtils.isWriteable(bo, checkboxName)) {
                                Class type = ObjectUtils.easyGetPropertyType(bo, checkboxName);
                                if (type == Boolean.TYPE || type == Boolean.class) {
                                    // ASSUMPTION: unchecked means false
                                    ObjectUtils.setObjectProperty(bo, checkboxName, type, "false");
                                }
                            }
                        }
                    }
                    // else, if not null, then it has a value, and we'll let the rest of the code handle it when the param is processed on
                    // another iteration (may be before or after this iteration).
                }
                else if (PropertyUtils.isWriteable(bo, propertyName) && fieldValues.get(propertyName) != null ) {
                    // if the field propertyName is a valid property on the bo class
                    Class type = ObjectUtils.easyGetPropertyType(bo, propertyName);
                    try {
                        ObjectUtils.setObjectProperty(bo, propertyName, type, fieldValues.get(propertyName));
                    }
                    catch (FormatException e) {
                        cachedValues.put(propertyNamePrefix + propertyName, fieldValues.get(propertyName));
                        errorMap.putError(propertyName, e.getErrorKey(), e.getErrorArgs());
                    }
                }
            }
        }
        catch (IllegalAccessException e) {
            LOG.error("unable to populate business object" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            LOG.error("unable to populate business object" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (NoSuchMethodException e) {
            LOG.error("unable to populate business object" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }

        return cachedValues;
    }

    /**
     * Does prefixing and read only settings of a Field UI for display in a maintenance document.
     * 
     * @param field - the Field object to be displayed
     * @param keyFieldNames - Primary key property names for the business object being maintained.
     * @param namePrefix - String to prefix Field names with.
     * @param maintenanceAction - The maintenance action requested.
     * @param readOnly - Indicates whether all fields should be read only.
     * @return Field
     */
    public static Field fixFieldForForm(Field field, List keyFieldNames, String namePrefix, String maintenanceAction, boolean readOnly, MaintenanceDocumentAuthorizations auths) {
        String propertyName = field.getPropertyName();
        // We only need to do the following processing if the field is not a sub section header
        if (field.containsBOData()) {

            // don't prefix submit fields, must start with dispatch parameter name
            if (!propertyName.startsWith(Constants.DISPATCH_REQUEST_PARAMETER)) {
                // if the developer hasn't set a specific prefix use the one supplied
                if (field.getPropertyPrefix() == null || field.getPropertyPrefix().equals("")) {
                    field.setPropertyName(namePrefix + propertyName);
                }
                else {
                    field.setPropertyName(field.getPropertyPrefix() + "." + propertyName);
                }
            }

            if (readOnly) {
                field.setReadOnly(true);
            }

            // set keys read only for edit
            if (keyFieldNames.contains(propertyName) && Constants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
                field.setReadOnly(true);
                field.setKeyField(true);
            }

            // apply any authorization restrictions to field availability on the UI
            applyAuthorization(field, auths);

            // if fieldConversions specified, prefix with new constant
            if (StringUtils.isNotBlank(field.getFieldConversions())) {
                String fieldConversions = field.getFieldConversions();
                String newFieldConversions = Constants.EMPTY_STRING;
                String[] conversions = StringUtils.split(fieldConversions, Constants.FIELD_CONVERSIONS_SEPERATOR);

                for (int l = 0; l < conversions.length; l++) {
                    String conversion = conversions[l];
                    String[] conversionPair = StringUtils.split(conversion, Constants.FIELD_CONVERSION_PAIR_SEPERATOR);
                    String conversionFrom = conversionPair[0];
                    String conversionTo = conversionPair[1];
                    conversionTo = Constants.MAINTENANCE_NEW_MAINTAINABLE + conversionTo;
                    newFieldConversions += (conversionFrom + Constants.FIELD_CONVERSION_PAIR_SEPERATOR + conversionTo);

                    if (l < conversions.length) {
                        newFieldConversions += Constants.FIELD_CONVERSIONS_SEPERATOR;
                    }
                }

                field.setFieldConversions(newFieldConversions);
            }

            if (Field.KUALIUSER.equals(field.getFieldType())) {
                // prefix the personNameAttributeName
            	int suffixIndex = field.getPropertyName().indexOf( field.getUserIdAttributeName() );
            	if ( suffixIndex != -1 ) {
            		field.setPersonNameAttributeName( field.getPropertyName().substring( 0, suffixIndex ) + field.getPersonNameAttributeName() );
            	} else {
            		field.setPersonNameAttributeName(namePrefix + field.getPersonNameAttributeName());
            	}
                
                // TODO: do we need to prefix the universalIdAttributeName in Field as well?
            }
            
            // if lookupParameters specified, prefix with new constant
            if (StringUtils.isNotBlank(field.getLookupParameters())) {
                String lookupParameters = field.getLookupParameters();
                String newLookupParameters = Constants.EMPTY_STRING;
                String[] conversions = StringUtils.split(lookupParameters, Constants.FIELD_CONVERSIONS_SEPERATOR);

                for (int m = 0; m < conversions.length; m++) {
                    String conversion = conversions[m];
                    String[] conversionPair = StringUtils.split(conversion, Constants.FIELD_CONVERSION_PAIR_SEPERATOR);
                    String conversionFrom = conversionPair[0];
                    String conversionTo = conversionPair[1];
                    conversionFrom = Constants.MAINTENANCE_NEW_MAINTAINABLE + conversionFrom;
                    newLookupParameters += (conversionFrom + Constants.FIELD_CONVERSION_PAIR_SEPERATOR + conversionTo);

                    if (m < conversions.length) {
                        newLookupParameters += Constants.FIELD_CONVERSIONS_SEPERATOR;
                    }
                }

                field.setLookupParameters(newLookupParameters);
            }

            // CONTAINER field types have nested rows and fields that need setup for the form
            if (Field.CONTAINER.equals(field.getFieldType())) {
                List containerRows = field.getContainerRows();
                List fixedRows = new ArrayList();

                for (Iterator iter = containerRows.iterator(); iter.hasNext();) {
                    Row containerRow = (Row) iter.next();
                    List containerFields = containerRow.getFields();
                    List fixedFields = new ArrayList();

                    for (Iterator iterator = containerFields.iterator(); iterator.hasNext();) {
                        Field containerField = (Field) iterator.next();
                        containerField = fixFieldForForm(containerField, keyFieldNames, namePrefix, maintenanceAction, readOnly, auths);
                        fixedFields.add(containerField);
                    }

                    fixedRows.add(new Row(fixedFields));
                }

                field.setContainerRows(fixedRows);
            }
        }
        return field;
    }

    public static void applyAuthorization(Field field, MaintenanceDocumentAuthorizations auths) {

        // only apply this on the newMaintainable
        if (field.getPropertyName().startsWith(Constants.MAINTENANCE_NEW_MAINTAINABLE)) {

            // get just the actual fieldName, with the document.newMaintainableObject, etc etc removed
            String fieldName = field.getPropertyName().substring(Constants.MAINTENANCE_NEW_MAINTAINABLE.length());

            // if the field is restricted somehow
            if (auths.hasAuthFieldRestricted(fieldName)) {
                FieldAuthorization fieldAuth = auths.getAuthFieldAuthorization(fieldName);

                // if its an editable field, allow decreasing availability to readonly or hidden
                if (Field.isInputField(field.getFieldType()) || field.getFieldType().equalsIgnoreCase(Field.CHECKBOX)) {

                    // only touch the field if the restricted type is hidden or readonly
                    if (fieldAuth.isReadOnly()) {
                        if (!field.isReadOnly()) {
                            field.setReadOnly(true);
                        }
                    }

                    else if (fieldAuth.isHidden()) {
                        if (field.getFieldType() != Field.HIDDEN) {
                            field.setFieldType(Field.HIDDEN);
                        }
                    }
                }

                // if the field is readOnly, and the authorization says it should be hidden,
                // then restrict it
                if (field.isReadOnly() && fieldAuth.isHidden()) {
                    field.setFieldType(Field.HIDDEN);
                }
            }
            // special check for old maintainable - need to ensure that fields hidden on the
            // "new" side are also hidden on the old side
        }
        else if (field.getPropertyName().startsWith(Constants.MAINTENANCE_OLD_MAINTAINABLE)) {
            // get just the actual fieldName, with the document.oldMaintainableObject, etc etc removed
            String fieldName = field.getPropertyName().substring(Constants.MAINTENANCE_OLD_MAINTAINABLE.length());
            // if the field is restricted somehow
            if (auths.hasAuthFieldRestricted(fieldName)) {
                FieldAuthorization fieldAuth = auths.getAuthFieldAuthorization(fieldName);

                if (fieldAuth.isHidden()) {
                    field.setFieldType(Field.HIDDEN);
                }
            }
        }
    }

    /**
     * Merges together sections of the old maintainable and new maintainable.
     * 
     * @param oldSections
     * @param newSections
     * @param keyFieldNames
     * @param maintenanceAction
     * @param readOnly
     * @return List of Section objects
     */
    public static List meshSections(List oldSections, List newSections, List keyFieldNames, String maintenanceAction, boolean readOnly, MaintenanceDocumentAuthorizations auths) {
        List meshedSections = new ArrayList();

        for (int i = 0; i < newSections.size(); i++) {
            Section maintSection = (Section) newSections.get(i);
            List sectionRows = maintSection.getRows();
            Section oldMaintSection = (Section) oldSections.get(i);
            List oldSectionRows = oldMaintSection.getRows();
            List<Row> meshedRows = new ArrayList();
            meshedRows = meshRows(oldSectionRows, sectionRows, keyFieldNames, maintenanceAction, readOnly, auths);
            maintSection.setRows(meshedRows);
            if (StringUtils.isBlank(maintSection.getErrorKey())) {
                maintSection.setErrorKey(MaintenanceUtils.generateErrorKeyForSection(maintSection));
            }
            meshedSections.add(maintSection);
        }

        return meshedSections;
    }

    /**
     * This method is a helper method for createRowsForNewFields. It puts together all the fields that should exist in a row after
     * calling the fixFieldForForm for the other necessary prefixing and setting up of the fields.
     * 
     * @param newFields
     * @param keyFieldNames
     * @param maintenanceAction
     * @param readOnly
     * @param auths
     * @return a List of Field
     */
    private static List<Field> arrangeNewFields(List newFields, List keyFieldNames, String maintenanceAction, boolean readOnly, MaintenanceDocumentAuthorizations auths) {
        List<Field> results = new ArrayList();
        for (int k = 0; k < newFields.size(); k++) {
            Field newMaintField = (Field) newFields.get(k);
            String propertyName = newMaintField.getPropertyName();
            newMaintField = FieldUtils.fixFieldForForm(newMaintField, keyFieldNames, Constants.MAINTENANCE_NEW_MAINTAINABLE, maintenanceAction, readOnly, auths);

            results.add(newMaintField);
        }
        return results;
    }

    /**
     * Merges together rows of an old maintainable section and new maintainable section.
     * 
     * @param oldRows
     * @param newRows
     * @param keyFieldNames
     * @param maintenanceAction
     * @param readOnly
     * @return List of Row objects
     */
    public static List meshRows(List oldRows, List newRows, List keyFieldNames, String maintenanceAction, boolean readOnly, MaintenanceDocumentAuthorizations auths) {
        List<Row> meshedRows = new ArrayList<Row>();

        for (int j = 0; j < newRows.size(); j++) {
            Row sectionRow = (Row) newRows.get(j);
            List rowFields = sectionRow.getFields();
            Row oldSectionRow = null;
            List oldRowFields = new ArrayList();

            if (null != oldRows && oldRows.size() > j) {
                oldSectionRow = (Row) oldRows.get(j);
                oldRowFields = oldSectionRow.getFields();
            }

            List meshedFields = meshFields(oldRowFields, rowFields, keyFieldNames, maintenanceAction, readOnly, auths);
            if (meshedFields.size() > 0) {
                Row meshedRow = new Row(meshedFields);
                if (sectionRow.isHidden()) {
                    meshedRow.setHidden(true);
                }
                
                meshedRows.add(meshedRow);
            }
        }

        return meshedRows;
    }


    /**
     * Merges together fields and an old maintainble row and new maintainable row, for each field call fixFieldForForm.
     * 
     * @param oldFields
     * @param newFields
     * @param keyFieldNames
     * @param maintenanceAction
     * @param readOnly
     * @return List of Field objects
     */
    public static List meshFields(List oldFields, List newFields, List keyFieldNames, String maintenanceAction, boolean readOnly, MaintenanceDocumentAuthorizations auths) {
        List meshedFields = new ArrayList();

        List newFieldsToMerge = new ArrayList();
        List oldFieldsToMerge = new ArrayList();

        for (int k = 0; k < newFields.size(); k++) {
            Field newMaintField = (Field) newFields.get(k);
            String propertyName = newMaintField.getPropertyName();
            // If this is an add button, then we have to have only this field for the entire row.
            if (Field.IMAGE_SUBMIT.equals(newMaintField.getFieldType())) {
                meshedFields.add(newMaintField);
            }
            else if (Field.CONTAINER.equals(newMaintField.getFieldType())) {
                if (oldFields.size() > k) {
                    Field oldMaintField = (Field) oldFields.get(k);
                    newMaintField = meshContainerFields(oldMaintField, newMaintField, keyFieldNames, maintenanceAction, readOnly, auths);
                }
                else {
                    newMaintField = meshContainerFields(newMaintField, newMaintField, keyFieldNames, maintenanceAction, readOnly, auths);
                }
                meshedFields.add(newMaintField);
            }
            else {
                newMaintField = FieldUtils.fixFieldForForm(newMaintField, keyFieldNames, Constants.MAINTENANCE_NEW_MAINTAINABLE, maintenanceAction, readOnly, auths);
                // add old fields for edit
                if (Constants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction) || Constants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                    Field oldMaintField = (Field) oldFields.get(k);
                    oldMaintField = FieldUtils.fixFieldForForm(oldMaintField, keyFieldNames, Constants.MAINTENANCE_OLD_MAINTAINABLE, maintenanceAction, true, auths);
                    oldFieldsToMerge.add(oldMaintField);

                    // compare values for change, and set new maintainable fields for highlighting
                    // no point in highlighting the hidden fields, since they won't be rendered anyways
                    if (!StringUtils.equalsIgnoreCase(newMaintField.getPropertyValue(), oldMaintField.getPropertyValue())
                            && !Field.HIDDEN.equals(newMaintField.getFieldType())) {
                        newMaintField.setHighlightField(true);
                    }
                }

                newFieldsToMerge.add(newMaintField);

                for (Iterator iter = oldFieldsToMerge.iterator(); iter.hasNext();) {
                    Field element = (Field) iter.next();
                    meshedFields.add(element);
                }

                for (Iterator iter = newFieldsToMerge.iterator(); iter.hasNext();) {
                    Field element = (Field) iter.next();
                    meshedFields.add(element);
                }
            }
        }
        return meshedFields;
    }

    public static List createAndPopulateFieldsForLookup(List<String> lookupFieldAttributeList, List<String> readOnlyFieldsList, Class businessObjectClass) throws InstantiationException, IllegalAccessException {
        List<Field> fields = new ArrayList<Field>();
        for (Iterator iter = lookupFieldAttributeList.iterator(); iter.hasNext();) {
            String attributeName = (String) iter.next();
            Field field = FieldUtils.getPropertyField(businessObjectClass, attributeName, true);

            // TODO: This makes no sense, why do we pass it in and then return the same thing
            // back to us?
            field = LookupUtils.setFieldQuickfinder((BusinessObject) businessObjectClass.newInstance(), attributeName, field, lookupFieldAttributeList);

            // overwrite maxLength to allow for wildcards and ranges in the select
            field.setMaxLength(100);
            fields.add(field);

            // set default value
            String defaultValue = businessObjectMetaDataService.getLookupFieldDefaultValue(businessObjectClass, attributeName);
            if (defaultValue != null) {
                field.setPropertyValue(defaultValue);
                field.setDefaultValue(defaultValue);
            }

            Class defaultValueFinderClass = businessObjectMetaDataService.getLookupFieldDefaultValueFinderClass(businessObjectClass, attributeName);
            if (defaultValueFinderClass != null) {
                field.setPropertyValue(((ValueFinder) defaultValueFinderClass.newInstance()).getValue());
                field.setDefaultValue(((ValueFinder) defaultValueFinderClass.newInstance()).getValue());
            }
            if (readOnlyFieldsList != null && readOnlyFieldsList.contains(field.getPropertyName())) {
                field.setReadOnly(true);
            }
        }
        return fields;
    }

    private static Field meshContainerFields(Field oldMaintField, Field newMaintField, List keyFieldNames, String maintenanceAction, boolean readOnly, MaintenanceDocumentAuthorizations auths) {
        List resultingRows = new ArrayList();
        resultingRows.addAll(meshRows(oldMaintField.getContainerRows(), newMaintField.getContainerRows(), keyFieldNames, maintenanceAction, readOnly, auths));
        Field resultingField = newMaintField;
        resultingField.setFieldType(Field.CONTAINER);

        // save the summary info
        resultingField.setContainerElementName(newMaintField.getContainerElementName());
        resultingField.setContainerDisplayFields(newMaintField.getContainerDisplayFields());
        resultingField.setNumberOfColumnsForCollection(newMaintField.getNumberOfColumnsForCollection());

        resultingField.setContainerRows(resultingRows);
        List resultingRowsList = newMaintField.getContainerRows();
        if (resultingRowsList.size() > 0) {
            List resultingFieldsList = ((Row) resultingRowsList.get(0)).getFields();
            if (resultingFieldsList.size() > 0) {
                // todo: assign the correct propertyName to the container in the first place. For now, I'm wary of the weird usages
                // of constructContainerField().
                String containedFieldName = ((Field) (resultingFieldsList.get(0))).getPropertyName();
                resultingField.setPropertyName(containedFieldName.substring(0, containedFieldName.lastIndexOf('.')));
            }
        }
        else {
            resultingField.setPropertyName(oldMaintField.getPropertyName());
        }
        return resultingField;
    }

    /**
     * This method modifies the passed in field so that it may be used to render a multiple values lookup button
     * 
     * @param field this object will be modified by this method
     * @param parents
     * @param definition
     */
    static final public void modifyFieldToSupportMultipleValueLookups(Field field, String parents, MaintainableCollectionDefinition definition) {
        field.setMultipleValueLookedUpCollectionName(parents + definition.getName());
        field.setMultipleValueLookupClassName(definition.getSourceClassName());
        field.setMultipleValueLookupClassLabel(dictionaryService.getDataDictionary().getBusinessObjectEntry(definition.getSourceClassName()).getObjectLabel());
    }

    /**
     * Returns whether the passed in collection has been properly configured in the maint doc dictionary to support multiple value
     * lookups.
     * 
     * @param definition
     * @return
     */
    static final public boolean isCollectionMultipleLookupEnabled(MaintainableCollectionDefinition definition) {
        return definition.getSourceClassName() != null && definition.getSourceClassName().length() > 0 && definition.isIncludeMultipleLookupLine();
    }

    /**
     * This method removes any duplicating spacing (internal or on the ends) from a String, meant to be exposed as a tag library
     * function.
     * 
     * @param s String to remove duplicate spacing from.
     * @return String without duplicate spacing.
     */
    public static String scrubWhitespace(String s) {
        return s.replaceAll("(\\s)(\\s+)", " ");
    }
}
