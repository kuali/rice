package org.kuali.rice.krms.api.engine;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

/**
 * Tests the {@link ExecutionOptions} class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class ExecutionOptionsTest {

	private ExecutionOptions options;
	
	@Before
	public void setUp() {
		options = new ExecutionOptions();
	}
	
	@Test
	public void testExecutionOptions() {
		assert options.getFlags().isEmpty();
		assert options.getOptions().isEmpty();
	}

	@Test
	public void testExecutionOptions_copyExecutionOptions() {
		options.setFlag(ExecutionFlag.LOG_EXECUTION, false);
		options.setOption("myOption1", "myOption1Value");
		
		// now copy it
		ExecutionOptions executionOptions2 = new ExecutionOptions(options);
		def checkOptions2 = {
			assert executionOptions2.isFlagSet(ExecutionFlag.LOG_EXECUTION);
			assertFalse executionOptions2.getFlag(ExecutionFlag.LOG_EXECUTION, true);
			assert executionOptions2.isOptionSet("myOption1");
			assert executionOptions2.getOption("myOption1") == "myOption1Value";
		}
		checkOptions2();
		
		// now try modifying original execution options and ensure it does not side-effect the new options
		options.setOption("newOption", "newOptionValue");
		options.setOption("myOption1", "newMyOption1Value");
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		options.setFlag(ExecutionFlag.CONTEXT_MUST_EXIST, true);
		
		// now check new options to make sure they weren't changed
		checkOptions2();
		assertFalse executionOptions2.isFlagSet(ExecutionFlag.CONTEXT_MUST_EXIST);
		assertFalse executionOptions2.isOptionSet("newOption");
	}
	
	@Test
	public void testExecutionOptions_copyExecutionOptionsNull() {
		assert options.getFlags().isEmpty();
		assert options.getOptions().isEmpty();
	}
	
	@Test(expected=IllegalArgumentException)
	public void testSetFlag_null() {
		options.setFlag(null, true);
	}

	@Test
	public void testSetFlag() {
		options.setFlag(ExecutionFlag.LOG_EXECUTION, false);
		assert options.isFlagSet(ExecutionFlag.LOG_EXECUTION);
		assertFalse options.getFlag(ExecutionFlag.LOG_EXECUTION, true);
	}

	@Test
	public void testSetOption() {
		options.setOption("myOption", "myOptionValue");
		assert options.isOptionSet("myOption");
		assert "myOptionValue" == options.getOption("myOption");
		assert options.getOptions().size() == 1;
	}
	
	@Test(expected=IllegalArgumentException)
	public void testSetOption_null() {
		options.setOption(null, "myOptionValue");
	}
	
	@Test
	public void testSetOption_nullValue() {
		options.setOption("myOption", null);
		assert options.isOptionSet("myOption");
		assertNull options.getOption("myOption");
		assert options.getOptions().size() == 1;
	}

	@Test
	public void testRemoveFlag() {
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		assert options.isFlagSet(ExecutionFlag.LOG_EXECUTION);
		options.removeFlag(ExecutionFlag.LOG_EXECUTION);
		assertFalse options.isFlagSet(ExecutionFlag.LOG_EXECUTION);
		assert options.getFlags().isEmpty();
	}
	
	@Test
	public void testRemoveFlag_nonExistent() {
		// this flag should not be set, but should be a "no-op"
		options.removeFlag(ExecutionFlag.LOG_EXECUTION);
		assertFalse options.isFlagSet(ExecutionFlag.LOG_EXECUTION);
		assert options.getFlags().isEmpty();
	}
	
	@Test(expected=IllegalArgumentException)
	public void testRemoveFlag_null() {
		options.removeFlag(null);
	}

	@Test
	public void testRemoveOption() {
		options.setOption("option", "value");
		assert options.isOptionSet("option");
		options.removeOption("option");
		assertFalse options.isOptionSet("option");
		assert options.getOptions().isEmpty();
	}
	
	@Test
	public void testRemoveOption_nonExistent() {
		// this option should not be set, but should be a "no-op"
		options.removeOption("doesnotexist");
		assertFalse options.isOptionSet("doesnotexist");
		assert options.getOptions().isEmpty();
	}
	
	@Test(expected=IllegalArgumentException)
	public void testRemoveOption_null() {
		options.removeOption(null);
	}
	
	@Test(expected=IllegalArgumentException)
	public void testRemoveOption_emptyString() {
		options.removeOption("");
	}
	
	@Test(expected=IllegalArgumentException)
	public void testRemoveOption_whitespace() {
		options.removeOption("  ");
	}

	@Test
	public void testGetFlag() {
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		assert options.getFlag(ExecutionFlag.LOG_EXECUTION, false);
		assert options.getFlag(ExecutionFlag.LOG_EXECUTION, true);
	}
	
	@Test
	public void testGetFlag_defaulted() {
		assertFalse options.getFlag(ExecutionFlag.LOG_EXECUTION, false);
		assertTrue options.getFlag(ExecutionFlag.LOG_EXECUTION, true);
	}
	
	@Test(expected=IllegalArgumentException)
	public void testGetFlag_null() {
		options.getFlag(null, false);
	}

	@Test
	public void testGetOption() {
		options.setOption("myOption", "myValue");
		assert "myValue" == options.getOption("myOption");
	}

	@Test
	public void testGetOption_nonExistent() {
		assertNull options.getOption("doesnotexist");
	}

	@Test(expected=IllegalArgumentException)
	public void testGetOption_null() {
		options.getOption(null);
	}
	
	@Test(expected=IllegalArgumentException)
	public void testGetOption_emptyString() {
		options.getOption("");
	}
	
	@Test(expected=IllegalArgumentException)
	public void testGetOption_whitespace() {
		options.getOption(" ");
	}
		
	@Test
	public void testIsFlagSet() {
		//fail("Not yet implemented");
	}

	@Test
	public void testIsOptionSet() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetFlags() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetOptions() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void testCallChaining() {
		
	}

}
