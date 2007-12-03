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
package org.kuali.core.service.impl;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.FieldDefinition;
import org.kuali.core.datadictionary.FieldPairDefinition;
import org.kuali.core.datadictionary.InquiryDefinition;
import org.kuali.core.datadictionary.InquirySectionDefinition;
import org.kuali.core.datadictionary.LookupDefinition;
import org.kuali.core.datadictionary.MaintenanceDocumentEntry;
import org.kuali.core.exceptions.IntrospectionException;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;

/**
 * This class is the service implementation for the BusinessObjectDictionary. This is the default, Kuali delivered implementation
 * which leverages the DataDictionaryService.
 */
public class BusinessObjectDictionaryServiceImpl implements BusinessObjectDictionaryService {
    private static Logger LOG = Logger.getLogger(BusinessObjectDictionaryServiceImpl.class);
    
    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;
    
    /**
     * Uses the DataDictionaryService.
     * 
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getBusinessObjectEntries()
     */
    public List getBusinessObjectClassnames() {
        return getDataDictionaryService().getDataDictionary().getBusinessObjectClassNames();
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#isLookupable(java.lang.Class)
     */
    public Boolean isLookupable(Class businessObjectClass) {
        Boolean isLookupable = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            isLookupable = Boolean.valueOf(entry.hasLookupDefinition());
        }

        return isLookupable;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#isInquirable(java.lang.Class)
     */
    public Boolean isInquirable(Class businessObjectClass) {
        Boolean isInquirable = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            isInquirable = Boolean.valueOf(entry.hasInquiryDefinition());
        }

        return isInquirable;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#isMaintainable(java.lang.Class)
     */
    public Boolean isMaintainable(Class businessObjectClass) {
        Boolean isMaintainable = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            isMaintainable = Boolean.valueOf(getMaintenanceDocumentEntry(businessObjectClass) != null);
        }

        return isMaintainable;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupFieldNames(java.lang.Class)
     */
    public List getLookupFieldNames(Class businessObjectClass) {
        List results = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            results = lookupDefinition.getLookupFieldNames();
        }

        return results;
    }


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupTitle(java.lang.Class)
     */
    public String getLookupTitle(Class businessObjectClass) {
        String lookupTitle = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            lookupTitle = lookupDefinition.getTitle();
        }

