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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObjectExtension;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.annotation.impl.AnnotationMetadataProviderImpl;
import org.kuali.rice.krad.data.provider.impl.CompositeMetadataProviderImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class EclipseLinkAnnotationMetadataProviderImplTest {

	static EclipseLinkJpaMetadataProviderImpl jpaMetadataProvider;
	CompositeMetadataProviderImpl compositeProvider;
	AnnotationMetadataProviderImpl annotationMetadataProvider;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		Logger.getLogger(CompositeMetadataProviderImpl.class).setLevel(Level.DEBUG);
		Logger.getLogger(AnnotationMetadataProviderImpl.class).setLevel(Level.DEBUG);
		Logger.getLogger(EclipseLinkJpaMetadataProviderImpl.class).setLevel(Level.DEBUG);
		jpaMetadataProvider = new EclipseLinkJpaMetadataProviderImpl();
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("krad-data-unit-test");
		jpaMetadataProvider.setEntityManager(entityManagerFactory.createEntityManager());
	}

	@Before
	public void setUp() throws Exception {
		if (annotationMetadataProvider == null) {
			annotationMetadataProvider = new AnnotationMetadataProviderImpl();
			ArrayList<MetadataProvider> providers = new ArrayList<MetadataProvider>();
			providers.add(jpaMetadataProvider);
			providers.add(annotationMetadataProvider);
			compositeProvider = new CompositeMetadataProviderImpl();
			compositeProvider.setProviders(providers);
			compositeProvider.provideMetadata();
		}
	}

	@Test
	public void testConvertersEstabished_directAssignment() throws Exception {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		DatabaseMapping attribute = classDescriptor.getMappingForAttributeName("nonStandardDataType");
		assertEquals("attribute data type mismatch", DirectToFieldMapping.class, attribute.getClass());
		Converter converter = ((org.eclipse.persistence.mappings.DirectToFieldMapping) attribute).getConverter();
		assertNotNull("converter not assigned", converter);
		assertEquals("Mismatch - converter should have been the EclipseLink JPA wrapper class", ConverterClass.class,
                converter.getClass());
		Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
		f.setAccessible(true);
		String attributeConverterClassName = (String) f.get(converter);
		assertNotNull("attributeConverterClassName missing", attributeConverterClassName);
		assertEquals("Converter class incorrect", "org.kuali.rice.krad.data.jpa.testbo.NonStandardDataTypeConverter",
                attributeConverterClassName);
	}

	@Test
	public void testConvertersEstabished_autoApply() throws Exception {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		DatabaseMapping attribute = classDescriptor.getMappingForAttributeName("currencyProperty");
		assertEquals("attribute data type mismatch", DirectToFieldMapping.class, attribute.getClass());
		Converter converter = ((org.eclipse.persistence.mappings.DirectToFieldMapping) attribute).getConverter();
		assertNotNull("converter not assigned", converter);
		assertEquals("Mismatch - converter should have been the EclipseLink JPA wrapper class", ConverterClass.class,
                converter.getClass());
		Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
		f.setAccessible(true);
		String attributeConverterClassName = (String) f.get(converter);
		assertNotNull("attributeConverterClassName missing", attributeConverterClassName);
		assertEquals("Converter class incorrect", "org.kuali.rice.krad.data.jpa.converters.KualiDecimalConverter",
                attributeConverterClassName);
	}

	@Test
	public void testConvertersEstabished_autoApply_Boolean() throws Exception {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		DatabaseMapping attribute = classDescriptor.getMappingForAttributeName("booleanProperty");
		assertEquals("attribute data type mismatch", DirectToFieldMapping.class, attribute.getClass());
		Converter converter = ((org.eclipse.persistence.mappings.DirectToFieldMapping) attribute).getConverter();
		assertNotNull("converter not assigned", converter);
		assertEquals("Mismatch - converter should have been the EclipseLink JPA wrapper class", ConverterClass.class,
                converter.getClass());
		Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
		f.setAccessible(true);
		String attributeConverterClassName = (String) f.get(converter);
		assertNotNull("attributeConverterClassName missing", attributeConverterClassName);
		assertEquals("Converter class incorrect", "org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter",
                attributeConverterClassName);
	}

	@Test
	public void testExtensionAttribute_eclipselink_data() {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		ClassDescriptor referenceDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObjectExtension.class);
		assertNotNull("A classDescriptor should have been retrieved from JPA for TestDataObject", classDescriptor);
		assertNotNull("A classDescriptor should have been retrieved from JPA for TestDataObjectExtension",
                referenceDescriptor);
		DatabaseMapping databaseMapping = classDescriptor.getMappingForAttributeName("extension");
        assertNotNull("extension mapping missing from metamodel", databaseMapping);
        assertTrue("Should be a OneToOne mapping", databaseMapping instanceof OneToOneMapping);
        OneToOneMapping mapping = (OneToOneMapping)databaseMapping;

        assertEquals("Should be mapped by primaryKeyProperty", "primaryKeyProperty", mapping.getMappedBy());
        Map<DatabaseField, DatabaseField> databaseFields = mapping.getSourceToTargetKeyFields();
        assertEquals(1, databaseFields.size());
        for (DatabaseField sourceField : databaseFields.keySet()) {
            DatabaseField targetField = databaseFields.get(sourceField);
            assertEquals("PK_PROP", sourceField.getName());
            assertEquals("PK_PROP", targetField.getName());
        }

		assertNotNull("Reference descriptor missing from relationship", mapping.getReferenceDescriptor());
		assertEquals("Reference descriptor should be the one for TestDataObjectExtension", referenceDescriptor,
                mapping.getReferenceDescriptor());

		assertNotNull("selection query relationship missing", mapping.getSelectionQuery());
		assertNotNull("selection query missing name", mapping.getSelectionQuery().getName());
		assertEquals("selection query name incorrect", "extension", mapping.getSelectionQuery().getName());
		assertNotNull("selection query reference class", mapping.getSelectionQuery().getReferenceClass());
		assertEquals("selection query reference class incorrect", TestDataObjectExtension.class,
                mapping.getSelectionQuery().getReferenceClass());
		assertNotNull("selection query reference class name", mapping.getSelectionQuery().getReferenceClassName());
		assertNotNull("selection query source mapping missing", mapping.getSelectionQuery().getSourceMapping());
		assertEquals("selection query source mapping incorrect", mapping,
                mapping.getSelectionQuery().getSourceMapping());
	}
}
