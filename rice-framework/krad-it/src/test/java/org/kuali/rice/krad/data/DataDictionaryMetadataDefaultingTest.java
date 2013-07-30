/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.data;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.TestDictionaryConfig;
import org.kuali.rice.krad.uif.control.TextControl;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@TestDictionaryConfig(namespaceCode="KR-NS",dataDictionaryFiles="classpath:org/kuali/rice/krad/test/datadictionary/TestDataObject.xml")
public class DataDictionaryMetadataDefaultingTest extends KRADTestCase {

    protected static final String MAIN_DATA_OBJECT_FOR_TESTING = "org.kuali.rice.krad.data.jpa.testbo.TestDataObject";

    @Override
    @Before
    public void setUp() throws Exception {
        setLogLevel("org.kuali.rice.krad.data.jpa.eclipselink.EclipseLinkJpaMetadataProviderImpl", Level.DEBUG);
        setLogLevel("org.kuali.rice.krad.data.provider.jpa.JpaMetadataProviderImpl", Level.DEBUG);
        setLogLevel("org.kuali.rice.krad.data.provider.SpringMetadataProviderImpl", Level.DEBUG);
        setLogLevel("org.kuali.rice.krad.data.provider.CompositeMetadataProviderImpl", Level.DEBUG);

        setLogLevel("org.kuali.rice.krad.datadictionary.AttributeDefinition", Level.DEBUG);
        setLogLevel("org.kuali.rice.krad.datadictionary.DataObjectEntry", Level.DEBUG);
        setLogLevel("org.kuali.rice.krad.datadictionary.AttributeDefinitionBase", Level.DEBUG);
        setLogLevel("org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase", Level.DEBUG);

        super.setUp();

        assertNotNull( "DD not set - configuration error!!!", dd );
    }

