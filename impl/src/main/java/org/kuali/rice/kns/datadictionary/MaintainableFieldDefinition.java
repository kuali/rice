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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;

/**
    The maintainableField element defines the specifications
    for one data field.
    JSTL: maintainableField is a Map accessed by the field name.
    It contains entries with the following keys:
        * field (boolean String)
        * name (String)
        * required (boolean String)

    * name is the name of the field
    * required is true if the field must contain a non-null value
    * readOnly is true if it cannot be updated
    * template documentation from MaintenanceUtils.java:
        Field templates are used in relation to multiple value lookups.
        When doing a MV lookup on a collection, the returned BOs
        are not necessarily of the same type as the elements of the
        collection. Therefore, a means of mapping between the fields
        for the 2 BOs are necessary. The template attribute of
        <maintainableField> contained within <maintainableCollection>
        tells us this mapping.
        Example:
        <maintainableField name="collectionAttrib" template="lookupBOAttrib">
        means that when a list of BOs are returned, the lookupBOAttrib value
        of the looked up BO will be placed into the collectionAttrib
        value of the BO added to the collection
    * webUILeaveFieldFunction is the name of a javascript function to called when
        when the user tabs out of the field.
    * webUILeaveFieldCallbackFunction
        This is the call javascript function related to the webUILeaveFieldFunction.
    * readOnlyAfterAdd
        This is used to indicate that the field is read-only after the record has been
        initially created.
 */
public class MaintainableFieldDefinition extends MaintainableItemDefinition implements FieldDefinitionI{

    protected boolean required = false;
    protected boolean readOnly = false;
    protected boolean readOnlyAfterAdd = false; 

    protected String defaultValue;
    protected String template;
    protected Class<? extends ValueFinder> defaultValueFinderClass;

    protected String displayEditMode;
    protected Mask displayMask;

    protected String webUILeaveFieldFunction = "";
    protected String webUILeaveFieldCallbackFunction = "";
    
    protected Class<? extends BusinessObject> overrideLookupClass;
    protected String overrideFieldConversions;
    
    public MaintainableFieldDefinition() {}

