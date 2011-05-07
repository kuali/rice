package org.rice.krms.test;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

@BaselineMode(Mode.ROLLBACK)
public abstract class AbstractBoTest extends KRMSTestCase {
	
	private final GenericTestDao dao = new GenericTestDao();
	private final TestBoService boService = new TestBoService(dao);
	
	// TODO: get rid of this hack that is needed to set up the OJB properties location at the right time. 
	// BEGIN hack
	
	private static String ojbPropertiesBefore;
	
	@BeforeClass
	public static void beforeClass() {
		ojbPropertiesBefore = System.getProperty("OJB.properties");
		// TODO: this is extra annoying, we have to have our own copy of RiceOJB.properties >:-[
		System.setProperty("OJB.properties", /*"./org/kuali/rice/core/ojb/RiceOJB.properties"*/ "./RiceOJB.properties");
	}
	
	@AfterClass
	public static void afterClass() {
		if (ojbPropertiesBefore != null) {
			System.setProperty("OJB.properties", ojbPropertiesBefore);
		} else {
			System.clearProperty("OJB.properties");
		}
	}
	
	// END hack

	
	@Before
	public void setup() {
		dao.setJcdAlias("krmsDataSource");
	}
	
	protected TestBoService getBoService() {
		return boService;
	}
	
	protected GenericTestDao getDao() {
		return dao;
	}
}
