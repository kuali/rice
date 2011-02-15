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
package org.kuali.rice.kns.datadictionary.validation.processor;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.MockAddress;
import org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.ErrorLevel;
import org.kuali.rice.kns.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.processor.MustOccurConstraintProcessor;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.DictionaryValidationResult;

/**
 * Things this test should check:
 * 
 * 1. city and state entered, but no postal code (success) {@link #testCityStateNoPostalSuccess()}
 * 2. city entered, no state or postal code (failure) {@link #testCityNoStateNoPostalFailure()}
 * 3. postal code entered but no city or state (success) {@link #testPostalNoCityStateSuccess()}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class MustOccurConstraintProcessorTest {

	private AttributeDefinition definition;
	private MustOccurConstraintProcessor processor;
	private BusinessObjectEntry addressEntry;
	
	private DictionaryValidationResult dictionaryValidationResult;
	
	private MockAddress noPostalCodeAddress = new MockAddress("893 Presidential Ave", "Suite 800", "Washington", "DC", "", "USA");
	private MockAddress noStateOrPostalCodeAddress = new MockAddress("893 Presidential Ave", "Suite 800", "Washington", "", "", "USA");
	private MockAddress noCityStateAddress = new MockAddress("893 Presidential Ave", "Suite 800", "", "", "12340", "USA");
	
	@SuppressWarnings("boxing")
	@Before
	public void setUp() throws Exception {
		
		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		
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
		MustOccurConstraint topLevelConstraint = new MustOccurConstraint();
		topLevelConstraint.setMax(2);
		topLevelConstraint.setMin(1);
		topLevelConstraint.setPrerequisiteConstraints(Collections.singletonList(postalCodeConstraint));
		topLevelConstraint.setMustOccurConstraints(Collections.singletonList(cityStateConstraint));
		
		mustOccurConstraints.add(topLevelConstraint);
		
		definition = new AttributeDefinition();
		definition.setMustOccurConstraints(mustOccurConstraints);
		
		processor = new MustOccurConstraintProcessor();	
		
		addressEntry = new BusinessObjectEntry();
		
		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();
		
		AttributeDefinition street1Definition = new AttributeDefinition();
		street1Definition.setName("street1");
		attributes.add(street1Definition);
		
		AttributeDefinition street2Definition = new AttributeDefinition();
		street2Definition.setName("street2");
		attributes.add(street2Definition);
		
		AttributeDefinition cityDefinition = new AttributeDefinition();
		cityDefinition.setName("city");
		attributes.add(cityDefinition);
		
		AttributeDefinition stateDefinition = new AttributeDefinition();
		stateDefinition.setName("state");
		attributes.add(stateDefinition);
		
		AttributeDefinition postalCodeDefinition = new AttributeDefinition();
		postalCodeDefinition.setName("postalCode");
		attributes.add(postalCodeDefinition);
		
		AttributeDefinition countryDefinition = new AttributeDefinition();
		countryDefinition.setName("country");
		attributes.add(countryDefinition);
		
		addressEntry.setAttributes(attributes);	
	}
	
	
	@Test
	public void testCityStateNoPostalSuccess() {
		ConstraintValidationResult result = process(dictionaryValidationResult, noPostalCodeAddress, "country");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.NOCONSTRAINT, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testCityNoStateNoPostalFailure() {
		ConstraintValidationResult result = process(dictionaryValidationResult, noStateOrPostalCodeAddress, "country");
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testPostalNoCityStateSuccess() {
		ConstraintValidationResult result = process(dictionaryValidationResult, noCityStateAddress, "country");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.NOCONSTRAINT, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}
	
	private ConstraintValidationResult process(DictionaryValidationResult dictionaryValidationResult, Object object, String attributeName) {

		AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry);
		attributeValueReader.setAttributeName(attributeName);
		
		Object value = attributeValueReader.getValue();
		
		return processor.process(dictionaryValidationResult, value, definition, attributeValueReader).getFirstConstraintValidationResult();
	}

}
