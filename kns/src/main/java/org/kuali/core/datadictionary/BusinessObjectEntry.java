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

package org.kuali.core.datadictionary;

import org.kuali.core.bo.BusinessObject;

/**
 * A single BusinessObject entry in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a BusinessObject and its attributes.
 * 
 * 
    DD: See BusinessObjectEntry.java

    JSTL: each businessObject is exposed as a Map which is accessed
    using a key of the business object class name.
    This map contains enties with the following keys

        * businessObjectClass (String)
        * inquiry (Map, optional)
        * lookup (Map, optional)
        * attributes (Map)
        * collections (Map, optional)
        * relationships (Map, optional)
        * objectLabel (String, optional)
        * objectDescription (String, optional)

    See BusinessObjectEntryMapper.java

    Note: the use of extraButton in the <businessObject> tag is deprecated, and may be removed in future versions of the data dictionary.

 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class BusinessObjectEntry extends DataDictionaryEntryBase {
    // logger
    //private static Log LOG = LogFactory.getLog(BusinessObjectEntry.class);

    protected Class<? extends BusinessObject> businessObjectClass;

    protected boolean boNotesEnabled = false;
    
    protected InquiryDefinition inquiryDefinition;
    protected LookupDefinition lookupDefinition;
    protected HelpDefinition helpDefinition;

    protected String titleAttribute;
    protected String objectLabel;
    protected String objectDescription;


    public BusinessObjectEntry() {}

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    public String getJstlKey() {
        if (businessObjectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: businessObjectClass is null");
        }

        return businessObjectClass.getSimpleName();
    }


    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    public boolean isBoNotesEnabled() {
        return boNotesEnabled;
    }

    /**
     *            boNotesEnabled = true or false
           * true indicates that notes and attachments will be permanently
             associated with the business object
           * false indicates that notes and attachments are associated
             with the document used to create or edit the business object.
     */
    public void setBoNotesEnabled(boolean boNotesEnabled) {
        this.boNotesEnabled = boNotesEnabled;
    }

    /**
     * @return true if this instance has an inquiryDefinition
     */
    public boolean hasInquiryDefinition() {
        return (inquiryDefinition != null);
    }

    /**
     * @return current inquiryDefinition for this BusinessObjectEntry, or null if there is none
     */
    public InquiryDefinition getInquiryDefinition() {
        return inquiryDefinition;
    }

    /**
           The inquiry element is used to specify the fields that will be displayed on the
            inquiry screen for this business object and the order in which they will appear.

            DD: See InquiryDefinition.java

            JSTL: The inquiry element is a Map which is accessed using
            a key of "inquiry".  This map contains the following keys:
                * title (String)
                * inquiryFields (Map)

            See InquiryMapBuilder.java
     */
    public void setInquiryDefinition(InquiryDefinition inquiryDefinition) {
        this.inquiryDefinition = inquiryDefinition;
    }

    /**
     * @return true if this instance has a lookupDefinition
     */
    public boolean hasLookupDefinition() {
        return (lookupDefinition != null);
    }

    /**
     * @return current lookupDefinition for this BusinessObjectEntry, or null if there is none
     */
    public LookupDefinition getLookupDefinition() {
        return lookupDefinition;
    }

    /**
            The lookup element is used to specify the rules for "looking up"
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
     */
    public void setLookupDefinition(LookupDefinition lookupDefinition) {
        this.lookupDefinition = lookupDefinition;
    }

    /**
     * @return Returns the titleAttribute.
     */
    public String getTitleAttribute() {
        return titleAttribute;
    }


    /**
           The titleAttribute element is the name of the attribute that
            will be used as an inquiry field when the lookup search results
            fields are displayed.

            For some business objects, there is no obvious field to serve
            as the inquiry field. in that case a special field may be required
            for inquiry purposes.
     */
    public void setTitleAttribute(String titleAttribute) {
        this.titleAttribute = titleAttribute;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    public void completeValidation() {
        super.completeValidation();

        if (hasInquiryDefinition()) {
            inquiryDefinition.completeValidation(businessObjectClass, null);
        }

        if (hasLookupDefinition()) {
            lookupDefinition.completeValidation(businessObjectClass, null);
        }

    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    public Class getEntryClass() {
        return businessObjectClass;
    }


    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    public String getFullClassName() {
        return businessObjectClass.getName();
    }

    /**
     * @return Returns the objectLabel.
     */
    public String getObjectLabel() {
        return objectLabel;
    }

    /**
           The objectLabel provides a short name of the business
           object for use on help screens.
     * 
     * @param objectLabel The objectLabel to set.
     */
    public void setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
    }

    /**
     * @return Returns the description.
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
           The objectDescription provides a brief description
           of the business object for use on help screens.
     * 
     * @param description The description to set.
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    /**
     * Gets the helpDefinition attribute.
     * 
     * @return Returns the helpDefinition.
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Sets the helpDefinition attribute value.
     * 
           The objectHelp element provides the keys to
           obtain a help description from the system parameters table.

           parameterNamespace the namespace of the parameter containing help information
           parameterName the name of the parameter containing help information
           parameterDetailType the detail type of the parameter containing help information
     * 
     * @param helpDefinition The helpDefinition to set.
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "BusinessObjectEntry for " + getBusinessObjectClass();
    }


}