        return lookupTitle;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupMenuBar(java.lang.Class)
     */
    public String getLookupMenuBar(Class businessObjectClass) {
        String menubar = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasMenubar()) {
                menubar = lookupDefinition.getMenubar();
            }
        }

        return menubar;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupInstructions(java.lang.Class)
     */
    public String getLookupInstructions(Class businessObjectClass) {
        String instructions = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasInstructions()) {
                instructions = lookupDefinition.getInstructions();
            }
        }

        return instructions;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getExtraButtonSource(java.lang.Class)
     */
    public String getExtraButtonSource(Class businessObjectClass) {
        String buttonSource = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasExtraButtonSource()) {
                buttonSource = lookupDefinition.getExtraButtonSource();
            }
        }

        return buttonSource;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getExtraButtonParams(java.lang.Class)
     */
    public String getExtraButtonParams(Class businessObjectClass) {
        String buttonParams = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasExtraButtonParams()) {
                buttonParams = lookupDefinition.getExtraButtonParams();
            }
        }

        return buttonParams;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupDefaultSortFieldName(java.lang.Class)
     */
    public List getLookupDefaultSortFieldNames(Class businessObjectClass) {
        List defaultSort = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasDefaultSort()) {
                defaultSort = lookupDefinition.getDefaultSort().getAttributeNames();
            }
        }
        if (defaultSort == null) {
            defaultSort = new ArrayList();
        }

        return defaultSort;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupResultFieldNames(java.lang.Class)
     */
    public List<String> getLookupResultFieldNames(Class businessObjectClass) {
        List<String> results = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            results = lookupDefinition.getResultFieldNames();
        }

        return results;
    }
    

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupResultFieldMaxLength(java.lang.Class, java.lang.String)
     */
    public int getLookupResultFieldMaxLength(Class businessObjectClass, String resultFieldName) {
        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        FieldDefinition field = lookupDefinition.getResultField(resultFieldName);
        return Integer.parseInt(field.getMaxLength());
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupAttributeRequired(java.lang.Class, java.lang.String)
     */
    public Boolean getLookupAttributeRequired(Class businessObjectClass, String attributeName) {
        Boolean isRequired = null;

        FieldDefinition definition = getLookupFieldDefinition(businessObjectClass, attributeName);
        if (definition != null) {
            isRequired = Boolean.valueOf(definition.isRequired());
        }

        return isRequired;
    }
   

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getInquiryFieldNames(java.lang.Class, java.lang.String)
     */
    public List getInquiryFieldNames(Class businessObjectClass, String sectionTitle) {
        List results = null;
        
        InquirySectionDefinition inquirySection = getInquiryDefinition(businessObjectClass).getInquirySection(sectionTitle);
        if (inquirySection != null) {
            results = inquirySection.getInquiryFieldNames();
        }
        
        return results;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getInquirySections(java.lang.Class)
     */
    public List getInquirySections(Class businessObjectClass) {
        List results = null;
        
        results = getInquiryDefinition(businessObjectClass).getInquirySections();
       
        return results;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getInquiryTitle(java.lang.Class)
     */
    public String getInquiryTitle(Class businessObjectClass) {
        String title = "";

        InquiryDefinition inquiryDefinition = getInquiryDefinition(businessObjectClass);
        if (inquiryDefinition != null) {
            title = inquiryDefinition.getTitle();
        }

        return title;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getInquirableClass(java.lang.Class)
     */
    public Class getInquirableClass(Class businessObjectClass) {
        Class clazz = null;

        InquiryDefinition inquiryDefinition = getInquiryDefinition(businessObjectClass);
        if (inquiryDefinition != null) {
            clazz = inquiryDefinition.getInquirableClass();
        }

        return clazz;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getMaintainableTitle(java.lang.Class)
     */
    public String getMaintainableLabel(Class businessObjectClass) {
        String label = "";

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(businessObjectClass);
        if (entry != null) {
            label = entry.getLabel();
        }

        return label;
    }

    /**
     * 
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupableID(java.lang.Class)
     */
    public String getLookupableID(Class businessObjectClass) {
        String lookupableID = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            lookupableID = lookupDefinition.getLookupableID();
        }

        return lookupableID;
    }


    /**
     * Recurses down the updatable references and collections of a BO, uppercasing those attributes which are marked as needing to be
     * uppercased in the data dictionary.  Updatability of a reference or collection is defined by the PersistenceStructureService
     * 
     * @param bo the BO to uppercase
     * 
     * @see PersistenceStructureService#isCollectionUpdatable(Class, String)
     * @see PersistenceStructureService#isReferenceUpdatable(Class, String)
     * @see DataDictionaryService#getAttributeForceUppercase(Class, String)
     */
    public void performForceUppercase(BusinessObject bo) {
        PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors(bo);
        for (int i = 0; i < descriptors.length; ++i) {
            try {
                if (descriptors[i] instanceof IndexedPropertyDescriptor) {
                    // Skip this case because PropertyUtils.getProperty(bo, descriptors[i].getName()) will throw a
                    // NoSuchMethodException on those. These
                    // fields are usually convenience methods in the BO and in the below code we anyway wouldn't know which index
                    // .toUpperCase().
                }
                else {
                    Object nestedObject = ObjectUtils.getPropertyValue(bo, descriptors[i].getName());
                    if (nestedObject instanceof BusinessObject) {
                        if (persistenceStructureService.isPersistable(nestedObject.getClass())) {
                                try {
                                if (persistenceStructureService.hasReference(bo.getClass(), descriptors[i].getName())) {
                                    if (persistenceStructureService.isReferenceUpdatable(bo.getClass(), descriptors[i].getName())) {
                                        if (persistenceStructureService.getForeignKeyFieldsPopulationState((PersistableBusinessObject) bo, descriptors[i].getName()).isAllFieldsPopulated()) {
                                        // check FKs to prevent probs caused by referential integrity problems
                                            performForceUppercase((BusinessObject) nestedObject);
                                    }
                                    }
                                }
                                } catch (org.kuali.core.exceptions.ReferenceAttributeNotAnOjbReferenceException ranaore) {
                                    LOG.debug("Propery "+descriptors[i].getName()+" is not a foreign key reference.");
                                }
                            }
                    } else if (nestedObject instanceof String) {
                        if (dataDictionaryService.isAttributeDefined(bo.getClass(), descriptors[i].getName()).booleanValue() && dataDictionaryService.getAttributeForceUppercase(bo.getClass(), descriptors[i].getName()).booleanValue()) {
                            String curValue = (String) nestedObject;
                            PropertyUtils.setProperty(bo, descriptors[i].getName(), curValue.toUpperCase());
                        }
                    }
                    else {
                        if (nestedObject instanceof Collection) {
                            if (persistenceStructureService.hasCollection(bo.getClass(), descriptors[i].getName())) {
                                if (persistenceStructureService.isCollectionUpdatable(bo.getClass(), descriptors[i].getName())) {
                            Iterator iter = ((Collection) nestedObject).iterator();
                            while (iter.hasNext()) {
                                Object collElem = iter.next();
                                if (collElem instanceof BusinessObject) {
                                            if (persistenceStructureService.isPersistable(collElem.getClass())) {
                                                performForceUppercase((BusinessObject) collElem);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (IllegalAccessException e) {
                throw new IntrospectionException("unable to performForceUppercase", e);
            }
            catch (InvocationTargetException e) {
                throw new IntrospectionException("unable to performForceUppercase", e);
            }
            catch (NoSuchMethodException e) {
                // if the getter/setter does not exist, just skip over
                //throw new IntrospectionException("unable to performForceUppercase", e);
            }
        }
    }

    /**
     * Sets the instance of the data dictionary service.
     * 
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * This method retrieves the instance of the data dictionary service.
     * 
     * @return An instance of the DataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    /**
     * @param businessObjectClass
     * @return BusinessObjectEntry for the given businessObjectClass, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private BusinessObjectEntry getBusinessObjectEntry(Class businessObjectClass) {
        validateBusinessObjectClass(businessObjectClass);

        BusinessObjectEntry entry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectClass.getName());
        return entry;
    }

    /**
     * @param businessObjectClass
     * @return MaintenanceDocumentEntry for the given businessObjectClass, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private MaintenanceDocumentEntry getMaintenanceDocumentEntry(Class businessObjectClass) {
        validateBusinessObjectClass(businessObjectClass);

        MaintenanceDocumentEntry entry = getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(businessObjectClass);
        return entry;
    }

    /**
     * @param businessObjectClass
     * @return LookupDefinition for the given businessObjectClass, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private LookupDefinition getLookupDefinition(Class businessObjectClass) {
        LookupDefinition lookupDefinition = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            if (entry.hasLookupDefinition()) {
                lookupDefinition = entry.getLookupDefinition();
            }
        }

        return lookupDefinition;
    }

    /**
     * @param businessObjectClass
     * @param attributeName
     * @return FieldDefinition for the given businessObjectClass and lookup field name, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private FieldDefinition getLookupFieldDefinition(Class businessObjectClass, String lookupFieldName) {
        if (StringUtils.isBlank(lookupFieldName)) {
            throw new IllegalArgumentException("invalid (blank) lookupFieldName");
        }

        FieldDefinition fieldDefinition = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            fieldDefinition = lookupDefinition.getLookupField(lookupFieldName);
        }

        return fieldDefinition;
    }

    /**
     * @param businessObjectClass
     * @param attributeName
     * @return FieldDefinition for the given businessObjectClass and lookup result field name, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private FieldDefinition getLookupResultFieldDefinition(Class businessObjectClass, String lookupFieldName) {
        if (StringUtils.isBlank(lookupFieldName)) {
            throw new IllegalArgumentException("invalid (blank) lookupFieldName");
        }

        FieldDefinition fieldDefinition = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            fieldDefinition = lookupDefinition.getResultField(lookupFieldName);
        }

        return fieldDefinition;
    }

    /**
     * @param businessObjectClass
     * @return InquiryDefinition for the given businessObjectClass, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private InquiryDefinition getInquiryDefinition(Class businessObjectClass) {
        InquiryDefinition inquiryDefinition = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            if (entry.hasInquiryDefinition()) {
                inquiryDefinition = entry.getInquiryDefinition();
            }
        }

        return inquiryDefinition;
    }


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getTitleAttribute(java.lang.Class)
     */
    public String getTitleAttribute(Class businessObjectClass) {
        String titleAttribute = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            titleAttribute = entry.getTitleAttribute();
        }

        return titleAttribute;
    }

    /**
     * @param businessObjectClass
     * @param attributeName
     * @return FieldDefinition for the given businessObjectClass and field name, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private FieldDefinition getInquiryFieldDefinition(Class businessObjectClass, String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("invalid (blank) fieldName");
        }

        FieldDefinition fieldDefinition = null;

        InquiryDefinition inquiryDefinition = getInquiryDefinition(businessObjectClass);
        if (inquiryDefinition != null) {
            fieldDefinition = inquiryDefinition.getFieldDefinition(fieldName);
        }

        return fieldDefinition;
    }

    /**
     * @param businessObjectClass
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private void validateBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }
        if (!BusinessObject.class.isAssignableFrom(businessObjectClass)) {
            throw new IllegalArgumentException("class '" + businessObjectClass.getName() + "' is not a descendent of BusinessObject");
        }
    }

    /**
     * @param fieldPairs
     * @param toFirst
     * @return
     */
    private String buildFieldPairString(List fieldPairs, boolean toFirst) {
        StringBuffer assembly = new StringBuffer();

        for (Iterator i = fieldPairs.iterator(); i.hasNext();) {
            FieldPairDefinition fieldPair = (FieldPairDefinition) i.next();
            String field1 = toFirst ? fieldPair.getFieldTo() : fieldPair.getFieldFrom();
            String field2 = toFirst ? fieldPair.getFieldFrom() : fieldPair.getFieldTo();
            // TODO BJM -- due to a bug? I switched the order of these below...
            assembly.append(field2 + ":" + field1);
            if (i.hasNext()) {
                assembly.append(",");
            }
        }

        return assembly.toString();
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#forceLookupResultFieldInquiry(java.lang.Class, java.lang.String)
     */
    public Boolean forceLookupResultFieldInquiry(Class businessObjectClass, String attributeName) {
        Boolean forceLookup = null;
        if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
            forceLookup = Boolean.valueOf(getLookupResultFieldDefinition(businessObjectClass, attributeName).isForceInquiry());
        }

        return forceLookup;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#noLookupResultFieldInquiry(java.lang.Class, java.lang.String)
     */
    public Boolean noLookupResultFieldInquiry(Class businessObjectClass, String attributeName) {
        Boolean noLookup = null;
        if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
            noLookup = Boolean.valueOf(getLookupResultFieldDefinition(businessObjectClass, attributeName).isNoInquiry());
        }

        return noLookup;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#forceLookupFieldLookup(java.lang.Class, java.lang.String)
     */
    public Boolean forceLookupFieldLookup(Class businessObjectClass, String attributeName) {
        Boolean forceLookup = null;
        if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
            forceLookup = Boolean.valueOf(getLookupFieldDefinition(businessObjectClass, attributeName).isForceLookup());
        }

        return forceLookup;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#noLookupFieldLookup(java.lang.Class, java.lang.String)
     */
    public Boolean noLookupFieldLookup(Class businessObjectClass, String attributeName) {
        Boolean noLookup = null;
        if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
            noLookup = Boolean.valueOf(getLookupFieldDefinition(businessObjectClass, attributeName).isNoLookup());
        }

        return noLookup;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#forceInquiryFieldInquiry(java.lang.Class, java.lang.String)
     */
    public Boolean forceInquiryFieldInquiry(Class businessObjectClass, String attributeName) {
        Boolean forceInquiry = null;
        if (getInquiryFieldDefinition(businessObjectClass, attributeName) != null) {
            forceInquiry = Boolean.valueOf(getInquiryFieldDefinition(businessObjectClass, attributeName).isForceInquiry());
        }

        return forceInquiry;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#noInquiryFieldInquiry(java.lang.Class, java.lang.String)
     */
    public Boolean noInquiryFieldInquiry(Class businessObjectClass, String attributeName) {
        Boolean noInquiry = null;
        if (getInquiryFieldDefinition(businessObjectClass, attributeName) != null) {
            noInquiry = Boolean.valueOf(getInquiryFieldDefinition(businessObjectClass, attributeName).isNoInquiry());
        }

        return noInquiry;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupFieldDefaultValue(java.lang.Class, java.lang.String)
     */
    public String getLookupFieldDefaultValue(Class businessObjectClass, String attributeName) {
        return getLookupFieldDefinition(businessObjectClass, attributeName).getDefaultValue();
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getLookupFieldDefaultValueFinderClass(java.lang.Class,
     *      java.lang.String)
     */
    public Class getLookupFieldDefaultValueFinderClass(Class businessObjectClass, String attributeName) {
        return getLookupFieldDefinition(businessObjectClass, attributeName).getDefaultValueFinderClass();
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public Boolean areNotesSupported(Class businessObjectClass) {
        Boolean hasNotesSupport = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            hasNotesSupport = Boolean.valueOf(entry.isBoNotesEnabled());
        }

        return hasNotesSupport;
    }


}