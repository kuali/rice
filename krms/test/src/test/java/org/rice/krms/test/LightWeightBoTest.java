package org.rice.krms.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextRepositoryService;
import org.kuali.rice.krms.impl.repository.ContextRepositoryServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsTypeBo;
import org.kuali.rice.krms.impl.repository.KrmsTypeRepositoryServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.krms.impl.repository.TermResolverParameterSpecificationBo;
import org.kuali.rice.krms.impl.repository.TermSpecificationBo;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.springframework.util.CollectionUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

@BaselineMode(Mode.ROLLBACK)
public class LightWeightBoTest extends KRMSTestCase {
	
	private final GenericDao dao = new GenericDao();
	
	private TermBoService termBoService;
	private ContextRepositoryService contextRepository;
	private KrmsTypeRepositoryService krmsTypeRepository;
	
	// TODO: get rid of this hack that is needed to set up the OJB properties location at the right time. 
	// BEGIN hack
	
	private static String ojbPropertiesBefore;
	
	@BeforeClass
	public static void beforeClass() {
		ojbPropertiesBefore = System.getProperty("OJB.properties");
		System.setProperty("OJB.properties", "./org/kuali/rice/core/ojb/RiceOJB.properties");
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
		
		// wire up BO services
		
		BoService boService = new BoService(dao);
		termBoService = new TermBoServiceImpl();
		((TermBoServiceImpl)termBoService).setBusinessObjectService(boService);

		contextRepository = new ContextRepositoryServiceImpl();
		((ContextRepositoryServiceImpl)contextRepository).setBusinessObjectService(boService);
		
		krmsTypeRepository = new KrmsTypeRepositoryServiceImpl();
		((KrmsTypeRepositoryServiceImpl)krmsTypeRepository).setBusinessObjectService(boService);
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
			TermSpecificationDefinition.Builder.create(null, contextDefinition.getId(), 
					"outputTermSpec", "java.lang.String").build();
		outputTermSpec = termBoService.createTermSpecification(outputTermSpec);

		// prereq TermSpec
		TermSpecificationDefinition prereqTermSpec = 
			TermSpecificationDefinition.Builder.create(null, contextDefinition.getId(), 
					"prereqTermSpec", "java.lang.String").build();
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
			TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(outputTermSpec), Collections.singleton(termParamBuilder)).build();
		termBoService.createTermDefinition(termDefinition);
	}

	public static class Dao<T> extends PersistenceBrokerDaoSupport {
		
		private final Class<T> clazz;
		private final String idFieldName;
		private final GenericDao dao;
		
		public Dao(Class<T> clazz, String idFieldName, GenericDao genericDao) {
			this.clazz = clazz;
			this.idFieldName = idFieldName;
			this.dao = genericDao;
		}
		
		public <T extends PersistableBusinessObject> T save(T object) {
			return dao.save(object);
		}
		
	    @SuppressWarnings("unchecked")
		public T findById(String id) {
	    	return dao.findById(id, idFieldName, clazz);
	    }
		
	}
	
	public static class GenericDao extends PersistenceBrokerDaoSupport {
		
		public <T extends PersistableBusinessObject> T save(T object) {
			this.getPersistenceBrokerTemplate().store(object);
			return object;
		}
		
	    @SuppressWarnings("unchecked")
		public <T> T findById(String id, String idFieldName, Class<T> clazz) {
	        Criteria crit = new Criteria();
	        crit.addEqualTo(idFieldName, id);
	        return (T) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(clazz, crit));
	    }
	    
	    @SuppressWarnings("unchecked")
		public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
	        Criteria crit = new Criteria();
	        if (fieldValues != null) for (Entry<String, ?> entry : fieldValues.entrySet()) {
		        crit.addEqualTo(entry.getKey(), entry.getValue());
	        }
	        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(clazz, crit));
	    }
		
	}
	
	public static class BoService implements BusinessObjectService {
		
		private Map<Class<?>, String> idFieldNamesMap = new HashMap<Class<?>, String>();
		
		{
			idFieldNamesMap.put(KrmsTypeBo.class, "id");
			idFieldNamesMap.put(ContextBo.class, "contextId");
			idFieldNamesMap.put(TermSpecificationBo.class, "id");
		}
		
		private final GenericDao dao;
		
		public BoService(GenericDao genericDao) {
			this.dao = genericDao;
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#save(org.kuali.rice.kns.bo.PersistableBusinessObject)
		 */
		@Override
		public PersistableBusinessObject save(PersistableBusinessObject bo) {
			return dao.save(bo);
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#save(java.util.List)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List<? extends PersistableBusinessObject> save(
				List<? extends PersistableBusinessObject> businessObjects) {
			List results = new ArrayList();
			if (!CollectionUtils.isEmpty(businessObjects)) {
				for (PersistableBusinessObject bo : businessObjects) {
					results.add(save(bo));
				}
			}
			return results;
		}
		

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#linkAndSave(org.kuali.rice.kns.bo.PersistableBusinessObject)
		 */
		@Override
		public PersistableBusinessObject linkAndSave(
				PersistableBusinessObject bo) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#linkAndSave(java.util.List)
		 */
		@Override
		public List<? extends PersistableBusinessObject> linkAndSave(
				List<? extends PersistableBusinessObject> businessObjects) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#findBySinglePrimaryKey(java.lang.Class, java.lang.Object)
		 */
		@Override
		public <T extends BusinessObject> T findBySinglePrimaryKey(
				Class<T> clazz, Object primaryKey) {
			String idFieldName = "id";
			if (idFieldNamesMap.containsKey(clazz)) idFieldName = idFieldNamesMap.get(clazz);
			return (T) dao.findById((String)primaryKey, idFieldName, clazz);
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#findByPrimaryKey(java.lang.Class, java.util.Map)
		 */
		@Override
		public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz,
				Map<String, ?> primaryKeys) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#retrieve(org.kuali.rice.kns.bo.PersistableBusinessObject)
		 */
		@Override
		public PersistableBusinessObject retrieve(
				PersistableBusinessObject object) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#findAll(java.lang.Class)
		 */
		@Override
		public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
		 */
		@Override
		public <T extends BusinessObject> Collection<T> findAllOrderBy(
				Class<T> clazz, String sortField, boolean sortAscending) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#findMatching(java.lang.Class, java.util.Map)
		 */
		@Override
		public <T extends BusinessObject> Collection<T> findMatching(
				Class<T> clazz, Map<String, ?> fieldValues) {
			return dao.findMatching(clazz, fieldValues);
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#countMatching(java.lang.Class, java.util.Map)
		 */
		@Override
		public int countMatching(Class clazz, Map<String, ?> fieldValues) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#countMatching(java.lang.Class, java.util.Map, java.util.Map)
		 */
		@Override
		public int countMatching(Class clazz,
				Map<String, ?> positiveFieldValues,
				Map<String, ?> negativeFieldValues) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#findMatchingOrderBy(java.lang.Class, java.util.Map, java.lang.String, boolean)
		 */
		@Override
		public <T extends BusinessObject> Collection<T> findMatchingOrderBy(
				Class<T> clazz, Map<String, ?> fieldValues, String sortField,
				boolean sortAscending) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#delete(org.kuali.rice.kns.bo.PersistableBusinessObject)
		 */
		@Override
		public void delete(PersistableBusinessObject bo) {
			throw new UnsupportedOperationException();			
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#delete(java.util.List)
		 */
		@Override
		public void delete(List<? extends PersistableBusinessObject> boList) {
			throw new UnsupportedOperationException();			
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#deleteMatching(java.lang.Class, java.util.Map)
		 */
		@Override
		public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
			throw new UnsupportedOperationException();			
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#getReferenceIfExists(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
		 */
		@Override
		public BusinessObject getReferenceIfExists(BusinessObject bo,
				String referenceName) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#linkUserFields(org.kuali.rice.kns.bo.PersistableBusinessObject)
		 */
		@Override
		public void linkUserFields(PersistableBusinessObject bo) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#linkUserFields(java.util.List)
		 */
		@Override
		public void linkUserFields(List<PersistableBusinessObject> bos) {
			throw new UnsupportedOperationException();		}

		/* (non-Javadoc)
		 * @see org.kuali.rice.kns.service.BusinessObjectService#manageReadOnly(org.kuali.rice.kns.bo.PersistableBusinessObject)
		 */
		@Override
		public PersistableBusinessObject manageReadOnly(
				PersistableBusinessObject bo) {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
