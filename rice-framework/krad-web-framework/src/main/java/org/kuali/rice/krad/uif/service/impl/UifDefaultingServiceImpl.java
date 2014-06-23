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
package org.kuali.rice.krad.uif.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.lookup.LookupInputField;
import org.kuali.rice.krad.lookup.LookupView;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.HiddenControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UserControl;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.service.UifDefaultingService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.InquiryView;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UifDefaultingServiceImpl implements UifDefaultingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifDefaultingServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected DataObjectService dataObjectService;

    protected static final String ANY_CHARACTER_PATTERN_CONSTRAINT = "UTF8AnyCharacterPatternConstraint";
    protected static final String DATE_PATTERN_CONSTRAINT = "BasicDatePatternConstraint";
    protected static final String FLOATING_POINT_PATTERN_CONSTRAINT = "FloatingPointPatternConstraintTemplate";
    protected static final String BIG_DECIMAL_PATTERN_CONSTRAINT = "BigDecimalPatternConstraintTemplate";
    protected static final String TIMESTAMP_PATTERN_CONSTRAINT = "TimestampPatternConstraint";
    protected static final String CURRENCY_PATTERN_CONSTRAINT = "CurrencyPatternConstraint";

    @Override
    public String deriveHumanFriendlyNameFromPropertyName(String camelCasedName) {
        // quick check to make sure there is a property name to modify
        if(StringUtils.isBlank(camelCasedName)) {
            return camelCasedName;
        }

        // We only want to include the component after the last property separator
        if (camelCasedName.contains(".")) {
            camelCasedName = StringUtils.substringAfterLast(camelCasedName, ".");
        }
        StringBuilder label = new StringBuilder(camelCasedName);
        // upper case the 1st letter
        label.replace(0, 1, label.substring(0, 1).toUpperCase());
        // loop through, inserting spaces when cap
        for (int i = 0; i < label.length(); i++) {
            if (Character.isUpperCase(label.charAt(i)) || Character.isDigit(label.charAt(i)) ) {
                label.insert(i, ' ');
                i++;
            }
        }

        return label.toString().trim();
    }


    protected UifDisplayHint getHintOfType( DataObjectAttribute attr, UifDisplayHintType hintType ) {
        if ( attr != null && attr.getDisplayHints() != null ) {
            for ( UifDisplayHint hint : attr.getDisplayHints() ) {
                if ( hint.value().equals(hintType) ) {
                    return hint;
                }
            }
        }
        return null;
    }

    /**
     * Check the {@link UifDisplayHint}s on an attribute, return true if any of them have the
     * given type.
     * @param attr data object attribute
     * @param hintType hint type
     * @return true if the hint type is present on the attribute
     */
    protected boolean hasHintOfType( DataObjectAttribute attr, UifDisplayHintType hintType ) {
        return getHintOfType(attr, hintType) != null;
    }

    protected Control getControlInstance( AttributeDefinition attrDef, DataObjectAttribute dataObjectAttribute ) {
        Control c = null;
        // Check for the hidden hint - if present - then use that control type
        if ( dataObjectAttribute != null && hasHintOfType(dataObjectAttribute, UifDisplayHintType.HIDDEN) ) {
            c = ComponentFactory.getHiddenControl();
        } else if ( attrDef.getOptionsFinder() != null ) {
            // if a values finder has been established, use a radio button group or drop-down list
            if ( dataObjectAttribute != null && hasHintOfType(dataObjectAttribute, UifDisplayHintType.RADIO) ) {
                c = ComponentFactory.getRadioGroupControl();
            } else {
                c = ComponentFactory.getSelectControl();
            }
        } else if ( attrDef.getName().endsWith( ".principalName" ) && dataObjectAttribute != null ) {
            // FIXME: JHK: Yes, I know this is a *HORRIBLE* hack - but the alternative
            // would look even more "hacky" and error-prone
            c = ComponentFactory.getUserControl();
            // Need to find the relationship information
            // get the relationship ID by removing .principalName from the attribute name
            String relationshipName = StringUtils.removeEnd(attrDef.getName(), ".principalName");
            DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(
                    dataObjectAttribute.getOwningType());
            if ( metadata != null ) {
                DataObjectRelationship relationship = metadata.getRelationship(relationshipName);
                if ( relationship != null && CollectionUtils.isNotEmpty(relationship.getAttributeRelationships())) {
                    ((UserControl)c).setPrincipalIdPropertyName(relationship.getAttributeRelationships().get(0).getParentAttributeName());
                    ((UserControl)c).setPersonNamePropertyName(relationshipName + "." + KimConstants.AttributeConstants.NAME);
                    ((UserControl)c).setPersonObjectPropertyName(relationshipName);
                }
            } else {
                LOG.warn( "Attempt to pull relationship name: " + relationshipName + " resulted in missing metadata when looking for: " + dataObjectAttribute.getOwningType() );
            }
        } else {
            switch ( attrDef.getDataType() ) {
                case STRING :
                    // TODO: Determine better way to store the "200" metric below
                    if ( attrDef.getMaxLength() != null && attrDef.getMaxLength().intValue() > 200 ) {
                        c = ComponentFactory.getTextAreaControl();
                    } else {
                        c = ComponentFactory.getTextControl();
                    }
                    break;
                case BOOLEAN:
                    c = ComponentFactory.getCheckboxControl();
                    break;
                case DATE:
                case DATETIME:
                case TRUNCATED_DATE:
                    c = ComponentFactory.getDateControl();
                    break;
                case CURRENCY:
                case DOUBLE:
                case FLOAT:
                case INTEGER:
                case LARGE_INTEGER:
                case LONG:
                case PRECISE_DECIMAL:
                    c = ComponentFactory.getTextControl();
                    break;
                case MARKUP:
                    c = ComponentFactory.getTextAreaControl();
                    break;
                default:
                    c = ComponentFactory.getTextControl();
                    break;
            }
        }
        return c;
    }

    protected void customizeControlInstance( Control c, AttributeDefinition attrDef, DataObjectAttribute dataObjectAttribute ) {
        c.setRequired(attrDef.isRequired());
        if ( c instanceof TextControl ) {
            if ( attrDef.getMaxLength() != null ) {
                ((TextControl) c).setMaxLength( attrDef.getMaxLength() );
                ((TextControl) c).setSize( attrDef.getMaxLength() );
                // If it's a larger field, add the expand icon by default
                if ( attrDef.getMaxLength() > 80 ) { // JHK : yes, this was a mostly arbitrary choice
                    ((TextControl) c).setTextExpand(true);
                }
            }
            if ( attrDef.getMinLength() != null ) {
                ((TextControl) c).setMinLength( attrDef.getMinLength() );
            }
        }
        if ( c instanceof TextAreaControl ) {
            if ( attrDef.getMaxLength() != null ) {
                ((TextAreaControl) c).setMaxLength( attrDef.getMaxLength() );
                ((TextAreaControl) c).setRows(attrDef.getMaxLength()/((TextAreaControl) c).getCols());
            }
            if ( attrDef.getMinLength() != null ) {
                ((TextAreaControl) c).setMinLength( attrDef.getMinLength() );
            }
        }
    }

    @Override
    public Control deriveControlAttributeFromMetadata( AttributeDefinition attrDef ) {
        DataObjectAttribute dataObjectAttribute = attrDef.getDataObjectAttribute();
        Control c = getControlInstance(attrDef, dataObjectAttribute);
        // If we a have a control...we should - but just in case - don't want to be too dependent on assumptions of the above code
        if (c != null) {
            customizeControlInstance(c, attrDef, dataObjectAttribute);
        }
        return c;
    }

    @Override
    public ValidCharactersConstraint deriveValidCharactersConstraint(AttributeDefinition attrDef) {
        ValidCharactersConstraint validCharactersConstraint = null;
        // First - see if one was defined in the metadata (provided by krad-data module annotations)
        if (attrDef.getDataObjectAttribute() != null) {
            if (StringUtils.isNotBlank(attrDef.getDataObjectAttribute().getValidCharactersConstraintBeanName())) {
                Object consObj = dataDictionaryService.getDictionaryBean(attrDef.getDataObjectAttribute()
                        .getValidCharactersConstraintBeanName());
                if (consObj != null && consObj instanceof ValidCharactersConstraint) {
                    validCharactersConstraint = (ValidCharactersConstraint) consObj;
                }
            }
        }
        // if not, make an intelligent guess from the data type
        if (validCharactersConstraint == null) {
            if (attrDef.getDataType() != null) {
                if (attrDef.getDataType() == DataType.CURRENCY) {
                    validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService
                            .getDictionaryBean(CURRENCY_PATTERN_CONSTRAINT);
                }else if (attrDef.getDataType() == DataType.PRECISE_DECIMAL ) {
                    validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService
                            .getDictionaryBean(BIG_DECIMAL_PATTERN_CONSTRAINT);
                } else if (attrDef.getDataType().isNumeric()) {
                    validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService
                            .getDictionaryBean(FLOATING_POINT_PATTERN_CONSTRAINT);
                } else if (attrDef.getDataType().isTemporal()) {
                    if (attrDef.getDataType() == DataType.DATE) {
                        validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService
                                .getDictionaryBean(DATE_PATTERN_CONSTRAINT);
                    } else if (attrDef.getDataType() == DataType.TIMESTAMP) {
                        validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService
                                .getDictionaryBean(TIMESTAMP_PATTERN_CONSTRAINT);
                    }
                }
            }
        }
        // default to UTF8
        if (validCharactersConstraint == null) {
            validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService
                    .getDictionaryBean(ANY_CHARACTER_PATTERN_CONSTRAINT);
        }

        return validCharactersConstraint;
    }

    protected Group createInquirySection( String groupId, String headerText ) {
        Group group = ComponentFactory.getGroupWithDisclosureGridLayout();
        group.setId(groupId);
        group.setHeaderText(headerText);
        group.setItems(new ArrayList<Component>());
        return group;
    }

    protected CollectionGroup createCollectionInquirySection( String groupId, String headerText ) {
        CollectionGroup group = ComponentFactory.getCollectionWithDisclosureGroupTableLayout();
        group.setId(groupId);
        group.setHeaderText(headerText);
        group.setItems(new ArrayList<Component>());
        ((TableLayoutManager)group.getLayoutManager()).setRenderSequenceField(false);
        return group;
    }

    @SuppressWarnings("unchecked")
    protected void addAttributeSectionsToInquiryView( InquiryView view, DataObjectEntry dataObjectEntry ) {
        // Set up data structures to manage the creation of sections
        Map<String,Group> inquirySectionsById = new HashMap<String,Group>();
        Group currentGroup = createInquirySection("default",dataObjectEntry.getObjectLabel());
        inquirySectionsById.put(currentGroup.getId(), currentGroup);
        ((List<Group>)view.getItems()).add(currentGroup);

        // Loop over the attributes on the data object, adding them into the inquiry
        // If we have an @Section notation, switch to the section, creating if the ID is unknown
        List<Component> items = (List<Component>) currentGroup.getItems(); // needed to deal with generics issue
        for ( AttributeDefinition attr : dataObjectEntry.getAttributes() ) {
            boolean dontDisplay = hasHintOfType(attr.getDataObjectAttribute(), UifDisplayHintType.NO_INQUIRY);
            dontDisplay |= (attr.getControlField() instanceof HiddenControl);
            // Check for a section hint
            // Create or retrieve existing section as determined by the ID on the annotation
            UifDisplayHint sectionHint = getHintOfType(attr.getDataObjectAttribute(), UifDisplayHintType.SECTION);
            if ( sectionHint != null ) {
                if ( StringUtils.isNotBlank( sectionHint.id() ) ) {
                    currentGroup = inquirySectionsById.get( sectionHint.id() );
                    if ( currentGroup == null ) {
                        String sectionLabel = sectionHint.label();
                        if ( StringUtils.isBlank(sectionLabel) ) {
                            sectionLabel = deriveHumanFriendlyNameFromPropertyName(sectionHint.id() );
                        }

                        currentGroup = createInquirySection(sectionHint.id(), sectionHint.label());
                        inquirySectionsById.put(currentGroup.getId(), currentGroup);
                        ((List<Group>)view.getItems()).add(currentGroup);
                    }
                } else {
                    LOG.warn( "SECTION UifDisplayHint given without an ID - assuming 'default'" );
                    currentGroup = inquirySectionsById.get("default");
                }
                items = (List<Component>) currentGroup.getItems();
            }

            // This is checked after the section test, since the @Section annotation
            // would be on the FK field
            if ( dontDisplay ) {
                continue;
            }

            DataField dataField = ComponentFactory.getDataField();
            dataField.setPropertyName(attr.getName());
            dataField.setLabel(attr.getLabel());
            items.add(dataField);
        }
    }

    @SuppressWarnings("unchecked")
    protected void addCollectionSectionsToInquiryView( InquiryView view, DataObjectEntry dataObjectEntry ) {
        for ( CollectionDefinition coll : dataObjectEntry.getCollections() ) {
            // Create a new section
            DataObjectEntry collectionEntry = dataDictionaryService.getDataDictionary().getDataObjectEntry(coll.getDataObjectClass());
            // Extract the key fields on the collection which are linked to the parent.
            // When auto-generating the Inquiry Collection table, we want to exclude those.
            Collection<String> collectionFieldsLinkedToParent = new HashSet<String>();

            if ( coll.getDataObjectCollection() != null ) {
                for ( DataObjectAttributeRelationship rel : coll.getDataObjectCollection().getAttributeRelationships() ) {
                    collectionFieldsLinkedToParent.add(rel.getChildAttributeName());
                }
            }

            if ( collectionEntry == null ) {
                LOG.warn( "Unable to find DataObjectEntry for collection class: " + coll.getDataObjectClass());
                continue;
            }

            CollectionGroup section = createCollectionInquirySection(coll.getName(), coll.getLabel());
            try {
                section.setCollectionObjectClass(Class.forName(coll.getDataObjectClass()));
            } catch (ClassNotFoundException e) {
                LOG.warn( "Unable to set class on collection section - class not found: " + coll.getDataObjectClass());
            }

            section.setPropertyName(coll.getName());
            // summary title : collection object label
            // Summary fields : PK fields?
            // add the attributes to the section
            for ( AttributeDefinition attr : collectionEntry.getAttributes() ) {
                boolean dontDisplay = hasHintOfType(attr.getDataObjectAttribute(), UifDisplayHintType.NO_INQUIRY);
                dontDisplay |= (attr.getControlField() instanceof HiddenControl);
                // Auto-exclude fields linked to the parent object
                dontDisplay |= collectionFieldsLinkedToParent.contains( attr.getName() );

                if ( dontDisplay ) {
                    continue;
                }

                DataField dataField = ComponentFactory.getDataField();
                dataField.setPropertyName(attr.getName());
                ((List<Component>)section.getItems()).add(dataField);
            }
            ((List<Group>)view.getItems()).add(section);
        }
    }
    /**
     * @see org.kuali.rice.krad.uif.service.UifDefaultingService#deriveInquiryViewFromMetadata(org.kuali.rice.krad.datadictionary.DataObjectEntry)
     */
    @Override
    public InquiryView deriveInquiryViewFromMetadata(DataObjectEntry dataObjectEntry) {
        // Create the main view object and set the title and BO class
        InquiryView view = ComponentFactory.getInquiryView();
        view.setHeaderText(dataObjectEntry.getObjectLabel());
        view.setDataObjectClassName(dataObjectEntry.getDataObjectClass());

        addAttributeSectionsToInquiryView(view, dataObjectEntry);

        // TODO: if there are updatable reference objects, include sections for them

        // If there are collections on the object, include sections for them
        addCollectionSectionsToInquiryView(view, dataObjectEntry);

        return view;
    }

    protected void addAttributesToLookupCriteria( LookupView view, DataObjectEntry dataObjectEntry ) {
        AttributeDefinition activeAttribute = null;
        for ( AttributeDefinition attr : dataObjectEntry.getAttributes() ) {
            // Check if we have been told not to display this attribute here
            boolean dontDisplay = hasHintOfType(attr.getDataObjectAttribute(), UifDisplayHintType.NO_LOOKUP_CRITERIA);
            dontDisplay |= (attr.getControlField() instanceof HiddenControl);

            if ( dontDisplay ) {
                continue;
            }
            if ( attr.getName().equals( KRADPropertyConstants.ACTIVE ) ) {
                activeAttribute = attr;
                continue; // leave until the end of the lookup criteria
            }
            LookupInputField field = ComponentFactory.getLookupCriteriaInputField();
            field.setPropertyName(attr.getName());
            field.setLabel(attr.getLabel());
            view.getCriteriaFields().add(field);
        }
        // If there was one, add the active attribute at the end
        if ( activeAttribute != null ) {
            LookupInputField field = ComponentFactory.getLookupCriteriaInputField();
            field.setPropertyName(activeAttribute.getName());
            field.setLabel(activeAttribute.getLabel());
            view.getCriteriaFields().add(field);
        }
    }

    protected void addAttributesToLookupResults( LookupView view, DataObjectEntry dataObjectEntry ) {
        AttributeDefinition activeAttribute = null;
        for ( AttributeDefinition attr : dataObjectEntry.getAttributes() ) {
            // Check if we have been told not to display this attribute here
            boolean dontDisplay = hasHintOfType(attr.getDataObjectAttribute(), UifDisplayHintType.NO_LOOKUP_RESULT);
            dontDisplay |= (attr.getControlField() instanceof HiddenControl);
            if ( dontDisplay ) {
                continue;
            }
            if ( attr.getName().equals( KRADPropertyConstants.ACTIVE ) ) {
                activeAttribute = attr;
                continue; // leave until the end of the lookup results
            }
            DataField field = ComponentFactory.getDataField();
            field.setPropertyName(attr.getName());
            view.getResultFields().add(field);
        }
        // If there was one, add the active attribute at the end
        if ( activeAttribute != null ) {
            DataField field = ComponentFactory.getDataField();
            field.setPropertyName(activeAttribute.getName());
            view.getResultFields().add(field);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.service.UifDefaultingService#deriveLookupViewFromMetadata(org.kuali.rice.krad.datadictionary.DataObjectEntry)
     */
    @Override
    public LookupView deriveLookupViewFromMetadata(DataObjectEntry dataObjectEntry) {
        LookupView view = ComponentFactory.getLookupView();
        view.setHeaderText(dataObjectEntry.getObjectLabel() + " Lookup");
        view.setDataObjectClass(dataObjectEntry.getDataObjectClass());
        view.setCriteriaFields(new ArrayList<Component>());
        view.setResultFields(new ArrayList<Component>());
        view.setDefaultSortAttributeNames(dataObjectEntry.getPrimaryKeys());

        addAttributesToLookupCriteria(view, dataObjectEntry);
        addAttributesToLookupResults(view, dataObjectEntry);

        return view;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
