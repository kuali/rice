/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.test.KNSTestCase;
import org.kuali.test.KNSWithTestSpringContext;

/**
 * Tests how refreshing works for Business Objects 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@KNSWithTestSpringContext
public class BusinessObjectRefreshTest extends KNSTestCase {

	@Test
	public void testLazyRefreshField() {
		final ParameterId knsParmId = new ParameterId("KR-NS", "All", "STRING_TO_TIMESTAMP_FORMATS", "KUALI");
		Parameter knsParam = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Parameter.class, knsParmId);
		
		final Namespace namespace = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Namespace.class, "KR-NS");
		Assert.assertEquals("Retrieved namespace codes should be equal", namespace.getNamespaceCode(), knsParam.getParameterNamespace().getNamespaceCode());
		
		knsParam.setParameterDetailTypeCode("Batch");
		knsParam.setParameterName("ACTIVE_FILE_TYPES");
		
		final ParameterDetailTypeId batchParmDetailId = new ParameterDetailTypeId("KR-NS", "Batch");
		Assert.assertEquals("Parameter detail type codes should be equal", batchParmDetailId.getParameterDetailTypeCode(), knsParam.getParameterDetailType().getParameterDetailTypeCode());
		
		knsParam.setParameterDetailTypeCode("All");
		knsParam.setParameterName("STRING_TO_TIMESTAMP_FORMATS");
		//knsParam.refreshReferenceObject("parameterDetailType");
		
		final ParameterDetailTypeId allKnsParmDetailId = new ParameterDetailTypeId("KR-NS", "All");
		Assert.assertEquals("Parameter detail type codes should be equal after refresh", allKnsParmDetailId.getParameterDetailTypeCode(), knsParam.getParameterDetailType().getParameterDetailTypeCode());
	}
	
	@Test
	public void testLazyRefreshWholeObject() {
		final ParameterId knsParmId = new ParameterId("KR-NS", "All", "STRING_TO_TIMESTAMP_FORMATS", "KUALI");
		Parameter knsParam = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Parameter.class, knsParmId);
		
		final Namespace namespace = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Namespace.class, "KR-NS");
		Assert.assertEquals("Retrieved namespace codes should be equal", namespace.getNamespaceCode(), knsParam.getParameterNamespace().getNamespaceCode());
		
		//knsParam.refresh();
		
		knsParam.setParameterDetailTypeCode("Batch");
		knsParam.setParameterName("ACTIVE_FILE_TYPES");
		
		final ParameterDetailTypeId knsParmDetailId = new ParameterDetailTypeId("KR-NS", "Batch");
		Assert.assertEquals("Parameter detail type code should not have been reset", "Batch", knsParmDetailId.getParameterDetailTypeCode());
		Assert.assertEquals("Parameter detail type codes should be equal", knsParmDetailId.getParameterDetailTypeCode(), knsParam.getParameterDetailType().getParameterDetailTypeCode());
	}
	
	@Test
	public void testEagerRefreshField() {
		final CountyImplId countyId = new CountyImplId("US", "COCONINO", "AZ");
		County county = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(CountyImpl.class, countyId);
		
		final StateImplId arizonaStateId = new StateImplId("US", "AZ");
		final State arizonaState = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(StateImpl.class, arizonaStateId);
		
		Assert.assertEquals("On retrieval from database, state should be Arizona", arizonaState.getPostalStateCode(), county.getState().getPostalStateCode());
		
		county.setStateCode("CA");
		county.setCountyCode("VENTURA");
		
		final StateImplId californiaStateId = new StateImplId("US", "CA");
		final State californiaState = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(StateImpl.class, californiaStateId);
		
		Assert.assertEquals("Does eager fetching automatically refresh?", californiaState.getPostalStateCode(), county.getState().getPostalStateCode());
	}
}
