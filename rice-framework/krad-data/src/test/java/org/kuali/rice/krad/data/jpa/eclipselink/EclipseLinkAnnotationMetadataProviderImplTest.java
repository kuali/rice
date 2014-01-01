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

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.annotation.impl.AnnotationMetadataProviderImpl;
import org.kuali.rice.krad.data.provider.impl.CompositeMetadataProviderImpl;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObjectExtension;

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
			annotationMetadataProvider.setJpaMetadataProvider(jpaMetadataProvider);
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
		Assert.assertEquals("attribute data type mismatch", DirectToFieldMapping.class, attribute.getClass());
		Converter converter = ((org.eclipse.persistence.mappings.DirectToFieldMapping) attribute).getConverter();
		Assert.assertNotNull("converter not assigned", converter);
		Assert.assertEquals("Mismatch - converter should have been the EclipseLink JPA wrapper class",
				ConverterClass.class, converter.getClass());
		Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
		f.setAccessible(true);
		String attributeConverterClassName = (String) f.get(converter);
		Assert.assertNotNull("attributeConverterClassName missing", attributeConverterClassName);
		Assert.assertEquals("Converter class incorrect",
				"org.kuali.rice.krad.data.jpa.testbo.NonStandardDataTypeConverter",
				attributeConverterClassName);
	}

	@Test
	public void testConvertersEstabished_autoApply() throws Exception {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		DatabaseMapping attribute = classDescriptor.getMappingForAttributeName("currencyProperty");
		Assert.assertEquals("attribute data type mismatch", DirectToFieldMapping.class, attribute.getClass());
		Converter converter = ((org.eclipse.persistence.mappings.DirectToFieldMapping) attribute).getConverter();
		Assert.assertNotNull("converter not assigned", converter);
		Assert.assertEquals("Mismatch - converter should have been the EclipseLink JPA wrapper class",
				ConverterClass.class, converter.getClass());
		Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
		f.setAccessible(true);
		String attributeConverterClassName = (String) f.get(converter);
		Assert.assertNotNull("attributeConverterClassName missing", attributeConverterClassName);
		Assert.assertEquals("Converter class incorrect", "org.kuali.rice.krad.data.jpa.converters.KualiDecimalConverter",
				attributeConverterClassName);
	}

	@Test
	public void testConvertersEstabished_autoApply_Boolean() throws Exception {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		DatabaseMapping attribute = classDescriptor.getMappingForAttributeName("booleanProperty");
		Assert.assertEquals("attribute data type mismatch", DirectToFieldMapping.class, attribute.getClass());
		Converter converter = ((org.eclipse.persistence.mappings.DirectToFieldMapping) attribute).getConverter();
		Assert.assertNotNull("converter not assigned", converter);
		Assert.assertEquals("Mismatch - converter should have been the EclipseLink JPA wrapper class",
				ConverterClass.class, converter.getClass());
		Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
		f.setAccessible(true);
		String attributeConverterClassName = (String) f.get(converter);
		Assert.assertNotNull("attributeConverterClassName missing", attributeConverterClassName);
		Assert.assertEquals("Converter class incorrect", "org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter",
				attributeConverterClassName);
	}

	@Test
	public void testExtensionAttribute_eclipselink_data() {
		ClassDescriptor classDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObject.class);
		ClassDescriptor referenceDescriptor = jpaMetadataProvider.getClassDescriptor(TestDataObjectExtension.class);
		Assert.assertNotNull("A classDescriptor should have been retrieved from JPA for TestDataObject",
				classDescriptor);
		Assert.assertNotNull("A classDescriptor should have been retrieved from JPA for TestDataObjectExtension",
				referenceDescriptor);
		System.err.println("Class Descriptor: " + classDescriptor);
		System.err.println(classDescriptor.getAllFields());
		DatabaseMapping attribute = classDescriptor.getMappingForAttributeName("extension");
		Assert.assertNotNull("extension attribute missing from metamodel", attribute);
		System.err.println("Extension Attribute: " + attribute);
		Assert.assertEquals("Mapping Type Incorrect", OneToOneMapping.class, attribute.getClass());
		System.err.println("Extension Attribute Fields: "
				+ ((OneToOneMapping) attribute).getForeignKeyFieldsForMapKey());

		Assert.assertNotNull("Field list on relationship should not be null", attribute.getFields());
		Assert.assertEquals("Field list on relationship length incorrect", 1, attribute.getFields().size());
		Assert.assertEquals("Field name on relationship incorect", "PK_PROP", attribute.getFields().get(0).getName());
		Assert.assertEquals("Table name on relationship incorect", "KRTST_TEST_TABLE_T", attribute.getFields().get(0)
				.getTableName());

		Assert.assertNotNull("Reference descriptor missing from relationship", attribute.getReferenceDescriptor());
		Assert.assertEquals("Reference descriptor should be the one for TestDataObjectExtension", referenceDescriptor,
				attribute.getReferenceDescriptor());

		Assert.assertNotNull("selection query relationship missing",
				((ForeignReferenceMapping) attribute).getSelectionQuery());
		Assert.assertNotNull("selection query missing name", ((ForeignReferenceMapping) attribute).getSelectionQuery()
				.getName());
		Assert.assertEquals("selection query name incorrect", "extension", ((ForeignReferenceMapping) attribute)
				.getSelectionQuery().getName());
		Assert.assertNotNull("selection query reference class", ((ForeignReferenceMapping) attribute)
				.getSelectionQuery().getReferenceClass());
		Assert.assertEquals("selection query reference class incorrect", TestDataObjectExtension.class,
				((ForeignReferenceMapping) attribute).getSelectionQuery().getReferenceClass());
		Assert.assertNotNull("selection query reference class name", ((ForeignReferenceMapping) attribute)
				.getSelectionQuery().getReferenceClassName());
		Assert.assertNotNull("selection query source mapping missing", ((ForeignReferenceMapping) attribute)
				.getSelectionQuery().getSourceMapping());
		Assert.assertEquals("selection query source mapping incorrect", attribute,
				((ForeignReferenceMapping) attribute).getSelectionQuery().getSourceMapping());
	}
}
