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
package org.kuali.rice.kns.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.test.KNSTestCase;

/**
 * Tests the LookupResultsService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class LookupResultsServiceTest extends KNSTestCase {
	public final static String MOCK_PARAMETER_NMSPC = "KR-NS";
	public final static String MOCK_PARAMETER_DETAIL_TYPE_CODE = "All";
	public final static String MOCK_PARAMETER_NAME = "DATE_TO_STRING_FORMAT_FOR_FILE_NAME";
	public final static String MOCK_PERSON = "quickstart";

	/**
	 * Tests that lookup ids work
	 *
	 */
	@Test public void testLookupIds() {
		Map<String, String> parameterPK = new HashMap<String, String>();
		parameterPK.put("parameterNamespaceCode", MOCK_PARAMETER_NMSPC);
		parameterPK.put("parameterDetailTypeCode", MOCK_PARAMETER_DETAIL_TYPE_CODE);
		parameterPK.put("parameterName", MOCK_PARAMETER_NAME);
		final Parameter parameter = (Parameter)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(Parameter.class, parameterPK);
		final Person person = KIMServiceLocator.getPersonService().getPerson(LookupResultsServiceTest.MOCK_PERSON);
		final LookupResultsDDBo ddBo = new LookupResultsDDBo("horse");
		final LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
		
		org.junit.Assert.assertEquals("Parameter's lookup id should be its object id", parameter.getObjectId(), lookupResultsService.getLookupId(parameter));
		org.junit.Assert.assertNull("Person's lookup id should be null", lookupResultsService.getLookupId(person));
		org.junit.Assert.assertEquals("LookupResultsDDBo's lookup id should be a squashed PK String", "someValue-horse", lookupResultsService.getLookupId(ddBo));
	}
	
	/**
	 * Tests that PersistableBusinessObjectSearches work
	 *
	 */
	@Test public void testPersistableBusinessObjectSearch() {
		Map<String, String> parameterPK = new HashMap<String, String>();
		parameterPK.put("parameterNamespaceCode", MOCK_PARAMETER_NMSPC);
		parameterPK.put("parameterDetailTypeCode", MOCK_PARAMETER_DETAIL_TYPE_CODE);
		parameterPK.put("parameterName", MOCK_PARAMETER_NAME);
		final Parameter parameter = (Parameter)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(Parameter.class, parameterPK);
		final LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
		
		try {
			Set<String> parameterIds = new HashSet<String>();
			parameterIds.add(lookupResultsService.getLookupId(parameter));
			lookupResultsService.persistSelectedObjectIds("testPBOSearch", parameterIds, LookupResultsServiceTest.MOCK_PERSON);
			
			// now try to retrieve
			Collection<Parameter> retrievedParameters = lookupResultsService.retrieveSelectedResultBOs("testPBOSearch", Parameter.class, LookupResultsServiceTest.MOCK_PERSON);
			org.junit.Assert.assertNotNull("We have a collection of retrieved Parameters", retrievedParameters);
			org.junit.Assert.assertEquals("Retrieved parameters collection size is 1", new Integer(1), new Integer(retrievedParameters.size()));
			final Iterator<Parameter> parameterIterator = retrievedParameters.iterator();
			final Parameter retrievedParameter = parameterIterator.next();
			while (parameterIterator.hasNext()) {
				parameterIterator.next(); // just in case!!
			}
			org.junit.Assert.assertTrue("Parameter was one which was saved", retrievedParameter.getParameterNamespaceCode().equals(MOCK_PARAMETER_NMSPC) && retrievedParameter.getParameterDetailTypeCode().equals(MOCK_PARAMETER_DETAIL_TYPE_CODE) && retrievedParameter.getParameterName().equals(MOCK_PARAMETER_NAME));
		} catch (Exception e) {
			org.junit.Assert.fail("Exception was thrown: "+e.toString());
		}
	}
	
	/**
	 * Tests that PersistableBusinessObjectSearches work
	 *
	 */
	@Test public void testDataDictionaryBusinessObjectSearch() {
		final LookupResultsDDBo ddBo = new LookupResultsDDBo("gorilla");
		final LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
		
		try {
			Set<String> ddBoIds = new HashSet<String>();
			ddBoIds.add(lookupResultsService.getLookupId(ddBo));
			lookupResultsService.persistSelectedObjectIds("testDDBOSearch", ddBoIds, LookupResultsServiceTest.MOCK_PERSON);
			
			Collection<LookupResultsDDBo> retrievedDDBos = lookupResultsService.retrieveSelectedResultBOs("testDDBOSearch", LookupResultsDDBo.class, LookupResultsServiceTest.MOCK_PERSON);
			org.junit.Assert.assertNotNull("We have a collection of retrieved Parameters", retrievedDDBos);
			org.junit.Assert.assertEquals("Retrieved parameters collection size is 1", new Integer(1), new Integer(retrievedDDBos.size()));
			final Iterator<LookupResultsDDBo> ddBosIterator = retrievedDDBos.iterator();
			final LookupResultsDDBo retrievedDDBo = ddBosIterator.next();
			while (ddBosIterator.hasNext()) {
				ddBosIterator.next(); // just in case!!
			}
			org.junit.Assert.assertEquals("LookupResultsDDBo lookup worked as expected", "gorilla", retrievedDDBo.getSomeValue());
		} catch (Exception e) {
			org.junit.Assert.fail("Exception was thrown: "+e.toString());
		}
	}
	
	/**
	 * Tests that BO which doesn't have qualified support strategy throws an exception on search
	 *
	 */
	@Test public void testBadSearch() {
		boolean threwException = false;
		final LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
		try {
			lookupResultsService.retrieveSelectedResultBOs("test data2", PersonImpl.class, LookupResultsServiceTest.MOCK_PERSON);
		} catch (RuntimeException re) {
			threwException = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		org.junit.Assert.assertTrue("Exception should have been thrown", threwException);
	}
}
