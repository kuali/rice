package org.rice.krms.test;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

@BaselineMode(Mode.ROLLBACK)
public abstract class AbstractBoTest extends KRMSTestCase {
	
	private GenericTestDao dao;
	private TestBoService boService;
	
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
	    dao = (GenericTestDao)GlobalResourceLoader.getService(new QName("genericTestDao"));
		boService = new TestBoService(dao);
	}
	
	protected TestBoService getBoService() {
		return boService;
	}
	
	protected GenericTestDao getDao() {
		return dao;
	}
}
