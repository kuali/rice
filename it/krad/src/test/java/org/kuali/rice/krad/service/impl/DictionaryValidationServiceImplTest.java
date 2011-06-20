/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;
import org.kuali.rice.krad.datadictionary.validation.MockAddress;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.WhenConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.provider.AttributeDefinitionConstraintProvider;
import org.kuali.rice.krad.datadictionary.validation.constraint.provider.ConstraintProvider;
import org.kuali.rice.krad.datadictionary.validation.constraint.provider.DataObjectEntryConstraintProvider;
import org.kuali.rice.krad.datadictionary.validation.processor.CaseConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.CollectionConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.CollectionSizeConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.ConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.DataTypeConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.ExistenceConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.LengthConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.MustOccurConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.PrerequisiteConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.RangeConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;


/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DictionaryValidationServiceImplTest {

	private DictionaryValidationServiceImpl service;
	
	protected AttributeDefinition street1Definition;
	protected AttributeDefinition street2Definition;
	protected AttributeDefinition stateDefinition;
	protected AttributeDefinition postalCodeDefinition;
	protected AttributeDefinition countryDefinition;
	protected BusinessObjectEntry addressEntry;
	
	protected CaseConstraint countryIsUSACaseConstraint;
	protected MustOccurConstraint topLevelConstraint;
	
	
	private MockAddress validLondonAddress = new MockAddress("8129 Maiden Lane", "", "London", "", "SE1 0P3", "UK", null);
	private MockAddress validUSAddress = new MockAddress("893 Presidential Ave", "Suite 800", "Washington", "DC", "12031", "USA", null);
	private MockAddress noStateUSAddress = new MockAddress("893 Presidential Ave", "Suite 800", "Washington", "", "92342", "USA", null);
	private MockAddress noZipNoCityUSAddress = new MockAddress("893 Presidential Ave", "Suite 800", "", "DC", "", "USA", null);
	private MockAddress validNonDCUSAddress = new MockAddress("89 11th Street", "Suite 800", "Seattle", "WA", "", "USA", null);
	private MockAddress invalidDCUSAddress = new MockAddress("89 Presidential Ave", "Suite 800", "Washington", "DC", "12031", "USA", null);
	
	
    @SuppressWarnings("rawtypes")
	private static final ConstraintProvider[] DEFAULT_CONSTRAINT_PROVIDERS = 
    {
    	new AttributeDefinitionConstraintProvider(),
    	new DataObjectEntryConstraintProvider()
    };
    
	@SuppressWarnings("rawtypes")
	private static final List<ConstraintProcessor> DEFAULT_ELEMENT_PROCESSORS = 
		Arrays.asList((ConstraintProcessor)new CaseConstraintProcessor(), 
				(ConstraintProcessor)new ExistenceConstraintProcessor(),
				(ConstraintProcessor)new DataTypeConstraintProcessor(), 
				(ConstraintProcessor)new RangeConstraintProcessor(),
				(ConstraintProcessor)new LengthConstraintProcessor(),
				(ConstraintProcessor)new ValidCharactersConstraintProcessor(),
				(ConstraintProcessor)new PrerequisiteConstraintProcessor(),
				(ConstraintProcessor)new MustOccurConstraintProcessor());
	
	@SuppressWarnings("rawtypes")
	private static final List<CollectionConstraintProcessor> DEFAULT_COLLECTION_PROCESSORS = 
		Arrays.asList((CollectionConstraintProcessor)new CollectionSizeConstraintProcessor());
	
	
	@Before
	public void setUp() throws Exception {
		service = new DictionaryValidationServiceImpl();
		
		service.setCollectionConstraintProcessors(DEFAULT_COLLECTION_PROCESSORS);
		service.setElementConstraintProcessors(DEFAULT_ELEMENT_PROCESSORS);
		service.setConstraintProviders(Arrays.asList(DEFAULT_CONSTRAINT_PROVIDERS));
	
		
		addressEntry = new BusinessObjectEntry();
		
		
		List<MustOccurConstraint> mustOccurConstraints = new ArrayList<MustOccurConstraint>();
		
		PrerequisiteConstraint postalCodeConstraint = new PrerequisiteConstraint();
		postalCodeConstraint.setAttributePath("postalCode");
			
		PrerequisiteConstraint cityConstraint = new PrerequisiteConstraint();
		cityConstraint.setAttributePath("city");
		
		PrerequisiteConstraint stateConstraint = new PrerequisiteConstraint();
		stateConstraint.setAttributePath("state");
		
		List<PrerequisiteConstraint> cityStateDependencyConstraints = new ArrayList<PrerequisiteConstraint>();
		cityStateDependencyConstraints.add(cityConstraint);
		cityStateDependencyConstraints.add(stateConstraint);
		
		MustOccurConstraint cityStateConstraint = new MustOccurConstraint();
		cityStateConstraint.setMin(2);
		cityStateConstraint.setMax(2);
		cityStateConstraint.setPrerequisiteConstraints(cityStateDependencyConstraints);
		
		// This basically means that at least one of the two child constraints must be satisfied... either the postal code must be entered or _both_ the city and state
		topLevelConstraint = new MustOccurConstraint();
		topLevelConstraint.setMax(2);
		topLevelConstraint.setMin(1);
		topLevelConstraint.setPrerequisiteConstraints(Collections.singletonList(postalCodeConstraint));
		topLevelConstraint.setMustOccurConstraints(Collections.singletonList(cityStateConstraint));
		
		mustOccurConstraints.add(topLevelConstraint);
		
		addressEntry.setMustOccurConstraints(mustOccurConstraints);
		
		List<WhenConstraint> countryWhenConstraints = new ArrayList<WhenConstraint>();
		List<WhenConstraint> stateWhenConstraints = new ArrayList<WhenConstraint>();
		
		PrerequisiteConstraint prerequisiteConstraint = new PrerequisiteConstraint();
		prerequisiteConstraint.setAttributePath("state");
		
		ValidCharactersConstraint street1ValidCharactersConstraint = new ValidCharactersConstraint();
		street1ValidCharactersConstraint.setValue("\\d{3}\\s+\\w+\\s+Ave");
		
		// If the country is USA, then it must have a state
		WhenConstraint stateMustBeThereConstraint = new WhenConstraint();
		stateMustBeThereConstraint.setValue("USA");
		stateMustBeThereConstraint.setConstraint(prerequisiteConstraint);
		countryWhenConstraints.add(stateMustBeThereConstraint);
		
		// Set a valid characters constraint for state = DC that it must be a 3-digit street address followed by ____ Ave
		WhenConstraint streetAddressMustBe3digitAve = new WhenConstraint();
		streetAddressMustBe3digitAve.setValue("DC");
		streetAddressMustBe3digitAve.setValuePath("street1");
		streetAddressMustBe3digitAve.setConstraint(street1ValidCharactersConstraint);
		
		stateWhenConstraints.add(streetAddressMustBe3digitAve);
		
		countryIsUSACaseConstraint = new CaseConstraint();
		countryIsUSACaseConstraint.setCaseSensitive(false);
//		countryIsUSACaseConstraint.setFieldPath("country");
		countryIsUSACaseConstraint.setWhenConstraint(countryWhenConstraints);
		
		CaseConstraint stateIsDCConstraint = new CaseConstraint();
		stateIsDCConstraint.setCaseSensitive(false);
		stateIsDCConstraint.setWhenConstraint(stateWhenConstraints);
		
		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();
		
		street1Definition = new AttributeDefinition();
		street1Definition.setName("street1");
		//street1Definition.setValidCharactersConstraint(street1ValidCharactersConstraint);
		attributes.add(street1Definition);
		
		street2Definition = new AttributeDefinition();
		street2Definition.setName("street2");
		attributes.add(street2Definition);
		
		AttributeDefinition cityDefinition = new AttributeDefinition();
		cityDefinition.setName("city");
		attributes.add(cityDefinition);
		
		ValidCharactersConstraint stateValidCharactersConstraint = new ValidCharactersConstraint();
		stateValidCharactersConstraint.setValue("ABCD");
		
		stateDefinition = new AttributeDefinition();
		stateDefinition.setName("state");
//		stateDefinition.setValidCharactersConstraint(stateValidCharactersConstraint);
		stateDefinition.setCaseConstraint(stateIsDCConstraint);
		attributes.add(stateDefinition);
		
		postalCodeDefinition = new AttributeDefinition();
		postalCodeDefinition.setName("postalCode");
//		postalCodeDefinition.setExclusiveMin("1000");
//		postalCodeDefinition.setInclusiveMax("99999");
//		postalCodeDefinition.setDataType(DataType.STRING);
		attributes.add(postalCodeDefinition);
		
		countryDefinition = new AttributeDefinition();
		countryDefinition.setName("country");
//		countryDefinition.setMustOccurConstraints(mustOccurConstraints);
		countryDefinition.setCaseConstraint(countryIsUSACaseConstraint);
		attributes.add(countryDefinition);
		
		addressEntry.setAttributes(attributes);	
	}
	
	
	@Test
	public void testValidNonUSAddress() {
		DictionaryValidationResult dictionaryValidationResult = service.validate(validLondonAddress, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry, true);
		
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
	}
	
	@Test
	public void testValidUSAddress() {
		DictionaryValidationResult dictionaryValidationResult = service.validate(validUSAddress, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry, true);
		
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
	}
	
	@Test
	public void testInvalidUSAddress() {
		DictionaryValidationResult dictionaryValidationResult = service.validate(noStateUSAddress, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry, true);
		
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
	}
	
	@Test
	public void testValidNonDCAddress() {
		DictionaryValidationResult dictionaryValidationResult = service.validate(validNonDCUSAddress, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry, true);
		
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
	}
	
	@Test
	public void testInvalidDCAddress() {
		DictionaryValidationResult dictionaryValidationResult = service.validate(invalidDCUSAddress, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry, true);
		
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
	}
	
	@Test
	public void testNoStateNoZipUSAddress() {
		DictionaryValidationResult dictionaryValidationResult = service.validate(noZipNoCityUSAddress, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry, true);
		
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		
		if (dictionaryValidationResult.getNumberOfErrors() > 0) {
	    	for (Iterator<ConstraintValidationResult> iterator = dictionaryValidationResult.iterator() ; iterator.hasNext() ;) {
	    		ConstraintValidationResult constraintValidationResult = iterator.next();
	    		if (constraintValidationResult.getStatus().getLevel() >= ErrorLevel.WARN.getLevel()) {
	    			// The top level error should be an occurs error
	    			Assert.assertEquals(ErrorLevel.ERROR, constraintValidationResult.getStatus());
	    			Assert.assertEquals("error.occurs", constraintValidationResult.getErrorKey());
	    			// It should have two children
	    			List<ConstraintValidationResult> children = constraintValidationResult.getChildren();
	    			Assert.assertNotNull(children);
	    			Assert.assertEquals(2, children.size());
	    			// The first child should have it's own child 
	    			ConstraintValidationResult child1 = children.get(0);
	    			ConstraintValidationResult child2 = children.get(1);
	    			
	    			Assert.assertEquals("error.requiresField", child1.getErrorKey());
	    			Assert.assertArrayEquals(new String[] { "postalCode" }, child1.getErrorParameters());
	    			
	    			List<ConstraintValidationResult> grandchildren = child2.getChildren();
	    			Assert.assertNotNull(grandchildren);
	    			Assert.assertEquals(2, grandchildren.size());
	    			ConstraintValidationResult grandchild1 = grandchildren.get(0);
	    			Assert.assertEquals(ErrorLevel.ERROR, grandchild1.getStatus());
	    			Assert.assertEquals("error.requiresField", grandchild1.getErrorKey());
	    			Assert.assertArrayEquals(new String[] { "city" }, grandchild1.getErrorParameters());
	    			ConstraintValidationResult grandchild2 = grandchildren.get(1);
	    			Assert.assertEquals(ErrorLevel.OK, grandchild2.getStatus());
	    			Assert.assertEquals(new MustOccurConstraintProcessor().getName(), grandchild2.getConstraintName());
	    		}
	    	}
    	}
	}
	
}
