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

package org.kuali.rice.kns.datadictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.exception.DuplicateEntryException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Contains lookup-related information relating to the parent BusinessObject.

 *            The lookup element is used to specify the rules for "looking up"
            a business object.  These specifications define the following:
            * How to specify the search criteria used to locate a set of business objects
            * How to display the search results

            DD: See LookupDefinition.java

            JSTL: The lookup element is a Map which is accessed using
            a key of "lookup".  This map contains the following keys:
            * lookupableID (String, optional)
            * title (String)
            * menubar (String, optional)
            * instructions (String, optional)
            * defaultSort (Map, optional)
            * lookupFields (Map)
            * resultFields (Map)
            * resultSetLimit (String, optional)

            See LookupMapBuilder.java

 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 *
 *
 */
public class LookupDefinition extends DataDictionaryDefinitionBase {

    protected String lookupableID;
    protected String title;
    protected String menubar;
    protected String instructions;
    protected SortDefinition defaultSort;

    protected List<FieldDefinition> lookupFields = new ArrayList<FieldDefinition>();
    protected Map<String,FieldDefinition> lookupFieldMap = new LinkedHashMap<String, FieldDefinition>();
    protected List<FieldDefinition> resultFields = new ArrayList<FieldDefinition>();
    protected Map<String,FieldDefinition> resultFieldMap = new LinkedHashMap<String, FieldDefinition>();

    protected Integer resultSetLimit = null;

    protected String extraButtonSource;
    protected String extraButtonParams;

    public LookupDefinition() {}

    /**

                The lookupableID element identifies the name of the Spring bean which
                will be used to obtain the lookupable helper service for the business object.
                For example, the Balance.xml file has a lookupableId = "glBalanceLookupable".
                The KualiSpringBeansGL.xml file determines that the helper service will be an
                instance of BalanceLookupableHelperServiceImpl.

                If this field is omitted, the default bean id used will be kualiLookupable which uses
                the KualiLookupableHelperServiceImpl helper service.
     */
    public void setLookupableID(String lookupableID) {
        if (lookupableID == null) {
            throw new IllegalArgumentException("invalid (null) lookupableID");
        }

        this.lookupableID = lookupableID;
    }

