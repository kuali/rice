package org.rice.krms.test;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krms.api.repository.TermSpecificationDefinition;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

public class TermBoServiceTest extends KRMSTestCase {
	
	TermBoService termBoService = null;
	
//	@Before
//	public void setUp() {
//		termBoService = (TermBoService)KSBServiceLocator.getService("termBoService");
//	}

	@Test
	public void persistTerm() {
//		termBoService = GlobalResourceLoader.getService("termBoService");
//		
//		TermSpecificationDefinition termSpec = 
//			TermSpecificationDefinition.Builder.create(null, "1", "testTermSpec", "java.lang.String").build();
//		
//		termBoService.createTermSpecification(termSpec);
	}
	
}
