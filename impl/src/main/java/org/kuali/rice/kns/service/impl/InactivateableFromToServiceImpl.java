/*
 * Copyright 2007-2010 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.DateTimeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.InactivateableFromTo;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.InactivateableFromToService;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Implementation of InactivateableFromToService that uses the lookup service for query implementation
 * 
 * @see org.kuali.rice.kns.service.InactivateableFromToService
 */
public class InactivateableFromToServiceImpl implements InactivateableFromToService {

	protected DateTimeService dateTimeService;
	protected BusinessObjectDictionaryService businessObjectDictionaryService;
	protected LookupService lookupService;

	/**
	 * Uses lookup service which will convert the active criteria to active begin/to field criteria
	 * 
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#findMatchingActive(java.lang.Class, java.util.Map)
	 */
	public List<InactivateableFromTo> findMatchingActive(Class<? extends InactivateableFromTo> clazz, Map fieldValues) {
		fieldValues.put(KNSPropertyConstants.ACTIVE, "true");

		return (List<InactivateableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * Uses lookup service which will convert the active criteria to active begin/to field criteria
	 * 
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#findMatchingActiveAsOfDate(java.lang.Class, java.util.Map,
	 *      java.util.Date)
	 */
	public List<InactivateableFromTo> findMatchingActiveAsOfDate(Class<? extends InactivateableFromTo> clazz,
			Map fieldValues, Date activeAsOfDate) {
		fieldValues.put(KNSPropertyConstants.ACTIVE, "true");
		fieldValues.put(KNSPropertyConstants.ACTIVE_AS_OF_DATE, dateTimeService.toDateString(activeAsOfDate));

		return (List<InactivateableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#filterOutNonActive(java.util.List)
	 */
	public List<InactivateableFromTo> filterOutNonActive(List<InactivateableFromTo> filterList) {
		return filterOutNonActive(filterList, dateTimeService.getCurrentDate());
	}

	/**
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#filterOutNonActive(java.util.List, java.util.Date)
	 */
	public List<InactivateableFromTo> filterOutNonActive(List<InactivateableFromTo> filterList, Date activeAsOfDate) {
		List<InactivateableFromTo> filteredList = new ArrayList<InactivateableFromTo>();

		for (InactivateableFromTo inactivateable : filterList) {
			inactivateable.setActiveAsOfDate(new java.sql.Timestamp(activeAsOfDate.getTime()));
			if (inactivateable.isActive()) {
				filteredList.add(inactivateable);
			}
		}

		return filteredList;
	}

	/**
	 * Uses lookup service which will convert the active and current criteria to active begin/to field criteria
	 * 
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#findMatchingCurrent(java.lang.Class, java.util.Map)
	 */
	public List<InactivateableFromTo> findMatchingCurrent(Class<? extends InactivateableFromTo> clazz,
			Map fieldValues) {
		fieldValues.put(KNSPropertyConstants.ACTIVE, "true");
		fieldValues.put(KNSPropertyConstants.CURRENT, "true");

		return (List<InactivateableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * Uses lookup service which will convert the active and current criteria to active begin/to field criteria
	 * 
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#findMatchingCurrent(java.lang.Class, java.util.Map, java.util.Date)
	 */
	public List<InactivateableFromTo> findMatchingCurrent(Class<? extends InactivateableFromTo> clazz,
			Map fieldValues, Date currentAsOfDate) {
		fieldValues.put(KNSPropertyConstants.ACTIVE, "true");
		fieldValues.put(KNSPropertyConstants.CURRENT, "true");
		fieldValues.put(KNSPropertyConstants.ACTIVE_AS_OF_DATE, dateTimeService.toDateString(currentAsOfDate));

		return (List<InactivateableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#filterOutNonCurrent(java.util.List)
	 */
	public List<InactivateableFromTo> filterOutNonCurrent(List<InactivateableFromTo> filterList) {
		return filterOutNonCurrent(filterList, dateTimeService.getCurrentDate());
	}

	/**
	 * @see org.kuali.rice.kns.service.InactivateableFromToService#filterOutNonCurrent(java.util.List, java.util.Date)
	 */
	public List<InactivateableFromTo> filterOutNonCurrent(List<InactivateableFromTo> filterList, Date currentAsOfDate) {
		List<InactivateableFromTo> activeList = filterOutNonActive(filterList, currentAsOfDate);

		if (activeList.isEmpty()) {
			return activeList;
		}

		List<InactivateableFromTo> currentList = new ArrayList<InactivateableFromTo>();

		List<String> groupByList = businessObjectDictionaryService.getGroupByAttributesForEffectiveDating(activeList
				.get(0).getClass());
		if (groupByList != null) {
			List<String> sortByList = new ArrayList<String>(groupByList);
			sortByList.add(KNSPropertyConstants.ACTIVE_FROM_DATE);

			// sort list so we get records together with same group by
			Collections.sort(activeList, new BeanPropertyComparator(sortByList, true));

			// reverse so we get max active begin date first within the group by
			Collections.reverse(activeList);

			String previousGroupByString = "";
			Date previousActiveFromDate = null;
			for (InactivateableFromTo inactivateable : activeList) {
				String groupByString = buildGroupByValueString((BusinessObject) inactivateable, groupByList);
				if (!StringUtils.equals(groupByString, previousGroupByString)) {
					// always add first record of new group by since list is sorted by active begin date descending
					currentList.add(inactivateable);
				}
				// active from date should not be null here since we are dealing with only active records
				else if (inactivateable.getActiveFromDate().equals(previousActiveFromDate)) {
					// have more than one record for the group by key with same active begin date, so they both are current
					currentList.add(inactivateable);
				}

				previousGroupByString = groupByString;
				previousActiveFromDate = inactivateable.getActiveFromDate();
			}
		} else {
			currentList = activeList;
		}

		return currentList;
	}

	/**
	 * Builds a string containing the values from the given business object for the fields in the given list, concatenated together using a
	 * bar. Null values are treated as an empty string
	 * 
	 * @param businessObject
	 *            - business object instance to get values from
	 * @param groupByList
	 *            - list of fields to get values for
	 * @return String
	 */
	protected String buildGroupByValueString(BusinessObject businessObject, List<String> groupByList) {
		String groupByValueString = "";

		for (String groupByField : groupByList) {
			Object fieldValue = ObjectUtils.getPropertyValue(businessObject, groupByField);
			groupByValueString += "|";
			if (fieldValue != null) {
				groupByValueString += fieldValue;
			}
		}

		return groupByValueString;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
		this.businessObjectDictionaryService = businessObjectDictionaryService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

}
