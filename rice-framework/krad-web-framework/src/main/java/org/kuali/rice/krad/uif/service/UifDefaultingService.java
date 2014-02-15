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
package org.kuali.rice.krad.uif.service;

import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.lookup.LookupView;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.view.InquiryView;

/**
 * This service helps build/define default controls for the UIF based on the associated data-level metadata.
 *
 * It will use the information provided by the krad-data module to attempt to build sensible
 * default controls based on the data type, maximum length, and other attributes available
 * in the ORM-level metadata or provided as annotations on the data object classes.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface UifDefaultingService {

    /**
     * Derives a UIF control from the information available on the passed in Data Dictionary {@link AttributeDefinition}.
     *
     * Attempts to build reasonable defaults based on the data type and other metadata which has
     * been included.
     *
     * If no special information is found in the metadata, it will return a standard/default text control.
     * @param attrDef attribute definition
     * @return derived control
     */
    Control deriveControlAttributeFromMetadata( AttributeDefinition attrDef );

    /**
     * Helper method to allow reasonable names to be defaulted from class or property names.
     *
     * This method assumes that the passed in name is camel-cased and will create the
     * name by adding spaces before each capital letter or digit as well as capitalizing each word.
     *
     * In the case that the name given is a nested property, only the portion of the
     * name after the last period will be used.
     * @param camelCasedName property name, in camel case
     * @return human friendly property name
     */
    String deriveHumanFriendlyNameFromPropertyName(String camelCasedName);

    /**
     * Derives a default valid characters constraint definition given the metadata in the {@link AttributeDefinition}
     * @param attrDef attribute definition
     *
     * @return A {@link ValidCharactersConstraint} object or null if no information in the {@link AttributeDefinition} suggests an appropriate default.
     */
    ValidCharactersConstraint deriveValidCharactersConstraint( AttributeDefinition attrDef );

    /**
     * Build an instance of an {@link InquiryView} for the given data object entry.
     * Information will be pulled from the {@link DataObjectEntry} and the embedded
     * {@link DataObjectMetadata} and {@link DataObjectAttribute} instances as needed.
     *
     * In the present implementation, all non-hidden properties on the DataObjectEntry
     * will be added to the inquiry.  Additionally, any collections on the object will be
     * displayed in their own sections.
     * @param dataObjectEntry data object entry
     * @return inquiry view based on the data object
     */
    InquiryView deriveInquiryViewFromMetadata( DataObjectEntry dataObjectEntry );

    /**
     * Build an instance of an {@link LookupView} for the given data object entry.
     * Information will be pulled from the {@link DataObjectEntry} and the embedded
     * {@link DataObjectMetadata} and {@link DataObjectAttribute} instances as needed.
     *
     * In the present implementation, all non-hidden properties on the DataObjectEntry
     * will be added to the lookup search criteria and results.
     * @param dataObjectEntry data object entry
     * @return lookup view based on the data object
     */
    LookupView deriveLookupViewFromMetadata( DataObjectEntry dataObjectEntry );
}
