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
package org.kuali.core.web.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.Summarizable;
import org.kuali.core.datadictionary.CollectionDefinitionI;
import org.kuali.core.datadictionary.FieldDefinition;
import org.kuali.core.datadictionary.FieldDefinitionI;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.datadictionary.control.ApcSelectControlDefinition;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.keyvalues.ApcValuesFinder;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.MaintenanceUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.format.SummarizableFormatter;
import org.kuali.rice.KNSServiceLocator;

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
                Class keyValuesFinderName = fieldControl.getValuesFinderClass();
                KeyValuesFinder finder = (KeyValuesFinder) keyValuesFinderName.newInstance();
                
                propValue = lookupFinderValue(fieldControl, prop, finder);
            } else {
                if (prop != null) {
                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    } else {
                        propValue = prop.toString();
                    }
                } else {
                    propValue = Constants.EMPTY_STRING;
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
                ((ApcValuesFinder) finder).setGroup(((ApcSelectControlDefinition) fieldControl).getGroup());
                ((ApcValuesFinder) finder).setParameterName(((ApcSelectControlDefinition) fieldControl).getParameterName());   
            }
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
        if(null != m) {
            m.overrideDataDictionarySectionConfiguration(sd);
        }
        
        Field field = new Field();
        
        // if FieldDefiniton, simply add a Field UI object
        if (id instanceof MaintainableFieldDefinition) {
            MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) id;
            field = FieldUtils.getPropertyField(o.getClass(), maintainableFieldDefinition.getName(), false);

            setupField(field, maintainableFieldDefinition);
            
            MaintenanceUtils.setFieldQuickfinder(o, field.getPropertyName(), maintainableFieldDefinition, field, displayedFieldNames, m);

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
                FieldUtils.setInquiryURL(collField, o, fieldDefinition.getName());
 
                if (fieldDefinition instanceof MaintainableFieldDefinition) {
                    setupField(collField, (MaintainableFieldDefinition)fieldDefinition);
                }
                //generate the error key for the add row 
                String[] nameParts = StringUtils.split(collField.getPropertyName(), "."); 
                String fieldErrorKey = Constants.MAINTENANCE_NEW_MAINTAINABLE + Constants.ADD_PREFIX + "."; 
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
                LookupUtils.setFieldQuickfinder(collectionBoInstance, parents+collectionDefinition.getName(), true, 0, fieldDefinition.getName(), collField, displayedFieldNames, m); 
  
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
            field.setPropertyName(Constants.MAINTENANCE_ADD_PREFIX + parents + collectionDefinition.getName() + "." + field.getPropertyName());
        }
        LOG.debug("Error Key for section " + collectionDefinition.getName() + " : " + containerRowErrorKey.toString());
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
        if(!hideAdd) {
            Field field = new Field();
            
            String addButtonName = Constants.DISPATCH_REQUEST_PARAMETER + "." + Constants.ADD_LINE_METHOD + "." + parents + collectionDefinition.getName() + "." + Constants.METHOD_TO_CALL_BOPARM_LEFT_DEL + collectionDefinition.getBusinessObjectClass().getName() + Constants.METHOD_TO_CALL_BOPARM_RIGHT_DEL;
            field.setPropertyName(addButtonName);
            field.setFieldType(Field.IMAGE_SUBMIT);
            field.setPropertyValue("kr/images/tinybutton-add1.gif");
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
        Field field = new Field();
        field = FieldUtils.getPropertyField(o.getClass(), d.getAttributeName(), false);
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
