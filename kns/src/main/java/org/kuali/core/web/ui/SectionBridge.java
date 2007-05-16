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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.CollectionDefinitionI;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.datadictionary.FieldDefinition;
import org.kuali.core.datadictionary.FieldDefinitionI;
import org.kuali.core.datadictionary.InquiryCollectionDefinition;
import org.kuali.core.datadictionary.InquirySectionDefinition;
import org.kuali.core.datadictionary.InquirySubSectionHeaderDefinition;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.core.datadictionary.SubSectionHeaderDefinitionI;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.rice.KNSServiceLocator;

public class SectionBridge {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SectionBridge.class);

    /**
     * This method creates a Section for display on an Inquiry Screen.
     * 
     * @param sd The DD definition from which to construct the Section.
     * @param o The BusinessObject from which to populate the Section values.
     * @return A populated Section.
     */
    public static final Section toSection(InquirySectionDefinition sd, BusinessObject o) {
        Section section = new Section();
        section.setSectionTitle(sd.getTitle());
        section.setRows(new ArrayList());
        if (StringUtils.isNotBlank(sd.getNumberOfColumns())) {
            section.setNumberOfColumns(Integer.parseInt(sd.getNumberOfColumns()));
        }
        else {
            section.setNumberOfColumns(Constants.DEFAULT_NUM_OF_COLUMNS);
        }

        List<Field> sectionFields = new ArrayList();
        for (FieldDefinition fieldDefinition : sd.getInquiryFields()) {
            List row = new ArrayList();

            Field f = null;
            if (fieldDefinition instanceof InquiryCollectionDefinition) {
                InquiryCollectionDefinition inquiryCollectionDefinition = (InquiryCollectionDefinition) fieldDefinition;

                List<Row> sectionRows = new ArrayList();
                sectionRows = getContainerRows(section, inquiryCollectionDefinition, o, null, new ArrayList(), new StringBuffer(section.getErrorKey()), Integer.parseInt(inquiryCollectionDefinition.getNumberOfColumns()));
                section.setRows(sectionRows);
            }
            else if (fieldDefinition instanceof InquirySubSectionHeaderDefinition) {
                f = createMaintainableSubSectionHeader((InquirySubSectionHeaderDefinition) fieldDefinition);
            }
            else {
                f = FieldBridge.toField(fieldDefinition, o, section);
            }

            if (null != f) {
                sectionFields.add(f);
            }

        }

        if (!sectionFields.isEmpty()) {
            section.setRows(FieldUtils.wrapFields(sectionFields, section.getNumberOfColumns()));
        }

        return section;
    }

    /**
     * This method creates a Section for a MaintenanceDocument.
     * 
     * @param sd The DD definition of the Section.
     * @param o The BusinessObject from which the Section will be populated.
     * @param m
     * @param maintenanceAction The action (new, newwithexisting, copy, edit, etc) requested from the UI.
     * @param autoFillDefaultValues Should default values be auto-filled?
     * @param autoFillBlankRequiredValues Should required values left blank on the UI be auto-filled?
     * @param displayedFieldNames What fields are displayed on the UI?
     * @return A populated Section.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static final Section toSection(MaintainableSectionDefinition sd, BusinessObject o, Maintainable m, String maintenanceAction, boolean autoFillDefaultValues, boolean autoFillBlankRequiredValues, List<String> displayedFieldNames) throws InstantiationException, IllegalAccessException {
        if (null != m) {
            sd = (MaintainableSectionDefinition) ObjectUtils.deepCopy(sd);
            m.overrideDataDictionarySectionConfiguration(sd);
        }

        Section section = new Section();

        section.setSectionTitle(sd.getTitle());
        section.setSectionClass(o.getClass());

        // iterate through section maint items and contruct Field UI objects
        Collection maintItems = sd.getMaintainableItems();
        List<Row> sectionRows = new ArrayList();
        List<Field> sectionFields = new ArrayList();

        for (Iterator iterator = maintItems.iterator(); iterator.hasNext();) {
            MaintainableItemDefinition item = (MaintainableItemDefinition) iterator.next();
            Field field = FieldBridge.toField(item, sd, o, m, section, autoFillDefaultValues, autoFillBlankRequiredValues, displayedFieldNames);
            boolean skipAdd = false;

            // if CollectionDefiniton, then have a many section
            if (item instanceof MaintainableCollectionDefinition) {
                MaintainableCollectionDefinition definition = (MaintainableCollectionDefinition) item;
                section.getContainedCollectionNames().add(((MaintainableCollectionDefinition) item).getName());

                StringBuffer containerRowErrorKey = new StringBuffer();
                sectionRows = getContainerRows(section, definition, o, m, displayedFieldNames, containerRowErrorKey, Constants.DEFAULT_NUM_OF_COLUMNS);
            }
            else if (item instanceof MaintainableSubSectionHeaderDefinition) {
                MaintainableSubSectionHeaderDefinition definition = (MaintainableSubSectionHeaderDefinition) item;
                field = createMaintainableSubSectionHeader(definition);
            }

            if (!skipAdd) {
                sectionFields.add(field);
            }
        }

        // populate field values from business object
        if (o != null && !autoFillDefaultValues) {
            sectionFields = FieldUtils.populateFieldsFromBusinessObject(sectionFields, o);

            /* if maintenance action is copy, clear out secure fields */
            if (Constants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                for (Iterator iterator = sectionFields.iterator(); iterator.hasNext();) {
                    Field element = (Field) iterator.next();
                    if (element.isSecure()) {
                        element.setPropertyValue("");
                    }
                }
            }
        }

        sectionRows.addAll(FieldUtils.wrapFields(sectionFields));
        section.setRows(sectionRows);

        return section;

    }

    /**
     * @see #getContainerRows(Section, CollectionDefinitionI, BusinessObject, Maintainable, List<String>, StringBuffer, String,
     *      boolean, int)
     */
    public static final List getContainerRows(Section s, CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, List<String> displayedFieldNames, StringBuffer containerRowErrorKey, int numberOfColumns) {
        return getContainerRows(s, collectionDefinition, o, m, displayedFieldNames, containerRowErrorKey, "", false, numberOfColumns);
    }

    /**
     * Builds a list of Rows with Fields of type containers for a many section.
     * 
     * @param s The Section containing the Collection/Container.
     * @param collectionDefinition The DD definition of the Collection.
     * @param o The BusinessObject from which the Container/Collection will be populated.
     * @param m The Maintainable for the BO (needed by some methods called on FieldBridge, FieldUtils etc.)
     * @param displayedFieldNames
     * @param containerRowErrorKey The error key for the Container/Collection.
     * @param parents
     * @param hideAdd Should the add line be added to the Container/Collection?
     * @param numberOfColumns In how many columns in the UI will the fields in the Container/Collection be shown?
     * @return
     */
     public static final List getContainerRows(Section s, CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, List<String> displayedFieldNames, StringBuffer containerRowErrorKey, String parents, boolean hideAdd, int numberOfColumns) {

        List containerRows = new ArrayList();
        List collFields = new ArrayList();
        String collName = collectionDefinition.getName();

        if (m != null && collectionDefinition instanceof DataDictionaryDefinitionBase) {
            collectionDefinition = (CollectionDefinitionI) ObjectUtils.deepCopy((DataDictionaryDefinitionBase) collectionDefinition);
            m.overrideDataDictionaryFieldConfiguration((DataDictionaryDefinitionBase) collectionDefinition);
        }
        
        // first need to populate the containerRows with the "new" form if available
        if (collectionDefinition.getIncludeAddLine()) {
            List<Field> newFormFields = new ArrayList<Field>();
            if (!hideAdd) {
                newFormFields = FieldBridge.getNewFormFields(collectionDefinition, o, m, displayedFieldNames, containerRowErrorKey, parents, hideAdd, numberOfColumns);
            }
            if (null != newFormFields) {
                containerRows.add(new Row(newFormFields));
            }
        }

        Collection collections = collectionDefinition.getCollections();
        for (Iterator iterator = collections.iterator(); iterator.hasNext();) {
            CollectionDefinitionI subCollectionDefinition = (CollectionDefinitionI) iterator.next();
            int subCollectionNumberOfColumn = numberOfColumns;
            if (collectionDefinition instanceof InquiryCollectionDefinition) {
                InquiryCollectionDefinition icd = (InquiryCollectionDefinition) subCollectionDefinition;
                if (StringUtils.isNotBlank(icd.getNumberOfColumns())) {
                    subCollectionNumberOfColumn = Integer.parseInt(icd.getNumberOfColumns());
                }
            }
            // no colNum for add rows
            containerRows.addAll(getContainerRows(s, subCollectionDefinition, o, m, displayedFieldNames, containerRowErrorKey, parents + collectionDefinition.getName() + ".", true, subCollectionNumberOfColumn));
        }

        // then we need to loop through the existing collection and add those fields
        Collection collectionFields = collectionDefinition.getFields();
        // get label for collection
        String collectionLabel = KNSServiceLocator.getDataDictionaryService().getCollectionLabel(o.getClass(), collectionDefinition.getName());

        // retrieve the summary label either from the override or from the DD
        String collectionElementLabel = collectionDefinition.getSummaryTitle();
        if (StringUtils.isEmpty(collectionElementLabel)) {
            collectionElementLabel = KNSServiceLocator.getDataDictionaryService().getCollectionElementLabel(o.getClass().getName(), collectionDefinition.getName(), collectionDefinition.getBusinessObjectClass());
        }

        if (o != null) {
            if (PropertyUtils.isWriteable(o, collectionDefinition.getName()) && ObjectUtils.getPropertyValue(o, collectionDefinition.getName()) != null) {

                String collectionName = collectionDefinition.getName();

                Object obj = ObjectUtils.getPropertyValue(o, collectionName);
                if (obj instanceof List) {
                    Map summaryFields = new HashMap();
                    for (int i = 0; i < ((List) obj).size(); i++) {
                        BusinessObject lineBusinessObject = (BusinessObject) ((List) obj).get(i);

                        collFields = new ArrayList();
                        for (Iterator iterator = collectionFields.iterator(); iterator.hasNext();) {
                            FieldDefinitionI fieldDefinition = (FieldDefinitionI) iterator.next();

                            // construct Field UI object from definition
                            Field collField = FieldUtils.getPropertyField(collectionDefinition.getBusinessObjectClass(), fieldDefinition.getName(), false);

                            FieldBridge.setupField(collField, fieldDefinition);
                            setPrimaryKeyFieldsReadOnly(collectionDefinition.getBusinessObjectClass(), collField);

                            FieldUtils.setInquiryURL(collField, lineBusinessObject, fieldDefinition.getName());
                            // save the simple property name
                            String name = collField.getPropertyName();

                            // prefix name for multi line (indexed)
                            collField.setPropertyName(collectionDefinition.getName() + "[" + (new Integer(i)).toString() + "]." + collField.getPropertyName());

                            // commenting out codes for sub-collections show/hide for now
                            // subCollField.setContainerName(collectionDefinition.getName() + "["+i+"]" +"." +
                            // subCollectionDefinition.getName() + "[" + j + "]");

                            LookupUtils.setFieldQuickfinder(lineBusinessObject, collectionDefinition.getName(), false, i, name, collField, displayedFieldNames, m);

                            Object propertyValue = ObjectUtils.getPropertyValue(lineBusinessObject, fieldDefinition.getName());
                            // set field value from business object
                            // check if field contains sensitive data and user is authorized to see value
                            boolean viewAuthorized = KNSServiceLocator.getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), lineBusinessObject.getClass().getName(), name);
                            if (!viewAuthorized) {

                                // set mask as field value
                                Mask propertyMask = KNSServiceLocator.getDataDictionaryService().getAttributeDisplayMask(lineBusinessObject.getClass(), name);
                                if (propertyMask == null) {
                                    throw new RuntimeException("No mask specified for secure field.");
                                }

                                collField.setPropertyValue(propertyMask.maskValue(propertyValue));
                                collField.setDisplayMaskValue(propertyMask.maskValue(propertyValue));

                            }
                            else {
                                collField.setPropertyValue(propertyValue);
                            }


                            // the the field as read only (if appropriate)
                            if (fieldDefinition.isReadOnlyAfterAdd()) {
                                collField.setReadOnly(true);
                            }

                            // check if this is a summary field
                            if (collectionDefinition.hasSummaryField(fieldDefinition.getName())) {
                                summaryFields.put(fieldDefinition.getName(), collField);
                            }

                            collFields.add(collField);
                        }

                        Field containerField;
                        containerField = FieldUtils.constructContainerField(Constants.EDIT_PREFIX + "[" + (new Integer(i)).toString() + "]", collectionLabel + " " + (i + 1), collFields, numberOfColumns);
                        // why is this only on collections and not subcollections any significance or just oversight?
                        containerField.setContainerName(collectionDefinition.getName() + "[" + (new Integer(i)).toString() + "].");

                        // If the collection line is pending (meaning added by this document) the isNewCollectionRecord will be set
                        // to true. In this
                        // case we give an option to delete the line. The parameters for the delete action method are embedded into
                        // the button name.
                        if (lineBusinessObject instanceof PersistableBusinessObject && ((PersistableBusinessObject) lineBusinessObject).isNewCollectionRecord()) {
                            containerField.getContainerRows().add(new Row(getDeleteRowButtonField(parents + collectionDefinition.getName(), (new Integer(i)).toString())));
                        }

                        if (StringUtils.isNotEmpty(collectionElementLabel)) {
                            containerField.setContainerElementName(collectionElementLabel + " " + (i + 1));
                            // reorder summaryFields to make sure they are in the order specified in the summary section
                            List orderedSummaryFields = getSummaryFields(summaryFields, collectionDefinition);
                            containerField.setContainerDisplayFields(orderedSummaryFields);
                        }
                        containerRows.add(new Row(containerField));


                        // TODO: Chris: Hack alert - fix code below, it should be factored out with above into helper method
                        // update the name in the subCollections and add rows here.
                        Collection subCollections = collectionDefinition.getCollections();
                        List subCollFields = new ArrayList();

                        summaryFields = new HashMap();
                        // iterate over the subCollections directly on this collection
                        for (Iterator iter = subCollections.iterator(); iter.hasNext();) {
                            CollectionDefinitionI subCollectionDefinition = (CollectionDefinitionI) iter.next();
                            Collection subCollectionFields = subCollectionDefinition.getFields();
                            int subCollectionNumberOfColumns = numberOfColumns;

                            if (!s.getContainedCollectionNames().contains(collectionDefinition.getName() + "." + subCollectionDefinition.getName())) {
                                s.getContainedCollectionNames().add(collectionDefinition.getName() + "." + subCollectionDefinition.getName());
                            }

                            if (subCollectionDefinition instanceof InquiryCollectionDefinition) {
                                InquiryCollectionDefinition icd = (InquiryCollectionDefinition) subCollectionDefinition;
                                if (StringUtils.isNotBlank(icd.getNumberOfColumns())) {
                                    subCollectionNumberOfColumns = Integer.parseInt(icd.getNumberOfColumns());
                                }
                            }
                            // get label for collection
                            String subCollectionLabel = KNSServiceLocator.getDataDictionaryService().getCollectionLabel(o.getClass(), subCollectionDefinition.getName());

                            // retrieve the summary label either from the override or from the DD
                            String subCollectionElementLabel = subCollectionDefinition.getSummaryTitle();
                            if (StringUtils.isEmpty(subCollectionElementLabel)) {
                                subCollectionElementLabel = KNSServiceLocator.getDataDictionaryService().getCollectionElementLabel(o.getClass().getName(), subCollectionDefinition.getName(), subCollectionDefinition.getBusinessObjectClass());
                            }
                            // make sure it's really a collection (i.e. list)

                            String subCollectionName = subCollectionDefinition.getName();
                            Object subObj = ObjectUtils.getPropertyValue(lineBusinessObject, subCollectionName);
                            if (subObj instanceof List) {
                                // recursively call this method to get the add row and exisiting members of the subCollections
                                // subcollections
                                // containerRows.addAll(getContainerRows(subCollectionDefinition,
                                // displayedFieldNames,containerRowErrorKey,
                                // parents+collectionDefinition.getName()+"["+i+"]"+".","[0]",false, subCollectionNumberOfColumn));
                                containerField.getContainerRows().addAll(getContainerRows(s, subCollectionDefinition, o, m, displayedFieldNames, containerRowErrorKey, parents + collectionDefinition.getName() + "[" + i + "]" + ".", false, subCollectionNumberOfColumns));
                                // iterate over the fields
                                for (int j = 0; j < ((List) subObj).size(); j++) {
                                    BusinessObject lineSubBusinessObject = (BusinessObject) ((List) subObj).get(j);
                                    subCollFields = new ArrayList();
                                    // construct field objects based on fields
                                    for (Iterator iterator = subCollectionFields.iterator(); iterator.hasNext();) {
                                        FieldDefinitionI fieldDefinition = (FieldDefinitionI) iterator.next();

                                        // construct Field UI object from definition
                                        Field subCollField = FieldUtils.getPropertyField(subCollectionDefinition.getBusinessObjectClass(), fieldDefinition.getName(), false);

                                        FieldBridge.setupField(subCollField, fieldDefinition);
                                        setPrimaryKeyFieldsReadOnly(subCollectionDefinition.getBusinessObjectClass(), subCollField);
                                        
                                        // save the simple property name
                                        String name = subCollField.getPropertyName();

                                        // prefix name for multi line (indexed)
                                        subCollField.setPropertyName(collectionDefinition.getName() + "[" + i + "]" + "." + subCollectionDefinition.getName() + "[" + j + "]." + subCollField.getPropertyName());

                                        // commenting out codes for sub-collections show/hide for now

                                        LookupUtils.setFieldQuickfinder(lineSubBusinessObject, collectionDefinition.getName() + "[" + i + "]" + "." + subCollectionDefinition.getName() + "[" + j + "].", false, i, name, subCollField, displayedFieldNames);

                                        Object propertyValue = ObjectUtils.getPropertyValue(lineSubBusinessObject, fieldDefinition.getName());
                                        // set field value from business object
                                        // check if field contains sensitive data and user is authorized to see value
                                        boolean viewAuthorized = KNSServiceLocator.getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), lineSubBusinessObject.getClass().getName(), name);
                                        if (!viewAuthorized) {

                                            // set mask as field value
                                            Mask propertyMask = KNSServiceLocator.getDataDictionaryService().getAttributeDisplayMask(lineSubBusinessObject.getClass(), name);
                                            if (propertyMask == null) {
                                                throw new RuntimeException("No mask specified for secure field.");
                                            }

                                            subCollField.setPropertyValue(propertyMask.maskValue(propertyValue));
                                            subCollField.setDisplayMaskValue(propertyMask.maskValue(propertyValue));

                                        }
                                        else {

                                            subCollField.setPropertyValue(propertyValue);
                                        }

                                        // Map fieldConversion = new HashMap();
                                        // String conversionName=collectionName+"["+j+"]"+"."+name;
                                        // fieldConversion.put(name,collField.getPropertyName());
                                        // collField.setFieldConversions(fieldConversion);

                                        // check if this is a summary field
                                        if (subCollectionDefinition.hasSummaryField(fieldDefinition.getName())) {
                                            summaryFields.put(fieldDefinition.getName(), subCollField);
                                        }

                                        subCollFields.add(subCollField);
                                    }

                                    Field subContainerField = FieldUtils.constructContainerField(Constants.EDIT_PREFIX + "[" + (new Integer(j)).toString() + "]", subCollectionLabel, subCollFields);
                                    if (lineSubBusinessObject instanceof PersistableBusinessObject && ((PersistableBusinessObject) lineSubBusinessObject).isNewCollectionRecord()) {
                                        subContainerField.getContainerRows().add(new Row(getDeleteRowButtonField(parents + collectionDefinition.getName() + "[" + i + "]" + "." + subCollectionName, (new Integer(j)).toString())));
                                    }

                                    // summary line code
                                    if (StringUtils.isNotEmpty(subCollectionElementLabel)) {
                                        subContainerField.setContainerElementName(subCollectionElementLabel + " " + (j + 1));
                                    }
                                    subContainerField.setContainerName(collectionDefinition.getName() + "." + subCollectionName);
                                    if (!summaryFields.isEmpty()) {
                                        // reorder summaryFields to make sure they are in the order specified in the summary section
                                        List orderedSummaryFields = getSummaryFields(summaryFields, subCollectionDefinition);
                                        subContainerField.setContainerDisplayFields(orderedSummaryFields);
                                    }
                                    containerField.getContainerRows().add(new Row(subContainerField));
                                    // containerRows.add(new Row(subContainerField));
                                }
                            }
                        }
                    }
                }
            }
        }
        return containerRows;
    }

    /**
     * Helper method to build up a Field containing a delete button mapped up to remove the collection record identified by the
     * given collection name and index.
     * 
     * @param collectionName - name of the collection
     * @param rowIndex - index of the record to associate delete button
     * @return Field - of type IMAGE_SUBMIT
     */
    private static final Field getDeleteRowButtonField(String collectionName, String rowIndex) {
        Field deleteButtonField = new Field();

        String deleteButtonName = Constants.DISPATCH_REQUEST_PARAMETER + "." + Constants.DELETE_LINE_METHOD + "." + collectionName + "." + Constants.METHOD_TO_CALL_BOPARM_LEFT_DEL + ".line" + rowIndex;
        deleteButtonField.setPropertyName(deleteButtonName);
        deleteButtonField.setFieldType(Field.IMAGE_SUBMIT);
        deleteButtonField.setPropertyValue("kr/images/tinybutton-delete1.gif");

        return deleteButtonField;
    }
    
    /**
     * Retrieves the primary key property names for the given class. If the field's property is one of those
     * keys, makes the field read-only.
     * This is called for collection lines. Since deletion is not allowed for existing lines, the pk fields must
     * be read-only, otherwise a user could change the pk value which would be equivalent to deleting the line and 
     * adding a new line.
     */
    private static final void setPrimaryKeyFieldsReadOnly(Class businessObjectClass, Field field) {
        List primaryKeyPropertyNames = KNSServiceLocator.getPersistenceStructureService().getPrimaryKeys(businessObjectClass);
        if (primaryKeyPropertyNames.contains(field.getPropertyName())) {
            field.setReadOnly(true);
        }
    }

    /**
     * This method returns an ordered list of fields.
     * 
     * @param collSummaryFields
     * @param collectionDefinition
     * @return
     */
    private static final List getSummaryFields(Map collSummaryFields, CollectionDefinitionI collectionDefinition) {
        List orderedSummaryFields = new ArrayList();
        for (FieldDefinitionI summaryField : collectionDefinition.getSummaryFields()) {
            String name = summaryField.getName();
            boolean found = false;
            Field addField = (Field) collSummaryFields.get(name);
            if (!(addField == null)) {
                orderedSummaryFields.add(addField);
                found = true;
            }

            if (!found) {
                // should we throw a real error here?
                LOG.error("summaryField " + summaryField + " not present in the list");
            }

        }
        return orderedSummaryFields;
    }

    /**
     * This is a helper method to create a sub section header
     * 
     * @param definition the MaintainableSubSectionHeaderDefinition that we'll use to create the sub section header
     * @return the Field, which is the sub section header
     */
    private static final Field createMaintainableSubSectionHeader(SubSectionHeaderDefinitionI definition) {
        Field separatorField = new Field();
        separatorField.setFieldLabel(definition.getName());
        separatorField.setFieldType(Field.SUB_SECTION_SEPARATOR);
        separatorField.setReadOnly(true);

        return separatorField;
    }
}
