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
package org.kuali.rice.krad.datadictionary;

import java.util.List;

import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * Generic dictionary entry for an object that does not have to implement BusinessObject. It provides support
 * for general objects
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "dataObjectEntry")
public class DataObjectEntry extends DataDictionaryEntryBase implements MustOccurConstrainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataObjectEntry.class);
    private static final long serialVersionUID = 1L;

    protected String name;
    protected Class<?> dataObjectClass;

    protected String titleAttribute;
    protected String objectLabel;
    protected String objectDescription;

    protected List<String> primaryKeys;
    protected Class<? extends Exporter> exporterClass;

    protected List<MustOccurConstraint> mustOccurConstraints;

    protected List<String> groupByAttributesForEffectiveDating;

    protected HelpDefinition helpDefinition;

    protected boolean boNotesEnabled = false;

    protected List<InactivationBlockingDefinition> inactivationBlockingDefinitions;

    @Override
    public void completeValidation() {
        completeValidation( new ValidationTrace() );
    }

    /**
     * Directly validate simple fields
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        super.completeValidation(tracer);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    @Override
    public String getJstlKey() {
        if (dataObjectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: dataObjectClass is null");
        }

        return dataObjectClass.getSimpleName();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    @Override
    public String getFullClassName() {
        return dataObjectClass.getName();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    @Override
    public Class<?> getEntryClass() {
        return dataObjectClass;
    }

    /**
     * @return the dataObjectClass
     */
    @BeanTagAttribute
    public Class<?> getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * @param dataObjectClass the dataObjectClass to set
     */
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * @return the name
     */
    @Override
    @BeanTagAttribute
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the objectLabel.
     */
    @BeanTagAttribute
    public String getObjectLabel() {
        // If the object label was set in the DD, use that
        if ( objectLabel != null ) {
            return objectLabel;
        }
        // Otherwise, pull what we can from the metadata model
        if ( getDataObjectMetadata() != null ) {
            return getDataObjectMetadata().getLabel();
        }
        return KRADServiceLocatorWeb.getUifDefaultingService().deriveHumanFriendlyNameFromPropertyName( dataObjectClass.getSimpleName() );
    }

    /**
     * The objectLabel provides a short name of the business
     * object for use on help screens.
     *
     * @param objectLabel The objectLabel to set.
     */
    public void setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
    }

    /**
     * @return Returns the description.
     */
    @BeanTagAttribute
    public String getObjectDescription() {
        if ( objectDescription != null ) {
            return objectDescription;
        }
        if ( getDataObjectMetadata() != null ) {
            return getDataObjectMetadata().getDescription();
        }
        return "";
    }

    /**
     * The objectDescription provides a brief description
     * of the business object for use on help screens.
     *
     * @param objectDescription The description to set
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    /**
     * Gets the helpDefinition attribute.
     *
     * @return Returns the helpDefinition.
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Sets the helpDefinition attribute value.
     *
     * The objectHelp element provides the keys to
     * obtain a help description from the system parameters table.
     *
     * parameterNamespace the namespace of the parameter containing help information
     * parameterName the name of the parameter containing help information
     * parameterDetailType the detail type of the parameter containing help information
     *
     * @param helpDefinition The helpDefinition to set.
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @return the mustOccurConstraints
     */
    @Override
    @BeanTagAttribute
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * @param mustOccurConstraints the mustOccurConstraints to set
     */
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * @return the titleAttribute
     */
    @BeanTagAttribute
    public String getTitleAttribute() {
        if ( titleAttribute != null ) {
            return titleAttribute;
        }
        if ( getDataObjectMetadata() != null ) {
            return getDataObjectMetadata().getPrimaryDisplayAttributeName();
        }
        return null;
    }

    /**
     * The titleAttribute element is the name of the attribute that
     * will be used as an inquiry field when the lookup search results
     * fields are displayed.
     *
     * For some business objects, there is no obvious field to serve
     * as the inquiry field. in that case a special field may be required
     * for inquiry purposes.
     */
    public void setTitleAttribute(String titleAttribute) {
        this.titleAttribute = titleAttribute;
    }

    /**
     * @return the primaryKeys
     */
    @BeanTagAttribute
    public List<String> getPrimaryKeys() {
        if ( primaryKeys != null ) {
            return primaryKeys;
        }
        if ( getDataObjectMetadata() != null ) {
            return getDataObjectMetadata().getPrimaryKeyAttributeNames();
        }
        return null;
    }

    /**
     * @param primaryKeys the primaryKeys to set
     */
    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    @BeanTagAttribute
    public Class<? extends Exporter> getExporterClass() {
        return this.exporterClass;
    }

    public void setExporterClass(Class<? extends Exporter> exporterClass) {
        this.exporterClass = exporterClass;
    }

    /**
     * Provides list of attributes that should be used for grouping
     * when performing effective dating logic in the framework
     *
     * @return List<String> list of attributes to group by
     */
    @BeanTagAttribute
    public List<String> getGroupByAttributesForEffectiveDating() {
        return this.groupByAttributesForEffectiveDating;
    }

    /**
     * Setter for the list of attributes to group by
     *
     * @param groupByAttributesForEffectiveDating
     */
    public void setGroupByAttributesForEffectiveDating(List<String> groupByAttributesForEffectiveDating) {
        this.groupByAttributesForEffectiveDating = groupByAttributesForEffectiveDating;
    }

    /**
     * Gets the boNotesEnabled flag for the Data object
     *
     * <p>
     * true indicates that notes and attachments will be permanently
     * associated with the business object
     * false indicates that notes and attachments are associated
     * with the document used to create or edit the business object.
     * </p>
     *
     * @return the boNotesEnabled flag
     */
    @BeanTagAttribute
    public boolean isBoNotesEnabled() {
        return boNotesEnabled;
    }

    /**
     * Setter for the boNotesEnabled flag
     */
    public void setBoNotesEnabled(boolean boNotesEnabled) {
        this.boNotesEnabled = boNotesEnabled;
    }

    /**
     * Gets the inactivationBlockingDefinitions for the Data object
     *
     * @return the list of <code>InactivationBlockingDefinition</code>
     */
    @BeanTagAttribute
    public List<InactivationBlockingDefinition> getInactivationBlockingDefinitions() {
        return this.inactivationBlockingDefinitions;
    }

    /**
     * Setter for the inactivationBlockingDefinitions
     */
    public void setInactivationBlockingDefinitions(
            List<InactivationBlockingDefinition> inactivationBlockingDefinitions) {
        this.inactivationBlockingDefinitions = inactivationBlockingDefinitions;
    }
}