    @Test
    public void verifyInRegistry() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);
        assertNotNull( "No embedded metadata object for TestDataObject", dataObjectEntry.getDataObjectMetadata() );
    }

    @Test
    public void verifyLabelOverride() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);

        Assert.assertEquals("Label not pulled from metadata", "A Spring-Provided Label", dataObjectEntry.getObjectLabel());
        Assert.assertEquals("Description not overridden", "UIF-Provided Description", dataObjectEntry.getObjectDescription());
    }

    @Test
    public void verifySummaryOverrideFromSpringMetadata() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);
        AttributeDefinition attributeDefinition = dataObjectEntry.getAttributeDefinition("nonStandardDataType");
        Assert.assertEquals("Label not pulled from Spring metadata", "Non Standard Label-Spring", attributeDefinition.getLabel());
    }

    @Test
    public void verifyAttributesExist() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);

        Assert.assertNotNull("attribute list should not have been null", dataObjectEntry.getAttributes() );
        Assert.assertFalse("attribute list should not have been empty", dataObjectEntry.getAttributes().isEmpty() );

        Assert.assertNotNull("stringProperty should have been present in the list", dataObjectEntry.getAttributeDefinition("stringProperty") );
    }

    @Test
    public void verifyAttributeProvidedAttributes() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);

        AttributeDefinition attributeDefinition = dataObjectEntry.getAttributeDefinition("stringProperty");
        Assert.assertEquals( "springProperty label incorrect", "Attribute Label From Annotation", attributeDefinition.getLabel());
        Assert.assertEquals( "springProperty data type incorrect", DataType.STRING, attributeDefinition.getDataType());
    }

    public void verifyTitleAttribute() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);

        Assert.assertEquals( "title attribute property incorrect", "primaryKeyProperty", dataObjectEntry.getTitleAttribute() );
    }

    @Test
    public void verifyPrimaryKey() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);

        Assert.assertNotNull("PK list should not have been null", dataObjectEntry.getPrimaryKeys() );
        Assert.assertFalse("PK list should not have been empty", dataObjectEntry.getPrimaryKeys().isEmpty() );
        Assert.assertEquals("PK list length wrong", 1, dataObjectEntry.getPrimaryKeys().size() );
        Assert.assertEquals("PK field incorrect", "primaryKeyProperty", dataObjectEntry.getPrimaryKeys().get(0) );
    }

    @Test
    public void verifyDefaultedControl_stringProperty() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);
        String propertyName = "stringProperty";
        AttributeDefinition attributeDefinition = dataObjectEntry.getAttributeDefinition(propertyName);
        Assert.assertNotNull(propertyName + " should have been present in the attribute list", attributeDefinition );

        Assert.assertNotNull( "the ControlField should not have been null", attributeDefinition.getControlField() );
        Assert.assertEquals( "Type of control field is incorrect", TextControl.class, attributeDefinition.getControlField().getClass() );
        Assert.assertEquals( "Size of control is incorrect", 40, ((TextControl) attributeDefinition.getControlField()).getSize() );
        Assert.assertNotNull( "MaxLength of control is missing", ((TextControl) attributeDefinition.getControlField()).getMaxLength() );
        Assert.assertEquals( "MaxLength of control is incorrect", 40, ((TextControl) attributeDefinition.getControlField()).getMaxLength().intValue() );
        Assert.assertEquals( "textExpand property incorrect", false, ((TextControl) attributeDefinition.getControlField()).isTextExpand() );
    }

    @Test
    public void verifyDefaultedControl_longStringProperty() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);
        String propertyName = "longStringProperty";
        AttributeDefinition attributeDefinition = dataObjectEntry.getAttributeDefinition(propertyName);
        Assert.assertNotNull(propertyName + " should have been present in the attribute list", attributeDefinition );

        Assert.assertNotNull( "the ControlField should not have been null", attributeDefinition.getControlField() );
        Assert.assertEquals( "Type of control field is incorrect", TextControl.class, attributeDefinition.getControlField().getClass() );
        Assert.assertEquals( "Size of control is incorrect", 200, ((TextControl) attributeDefinition.getControlField()).getSize() );
        Assert.assertNotNull( "MaxLength of control is missing", ((TextControl) attributeDefinition.getControlField()).getMaxLength() );
        Assert.assertEquals( "MaxLength of control is incorrect", 200, ((TextControl) attributeDefinition.getControlField()).getMaxLength().intValue() );
        Assert.assertEquals( "textExpand property incorrect", true, ((TextControl) attributeDefinition.getControlField()).isTextExpand() );
    }

    @Test
    public void verifyDefaultedControl_dateProperty() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);
        String propertyName = "dateProperty";
        AttributeDefinition attributeDefinition = dataObjectEntry.getAttributeDefinition(propertyName);
        Assert.assertNotNull(propertyName + " should have been present in the attribute list", attributeDefinition );

        Assert.assertNotNull( "the ControlField should not have been null", attributeDefinition.getControlField() );
        Assert.assertEquals( "Type of control field is incorrect", TextControl.class, attributeDefinition.getControlField().getClass() );
        Assert.assertNotNull( "control field is missing a datepicker", ((TextControl) attributeDefinition.getControlField()).getDatePicker() );
    }

    @Test
    public void verifyDefaultedRelationship_existence() {
        DataObjectEntry dataObjectEntry = getDataObjectEntry(MAIN_DATA_OBJECT_FOR_TESTING);
        String relationshipName = "referencedObject";
        RelationshipDefinition relationshipDefinition = dataObjectEntry.getRelationshipDefinition(relationshipName);
        Assert.assertNotNull(relationshipName + " should have been present in the relationship list", relationshipDefinition );
    }

    protected DataObjectEntry getDataObjectEntry( String dataObjectClassName ) {
        DataObjectEntry dataObjectEntry = dd.getDataObjectEntry(dataObjectClassName);
        LOG.info("Loading DataObjectEntry for : " + dataObjectClassName);
        assertNotNull( "Unable to retrieve data object entry for : " + dataObjectClassName, dataObjectEntry );
        LOG.info( "DataObjectEntry: " + dataObjectEntry);
        return dataObjectEntry;
    }

}
