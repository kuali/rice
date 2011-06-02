package org.rice.krms.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.KrmsTypeBo;
import org.kuali.rice.krms.impl.repository.TermSpecificationBo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

public class TestBoService implements BusinessObjectService {

	private Map<Class<?>, String> idFieldNamesMap = new HashMap<Class<?>, String>();

	{
		idFieldNamesMap.put(KrmsTypeBo.class, "id");
		idFieldNamesMap.put(ContextBo.class, "contextId");
		idFieldNamesMap.put(TermSpecificationBo.class, "id");
	}

	private final GenericTestDao dao;

	public TestBoService(GenericTestDao genericDao) {
		this.dao = genericDao;
	}

	public void addIdFieldNameMapping(Class<?> clazz, String fieldName) {
		idFieldNamesMap.put(clazz, fieldName);
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
		Collection<T> results = dao.findMatching(clazz, primaryKeys);
		if (!CollectionUtils.isEmpty(results)) {
			if (results.size() > 1) throw new IllegalStateException("primary key fetch should only return one result!");
			return results.iterator().next();
		}
		return null;
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
    @Transactional
    public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
        dao.deleteMatching(clazz, fieldValues);
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