    /**
     * @return custom lookupable id
     */
    public String getLookupableID() {
        return this.lookupableID;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to the given value.
     *
     * @param title
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }
        this.title = title;
    }

    /**
     * @return true if this instance has a menubar
     */
    public boolean hasMenubar() {
        return (menubar != null);
    }

    /**
     * @return menubar
     */
    public String getMenubar() {
        return menubar;
    }

    /**

                The menubar element is used to add additional html code
                to the header line on the lookup screen.

                For example, Account.xml uses this element to
                add the "create new global" button to the Account Lookup header.
     * @throws IllegalArgumentException if the given menubar is blank
     */
    public void setMenubar(String menubar) {
        if (StringUtils.isBlank(menubar)) {
            throw new IllegalArgumentException("invalid (blank) menubar");
        }
        // TODO: catch exception if service locator call fails
        this.menubar = menubar.replace("${kr.externalizable.images.url}", KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.EXTERNALIZABLE_IMAGES_URL_KEY)).replace("${externalizable.images.url}", KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY));
    }


    /**
     * @return true if this instance has instructions
     */
    public boolean hasInstructions() {
        return (instructions != null);
    }

    /**
     * @return instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
                The instructions element is used to display instructions to the
                user on how to use the lookup screen.  It appears that this field
                is not currently being used on the Kuali Rice lookup screens.
     * @throws IllegalArgumentException if the given instructions are blank
     */
    public void setInstructions(String instructions) {
        if (StringUtils.isBlank(instructions)) {
            throw new IllegalArgumentException("invalid (blank) instructions");
        }
        this.instructions = instructions;
    }

    /**
     * @return true if this instance has a default sort defined
     */
    public boolean hasDefaultSort() {
        return (defaultSort != null);
    }

    /**
     * @return defaultSort
     */
    public SortDefinition getDefaultSort() {
        return defaultSort;
    }

    /**
                The defaultSort element specifies the sequence in which the
                lookup search results should be displayed.  It contains an
                ascending/descending indicator and a list of attribute names.

                DD: See SortDefinition.java

                JSTL: defaultSort is a Map with the following keys:
                * sortAscending (boolean String)
                * sortAttributes (Map)

                By the time JSTL export occurs, the optional attributeName from the defaultSort
                tag will have been converted into the first contained sortAttribute

                See LookupMapBuilder.java
     * @throws IllegalArgumentException if the given defaultSort is blank
     */
    public void setDefaultSort(SortDefinition defaultSort) {
        if (defaultSort == null) {
            throw new IllegalArgumentException("invalid (null) defaultSort");
        }
        this.defaultSort = defaultSort;
    }

    /**
     * @return List of attributeNames of all lookupField FieldDefinitions associated with this LookupDefinition, in the order in
     *         which they were added
     */
    public List getLookupFieldNames() {
        List fieldNames = new ArrayList();
        fieldNames.addAll(this.lookupFieldMap.keySet());

        return fieldNames;
    }

    /**
     * @return Collection of all lookupField FieldDefinitions associated with this LookupDefinition, in the order in which they were
     *         added
     */
    public List<FieldDefinition> getLookupFields() {
        return lookupFields;
    }

    /**
     * @return FieldDefinition associated with the named lookup field, or null if there is none
     * @param fieldName
     */
    public FieldDefinition getLookupField(String attributeName) {
        return lookupFieldMap.get(attributeName);
    }

    /**
     * @return List of attributeNames of all resultField FieldDefinitions associated with this LookupDefinition, in the order in
     *         which they were added
     */
    public List<String> getResultFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(resultFieldMap.keySet());

        return fieldNames;
    }

    /**
     * @return Collection of all resultField FieldDefinitions associated with this LookupDefinition, in the order in which they were
     *         added
     */
    public List<FieldDefinition> getResultFields() {
        return resultFields;
    }


    /**
     * @return FieldDefinition associated with the named result field, or null if there is none
     * @param fieldName
     */
    public FieldDefinition getResultField(String attributeName) {
        return resultFieldMap.get(attributeName);
    }

    /**
        The resultSetLimit element specifies the maximum number of records that will be listed
        as a result of the lookup search.
     */
    public void setResultSetLimit(Integer resultSetLimit) {
        this.resultSetLimit = resultSetLimit;
    }

    /**
     * @return true if this instance has instructions
     */
    public boolean hasResultSetLimit() {
        return (resultSetLimit != null);
    }


    /**
                The resultSetLimit element specifies the maximum number of records that will be listed
                as a result of the lookup search.
     */
    public Integer getResultSetLimit() {
        return resultSetLimit;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (hasDefaultSort()) {
            defaultSort.completeValidation(rootBusinessObjectClass, null);
        }

        for ( FieldDefinition lookupField : lookupFields ) {
            lookupField.completeValidation(rootBusinessObjectClass, null);
        }

        for ( FieldDefinition resultField : resultFields ) {
            resultField.completeValidation(rootBusinessObjectClass, null);
        }
    }

    /**
     * @return true if this instance has extraButtonSource
     */
    public boolean hasExtraButtonSource() {
        return extraButtonSource != null;
    }

    /**
     * @return extraButtonSource
     */
    public String getExtraButtonSource() {
        return extraButtonSource;
    }

    /**
                The extraButton element is used to define additional buttons which will
                appear on the lookup screen next to the Search and Clear buttons.
                You can define the image source and additional html parameters for
                each button.

               The extraButtonSource element defines the location of an image file
               to use for the extra button.

                
     * @throws IllegalArgumentException if the given source is blank
     */
    public void setExtraButtonSource(String extraButtonSource) {
        if (StringUtils.isBlank(extraButtonSource)) {
            throw new IllegalArgumentException("invalid (blank) button source");
        }
        this.extraButtonSource = extraButtonSource;
    }

    /**
     * @return true if this instance has extraButtonParams
     */
    public boolean hasExtraButtonParams() {
        return extraButtonParams != null;
    }

    /**
     * @return extraButtonParams
     */
    public String getExtraButtonParams() {
        return extraButtonParams;
    }

    /**
                The extraButton element is used to define additional buttons which will
                appear on the lookup screen next to the Search and Clear buttons.
                You can define the image source and additional html parameters for
                each button.
                
                The extraButtonParams contains extra HTML parameters that be associated
                with the button.

     */
    public void setExtraButtonParams(String extraButtonParams) {
        this.extraButtonParams = extraButtonParams;
    }

    public String toString() {
        return "LookupDefinition '" + getTitle() + "'";
    }

    /**
                The lookupFields element defines the set of fields in which the user
                can enter values representing search selection criteria.  A search result
                record will be returned only if the criteria entered in all the
                lookup fields are met.

                DD:  See LookupDefinition.java

                JSTL: lookupFields is a Map which is accessed using a key of "lookupFields".
                This map contains the following keys:
                    * attributeName of first lookup field
                    * attributeName of second lookup field
                    etc.
                The corresponding values are lookupField Export Maps.
                See LookupMapBuilder.java.

                    The lookupField element defines one lookup search
                    criterion field.
                    DD: See LookupDefinition.java.

                    JSTL: lookupField is a Map which is accessed by a key
                    which is the attributeName of a lookup field.  This map contains
                    entries with the following keys:
                    * "attributeName" (String)
                    * "required" (boolean String)

                lookupField attribute definitions:

                    * required = true means that the user must enter something
                        into the search criterion lookup field
                    * forceLookup = this attribute is not used
                    * noLookup = true means that field should not include magnifying glass (i.e. quickfinder)

     */
    public void setLookupFields(List<FieldDefinition> lookupFields) {
        lookupFieldMap.clear();
        for ( FieldDefinition lookupField : lookupFields ) {
            if (lookupField == null) {
                throw new IllegalArgumentException("invalid (null) lookupField");
            }
            String keyName = lookupField.getAttributeName();
            if (lookupFieldMap.containsKey(keyName)) {
                throw new DuplicateEntryException("duplicate lookupField entry for attribute '" + keyName + "'");
            }

            lookupFieldMap.put(keyName, lookupField);
        }
        this.lookupFields = lookupFields;
    }

    /**
                The resultFields element specifies the list of fields that are shown as a result
                of the lookup search.

                JSTL: resultFields is a Map which is accesseed by a key of "resultFields".
                This map contains entries with the following keys:
                    * attributeName of first result field
                    * attributeName of second result field
                    etc.
                The corresponding values are ExportMap's

                The ExportMaps are accessed using a key of attributeName.
                Each ExportMap contains a single entry as follows:
                    * "attributeName"
                The corresponding value is the attributeName of the field.

                See LookupMapBuilder.java.
     */
    public void setResultFields(List<FieldDefinition> resultFields) {
        resultFieldMap.clear();
        for ( FieldDefinition resultField : resultFields ) {
            if (resultField == null) {
                throw new IllegalArgumentException("invalid (null) resultField");
            }
    
            String keyName = resultField.getAttributeName();
            if (resultFieldMap.containsKey(keyName)) {
                throw new DuplicateEntryException("duplicate resultField entry for attribute '" + keyName + "'");
            }
    
            resultFieldMap.put(keyName, resultField);
        }
        this.resultFields = resultFields;
    }
}