    /**
     * @return true if this attribute is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
required is true if the field must contain a non-null value
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
     * 
                       The defaultValue element will pre-load the specified value
                       into the lookup field.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * @return custom defaultValue class
     */
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return defaultValueFinderClass;
    }

    /**
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * readOnly is true if it cannot be updated
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }


    /**
     * Gets the displayEditMode attribute.
     * 
     * @return Returns the displayEditMode.
     */
    public String getDisplayEditMode() {
        return displayEditMode;
    }


    /**
     * The document authorizer classes have a method getEditMode, which is a map of edit mode to
                        value mappings.  Depending on the context, the value of the mapping may be relevant, and the logic determining
                        whether the value is relevant is often implemented in the JSP/tag layer.

                        Fields on a document (particularily maintenance documents) may be associated with
                        an edit mode.  If the edit mode is mapped to a relevant value, then the all fields associated with the edit mode
                        will be rendered unhidden.

                        The displayEditMode element is used to specify the edit mode that will be associated with the field.
                        If the document authorizer returns a map with this edit mode mapped to a proper value, then the field will be unhidden to the user.
                    
     */
    public void setDisplayEditMode(String displayEditMode) {
        this.displayEditMode = displayEditMode;
    }


    /**
     * Gets the displayMask attribute.
     * 
     * @return Returns the displayMask.
     */
    public Mask getDisplayMask() {
        return displayMask;
    }


    /**
     * The displayMask element specifies the type of masking to
                    be used to hide the value from un-authorized users.
                    There are three types of masking.
     */
    public void setDisplayMask(Mask displayMask) {
        this.displayMask = displayMask;
    }
    
    /**
     * Gets the overrideFieldConversions attribute. 
     * @return Returns the overrideFieldConversions.
     */
    public String getOverrideFieldConversions() {
        return overrideFieldConversions;
    }


    /**
     * Single value lookups expect field conversions to be passed in as a HTTP parameter when the lookups is invoked from a quickfinder icon (i.e. magnifying glass on page).
                        Field conversions are normally used to determine which fields will be returned when the "return value" link is clicked.

                        For example, if we're performing a quickfinder lookup and the field conversion string "a:document.someObject.a1,b:document.someObject.b1" is passed into the lookup,
                        this means that when we click on a lookup result row to be returned:

                        * the value of property "a" from the selected result bo will be passed as the value of the HTTP parameter named "document.someObject.a1",
                          which, in turn, populates the POJO property of the same name on the form
                        * the value of property "b" from the selected result bo will be passed as the value of the HTTP parameter named "document.someObject.b1",
                          which, in turn, populates the POJO property of the same name on the form

                        Normally, the field conversion string is automatically computed by the framework to return all of the primary key values of the looked up BO into the corresponding
                        foreign key values of the destination BO (i.e. document.someObject in the example above).  However, putting in this element will allow for the overriding of the
                        field conversions string.
     */
    public void setOverrideFieldConversions(String overrideFieldConversions) {
        this.overrideFieldConversions = overrideFieldConversions;
    }


    /**
     * Gets the overrideLookupClass attribute. 
     * @return Returns the overrideLookupClass.
     */
    public Class<? extends BusinessObject> getOverrideLookupClass() {
        return overrideLookupClass;
    }




    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getName())) {
            throw new AttributeValidationException("unable to find attribute or collection named '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + getName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }

        if (StringUtils.isNotBlank(displayEditMode) && displayMask == null) {
            throw new AttributeValidationException("property '" + getName() + "' has a display edit mode defined but not a valid display mask '" + "' (" + "" + ")");
        }

        if (displayMask != null) {
            if (getDisplayMask().getMaskFormatter() == null && getDisplayMask().getMaskFormatterClass() == null) {
                throw new AttributeValidationException("No mask formatter or formatter class specified for secure attribute " + getName() + "' (" + "" + ")");
            }
        }
        
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintainableFieldDefinition for field " + getName();
    }


    public String getTemplate() {
        return template;
    }


    /**
template documentation from MaintenanceUtils.java:
                            Field templates are used in relation to multiple value lookups.
                            When doing a MV lookup on a collection, the returned BOs
                            are not necessarily of the same type as the elements of the
                            collection. Therefore, a means of mapping between the fields
                            for the 2 BOs are necessary. The template attribute of
                            <maintainableField> contained within <maintainableCollection>
                            tells us this mapping.
                            Example:
                            <maintainableField name="collectionAttrib" template="lookupBOAttrib">
                            means that when a list of BOs are returned, the lookupBOAttrib value
                            of the looked up BO will be placed into the collectionAttrib
                            value of the BO added to the collection
 */
    public void setTemplate(String template) {
        this.template = template;
    }


    public String getWebUILeaveFieldCallbackFunction() {
        return webUILeaveFieldCallbackFunction;
    }


    /**
                        * webUILeaveFieldCallbackFunction
                            This is the call javascript function related to the webUILeaveFieldFunction.
     */
    public void setWebUILeaveFieldCallbackFunction(String webUILeaveFieldCallbackFunction) {
        this.webUILeaveFieldCallbackFunction = webUILeaveFieldCallbackFunction;
    }


    public String getWebUILeaveFieldFunction() {
        return webUILeaveFieldFunction;
    }


    /**
                        * webUILeaveFieldFunction is the name of a javascript function to called when
                            when the user tabs out of the field.
     */
    public void setWebUILeaveFieldFunction(String webUILeaveFieldFunction) {
        this.webUILeaveFieldFunction = webUILeaveFieldFunction;
    }


    public boolean isReadOnlyAfterAdd() {
        return readOnlyAfterAdd;
    }


    /**
     * This is used to indicate that the field is read-only after the record has been
                            initially created.
     */
    public void setReadOnlyAfterAdd(boolean readOnlyAfterAdd) {
        this.readOnlyAfterAdd = readOnlyAfterAdd;
    }


    /**
The defaultValueFinderClass specifies the java class that will be
                      used to determine the default value of a lookup field.  The classname
                      specified in this field must implement org.kuali.rice.kns.lookup.valueFinder.ValueFinder
   */
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        this.defaultValueFinderClass = defaultValueFinderClass;
    }


    /**
     * The overrideLookupClass element is used to indicate the
                        class that should be used for the magnifying glass lookup.
                        The specified class must be a subclass of the business object
                        class.
     */
    public void setOverrideLookupClass(Class<? extends BusinessObject> overrideLookupClass) {
        this.overrideLookupClass = overrideLookupClass;
    }
    
    
}