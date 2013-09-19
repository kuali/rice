/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UserControl;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.service.UifDefaultingService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.InquiryView;

public class UifDefaultingServiceImpl implements UifDefaultingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifDefaultingServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected MetadataRepository metadataRepository;

    protected static final String ANY_CHARACTER_PATTERN_CONSTRAINT = "UTF8AnyCharacterPatternConstraint";
    protected static final String DATE_PATTERN_CONSTRAINT = "BasicDatePatternConstraint";
    protected static final String FLOATING_POINT_PATTERN_CONSTRAINT = "FloatingPointPatternConstraintTemplate";
    protected static final String TIMESTAMP_PATTERN_CONSTRAINT = "TimestampPatternConstraint";

    @Override
    public String deriveHumanFriendlyNameFromPropertyName(String camelCasedName) {
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

    @Override
    public Control deriveControlAttributeFromMetadata( AttributeDefinition attrDef ) {
        Control c = null;
        // First, check if a values finder has been established
        // If so, use a drop-down list
        if ( attrDef.getOptionsFinder() != null ) {
            c = ComponentFactory.getSelectControl();
        // FIXME: JHK: Yes, I know this is a *HORRIBLE* hack - but the alternative
        // would look even more "hacky" and error-prone
        } else if ( attrDef.getName().endsWith( ".principalName" ) && attrDef.getDataObjectAttribute() != null ) {
            c = ComponentFactory.getUserControl();
            // Need to find the relationship information
            // get the relationship ID by removing .principalName from the attribute name
            String relationshipName = StringUtils.removeEnd(attrDef.getName(), ".principalName");
            DataObjectMetadata metadata = metadataRepository.getMetadata(attrDef.getDataObjectAttribute().getOwningType());
            if ( metadata != null ) {
                DataObjectRelationship relationship = metadata.getRelationship(relationshipName);
                if ( relationship != null ) {
                    ((UserControl)c).setPrincipalIdPropertyName(relationship.getAttributeRelationships().get(0).getParentAttributeName());
                    ((UserControl)c).setPersonObjectPropertyName(relationshipName);
                }
            } else {
                LOG.warn( "Attempt to pull relationship name: " + relationshipName + " resulted in missing metadata when looking for: " + attrDef.getDataObjectAttribute().getOwningType() );
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
        // If we a have a control...we should - but just in case - don't want to be too dependent on assumptions of the above code
        if ( c != null ) {
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
        return c;
    }

    @Override
    public ValidCharactersConstraint deriveValidCharactersConstraint(AttributeDefinition attrDef) {
        ValidCharactersConstraint validCharactersConstraint = null;
        // First - see if one was defined in the metadata (provided by krad-data module annotations)
        if ( attrDef.getDataObjectAttribute() != null ) {
            if ( StringUtils.isNotBlank( attrDef.getDataObjectAttribute().getValidCharactersConstraintBeanName() ) ) {
                Object consObj = dataDictionaryService.getDictionaryObject(attrDef.getDataObjectAttribute().getValidCharactersConstraintBeanName());
                if ( consObj != null && consObj instanceof ValidCharactersConstraint ) {
                    validCharactersConstraint  = (ValidCharactersConstraint) consObj;
                }
            }
        }
        // if not, make an intelligent guess from the data type
        if ( validCharactersConstraint == null ) {
            if ( attrDef.getDataType() != null ) {
                if ( attrDef.getDataType().isNumeric() ) {
                    validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService.getDictionaryObject(FLOATING_POINT_PATTERN_CONSTRAINT);
                } else if ( attrDef.getDataType().isTemporal() ) {
                    if ( attrDef.getDataType() == DataType.DATE ) {
                        validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService.getDictionaryObject(DATE_PATTERN_CONSTRAINT);
                    } else if ( attrDef.getDataType() == DataType.TIMESTAMP ) {
                        validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService.getDictionaryObject(TIMESTAMP_PATTERN_CONSTRAINT);
                    }
                }
            }
        }
        // default to UTF8
        if ( validCharactersConstraint == null ) {
            validCharactersConstraint = (ValidCharactersConstraint) dataDictionaryService.getDictionaryObject(ANY_CHARACTER_PATTERN_CONSTRAINT);
        }

        return validCharactersConstraint;
    }

    protected Group createInquirySection( String groupId, String headerText ) {
        Group group = ComponentFactory.getGroupWithDisclosureGridLayout();
        group.setId(groupId);
        group.setHeaderText(headerText);
        return group;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.krad.uif.service.UifDefaultingService#deriveInquiryViewFromMetadata(org.kuali.rice.krad.datadictionary.DataObjectEntry)
     */
    @Override
    public InquiryView deriveInquiryViewFromMetadata(DataObjectEntry dataObjectEntry) {
        // Create the main view object and set the title and BO class
        InquiryView view = ComponentFactory.getInquiryView();
        view.setHeaderText(dataObjectEntry.getObjectLabel());
        view.setDataObjectClassName(dataObjectEntry.getDataObjectClass());

        // Set up data structures to manage the creation of sections
        Map<String,Group> inquirySectionsById = new HashMap<String,Group>();
        Group currentGroup = createInquirySection("default",dataObjectEntry.getObjectLabel());
        inquirySectionsById.put(currentGroup.getId(), currentGroup);
        ((List<Group>)view.getItems()).add(currentGroup);

        // Loop over the attributes on the data object, adding them into the inquiry
        // TODO: If we have an @Section notation, switch to the section, creating if the ID is unknown
        List<Component> items = (List<Component>) currentGroup.getItems(); // needed to deal with generics issue
        for ( AttributeDefinition attr : dataObjectEntry.getAttributes() ) {
            DataField dataField = ComponentFactory.getDataField();
            dataField.setPropertyName(attr.getName());
            items.add(dataField);
        }
        // TODO: if there are updatable reference objects, include sections for them

        // TODO: If there are collections on the object, include sections for them

        return view;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }
}
