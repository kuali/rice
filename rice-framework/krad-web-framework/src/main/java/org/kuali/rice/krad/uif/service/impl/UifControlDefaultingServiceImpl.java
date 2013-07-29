/*
 * Copyright 2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.uif.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UserControl;
import org.kuali.rice.krad.uif.service.UifControlDefaultingService;
import org.kuali.rice.krad.uif.util.ComponentFactory;

public class UifControlDefaultingServiceImpl implements UifControlDefaultingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifControlDefaultingServiceImpl.class);
    
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
            DataObjectMetadata metadata = KradDataServiceLocator.getMetadataRepository().getMetadata(attrDef.getDataObjectAttribute().getOwningType());
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
        if ( c != null ) {
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
                    ((TextControl) c).setMinLength( attrDef.getMinLength() );
                }
            }
            c.setRequired(attrDef.isRequired());
        }
        return c;
        
    }
}
