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
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.CollectionDefinitionI;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.datadictionary.FieldDefinition;
import org.kuali.core.datadictionary.FieldDefinitionI;
import org.kuali.core.datadictionary.InquiryCollectionDefinition;
import org.kuali.core.datadictionary.InquirySectionDefinition;
import org.kuali.core.datadictionary.InquirySubSectionHeaderDefinition;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.core.datadictionary.SubSectionHeaderDefinitionI;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.inquiry.Inquirable;
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
    public static final Section toSection(Inquirable inquirable, InquirySectionDefinition sd, BusinessObject o) {
        Section section = new Section();
        section.setSectionTitle(sd.getTitle());
        section.setRows(new ArrayList());
        if (StringUtils.isNotBlank(sd.getNumberOfColumns())) {
            section.setNumberOfColumns(Integer.parseInt(sd.getNumberOfColumns()));
        }
        else {
            section.setNumberOfColumns(RiceConstants.DEFAULT_NUM_OF_COLUMNS);
        }

        List<Field> sectionFields = new ArrayList();
        for (FieldDefinition fieldDefinition : sd.getInquiryFields()) {
            List row = new ArrayList();

            Field f = null;
            if (fieldDefinition instanceof InquiryCollectionDefinition) {
                InquiryCollectionDefinition inquiryCollectionDefinition = (InquiryCollectionDefinition) fieldDefinition;

                List<Row> sectionRows = new ArrayList();
                sectionRows = getContainerRows(section, inquiryCollectionDefinition, o, null, null, new ArrayList(), new StringBuffer(section.getErrorKey()), Integer.parseInt(inquiryCollectionDefinition.getNumberOfColumns()), inquirable);
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
     * @param maintainable
     * @param maintenanceAction The action (new, newwithexisting, copy, edit, etc) requested from the UI.
     * @param autoFillDefaultValues Should default values be auto-filled?
     * @param autoFillBlankRequiredValues Should required values left blank on the UI be auto-filled?
     * @param displayedFieldNames What fields are displayed on the UI?
     * @return A populated Section.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static final Section toSection(MaintainableSectionDefinition sd, BusinessObject o, Maintainable maintainable, Maintainable oldMaintainable, String maintenanceAction, boolean autoFillDefaultValues, boolean autoFillBlankRequiredValues, List<String> displayedFieldNames) throws InstantiationException, IllegalAccessException {
        if (null != maintainable) {
            sd = (MaintainableSectionDefinition) ObjectUtils.deepCopy(sd);
            maintainable.overrideDataDictionarySectionConfiguration(sd);
        }

        Section section = new Section();

        section.setSectionTitle(sd.getTitle());
        section.setSectionClass(o.getClass());

        // iterate through section maint items and contruct Field UI objects
        Collection maintItems = sd.getMaintainableItems();
        List<Row> sectionRows = new ArrayList<Row>();
        List<Field> sectionFields = new ArrayList<Field>();

        for (Iterator iterator = maintItems.iterator(); iterator.hasNext();) {
            MaintainableItemDefinition item = (MaintainableItemDefinition) iterator.next();
            Field field = FieldBridge.toField(item, sd, o, maintainable, section, autoFillDefaultValues, autoFillBlankRequiredValues, displayedFieldNames);
            boolean skipAdd = false;

            // if CollectionDefiniton, then have a many section
            if (item instanceof MaintainableCollectionDefinition) {
                MaintainableCollectionDefinition definition = (MaintainableCollectionDefinition) item;
                section.getContainedCollectionNames().add(((MaintainableCollectionDefinition) item).getName());

                StringBuffer containerRowErrorKey = new StringBuffer();
                sectionRows = getContainerRows(section, definition, o, maintainable, oldMaintainable, displayedFieldNames, containerRowErrorKey, RiceConstants.DEFAULT_NUM_OF_COLUMNS, null);
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
            if (RiceConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
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
    public static final List<Row> getContainerRows(Section s, CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, Maintainable oldMaintainable, List<String> displayedFieldNames, StringBuffer containerRowErrorKey, int numberOfColumns, Inquirable inquirable) {
        return getContainerRows(s, collectionDefinition, o, m, oldMaintainable, displayedFieldNames, containerRowErrorKey, "", false, numberOfColumns, inquirable);
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
     public static final List<Row> getContainerRows(Section s, CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, Maintainable oldMaintainable, List<String> displayedFieldNames, StringBuffer containerRowErrorKey, String parents, boolean hideAdd, int numberOfColumns, Inquirable inquirable) {
        List<Row> containerRows = new ArrayList<Row>();
        List<Field> collFields = new ArrayList<Field>();
        
        String collectionName = collectionDefinition.getName();

        if (m != null && collectionDefinition instanceof DataDictionaryDefinitionBase) {
            collectionDefinition = (CollectionDefinitionI) ObjectUtils.deepCopy((DataDictionaryDefinitionBase) collectionDefinition);
            m.overrideDataDictionaryFieldConfiguration((DataDictionaryDefinitionBase) collectionDefinition);
        }
        
        // add the toggle inactive record display button for the collection
        if (m != null && Inactivateable.class.isAssignableFrom(collectionDefinition.getBusinessObjectClass()) && StringUtils.isBlank(parents)) {
            addShowInactiveButtonField(s, collectionName, !m.getShowInactiveRecords(collectionName));
        }
        if (inquirable != null && Inactivateable.class.isAssignableFrom(collectionDefinition.getBusinessObjectClass()) && StringUtils.isBlank(parents)) {
            addShowInactiveButtonField(s, collectionName, !inquirable.getShowInactiveRecords(collectionName));
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
            containerRows.addAll(getContainerRows(s, subCollectionDefinition, o, m, oldMaintainable, displayedFieldNames, containerRowErrorKey, parents + collectionDefinition.getName() + ".", true, subCollectionNumberOfColumn, inquirable));
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
                Object obj = ObjectUtils.getPropertyValue(o, collectionName);
                
                Object oldObj = null;
                if (oldMaintainable != null && oldMaintainable.getBusinessObject() != null) {
                    oldObj = ObjectUtils.getPropertyValue(oldMaintainable.getBusinessObject(), collectionName);
                }

                if (obj instanceof List) {
                    Map summaryFields = new HashMap();
                    boolean hidableRowsPresent = false;
                    for (int i = 0; i < ((List) obj).size(); i++) {
                        BusinessObject lineBusinessObject = (BusinessObject) ((List) obj).get(i);
                        
                        /*
                         * Handle display of inactive records. The old maintainable is used to compare the old side (if it exists). If the row should not be displayed, it is set as
                         * hidden and will be handled in the maintenance rowDisplay.tag.   
                         */  
                        boolean setRowHidden = false;
                        BusinessObject oldLineBusinessObject = null;
                        if (oldObj != null) {
                            oldLineBusinessObject = (BusinessObject) ((List) oldObj).get(i);
                        }
                        
                        if (lineBusinessObject instanceof Inactivateable && !((Inactivateable) lineBusinessObject).isActive()) {
                            if (m != null) {
                                // rendering a maint doc
                                if (!hidableRowsPresent) {
                                    hidableRowsPresent = isRowHideableForMaintenanceDocument(lineBusinessObject, oldLineBusinessObject);
                                	}
                                setRowHidden = isRowHiddenForMaintenanceDocument(lineBusinessObject, oldLineBusinessObject, m, collectionName);
                                	}
                            if (inquirable != null) {
                                // rendering an inquiry screen
                                if (!hidableRowsPresent) {
                                    hidableRowsPresent = isRowHideableForInquiry(lineBusinessObject);
                        		}
                                setRowHidden = isRowHiddenForInquiry(lineBusinessObject, inquirable, collectionName);
                        	}
                        }

                        collFields = new ArrayList<Field>();
                        List<String> duplicateIdentificationFieldNames = new ArrayList<String>(); 
                        //We only need to do this if the collection definition is a maintainable collection definition, 
                        //don't need it for inquiry collection definition.
                        if (collectionDefinition instanceof MaintainableCollectionDefinition) {
	                        Collection<MaintainableFieldDefinition> duplicateFieldDefs = ((MaintainableCollectionDefinition)collectionDefinition).getDuplicateIdentificationFields();
	                        for (MaintainableFieldDefinition eachFieldDef : duplicateFieldDefs) {
	                    		duplicateIdentificationFieldNames.add(eachFieldDef.getName());
	                    	}
                        }
                        
                        for (Iterator iterator = collectionFields.iterator(); iterator.hasNext();) {
                            FieldDefinitionI fieldDefinition = (FieldDefinitionI) iterator.next();

                            // construct Field UI object from definition
                            Field collField = FieldUtils.getPropertyField(collectionDefinition.getBusinessObjectClass(), fieldDefinition.getName(), false);
                            
                            FieldBridge.setupField(collField, fieldDefinition);
                            setPrimaryKeyFieldsReadOnly(collectionDefinition.getBusinessObjectClass(), collField);

                            //If the duplicateIdentificationFields were specified in the maint. doc. DD, we'll need
                            //to set the fields to be read only as well, in addition to the primary key fields.
                            if (duplicateIdentificationFieldNames.size() > 0) {
                            	setDuplicateIdentificationFieldsReadOnly(collField, duplicateIdentificationFieldNames);
                            }
                            
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
                        containerField = FieldUtils.constructContainerField(RiceConstants.EDIT_PREFIX + "[" + (new Integer(i)).toString() + "]", collectionLabel + " " + (i + 1), collFields, numberOfColumns);
                        // why is this only on collections and not subcollections any significance or just oversight?
                        containerField.setContainerName(collectionDefinition.getName() + "[" + (new Integer(i)).toString() + "].");

                        /* If the collection line is pending (meaning added by this document) the isNewCollectionRecord will be set to true. In this
                           case we give an option to delete the line. The parameters for the delete action method are embedded into the button name. */
                        if (lineBusinessObject instanceof PersistableBusinessObject && ((PersistableBusinessObject) lineBusinessObject).isNewCollectionRecord()) {
                            containerField.getContainerRows().add(new Row(getDeleteRowButtonField(parents + collectionDefinition.getName(), (new Integer(i)).toString())));
                        }

                        if (StringUtils.isNotEmpty(collectionElementLabel)) {
                        	//We don't want to associate any indexes to the containerElementName anymore so that
                        	//when the element is deleted, the currentTabIndex won't be associated with the
                        	//wrong tab for the remaining tab.
                        	//containerField.setContainerElementName(collectionElementLabel + " " + (i + 1));
                        	containerField.setContainerElementName(collectionElementLabel);
                            // reorder summaryFields to make sure they are in the order specified in the summary section
                            List orderedSummaryFields = getSummaryFields(summaryFields, collectionDefinition);
                            containerField.setContainerDisplayFields(orderedSummaryFields);
                        }
                        
                        Row containerRow = new Row(containerField);
                        if (setRowHidden) {
                            containerRow.setHidden(true);
                        }
                        containerRows.add(containerRow);
                        
                        

                        Collection subCollections = collectionDefinition.getCollections();
                        List<Field> subCollFields = new ArrayList<Field>();

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
                            
                            Object oldSubObj = null;
                            if (oldLineBusinessObject != null) {
                                oldSubObj = ObjectUtils.getPropertyValue(oldLineBusinessObject, subCollectionName);
                            }
                            
                            if (subObj instanceof List) {
                                /* recursively call this method to get the add row and exisiting members of the subCollections subcollections containerRows.addAll(getContainerRows(subCollectionDefinition,
                                   displayedFieldNames,containerRowErrorKey, parents+collectionDefinition.getName()+"["+i+"]"+".","[0]",false, subCollectionNumberOfColumn)); */
                                containerField.getContainerRows().addAll(getContainerRows(s, subCollectionDefinition, o, m, oldMaintainable, displayedFieldNames, containerRowErrorKey, parents + collectionDefinition.getName() + "[" + i + "]" + ".", false, subCollectionNumberOfColumns, inquirable));
                             
                                // iterate over the fields
                                for (int j = 0; j < ((List) subObj).size(); j++) {
                                    BusinessObject lineSubBusinessObject = (BusinessObject) ((List) subObj).get(j);
                                    
                                    // determine if sub collection line is inactive and should be hidden
                                    boolean setSubRowHidden = false;
                                    if (lineSubBusinessObject instanceof Inactivateable && !((Inactivateable) lineSubBusinessObject).isActive() ) {
                                    	if (oldSubObj != null) { 
                                            // get corresponding elements in both the new list and the old list
                                            BusinessObject oldLineSubBusinessObject = (BusinessObject) ((List) oldSubObj).get(j);
                                            if (m != null) {
                                                    if (!hidableRowsPresent) {
                                                        hidableRowsPresent = isRowHideableForMaintenanceDocument(lineSubBusinessObject, oldLineSubBusinessObject);
                                            	}
                                                    setSubRowHidden = isRowHiddenForMaintenanceDocument(lineSubBusinessObject, oldLineSubBusinessObject, m, collectionName);
                                            	}
                                    		}
                                        if (inquirable != null) {
                                            if (!hidableRowsPresent) {
                                                hidableRowsPresent = isRowHideableForInquiry(lineSubBusinessObject);
                                    	}
                                            setSubRowHidden = isRowHiddenForInquiry(lineSubBusinessObject, inquirable, collectionName);
                                    }
                                    }

                                    
                                    
                                    subCollFields = new ArrayList<Field>();
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

                                        // check if this is a summary field
                                        if (subCollectionDefinition.hasSummaryField(fieldDefinition.getName())) {
                                            summaryFields.put(fieldDefinition.getName(), subCollField);
                                        }

                                        subCollFields.add(subCollField);
                                    }

                                    Field subContainerField = FieldUtils.constructContainerField(RiceConstants.EDIT_PREFIX + "[" + (new Integer(j)).toString() + "]", subCollectionLabel, subCollFields);
                                    if (lineSubBusinessObject instanceof PersistableBusinessObject && ((PersistableBusinessObject) lineSubBusinessObject).isNewCollectionRecord()) {
                                        subContainerField.getContainerRows().add(new Row(getDeleteRowButtonField(parents + collectionDefinition.getName() + "[" + i + "]" + "." + subCollectionName, (new Integer(j)).toString())));
                                    }

                                    // summary line code
                                    if (StringUtils.isNotEmpty(subCollectionElementLabel)) {
                                        //We don't want to associate any indexes to the containerElementName anymore so that
                                    	//when the element is deleted, the currentTabIndex won't be associated with the
                                    	//wrong tab for the remaining tab.
                                        //subContainerField.setContainerElementName(subCollectionElementLabel + " " + (j + 1));
                                    	subContainerField.setContainerElementName(collectionElementLabel + "-" + subCollectionElementLabel);
                                    }
                                    subContainerField.setContainerName(collectionDefinition.getName() + "." + subCollectionName);
                                    if (!summaryFields.isEmpty()) {
                                        // reorder summaryFields to make sure they are in the order specified in the summary section
                                        List orderedSummaryFields = getSummaryFields(summaryFields, subCollectionDefinition);
                                        subContainerField.setContainerDisplayFields(orderedSummaryFields);
                                    }
                                    
                                    Row subContainerRow = new Row(subContainerField);
                                    if (setRowHidden || setSubRowHidden) {
                                        subContainerRow.setHidden(true);
                                    }
                                    containerField.getContainerRows().add(subContainerRow);
                                }
                            }
                        }
                    }
                    if ( !hidableRowsPresent ) {
                    	s.setExtraButtonSource( "" );
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

        String deleteButtonName = RiceConstants.DISPATCH_REQUEST_PARAMETER + "." + RiceConstants.DELETE_LINE_METHOD + "." + collectionName + "." + RiceConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL + ".line" + rowIndex;
        deleteButtonField.setPropertyName(deleteButtonName);
        deleteButtonField.setFieldType(Field.IMAGE_SUBMIT);
        deleteButtonField.setPropertyValue("images/tinybutton-delete1.gif");

        return deleteButtonField;
    }
    
    /**
     * Helper method to build up the show inactive button source and place in the section.
     * 
     * @param section - section that will display the button
     * @param collectionName - name of the collection to toggle setting
     * @param showInactive - boolean indicating whether inactive rows should be displayed
     * @return Field - of type IMAGE_SUBMIT
     */
    private static final void addShowInactiveButtonField(Section section, String collectionName, boolean showInactive) {
        String showInactiveButton = "<a name=\"showInactive" + collectionName + "\"><input type=\"image\" name=\"" + RiceConstants.DISPATCH_REQUEST_PARAMETER + "." + RiceConstants.TOGGLE_INACTIVE_METHOD + "." + collectionName.replace( '.', '_' );
        showInactiveButton += "." + RiceConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL + showInactive  + ".anchorshowInactive" + collectionName + "\" src=\"";
        
        if (showInactive) {
            showInactiveButton += "images/tinybutton-showinact.gif";
        }
        else {
            showInactiveButton += "images/tinybutton-hideinact.gif";
        }

        showInactiveButton += "\" alt=\"show(hide) inactive\" class=\"tinybutton\" /></a>";
        section.setExtraButtonSource(showInactiveButton);
    }
    
    /**
     * Retrieves the primary key property names for the given class. If the field's property is one of those keys, makes the field
     * read-only. This is called for collection lines. Since deletion is not allowed for existing lines, the pk fields must be
     * read-only, otherwise a user could change the pk value which would be equivalent to deleting the line and adding a new line.
     */
    private static final void setPrimaryKeyFieldsReadOnly(Class businessObjectClass, Field field) {
        List primaryKeyPropertyNames = KNSServiceLocator.getPersistenceStructureService().getPrimaryKeys(businessObjectClass);
        if (primaryKeyPropertyNames.contains(field.getPropertyName())) {
            field.setReadOnly(true);
        }
    }
    
    private static void setDuplicateIdentificationFieldsReadOnly(Field field, List<String>duplicateIdentificationFieldNames) {
        if (duplicateIdentificationFieldNames.contains(field.getPropertyName())) {
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
    private static final List<Field> getSummaryFields(Map collSummaryFields, CollectionDefinitionI collectionDefinition) {
        List<Field> orderedSummaryFields = new ArrayList<Field>();
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
    
    /**
     * Determines whether a business object is hidable on a maintenance document.  Hidable means that if the user chose to hide the inactive
     * elements in the collection in which the passed in BOs reside, then the BOs would be hidden
     * 
     * @param lineBusinessObject the BO in the new maintainable, should be of type {@link PersistableBusinessObject} and {@link Inquirable}
     * @param oldLineBusinessObject the corresponding BO in the old maintainable, should be of type {@link PersistableBusinessObject} and 
     * {@link Inquirable}
     * @return whether the BOs are eligible to be hidden if the user decides to hide them
     */
    protected static boolean isRowHideableForMaintenanceDocument(BusinessObject lineBusinessObject, BusinessObject oldLineBusinessObject) {
        if (oldLineBusinessObject != null) {
            if (((PersistableBusinessObject) lineBusinessObject).isNewCollectionRecord()) {
                // new records are never hidden, regardless of active status
                return false;
}
            if (!((Inactivateable) lineBusinessObject).isActive() && !((Inactivateable) oldLineBusinessObject).isActive()) {
                // records with an old and new collection elements of NOT active are eligible to be hidden
                return true;
            }
        }
        return false;
    }
    /**
     * Determines whether a business object is hidden on a maintenance document.
     * 
     * @param lineBusinessObject the BO in the new maintainable, should be of type {@link PersistableBusinessObject}
     * @param oldLineBusinessObject the corresponding BO in the old maintainable
     * @param newMaintainable the new maintainable from the maintenace document
     * @param collectionName the name of the collection from which these BOs come
     * @return
     */
    protected static boolean isRowHiddenForMaintenanceDocument(BusinessObject lineBusinessObject, BusinessObject oldLineBusinessObject,
            Maintainable newMaintainable, String collectionName) {
        if (isRowHideableForMaintenanceDocument(lineBusinessObject, oldLineBusinessObject)) {
            return !newMaintainable.getShowInactiveRecords(collectionName);
        }
        return false;
    }
    
    /**
     * Determines whether a business object is hidable on an inquiry screen.  Hidable means that if the user chose to hide the inactive
     * elements in the collection in which the passed in BO resides, then the BO would be hidden
     * 
     * @param lineBusinessObject the collection element BO, should be of type {@link PersistableBusinessObject} and {@link Inquirable}
     * @return whether the BO is eligible to be hidden if the user decides to hide them
     */
    protected static boolean isRowHideableForInquiry(BusinessObject lineBusinessObject) {
        return !((Inactivateable) lineBusinessObject).isActive();
    }
    
    /**
     * Determines whether a business object is hidden on an inquiry screen.
     * 
     * @param lineBusinessObject the BO in the collection, should be of type {@link PersistableBusinessObject} and {@link Inquirable}
     * @param inquirable the inquirable
     * @param collectionName the name of the collection from which the BO comes
     * @return true if the business object is to be hidden; false otherwise
     */
    protected static boolean isRowHiddenForInquiry(BusinessObject lineBusinessObject, Inquirable inquirable, String collectionName) {
        if (isRowHideableForInquiry(lineBusinessObject)) {
            return !inquirable.getShowInactiveRecords(collectionName);
        }
        return false;
    }
}
