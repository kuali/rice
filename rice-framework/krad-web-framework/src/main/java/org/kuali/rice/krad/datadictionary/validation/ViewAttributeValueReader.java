/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.datadictionary.validation;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.capability.CaseConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.PrerequisiteConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.SimpleConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.ValidCharactersConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AttributeValueReader which can read the correct values from all InputFields which exist on the View
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewAttributeValueReader extends BaseAttributeValueReader {
    private ViewModel form;

    private List<Constrainable> inputFields = new ArrayList<Constrainable>();
    private Map<String, InputFieldConstrainableInfo> inputFieldMap = new HashMap<String, InputFieldConstrainableInfo>();

    /**
     * Constructor for ViewAttributeValueReader, the View must already be indexed and
     * the InputFields must have already be initialized for this reader to work properly
     *
     * @param form model object representing the View's form data
     */
    public ViewAttributeValueReader(ViewModel form) {
        this.form = form;

        ViewPostMetadata viewPostMetadata = form.getViewPostMetadata();

        // Copying information stored about InputField in the post metadata to info objects for use by this reader
        for (String id : viewPostMetadata.getInputFieldIds()) {
            InputFieldConstrainableInfo info = new InputFieldConstrainableInfo();

            Object label = viewPostMetadata.getComponentPostData(id, UifConstants.PostMetadata.LABEL);
            if (label != null) {
                info.setLabel((String) label);
            }

            Object name = viewPostMetadata.getComponentPostData(id, UifConstants.PostMetadata.PATH);
            if (name != null) {
                info.setName((String) name);
            }

            Object validCharactersConstraint = viewPostMetadata.getComponentPostData(id,
                    UifConstants.PostMetadata.VALID_CHARACTER_CONSTRAINT);
            if (validCharactersConstraint != null) {
                info.setValidCharactersConstraint((ValidCharactersConstraint) validCharactersConstraint);
            }

            Object caseConstraint = viewPostMetadata.getComponentPostData(id,
                    UifConstants.PostMetadata.CASE_CONSTRAINT);
            if (caseConstraint != null) {
                info.setCaseConstraint((CaseConstraint) caseConstraint);
            }

            Object prerequisiteConstraints = viewPostMetadata.getComponentPostData(id,
                    UifConstants.PostMetadata.PREREQ_CONSTSTRAINTS);
            if (prerequisiteConstraints != null) {
                info.setPrerequisiteConstraints((List<PrerequisiteConstraint>) prerequisiteConstraints);
            }

            Object mustOccurConstraints = viewPostMetadata.getComponentPostData(id,
                    UifConstants.PostMetadata.MUST_OCCUR_CONSTRAINTS);
            if (mustOccurConstraints != null) {
                info.setMustOccurConstraints((List<MustOccurConstraint>) mustOccurConstraints);
            }

            Object simpleConstraint = viewPostMetadata.getComponentPostData(id,
                    UifConstants.PostMetadata.SIMPLE_CONSTRAINT);
            if (simpleConstraint != null) {
                info.setSimpleConstraint((SimpleConstraint) simpleConstraint);
            }

            inputFields.add(info);
            inputFieldMap.put(info.getName(), info);
        }
    }

    /**
     * Gets the definition which is an InputField on the View/Page
     */
    @Override
    public Constrainable getDefinition(String attributeName) {
        InputFieldConstrainableInfo field = inputFieldMap.get(attributeName);
        if (field != null) {
            return field;
        } else {
            return null;
        }
    }

    /**
     * Gets all InputFields (which extend Constrainable)
     *
     * @return constrainable input fields
     */
    @Override
    public List<Constrainable> getDefinitions() {
        return inputFields;
    }

    /**
     * Returns the label associated with the InputField which has that AttributeName
     *
     * @param attributeName attribute name
     * @return label associated with the named attribute
     */
    @Override
    public String getLabel(String attributeName) {
        InputFieldConstrainableInfo field = inputFieldMap.get(attributeName);
        if (field != null) {
            return field.getLabel();
        } else {
            return "";
        }
    }

    /**
     * Returns the Form object
     *
     * @return form set in the constructor
     */
    @Override
    public Object getObject() {
        return form;
    }

    /**
     * Not used for this reader, returns null
     *
     * @return null
     */
    @Override
    public Constrainable getEntry() {
        return null;
    }

    /**
     * Returns current attributeName which represents the path
     *
     * @return attributeName set on this reader
     */
    @Override
    public String getPath() {
        return this.attributeName;
    }

    /**
     * Gets the type of value for this AttributeName as represented on the Form
     *
     * @param attributeName
     * @return attribute type
     */
    @Override
    public Class<?> getType(String attributeName) {
        Object fieldValue = ObjectPropertyUtils.getPropertyValue(form, attributeName);
        return fieldValue.getClass();
    }

    /**
     * If the current attribute being evaluated is a field of an addLine return false because it should not
     * be evaluated during Validation.
     *
     * @return false if InputField is part of an addLine for a collection, true otherwise
     */
    @Override
    public boolean isReadable() {
        if (attributeName != null && attributeName.contains(UifPropertyPaths.NEW_COLLECTION_LINES)) {
            return false;
        }
        return true;
    }

    /**
     * Return value of the field for the attributeName currently set on this reader
     *
     * @param <X> return type
     * @return value of the field for the attributeName currently set on this reader
     * @throws AttributeValidationException
     */
    @Override
    public <X> X getValue() throws AttributeValidationException {
        X fieldValue = null;
        if (StringUtils.isNotBlank(this.attributeName)) {
            fieldValue = ObjectPropertyUtils.<X>getPropertyValue(form, this.attributeName);
        }
        return fieldValue;
    }

    /**
     * Return value of the field for the attributeName passed in
     *
     * @param attributeName name (which represents a path) of the value to be retrieved on the Form
     * @param <X> return type
     * @return value of that attributeName represents on the form
     * @throws AttributeValidationException
     */
    @Override
    public <X> X getValue(String attributeName) throws AttributeValidationException {
        X fieldValue = null;
        if (StringUtils.isNotBlank(attributeName)) {
            fieldValue = ObjectPropertyUtils.<X>getPropertyValue(form, this.attributeName);
        }
        return fieldValue;
    }

    /**
     * Cones this AttributeValueReader
     *
     * @return AttributeValueReader
     */
    @Override
    public AttributeValueReader clone() {
        ViewAttributeValueReader clone = new ViewAttributeValueReader(form);
        clone.setAttributeName(this.attributeName);
        return clone;
    }

    /**
     * This is a simple object used to contain information about InputFields that are being evaluated and used by
     * this ViewAttributeValueReader.
     *
     * <p>For full documentation refer to the {@link org.kuali.rice.krad.uif.field.InputField} class.</p>
     */
    public class InputFieldConstrainableInfo implements SimpleConstrainable, CaseConstrainable, PrerequisiteConstrainable, MustOccurConstrainable, ValidCharactersConstrainable {

        private String label;
        private String name;
        private ValidCharactersConstraint validCharactersConstraint;
        private CaseConstraint caseConstraint;
        private List<PrerequisiteConstraint> prerequisiteConstraints;
        private List<MustOccurConstraint> mustOccurConstraints;
        private SimpleConstraint simpleConstraint;

        /**
         * Get the field's label
         *
         * @return the label
         */
        public String getLabel() {
            return label;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getLabel()
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getName()
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ValidCharactersConstraint getValidCharactersConstraint() {
            return validCharactersConstraint;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getValidCharactersConstraint()
         */
        public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
            this.validCharactersConstraint = validCharactersConstraint;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CaseConstraint getCaseConstraint() {
            return caseConstraint;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getCaseConstraint()
         */
        public void setCaseConstraint(CaseConstraint caseConstraint) {
            this.caseConstraint = caseConstraint;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
            return prerequisiteConstraints;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getPrerequisiteConstraints()
         */
        public void setPrerequisiteConstraints(List<PrerequisiteConstraint> prerequisiteConstraints) {
            this.prerequisiteConstraints = prerequisiteConstraints;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<MustOccurConstraint> getMustOccurConstraints() {
            return mustOccurConstraints;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getMustOccurConstraints()
         */
        public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
            this.mustOccurConstraints = mustOccurConstraints;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SimpleConstraint getSimpleConstraint() {
            return simpleConstraint;
        }

        /**
         * @see org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader.InputFieldConstrainableInfo#getSimpleConstraint()
         */
        public void setSimpleConstraint(SimpleConstraint simpleConstraint) {
            this.simpleConstraint = simpleConstraint;
        }
    }

}
