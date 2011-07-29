package org.rice.krms.test;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.ContextBoServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

@BaselineMode(Mode.CLEAR_DB)
public class TermRelatedBoTest extends AbstractBoTest {
	
	private TermBoService termBoService;
	private ContextBoService contextRepository;
	private KrmsTypeRepositoryService krmsTypeRepository;
	
	@Before
	public void setup() {
		super.setup();
		
		// wire up BO services

		termBoService = new TermBoServiceImpl();
		((TermBoServiceImpl)termBoService).setBusinessObjectService(getBoService());

		contextRepository = new ContextBoServiceImpl();
		((ContextBoServiceImpl)contextRepository).setBusinessObjectService(getBoService());
		
		krmsTypeRepository = new KrmsTypeBoServiceImpl();
		((KrmsTypeBoServiceImpl)krmsTypeRepository).setBusinessObjectService(getBoService());
	}
	
	@Test
	public void creationTest() {

		// KrmsType for context
		KrmsTypeDefinition krmsContextTypeDefinition = KrmsTypeDefinition.Builder.create(null, "KrmsTestContextType", "KRMS").build();
		krmsContextTypeDefinition = krmsTypeRepository.createKrmsType(krmsContextTypeDefinition);

		// Context
		ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create("KRMS", "testContext");
		contextBuilder.setTypeId(krmsContextTypeDefinition.getId());
		ContextDefinition contextDefinition = contextBuilder.build();
		contextDefinition = contextRepository.createContext(contextDefinition);
		
		// output TermSpec
		TermSpecificationDefinition outputTermSpec = 
			TermSpecificationDefinition.Builder.create(null, "outputTermSpec", contextDefinition.getId(),
                    "java.lang.String").build();
		outputTermSpec = termBoService.createTermSpecification(outputTermSpec);

		// prereq TermSpec
		TermSpecificationDefinition prereqTermSpec = 
			TermSpecificationDefinition.Builder.create(null, "prereqTermSpec", contextDefinition.getId(),
                    "java.lang.String").build();
		prereqTermSpec = termBoService.createTermSpecification(prereqTermSpec);

		// KrmsType for TermResolver
		KrmsTypeDefinition krmsTermResolverTypeDefinition = KrmsTypeDefinition.Builder.create(null, "KrmsTestResolverType", "KRMS").build();
		krmsTermResolverTypeDefinition = krmsTypeRepository.createKrmsType(krmsTermResolverTypeDefinition);

		// TermResolver
		TermResolverDefinition termResolverDef = 
			TermResolverDefinition.Builder.create(null, "KRMS", "testResolver", krmsTermResolverTypeDefinition.getId(),
					TermSpecificationDefinition.Builder.create(outputTermSpec), 
					Collections.singleton(TermSpecificationDefinition.Builder.create(prereqTermSpec)), 
					null, 
					Collections.singleton("testParamName")).build();
		termResolverDef = termBoService.createTermResolver(termResolverDef);

		// Term Param
		TermParameterDefinition.Builder termParamBuilder = 
			TermParameterDefinition.Builder.create(null, null, "testParamName", "testParamValue");
		
		// Term
		TermDefinition termDefinition = 
			TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(outputTermSpec), Collections.singletonList(termParamBuilder)).build();
		termBoService.createTermDefinition(termDefinition);
	}
	
}
