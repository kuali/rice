/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.dao.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.jpa.metadata.EntityDescriptor;
import org.kuali.rice.core.jpa.metadata.FieldDescriptor;
import org.kuali.rice.core.jpa.metadata.MetadataManager;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.kns.dao.LookupDao;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.TypeUtils;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * This class is the OJB implementation of the LookupDao interface.
 */
public class LookupDaoJpa implements LookupDao {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupDao.class);

	private DateTimeService dateTimeService;

	private PersistenceStructureService persistenceStructureService;

	@PersistenceContext
	private EntityManager entityManager;

	public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}

	// TODO: Add the JPA implementation
	// TODO WARNING: this does not support nested joins, because i don't have a
	// test case
	public Collection findCollectionBySearchHelperWithPersonJoin(Class businessObjectClass, Map nonPersonSearchCriteria, Map personSearchCriteria, boolean unbounded, boolean usePrimaryKeyValuesOnly) {
		PersistableBusinessObject businessObject = checkBusinessObjectClass(businessObjectClass);
		Criteria criteria = null;
		/*
		if (usePrimaryKeyValuesOnly) {
			criteria = getCollectionCriteriaFromMapUsingPrimaryKeysOnly(businessObjectClass, nonPersonSearchCriteria);
		} else {
			criteria = getCollectionCriteriaFromMap(businessObject, nonPersonSearchCriteria);
			Iterator personReferenceItr = personSearchCriteria.keySet().iterator();
			Person personExample = new org.kuali.rice.kim.bo.impl.PersonImpl();
			while (personReferenceItr.hasNext()) {
				String institutionalIdSourcePrimitivePropertyName = (String) personReferenceItr.next();
				Map personReferenceSearchCriteria = (Map) personSearchCriteria.get(institutionalIdSourcePrimitivePropertyName);
				Iterator personReferenceSearchCriterionItr = personReferenceSearchCriteria.keySet().iterator();
				Criteria personSubCriteria = new Criteria();
				while (personReferenceSearchCriterionItr.hasNext()) {
					String personSearchFieldName = (String) personReferenceSearchCriterionItr.next();
					Boolean caseInsensitive = Boolean.FALSE;
					if (KNSServiceLocator.getDataDictionaryService().isAttributeDefined(businessObjectClass, personSearchFieldName)) {
						caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase(Person.class, personSearchFieldName);
					}
					if (caseInsensitive == null) {
						caseInsensitive = Boolean.FALSE;
					}
					createCriteria(personExample, (String) personReferenceSearchCriteria.get(personSearchFieldName), personSearchFieldName, caseInsensitive, personSubCriteria);
				}
				ReportQueryByCriteria personSubQuery = QueryFactory.newReportQuery(Person.class, personSubCriteria);
				personSubQuery.setAttributes(new String[] { "principalId" });
				criteria.addIn(institutionalIdSourcePrimitivePropertyName, personSubQuery);
			}
		}
		*/
		return executeSearch(businessObjectClass, criteria, unbounded);
	}
	
    public Long findCountByMap(Object example, Map formProps) {
		Criteria criteria = new Criteria(example.getClass().getName());

		// iterate through the parameter map for key values search criteria
		Iterator propsIter = formProps.keySet().iterator();
		while (propsIter.hasNext()) {
			String propertyName = (String) propsIter.next();
			String searchValue = (String) formProps.get(propertyName);

			// if searchValue is empty and the key is not a valid property ignore
			if (StringUtils.isBlank(searchValue) || !(PropertyUtils.isWriteable(example, propertyName))) {
				continue;
			}

			// get property type which is used to determine type of criteria
			Class propertyType = ObjectUtils.getPropertyType(example, propertyName, persistenceStructureService);
			if (propertyType == null) {
				continue;
			}
			Boolean caseInsensitive = Boolean.FALSE;
			if (KNSServiceLocator.getDataDictionaryService().isAttributeDefined(example.getClass(), propertyName)) {
				caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase(example.getClass(), propertyName);
			}
			if (caseInsensitive == null) {
				caseInsensitive = Boolean.FALSE;
			}

			// build criteria
			addCriteria(propertyName, searchValue, propertyType, caseInsensitive, criteria);
		}

		// execute query and return result list
		return (Long) new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, criteria).toCountQuery().getSingleResult();
	}

	public Collection findCollectionBySearchHelper(Class businessObjectClass, Map formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly) {
		return findCollectionBySearchHelper(businessObjectClass, formProps, unbounded, usePrimaryKeyValuesOnly, null);
	}

	public Collection findCollectionBySearchHelper(Class businessObjectClass, Map formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly, Object additionalCriteria) {
		PersistableBusinessObject businessObject = checkBusinessObjectClass(businessObjectClass);
		if (usePrimaryKeyValuesOnly) {
			return executeSearch(businessObjectClass, getCollectionCriteriaFromMapUsingPrimaryKeysOnly(businessObjectClass, formProps), unbounded);
		} else {
			Criteria crit = getCollectionCriteriaFromMap(businessObject, formProps);
			if (additionalCriteria != null && additionalCriteria instanceof Criteria) {
				crit.and((Criteria) additionalCriteria);
			}
			return executeSearch(businessObjectClass, crit, unbounded);
		}
	}

	public Criteria getCollectionCriteriaFromMap(PersistableBusinessObject example, Map formProps) {
		Criteria criteria = new Criteria(example.getClass().getName());
		Iterator propsIter = formProps.keySet().iterator();
		while (propsIter.hasNext()) {
			String propertyName = (String) propsIter.next();
			if (formProps.get(propertyName) instanceof Collection) {
				Iterator iter = ((Collection) formProps.get(propertyName)).iterator();
				while (iter.hasNext()) {
					Boolean caseInsensitive = Boolean.FALSE;
					if (KNSServiceLocator.getDataDictionaryService().isAttributeDefined(example.getClass(), propertyName)) {
						caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase(example.getClass(), propertyName);
					}
					if (caseInsensitive == null) {
						caseInsensitive = Boolean.FALSE;
					}
					if (!createCriteria(example, (String) iter.next(), propertyName, caseInsensitive.booleanValue(), criteria)) {
						throw new RuntimeException("Invalid value in Collection");
					}
				}
			} else {
				Boolean caseInsensitive = Boolean.FALSE;
				if (KNSServiceLocator.getDataDictionaryService().isAttributeDefined(example.getClass(), propertyName)) {
					caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase(example.getClass(), propertyName);
				}
				if (caseInsensitive == null) {
					caseInsensitive = Boolean.FALSE;
				}
				if (!createCriteria(example, (String) formProps.get(propertyName), propertyName, caseInsensitive.booleanValue(), criteria)) {
					continue;
				}
			}
		}
		return criteria;
	}

	public Criteria getCollectionCriteriaFromMapUsingPrimaryKeysOnly(Class businessObjectClass, Map formProps) {
		PersistableBusinessObject businessObject = checkBusinessObjectClass(businessObjectClass);
		Criteria criteria = new Criteria(businessObjectClass.getName());
		List pkFields = persistenceStructureService.listPrimaryKeyFieldNames(businessObjectClass);
		Iterator pkIter = pkFields.iterator();
		while (pkIter.hasNext()) {
			String pkFieldName = (String) pkIter.next();
			String pkValue = (String) formProps.get(pkFieldName);

			if (StringUtils.isBlank(pkValue)) {
				throw new RuntimeException("Missing pk value for field " + pkFieldName + " when a search based on PK values only is performed.");
			} else if (StringUtils.indexOfAny(pkValue, KNSConstants.QUERY_CHARACTERS) != -1) {
				throw new RuntimeException("Value \"" + pkValue + "\" for PK field " + pkFieldName + " contains wildcard/operator characters.");
			}
			createCriteria(businessObject, pkValue, pkFieldName, false, criteria);
		}
		return criteria;
	}

	private PersistableBusinessObject checkBusinessObjectClass(Class businessObjectClass) {
		if (businessObjectClass == null) {
			throw new IllegalArgumentException("BusinessObject class passed to LookupDao findCollectionBySearchHelper... method was null");
		}
		PersistableBusinessObject businessObject = null;
		try {
			businessObject = (PersistableBusinessObject) businessObjectClass.newInstance();
		} catch (IllegalAccessException e) {
			throw new RuntimeException("LookupDao could not get instance of " + businessObjectClass.getName(), e);
		} catch (InstantiationException e) {
			throw new RuntimeException("LookupDao could not get instance of " + businessObjectClass.getName(), e);
		}
		return businessObject;
	}

	private Collection executeSearch(Class businessObjectClass, Criteria criteria, boolean unbounded) {
		Collection<PersistableBusinessObject> searchResults = new ArrayList<PersistableBusinessObject>();
		Long matchingResultsCount = null;
		try {
			Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(businessObjectClass);
			if (!unbounded && (searchResultsLimit != null)) {
				matchingResultsCount = (Long) new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, criteria).toCountQuery().getSingleResult();
				searchResults = new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().setMaxResults(searchResultsLimit).getResultList();
			} else {
				searchResults = new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().getResultList();
			}
			if ((matchingResultsCount == null) || (matchingResultsCount.intValue() <= searchResultsLimit.intValue())) {
				matchingResultsCount = new Long(0);
			}
			// Temp solution for loading extension objects - need to find a
			// better way
			// Should look for a JOIN query, for the above query, that will grab
			// the PBOEs as well (1+n query problem)
			for (PersistableBusinessObject bo : searchResults) {
				if (bo.getExtension() != null) {
					PersistableBusinessObjectExtension boe = bo.getExtension();
					EntityDescriptor entity = MetadataManager.getEntityDescriptor(bo.getExtension().getClass());
					Criteria extensionCriteria = new Criteria(boe.getClass().getName());
					for (FieldDescriptor fieldDescriptor : entity.getPrimaryKeys()) {
						try {
							Field field = bo.getClass().getDeclaredField(fieldDescriptor.getName());
							field.setAccessible(true);
							extensionCriteria.eq(fieldDescriptor.getName(), field.get(bo));
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
					}
					try {
						boe = (PersistableBusinessObjectExtension) new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, extensionCriteria).toQuery().getSingleResult();
					} catch (PersistenceException e) {}
					bo.setExtension(boe);
				}
			}
			// populate Person objects in business objects
			List bos = new ArrayList();
			bos.addAll(searchResults);
			searchResults = bos;
		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("LookupDao encountered exception during executeSearch", e);
		}
		return new CollectionIncomplete(searchResults, matchingResultsCount);
	}

	/**
	 * Return whether or not an attribute is writeable. This method is aware
	 * that that Collections may be involved and handles them consistently with
	 * the way in which OJB handles specifying the attributes of elements of a
	 * Collection.
	 * 
	 * @param o
	 * @param p
	 * @return
	 * @throws IllegalArgumentException
	 */
	private boolean isWriteable(Object o, String p) throws IllegalArgumentException {
		if (null == o || null == p) {
			throw new IllegalArgumentException("Cannot check writeable status with null arguments.");
		}

		boolean b = false;

		// Try the easy way.
		if (!(PropertyUtils.isWriteable(o, p))) {

			// If that fails lets try to be a bit smarter, understanding that
			// Collections may be involved.
			if (-1 != p.indexOf('.')) {

				String[] parts = p.split("\\.");

				// Get the type of the attribute.
				Class c = ObjectUtils.getPropertyType(o, parts[0], persistenceStructureService);

				Object i = null;

				// If the next level is a Collection, look into the collection,
				// to find out what type its elements are.
				if (Collection.class.isAssignableFrom(c)) {
					Map<String, Class> m = persistenceStructureService.listCollectionObjectTypes(o.getClass());
					c = m.get(parts[0]);
				}

				// Look into the attribute class to see if it is writeable.
				try {
					i = c.newInstance();
					StringBuffer sb = new StringBuffer();
					for (int x = 1; x < parts.length; x++) {
						sb.append(1 == x ? "" : ".").append(parts[x]);
					}
					b = isWriteable(i, sb.toString());
				} catch (InstantiationException ie) {
					LOG.info(ie);
				} catch (IllegalAccessException iae) {
					LOG.info(iae);
				}
			}
		} else {
			b = true;
		}

		return b;
	}

	public boolean createCriteria(Object example, String searchValue, String propertyName, Object criteria) {
		return createCriteria(example, searchValue, propertyName, false, criteria);
	}

	public boolean createCriteria(Object example, String searchValue, String propertyName, boolean caseInsensitive, Object criteria) {
		// if searchValue is empty and the key is not a valid property ignore
		if (!(criteria instanceof Criteria) || StringUtils.isBlank(searchValue) || !isWriteable(example, propertyName)) {
			return false;
		}

		// get property type which is used to determine type of criteria
		Class propertyType = ObjectUtils.getPropertyType(example, propertyName, persistenceStructureService);
		if (propertyType == null) {
			return false;
		}

		// build criteria
		addCriteria(propertyName, searchValue, propertyType, caseInsensitive, (Criteria)criteria);
		return true;
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findObjectByMap(java.lang.Object,
	 *      java.util.Map)
	 */
	public Object findObjectByMap(Object example, Map formProps) {
		Criteria jpaCriteria = new Criteria(example.getClass().getName());

		Iterator propsIter = formProps.keySet().iterator();
		while (propsIter.hasNext()) {
			String propertyName = (String) propsIter.next();
			String searchValue = "";
			if (formProps.get(propertyName) != null) {
				searchValue = (formProps.get(propertyName)).toString();
			}

			if (StringUtils.isNotBlank(searchValue) & PropertyUtils.isWriteable(example, propertyName)) {
				Class propertyType = ObjectUtils.getPropertyType(example, propertyName, persistenceStructureService);
				if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType)) {
					if (propertyType.equals(Long.class)) {
						jpaCriteria.eq(propertyName, new Long(searchValue));
					} else {
						jpaCriteria.eq(propertyName, new Integer(searchValue));
					}
				} else if (TypeUtils.isTemporalClass(propertyType)) {
					jpaCriteria.eq(propertyName, parseDate(ObjectUtils.clean(searchValue)));
				} else {
					jpaCriteria.eq(propertyName, searchValue);
				}
			}
		}

		return new QueryByCriteria(entityManager, jpaCriteria).toQuery().getSingleResult();
	}

	private void addCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
		if (StringUtils.contains(propertyValue, KNSConstants.OR_LOGICAL_OPERATOR)) {
			addOrCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria);
			return;
		}

		if (StringUtils.contains(propertyValue, KNSConstants.AND_LOGICAL_OPERATOR)) {
			addAndCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria);
			return;
		}

		if (TypeUtils.isStringClass(propertyType)) {
			// KULRICE-85 : made string searches case insensitive - used new
			// DBPlatform function to force strings to upper case
			if (caseInsensitive) {
				// TODO: What to do here now that the JPA version does not extend platform aware?
				//propertyName = getDbPlatform().getUpperCaseFunction() + "(__JPA_ALIAS__." + propertyName + ")";
				propertyName = "UPPER(__JPA_ALIAS__." + propertyName + ")";
				propertyValue = propertyValue.toUpperCase();
			}
			if (StringUtils.contains(propertyValue, KNSConstants.NOT_LOGICAL_OPERATOR)) {
				addNotCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria);
			} else if (StringUtils.contains(propertyValue, "..") || StringUtils.contains(propertyValue, ">") || StringUtils.contains(propertyValue, "<") || StringUtils.contains(propertyValue, ">=") || StringUtils.contains(propertyValue, "<=")) {
				addStringRangeCriteria(propertyName, propertyValue, criteria);
			} else {
				criteria.like(propertyName, propertyValue);
			}
		} else if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType)) {
			addNumericRangeCriteria(propertyName, propertyValue, criteria);
		} else if (TypeUtils.isTemporalClass(propertyType)) {
			addDateRangeCriteria(propertyName, propertyValue, criteria);
		} else if (TypeUtils.isBooleanClass(propertyType)) {
			String temp = ObjectUtils.clean(propertyValue);
			criteria.eq(propertyName, ("Y".equalsIgnoreCase(temp) || "T".equalsIgnoreCase(temp) || "1".equalsIgnoreCase(temp) || "true".equalsIgnoreCase(temp)) ? true : false);
		} else {
			LOG.error("not adding criterion for: " + propertyName + "," + propertyType + "," + propertyValue);
		}
	}

	private void addOrCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
		addLogicalOperatorCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, KNSConstants.OR_LOGICAL_OPERATOR);
	}

	private void addAndCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
		addLogicalOperatorCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, KNSConstants.AND_LOGICAL_OPERATOR);
	}

	private void addNotCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
		String[] splitPropVal = StringUtils.split(propertyValue, KNSConstants.NOT_LOGICAL_OPERATOR);

		int strLength = splitPropVal.length;
		// if more than one NOT operator assume an implicit and (i.e. !a!b = !a&!b)
		if (strLength > 1) {
			String expandedNot = "!" + StringUtils.join(splitPropVal, KNSConstants.AND_LOGICAL_OPERATOR + KNSConstants.NOT_LOGICAL_OPERATOR);
			addCriteria(propertyName, expandedNot, propertyType, caseInsensitive, criteria);
		} else {
			// only one so add a not like
			criteria.notLike(propertyName, splitPropVal[0]);
		}
	}

	private void addLogicalOperatorCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria, String splitValue) {
		String[] splitPropVal = StringUtils.split(propertyValue, splitValue);

		Criteria subCriteria = new Criteria("N/A");
		for (int i = 0; i < splitPropVal.length; i++) {
			Criteria predicate = new Criteria("N/A");

			addCriteria(propertyName, splitPropVal[i], propertyType, caseInsensitive, predicate);
			if (splitValue == KNSConstants.OR_LOGICAL_OPERATOR) {
				subCriteria.or(predicate);
			}
			if (splitValue == KNSConstants.AND_LOGICAL_OPERATOR) {
				subCriteria.and(predicate);
			}
		}

		criteria.and(subCriteria);
	}

	private java.sql.Date parseDate(String dateString) {
		dateString = dateString.trim();
		try {
			return dateTimeService.convertToSqlDate(dateString);
		} catch (ParseException ex) {
			return null;
		}
	}

	private void addDateRangeCriteria(String propertyName, String propertyValue, Criteria criteria) {

		if (StringUtils.contains(propertyValue, "..")) {
			String[] rangeValues = StringUtils.split(propertyValue, "..");
			criteria.between(propertyName, parseDate(ObjectUtils.clean(rangeValues[0])), parseDate(ObjectUtils.clean(rangeValues[1])));
		} else if (propertyValue.startsWith(">")) {
			criteria.gt(propertyName, parseDate(ObjectUtils.clean(propertyValue)));
		} else if (propertyValue.startsWith("<")) {
			criteria.lt(propertyName, parseDate(ObjectUtils.clean(propertyValue)));
		} else if (propertyValue.startsWith(">=")) {
			criteria.gte(propertyName, parseDate(ObjectUtils.clean(propertyValue)));
		} else if (propertyValue.startsWith("<=")) {
			criteria.lte(propertyName, parseDate(ObjectUtils.clean(propertyValue)));
		} else {
			criteria.eq(propertyName, parseDate(ObjectUtils.clean(propertyValue)));
		}
	}

	private BigDecimal cleanNumeric(String value) {
		String cleanedValue = value.replaceAll("[^-0-9.]", "");
		// ensure only one "minus" at the beginning, if any
		if (cleanedValue.lastIndexOf('-') > 0) {
			if (cleanedValue.charAt(0) == '-') {
				cleanedValue = "-" + cleanedValue.replaceAll("-", "");
			} else {
				cleanedValue = cleanedValue.replaceAll("-", "");
			}
		}
		// ensure only one decimal in the string
		int decimalLoc = cleanedValue.lastIndexOf('.');
		if (cleanedValue.indexOf('.') != decimalLoc) {
			cleanedValue = cleanedValue.substring(0, decimalLoc).replaceAll("\\.", "") + cleanedValue.substring(decimalLoc);
		}
		try {
			return new BigDecimal(cleanedValue);
		} catch (NumberFormatException ex) {
			GlobalVariables.getErrorMap().putError(KNSConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Numeric Input: " + value });
			return null;
		}
	}

	private void addNumericRangeCriteria(String propertyName, String propertyValue, Criteria criteria) {

		if (StringUtils.contains(propertyValue, "..")) {
			String[] rangeValues = StringUtils.split(propertyValue, "..");
			criteria.between(propertyName, cleanNumeric(rangeValues[0]), cleanNumeric(rangeValues[1]));
		} else if (propertyValue.startsWith(">")) {
			criteria.gt(propertyName, cleanNumeric(propertyValue));
		} else if (propertyValue.startsWith("<")) {
			criteria.lt(propertyName, cleanNumeric(propertyValue));
		} else if (propertyValue.startsWith(">=")) {
			criteria.gte(propertyName, cleanNumeric(propertyValue));
		} else if (propertyValue.startsWith("<=")) {
			criteria.lte(propertyName, cleanNumeric(propertyValue));
		} else {
			criteria.eq(propertyName, cleanNumeric(propertyValue));
		}
	}

	private void addStringRangeCriteria(String propertyName, String propertyValue, Criteria criteria) {

		if (StringUtils.contains(propertyValue, "..")) {
			String[] rangeValues = StringUtils.split(propertyValue, "..");
			criteria.between(propertyName, rangeValues[0], rangeValues[1]);

			// To fix a bug related on number of digits issues for searching
			// String field with range operator
			Criteria orCriteria = new Criteria("N/A");
			orCriteria.gt(propertyName, rangeValues[0].length());
			criteria.or(orCriteria);
			criteria.lte(propertyName, rangeValues[1].length());

		} else if (propertyValue.startsWith(">")) {
			criteria.gt(propertyName, ObjectUtils.clean(propertyValue));

			// To fix a bug related on number of digits issues for searching
			// String field with range operator
			Criteria orCriteria = new Criteria("N/A");
			orCriteria.gt(propertyName, ObjectUtils.clean(propertyValue).length());
			criteria.or(orCriteria);

		} else if (propertyValue.startsWith("<")) {
			criteria.lt(propertyName, ObjectUtils.clean(propertyValue));

			// To fix a bug related on number of digits issues for searching
			// String field with range operator
			criteria.lte(propertyName, ObjectUtils.clean(propertyValue).length());
		} else if (propertyValue.startsWith(">=")) {
			criteria.gte(propertyName, ObjectUtils.clean(propertyValue));

			// To fix a bug related on number of digits issues for searching
			// String field with range operator
			criteria.gte(propertyName, ObjectUtils.clean(propertyValue).length());
		} else if (propertyValue.startsWith("<=")) {
			criteria.lte(propertyName, ObjectUtils.clean(propertyValue));

			// To fix a bug related on number of digits issues for searching
			// String field with range operator
			criteria.lte(propertyName, ObjectUtils.clean(propertyValue).length());
		}
	}

	/**
	 * @param dateTimeService
	 *            the dateTimeService to set
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
	
}
