package org.kuali.rice.core.api.parameter;

import static org.junit.Assert.fail;

import org.junit.Test;

public class ParameterTest {

	/**
	 * Tests the various failure conditions on Parameter.Builder.
	 */
	@Test
	public void testBuilder_FailureConditions() {
		
		String applicationCode = "appCode";
		String namespaceCode = "nmspc";
		String componentCode = "cmpnt";
		String name = "parameterName";
		ParameterType.Builder parameterTypeBuilder = ParameterType.Builder.create("parameterType");
		
		// all parameters null
		try {
			Parameter.Builder.create(null, null, null, null, null);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// first parameter null
		try {
			Parameter.Builder.create(null, namespaceCode, componentCode, name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// first parameter empty
		try {
			Parameter.Builder.create("", namespaceCode, componentCode, name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// first parameter with whitespace, just test this on one parameter, no need to test all permutations
		try {
			Parameter.Builder.create(" ", namespaceCode, componentCode, name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}

		// second parameter null
		try {
			Parameter.Builder.create(applicationCode, null, componentCode, name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// second parameter empty
		try {
			Parameter.Builder.create(applicationCode, "", componentCode, name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// third parameter null
		try {
			Parameter.Builder.create(applicationCode, namespaceCode, null, name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// third parameter empty
		try {
			Parameter.Builder.create(applicationCode, namespaceCode, "", name, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// fourth parameter null
		try {
			Parameter.Builder.create(applicationCode, namespaceCode, componentCode, null, parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// fourth parameter empty
		try {
			Parameter.Builder.create(applicationCode, namespaceCode, componentCode, "", parameterTypeBuilder);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
		// fifth parameter null
		try {
			Parameter.Builder.create(applicationCode, namespaceCode, componentCode, name, null);
			failNoIllegalArgument();
		} catch (IllegalArgumentException e) {
			// exception should always be thrown in this case
		}
		
	}
	
	private void failNoIllegalArgument() {
		fail("An IllegalArgumentException should have been thrown.");
	}

}
