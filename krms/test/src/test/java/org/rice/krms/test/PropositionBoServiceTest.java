package org.rice.krms.test;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.PropositionBoService;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

@BaselineMode(Mode.CLEAR_DB)
public class PropositionBoServiceTest extends AbstractBoTest {
//public class PropositionBoServiceTest extends LightWeightBoTest {
	
	private PropositionBoService propositionBoService;
	private KrmsTypeRepositoryService krmsTypeRepository;
	
	@Before
	public void setup() {
		super.setup();
		
		krmsTypeRepository = new KrmsTypeBoServiceImpl();
		((KrmsTypeBoServiceImpl) krmsTypeRepository).setBusinessObjectService(getBoService());
		
//		dao.setJcdAlias("krmsDataSource");
//		
//		// wire up BO services
//		
//		propositionBoService = new PropositionBoServiceImpl();
//		((PropositionBoServiceImpl)propositionBoService).setBusinessObjectService(getBoService());
//
//		contextRepository = new ContextBoServiceImpl();
//		((ContextBoServiceImpl)contextRepository).setBusinessObjectService(getBoService());
//		
//		krmsTypeRepository = new KrmsTypeBoServiceImpl();
//		((KrmsTypeBoServiceImpl)krmsTypeRepository).setBusinessObjectService(getBoService());
	}
	
	@Test
	public void creationTest() {

		// KrmsType for context
		KrmsTypeDefinition krmsContextTypeDefinition = KrmsTypeDefinition.Builder.create(null, "KrmsTestContextType", "KRMS").build();
		krmsContextTypeDefinition = krmsTypeRepository.createKrmsType(krmsContextTypeDefinition);

//		// Context
//		ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create("KRMS", "testContext");
//		contextBuilder.setTypeId(krmsContextTypeDefinition.getId());
//		ContextDefinition contextDefinition = contextBuilder.build();
//		contextDefinition = contextRepository.createContext(contextDefinition);
//		
//		// output TermSpec
//		TermSpecificationDefinition outputTermSpec = 
//			TermSpecificationDefinition.Builder.create(null, contextDefinition.getId(), 
//					"outputTermSpec", "java.lang.String").build();
//		outputTermSpec = propositionBoService.createTermSpecification(outputTermSpec);
//
//		// prereq TermSpec
//		TermSpecificationDefinition prereqTermSpec = 
//			TermSpecificationDefinition.Builder.create(null, contextDefinition.getId(), 
//					"prereqTermSpec", "java.lang.String").build();
//		prereqTermSpec = propositionBoService.createTermSpecification(prereqTermSpec);
//
//		// KrmsType for TermResolver
//		KrmsTypeDefinition krmsTermResolverTypeDefinition = KrmsTypeDefinition.Builder.create(null, "KrmsTestResolverType", "KRMS").build();
//		krmsTermResolverTypeDefinition = krmsTypeRepository.createKrmsType(krmsTermResolverTypeDefinition);
//
//		// TermResolver
//		TermResolverDefinition termResolverDef = 
//			TermResolverDefinition.Builder.create(null, "KRMS", "testResolver", krmsTermResolverTypeDefinition.getId(), 
//					TermSpecificationDefinition.Builder.create(outputTermSpec), 
//					Collections.singleton(TermSpecificationDefinition.Builder.create(prereqTermSpec)), 
//					null, 
//					Collections.singleton("testParamName")).build();
//		termResolverDef = propositionBoService.createTermResolver(termResolverDef);
//
//		// Term Param
//		TermParameterDefinition.Builder termParamBuilder = 
//			TermParameterDefinition.Builder.create(null, null, "testParamName", "testParamValue");
//		
//		// Term
//		TermDefinition termDefinition = 
//			TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(outputTermSpec), Collections.singleton(termParamBuilder)).build();
//		propositionBoService.createTermDefinition(termDefinition);
	}

}
