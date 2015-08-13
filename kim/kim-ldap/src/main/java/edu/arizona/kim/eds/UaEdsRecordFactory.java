/*
 * Copyright 2012 The Kuali Foundation.
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
package edu.arizona.kim.eds;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.ldap.core.DirContextOperations;

public class UaEdsRecordFactory {

	private static final String DCC_EMPLOYEE_TYPE = "Z";
	private static final String EDS_CONSTANTS = "edsConstants";
	private static final Logger LOG = Logger.getLogger(UaEdsRecordFactory.class);
	private static final ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
	private static final UaEdsConstants edsConstants = GlobalResourceLoader.<UaEdsConstants> getService(EDS_CONSTANTS);

	/**
	 * This method will hyrate and EdsRecord from e Dirctory context.
	 * 
	 * @param context
	 *            The DirectoryContext that contains the EDS fields form which
	 *            we pull.
	 * @return An EdsRecord will be returned if one or more relevant
	 *         affiliations are found, otherwise null.
	 */
	public static UaEdsRecord getEdsRecord(DirContextOperations context) {

		// Our return result
		UaAffiliationComparator affiliationComparator = new UaAffiliationComparator(getOrderedAffs(), getOrderedActiveIndicators());
		UaEdsRecord edsRecord = new UaEdsRecord(affiliationComparator);

		// For record hydration
		Set<String> activeIndicators = getValueSetForParameter(edsConstants.getEdsOrderedActiveStatusIndicatorsParamKey());
		List<String> orderedAffStrings = getOrderedAffs();

		// Hydrate common attributes
		hydrateCommonAttributes(context, edsRecord);

		// Hydrate employee specific attributes
		hydrateEmplAttributes(context, edsRecord, orderedAffStrings, activeIndicators);

		// Hydrate DCC specific attributes
		hydrateDccAttributes(context, edsRecord, orderedAffStrings, activeIndicators);

		// Sanity check, make sure we have at least one affiliation
		SortedSet<UaEdsAffiliation> orderedAffs = edsRecord.getOrderedAffiliations();
		if (orderedAffs.isEmpty()) {
			return null;
		}

		// Now that all affiliations are present, set convenience field
		// 'isActive' based off of best affiliation
		UaEdsAffiliation bestAffiliation = edsRecord.getBestAffiliation();
		String status = bestAffiliation.getStatusCode();
		boolean isActive = activeIndicators.contains(status);
		edsRecord.setIsActive(isActive);

		// All done
		return edsRecord;

	}

	private static void hydrateCommonAttributes(DirContextOperations context, UaEdsRecord edsRecord) {

		String uaId = context.getStringAttribute(edsConstants.getUaIdContextKey());
		String uid = context.getStringAttribute(edsConstants.getUidContextKey());
		String emplId = context.getStringAttribute(edsConstants.getEmplIdContextKey());
		String givenName = context.getStringAttribute(edsConstants.getGivenNameContextKey());
		String cn = context.getStringAttribute(edsConstants.getCnContextKey());
		String sn = context.getStringAttribute(edsConstants.getSnContextKey());
		String mail = context.getStringAttribute(edsConstants.getMailContextKey());
		String employeePoBox = context.getStringAttribute(edsConstants.getEmployeePoBoxContextKey());
		String employeeCity = context.getStringAttribute(edsConstants.getEmployeeCityContextKey());
		String employeeState = context.getStringAttribute(edsConstants.getEmployeeStateContextKey());
		@SuppressWarnings("unused")
		String employeeZip = context.getStringAttribute(edsConstants.getEmployeeZipContextKey());

		edsRecord.setUaId(uaId);
		edsRecord.setUid(uid);
		edsRecord.setEmplId(emplId);
		edsRecord.setGivenName(givenName);
		edsRecord.setCn(cn);
		edsRecord.setSn(sn);
		edsRecord.setMail(mail);
		edsRecord.setEmployeePoBox(employeePoBox);
		edsRecord.setEmployeeCity(employeeCity);
		edsRecord.setEmployeeState(employeeState);
		edsRecord.setEmployeeState(employeeState);
	}

	private static void hydrateEmplAttributes(DirContextOperations context, UaEdsRecord edsRecord, List<String> orderedAffStrings, Set<String> activeIndicators) {

		// Pull all affiliations for this user from EDS. This might also have
		// 'dcc', but it is not a leaf node, so will be dropped
		String[] personEmplAffiliations = context.getStringAttributes(edsConstants.getPersonAffiliationContextKey());

		if (personEmplAffiliations != null) {

			List<String> nonEmployeeAffs = getValueListForParameter(edsConstants.getEdsNonEmployeeAffsParamKey());

			// Collect KFS respected/unrespected employee affiliations; this
			// will drop any non-leaf subtypes
			for (String affString : personEmplAffiliations) {

				// Hydrate the affiliation
				String deptCode = null;
				String deptName = null;
				String employeeStatus = null;
				String employeeType = null;
				if (!nonEmployeeAffs.contains(affString)) {
					// Non-employee affiliations don't have these fields
					deptCode = context.getStringAttribute(edsConstants.getEmployeeDeptCodeContextKey());
					deptName = context.getStringAttribute(edsConstants.getEmployeePrimaryDeptNameContextKey());
					employeeStatus = context.getStringAttribute(edsConstants.getEmployeeStatusCodeContextKey());
					employeeType = context.getStringAttribute(edsConstants.getEmployeeTypeCodeContextKey());
				} else {
					// Need this for nonEmployeeTypes, as the UI won't show them
					// otherwise
					// records for 'student' or 'retiree'
					employeeStatus = "A";
				}
				UaEdsAffiliation edsAff = new UaEdsAffiliation(affString, deptCode, deptName, employeeStatus, employeeType);

				// Set the status for convenience
				edsAff.setActive(activeIndicators.contains(edsAff.getStatusCode()));

				// Add it if its one of the ones KFS cares about
				if (orderedAffStrings.contains(affString)) {
					edsRecord.addAffiliation(edsAff);
				}

			}// for

		}// if

	}// hydrateEmplAttributes

	private static void hydrateDccAttributes(DirContextOperations context, UaEdsRecord edsRecord, List<String> orderedAffStrings, Set<String> activeIndicators) {

		// Pull all DCC relations for this user from EDS
		String[] dccRelationStrings = context.getStringAttributes(edsConstants.getDccRelationContextKey());

		// Collect all respected/unrespected dcc affiliations
		if (dccRelationStrings != null) {

			for (String dccRelationString : dccRelationStrings) {
				UaEdsDccRelation dccRelation = new UaEdsDccRelation(dccRelationString);
				UaEdsAffiliation edsAff = new UaEdsAffiliation(dccRelation.getType(), dccRelation.getDeptCode(), dccRelation.getDepartmentName(), dccRelation.getStatus(), UaEdsRecordFactory.DCC_EMPLOYEE_TYPE);
				edsAff.setActive(activeIndicators.contains(edsAff.getStatusCode()));
				if (orderedAffStrings.contains(edsAff.getAffiliatonString())) {
					edsRecord.addAffiliation(edsAff);
				}
			}

		}

	}

	/*
	 * Guard employeeType from EDS, HR has a habit of adding new employee types
	 * without telling anyone
	 */
	@SuppressWarnings("unused")
	private static String guardEmployeeTypeCode(String employeeTypeCode) {

		String defaultCode = getStringForParameter(edsConstants.getEdsDefaultEmployeeTypeParamKey());

		if (employeeTypeCode == null) {
			// Short circuit to cut down on excessive logging in the next 'if'
			return defaultCode;
		}

		Set<String> edsEmployeeTypeCodes = getValueSetForParameter(edsConstants.getEdsEmployeeTypesParamKey());
		if (!edsEmployeeTypeCodes.contains(employeeTypeCode)) {
			if (employeeTypeCode != null) {
				String message = String.format("Did not find EDS employeeType '%s' in the KFS param 'EDS_EMPLOYEE_TYPES'; setting to '%s' instead.", employeeTypeCode, defaultCode);
				LOG.warn(message);
			}
			return defaultCode;
		}

		// Code is found in approved set, return it
		return employeeTypeCode;
	}

	@SuppressWarnings("unused")
	private static Set<String> getRespectedAndOrderedAffs() {
		return getValueSetForParameter(edsConstants.getEdsRespectedAndOrderedAffsParamKey());
	}

	private static List<String> getOrderedAffs() {

		// The result
		List<String> orderedAffs = new LinkedList<String>();

		// KFS respected 'employee' affiliations
		List<String> respectedAndOrderedEmplAffs = getValueListForParameter(edsConstants.getEdsRespectedAndOrderedAffsParamKey());
		for (String emplAff : respectedAndOrderedEmplAffs) {
			orderedAffs.add(emplAff);
		}

		// KFS unrespected affiliations
		List<String> unrespectedAndOrderedAffs = getValueListForParameter(edsConstants.getEdsUnrespectedAndOrderedAffsParamKey());
		for (String unrespectedAff : unrespectedAndOrderedAffs) {
			orderedAffs.add(unrespectedAff);
		}

		return orderedAffs;
	}

	private static List<String> getOrderedActiveIndicators() {
		return getValueListForParameter(edsConstants.getEdsOrderedActiveStatusIndicatorsParamKey());
	}

	// Maintain order as found in the param
	private static List<String> getValueListForParameter(String parameterKey) {
		String listAsCommaString = getStringForParameter(parameterKey);
		String[] listAsArray = listAsCommaString.split(edsConstants.getKfsParamDelimiter());
		List<String> resultList = new LinkedList<String>();
		for (String result : listAsArray) {
			resultList.add(result);
		}
		return resultList;
	}

	/*
	 * Helper to pull params from KFS params
	 */
	private static Set<String> getValueSetForParameter(String parameterKey) {
		String listAsCommaString = getStringForParameter(parameterKey);
		String[] listAsArray = listAsCommaString.split(edsConstants.getKfsParamDelimiter());
		Set<String> resultSet = new HashSet<String>();
		for (String result : listAsArray) {
			resultSet.add(result);
		}
		return resultSet;
	}

	private static String getStringForParameter(String parameterKey) {
		String namespaceCode = edsConstants.getParameterNamespaceCode();
		String detailTypeCode = edsConstants.getParameterDetailTypeCode();
		Parameter parameter = parameterService.getParameter(namespaceCode, detailTypeCode, parameterKey);
		if (parameter == null) {
			String message = String.format("ParameterService returned null for parameterKey: '%s', namespaceCode: '%s', detailTypeCode: '%s'", parameterKey, namespaceCode, detailTypeCode);
			throw new RuntimeException(message);
		}
		return parameter.getValue();
	}

}
