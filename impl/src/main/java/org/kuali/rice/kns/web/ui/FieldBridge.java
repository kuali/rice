/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.web.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.Summarizable;
import org.kuali.rice.kns.datadictionary.CollectionDefinitionI;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.datadictionary.FieldDefinitionI;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.control.ApcSelectControlDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.keyvalues.ApcValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.format.SummarizableFormatter;

public class FieldBridge {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FieldBridge.class);

    /**
     * This method creates a Field for an Inquiry Screen.
     *
     * @param field The field to populate.
     * @param definition The DD specification for the field.
     */
    public static final void setupField(Field field, FieldDefinitionI definition) {
        if (definition instanceof MaintainableFieldDefinition) {

            MaintainableFieldDefinition maintainableFieldDefinition = ((MaintainableFieldDefinition) definition);
            field.setFieldRequired(maintainableFieldDefinition.isRequired());
            field.setReadOnly(maintainableFieldDefinition.isReadOnly());

            // set onblur and callback functions
            if (StringUtils.isNotBlank(maintainableFieldDefinition.getWebUILeaveFieldFunction())) {
                field.setWebOnBlurHandler(maintainableFieldDefinition.getWebUILeaveFieldFunction());
            }

            if (StringUtils.isNotBlank(maintainableFieldDefinition.getWebUILeaveFieldCallbackFunction())) {
                field.setWebOnBlurHandlerCallback(maintainableFieldDefinition.getWebUILeaveFieldCallbackFunction());
            }

        }

        /* setup security of field (sensitive data) if needed, note this will always be true on old maintainables since
         * maintenanceAction is not set
         */
        String displayEditMode = definition.getDisplayEditMode();
        if (StringUtils.isNotBlank(displayEditMode)) {

            field.setSecure(true);
            field.setDisplayEditMode(displayEditMode);
            field.setDisplayMask(definition.getDisplayMask());

        }

    }

    /**
     * Uses reflection to populate the rows of the inquiry from the business object value. Also formats if needed.
     *
     * @param field The Field to populate.
     * @param bo The BusinessObject from which the Field will be popualated.
     */
    public static final void populateFieldFromBusinessObject(Field field, BusinessObject bo) {
        if (bo == null) {
            throw new RuntimeException("Inquiry Business object is null.");
        }

        field.setReadOnly(true); // inquiry fields are always read only

        Formatter formatter = field.getFormatter();
        String propertyName = field.getPropertyName();

        // get the field type for the property
        ControlDefinition fieldControl = KNSServiceLocator.getDataDictionaryService().getAttributeControlDefinition(bo.getClass(), propertyName);

        try {
            String propValue = null;
            Object prop = ObjectUtils.getPropertyValue(bo, field.getPropertyName());

            // for boolean types always use BooleanFormatter
            if (prop != null && prop instanceof Boolean) {
                formatter = new BooleanFormatter();
            }

            if (formatter == null && prop != null) {
                Map<String, Class> references = KNSServiceLocator.getPersistenceStructureService().getReferencesForForeignKey(bo.getClass(), propertyName);
                if (references != null && references.size() > 0) {
                    for (String fieldName : references.keySet()) {
                        if (propertyName.startsWith(fieldName)) {
                            Class referenceClass = references.get(fieldName);
                            if (Summarizable.class.isAssignableFrom(referenceClass)) {
                                prop = ObjectUtils.getPropertyValue(bo, fieldName); // replace key with referenced object
                                formatter = new SummarizableFormatter();
                            }
                        }
                    }
                }
            }

            // for select fields, display the associated label (unless we've got a formatter from the reference found just above...)
            if (fieldControl != null && fieldControl.isSelect() && formatter == null) {
                Class<KeyValuesFinder> keyValuesFinderName = (Class<KeyValuesFinder>)fieldControl.getValuesFinderClass();
                KeyValuesFinder finder = keyValuesFinderName.newInstance();

                propValue = lookupFinderValue(fieldControl, prop, finder);
            } else {
                if (!ObjectUtils.isNull(prop)) {
                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    } else {
                        propValue = prop.toString();
                    }
                } else {
                    propValue = KNSConstants.EMPTY_STRING;
                }

            }

            // check if field contains sensitive data and user is authorized to see value
            boolean viewAuthorized = KNSServiceLocator.getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), bo.getClass().getName(), propertyName);
            if (!viewAuthorized) {
                // set mask as field value
                Mask propertyMask = KNSServiceLocator.getDataDictionaryService().getAttributeDisplayMask(bo.getClass(), propertyName);
                if (propertyMask == null) {
                    throw new RuntimeException("No mask specified for secure field.");
                }

                field.setPropertyValue(propertyMask.maskValue(propValue));
                field.setDisplayMaskValue(propertyMask.maskValue(propValue));

            } else {
                field.setPropertyValue(propValue);
                FieldUtils.setInquiryURL(field, bo, propertyName);
            }

            // populatedColumns.add(col);
        }
        catch (InstantiationException e) {
            LOG.error("Unable to get instance of KeyValuesFinder: " + e.getMessage());
            throw new RuntimeException("Unable to get instance of KeyValuesFinder: " + e.getMessage());
        }
        catch (IllegalAccessException e) {
            LOG.error("Unable to set columns: " + e.getMessage());
            throw new RuntimeException("Unable to set columns: " + e.getMessage());
        }

    }

    /**
     * This method looks up a value in a finder class.
     * @param fieldControl the type of web control that is associated with this field.
     * @param prop the property to look up - either a property name as a String, or a referenced object
     * @param finder finder to look the value up in
     * @return the value that was returned from the lookup
     */
    private static String lookupFinderValue(ControlDefinition fieldControl, Object prop, KeyValuesFinder finder) {
        String propValue = null;

        if (finder != null) {
            if (finder instanceof ApcValuesFinder && fieldControl instanceof ApcSelectControlDefinition) {
                ((ApcValuesFinder) finder).setParameterNamespace(((ApcSelectControlDefinition) fieldControl).getParameterNamespace());
                ((ApcValuesFinder) finder).setParameterDetailType(((ApcSelectControlDefinition) fieldControl).getParameterDetailType());
                ((ApcValuesFinder) finder).setParameterName(((ApcSelectControlDefinition) fieldControl).getParameterName());
            }
        }

        // KULRICE-1808 : PersistableBusinessObjectValuesFinder is not working for inquiries that have child objects with ValuesFinder populated select lists
        if (finder instanceof PersistableBusinessObjectValuesFinder) {
            ((PersistableBusinessObjectValuesFinder) finder).setBusinessObjectClass(fieldControl.getBusinessObjectClass());
            ((PersistableBusinessObjectValuesFinder) finder).setKeyAttributeName(fieldControl.getKeyAttribute());
            ((PersistableBusinessObjectValuesFinder) finder).setLabelAttributeName(fieldControl.getLabelAttribute());
            ((PersistableBusinessObjectValuesFinder) finder).setIncludeKeyInDescription(fieldControl.getIncludeKeyInLabel());
        }
        List keyValues = finder.getKeyValues();
        if (prop != null) {
            for (Iterator iter = keyValues.iterator(); iter.hasNext();) {
                KeyLabelPair element = (KeyLabelPair) iter.next();
                if (element.getKey().toString().equals(prop.toString())) {
                    propValue = element.getLabel();
                }
            }
        }
        return propValue;
    }

    /**
     * Determines whether field level help is enabled for the field corresponding to the businessObjectClass and attribute name
     *
     * If this value is true, then the field level help will be enabled.
     * If false, then whether a field is enabled is determined by the value returned by {@link #isMaintenanceFieldLevelHelpDisabled(Maintainable, MaintainableFieldDefinition)}
     * and the system-wide parameter setting.  Note that if a field is read-only, that may cause field-level help to not be rendered.
     *
     * @param businessObjectClass the looked up class
     * @param attributeName the attribute for the field
     * @return true if field level help is enabled, false if the value of this method should NOT be used to determine whether this method's return value
     * affects the enablement of field level help
     */
    protected static boolean isMaintenanceFieldLevelHelpEnabled(Maintainable m, MaintainableFieldDefinition fieldDefinition) {
        return false;
    }

    /**
     * Determines whether field level help is disabled for the field corresponding to the businessObjectClass and attribute name
     *
     * If this value is true and {@link #isMaintenanceFieldLevelHelpEnabled(Maintainable, MaintainableFieldDefinition)} returns false,
     * then the field level help will not be rendered.  If both this and {@link #isMaintenanceFieldLevelHelpEnabled(Maintainable, MaintainableFieldDefinition)} return false,
     * then the system-wide setting will determine whether field level help is enabled.  Note that if a field is read-only, that may cause
     * field-level help to not be rendered.
     *
     * @param businessObjectClass the looked up class
     * @param attributeName the attribute for the field
     * @return true if field level help is disabled, false if the value of this method should NOT be used to determine whether this method's return value
     * affects the enablement of field level help
     */
    protected static boolean isMaintenanceFieldLevelHelpDisabled(Maintainable m, MaintainableFieldDefinition fieldDefinition) {
        return false;
    }

    /**
     * This method creates a Field for display on a Maintenance Document.
     *
     * @param id The DD definition for the Field (can be a Collection).
     * @param sd The DD definition for the Section in which the field will be displayed.
     * @param o The BusinessObject will be populated from this BO.
     * @param m
     * @param s The Section in which the Field will be displayed.
     * @param autoFillDefaultValues Should default values be filled in?
     * @param autoFillBlankRequiredValues Should values be filled in for fields that are required but which were left blank when submitting the form from the UI?
     * @param displayedFieldNames What fields are being displayed on the form in the UI?
     *
     * @return
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static final Field toField(MaintainableItemDefinition id, MaintainableSectionDefinition sd, BusinessObject o, Maintainable m, Section s, boolean autoFillDefaultValues, boolean autoFillBlankRequiredValues, List<String> displayedFieldNames) throws InstantiationException, IllegalAccessException {
        Field field = new Field();

        // if FieldDefiniton, simply add a Field UI object
        if (id instanceof MaintainableFieldDefinition) {
            MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) id;
            field = FieldUtils.getPropertyField(o.getClass(), maintainableFieldDefinition.getName(), false);

            setupField(field, maintainableFieldDefinition);

            MaintenanceUtils.setFieldQuickfinder(o, field.getPropertyName(), maintainableFieldDefinition, field, displayedFieldNames, m);
            MaintenanceUtils.setFieldDirectInquiry(o, field.getPropertyName(), maintainableFieldDefinition, field, displayedFieldNames);

            // set default value
            //TODO St. Ailish says review this. A question was raised on 11-16-2006 Tuscon meeting as to why this is done here and not in the formatter.
            if (autoFillDefaultValues) {
                Object defaultValue = maintainableFieldDefinition.getDefaultValue();
                if (defaultValue != null) {
                    if (defaultValue.toString().equals("true")) {
                        defaultValue = "Yes";
                    }
                    else if (defaultValue.toString().equals("false")) {
                        defaultValue = "No";
                    }
                    field.setPropertyValue(defaultValue);
                }

                Class defaultValueFinderClass = maintainableFieldDefinition.getDefaultValueFinderClass();
                if (defaultValueFinderClass != null) {
                    field.setPropertyValue(((ValueFinder) defaultValueFinderClass.newInstance()).getValue());
                }
            }

            // if this flag is set, and the current field is required, and readonly, and blank, use the
            // defaultValueFinder if one exists
            if (autoFillBlankRequiredValues) {
                if ( maintainableFieldDefinition.isRequired() && maintainableFieldDefinition.isReadOnly() ) {
                    if ( StringUtils.isBlank( field.getPropertyValue() ) ) {
                        Class defaultValueFinderClass = maintainableFieldDefinition.getDefaultValueFinderClass();
                        if (defaultValueFinderClass != null) {
                            field.setPropertyValue(((ValueFinder) defaultValueFinderClass.newInstance()).getValue());
                        }
                    }
                }
            }

            field.setFieldLevelHelpEnabled(isMaintenanceFieldLevelHelpEnabled(m, maintainableFieldDefinition));
            field.setFieldLevelHelpDisabled(isMaintenanceFieldLevelHelpDisabled(m, maintainableFieldDefinition));
        }

        return field;

    }

    /**
     * This method will return a new form for adding in a BO for a collection.
     * This should be customized in a subclass so the default behavior is to return nothing.
     *
     * @param collectionDefinition The DD definition for the Collection.
     * @param o The BusinessObject form which the new Fields will be populated.
     * @param m
     * @param displayedFieldNames What Fields are being displayed on the form in the UI?
     * @param containerRowErrorKey The error key for the Container/Collection used for displaying error messages.
     * @param parents
     * @param hideAdd Should the add line be hidden when displaying this Collection/Container in the UI?
     * @param numberOfColumns How many columns the Fields in the Collection will be split into when displaying them in the UI.
     *
     * @return The List of new Fields.
     */
    public static final List<Field> getNewFormFields(CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, List<String> displayedFieldNames, StringBuffer containerRowErrorKey, String parents, boolean hideAdd, int numberOfColumns) {
        LOG.debug( "getNewFormFields" );
        String collName = collectionDefinition.getName();

        List<Field> collFields = new ArrayList<Field>();
        Collection<? extends FieldDefinitionI> collectionFields;
        //Class boClass = collectionDefinition.getBusinessObjectClass();
        BusinessObject collBO = null;
        try {
            collectionFields = collectionDefinition.getFields();
            collBO = m.getNewCollectionLine(parents + collName);

            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "newBO for add line: " + collBO );
            }

            for ( FieldDefinitionI fieldDefinition : collectionFields  ) {
                // construct Field UI object from definition
                Field collField = FieldUtils.getPropertyField(collectionDefinition.getBusinessObjectClass(), fieldDefinition.getName(), false);

                if (fieldDefinition instanceof MaintainableFieldDefinition) {
                    setupField(collField, (MaintainableFieldDefinition)fieldDefinition);
                }
                //generate the error key for the add row
                String[] nameParts = StringUtils.split(collField.getPropertyName(), ".");
                String fieldErrorKey = KNSConstants.MAINTENANCE_NEW_MAINTAINABLE + KNSConstants.ADD_PREFIX + ".";
                fieldErrorKey += collName + ".";
                for (int i = 0; i < nameParts.length; i++) {
                    fieldErrorKey += nameParts[i];
                    containerRowErrorKey.append(fieldErrorKey);
                    if (i < nameParts.length) {
                        fieldErrorKey += ".";
                        containerRowErrorKey.append(",");
                    }
                }

                //  set the QuickFinderClass
                BusinessObject collectionBoInstance = (BusinessObject) collectionDefinition.getBusinessObjectClass().newInstance();
                FieldUtils.setInquiryURL(collField, collectionBoInstance, fieldDefinition.getName());
                if (collectionDefinition instanceof MaintainableCollectionDefinition) {
                    MaintenanceUtils.setFieldQuickfinder(collectionBoInstance, parents+collectionDefinition.getName(), true, 0, fieldDefinition.getName(), collField, displayedFieldNames, m, (MaintainableFieldDefinition) fieldDefinition);
                    MaintenanceUtils.setFieldDirectInquiry(collectionBoInstance, parents+collectionDefinition.getName(), true, 0, fieldDefinition.getName(), collField, displayedFieldNames, m, (MaintainableFieldDefinition) fieldDefinition);
                }
                else {
                    LookupUtils.setFieldQuickfinder(collectionBoInstance, parents+collectionDefinition.getName(), true, 0, fieldDefinition.getName(), collField, displayedFieldNames, m);
                    LookupUtils.setFieldDirectInquiry(collField);
                }

                collFields.add(collField);
            }

        } catch (InstantiationException e) {
            LOG.error("Unable to create instance of object class" + e.getMessage());
            throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.error("Unable to create instance of object class" + e.getMessage());
            throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
        }

        // populate field values from business object
        collFields = FieldUtils.populateFieldsFromBusinessObject(collFields,collBO);

        // need to append the prefix afterwards since the population command (above)
        // does not handle the prefixes on the property names
        for ( Field field : collFields ) {
            // prefix name for add line
            field.setPropertyName(KNSConstants.MAINTENANCE_ADD_PREFIX + parents + collectionDefinition.getName() + "." + field.getPropertyName());
        }
        LOG.debug("Error Key for section " + collectionDefinition.getName() + " : " + containerRowErrorKey.toString());

        
        collFields = constructContainerField(collectionDefinition, parents, o, hideAdd, numberOfColumns, collName, collFields);

        return collFields;
    }

    /**
     * 
     * This method handles setting up a container field not including the add fields
     * 
     * @param collectionDefinition
     * @param parents
     * @param o
     * @param hideAdd
     * @param numberOfColumns
     * @param collName
     * @param collFields
     * @return
     */
    public static List<Field> constructContainerField(CollectionDefinitionI collectionDefinition, String parents, BusinessObject o, boolean hideAdd, int numberOfColumns, String collName, List<Field> collFields) {
        // get label for collection
        String collectionLabel = KNSServiceLocator.getDataDictionaryService().getCollectionLabel(o.getClass(), collectionDefinition.getName());

        // retrieve the summary label either from the override or from the DD
        String collectionElementLabel = collectionDefinition.getSummaryTitle();
        if(StringUtils.isEmpty(collectionElementLabel)){
            collectionElementLabel = KNSServiceLocator.getDataDictionaryService().getCollectionElementLabel(o.getClass().getName(), collectionDefinition.getName(),collectionDefinition.getBusinessObjectClass());
        }

        // container field
        Field containerField;
        containerField = FieldUtils.constructContainerField(collName, collectionLabel, collFields, numberOfColumns);
        if(StringUtils.isNotEmpty(collectionElementLabel)) {
            containerField.setContainerElementName(collectionElementLabel);
        }
        collFields = new ArrayList();
        collFields.add(containerField);

        // field button for adding lines
        if(!hideAdd  && collectionDefinition.getIncludeAddLine()) {
            Field field = new Field();

            String addButtonName = KNSConstants.DISPATCH_REQUEST_PARAMETER + "." + KNSConstants.ADD_LINE_METHOD + "." + parents + collectionDefinition.getName() + "." + KNSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL + collectionDefinition.getBusinessObjectClass().getName() + KNSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL;
            field.setPropertyName(addButtonName);
            field.setFieldType(Field.IMAGE_SUBMIT);
            field.setPropertyValue("images/tinybutton-add1.gif");
            // collFields.add(field);
            containerField.getContainerRows().add(new Row(field));
        }

        if (collectionDefinition instanceof MaintainableCollectionDefinition) {
            if (FieldUtils.isCollectionMultipleLookupEnabled((MaintainableCollectionDefinition) collectionDefinition)) {
                FieldUtils.modifyFieldToSupportMultipleValueLookups(containerField, parents, (MaintainableCollectionDefinition) collectionDefinition);
            }
        }

        return collFields;
    }

    /**
     * Call getNewFormFields with no parents.
     *
     * @see #getNewFormFields(CollectionDefinitionI, BusinessObject, Maintainable, List, StringBuffer, String, boolean, int)
     */
    public static final List<Field> getNewFormFields(MaintainableCollectionDefinition collectionDefinition, BusinessObject o, Maintainable m, List<String> displayedFieldNames, StringBuffer containerRowErrorKey, int numberOfColumns) {
        String parent = "";
        return getNewFormFields(collectionDefinition, o, m, displayedFieldNames, containerRowErrorKey, parent, false, numberOfColumns);
    }

    /**
     * Create a Field for display on an Inquiry screen.
     *
     * @param d The DD definition for the Field.
     * @param o The BusinessObject from which the Field will be populated.
     * @param s The Section in which the Field will be displayed.
     *
     * @return The populated Field.
     */
    public static final Field toField(FieldDefinition d, BusinessObject o, Section s) {
        final Field field = FieldUtils.getPropertyField(o.getClass(), d.getAttributeName(), false);
        field.setPropertyName(d.getAttributeName());
        field.setBusinessObjectClassName(o.getClass().getName());
        field.setFieldLabel(KNSServiceLocator.getDataDictionaryService().getAttributeLabel(o.getClass(), d.getAttributeName()));
        FieldUtils.setInquiryURL(field, o, field.getPropertyName());

        Class formatterClass = KNSServiceLocator.getDataDictionaryService().getAttributeFormatter(o.getClass(), d.getAttributeName());
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

        populateFieldFromBusinessObject(field, o);

        return field;

    }

}
