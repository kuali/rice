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
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.test.document.bo.Account;
import org.kuali.rice.kns.test.document.bo.AccountManager;
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
		final String accountNumber = "a1";
		Account account = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Account.class, accountNumber);
		
		Assert.assertEquals("Retrieved account should have name a1", "a1", account.getName());
		Assert.assertEquals("Retrieved account should have a account manager with user name fred", "fred", account.getAccountManager().getUserName());
		
		account.setAmId(2L);
		account.refreshReferenceObject("accountManager");
		
		Assert.assertEquals("Account Manager should now have user name of fran", "fran", account.getAccountManager().getUserName());
		
	}
	
	@Test
	public void testLazyRefreshWholeObject() {
		final String accountNumber = "a1";
		Account account = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Account.class, accountNumber);
		
		Assert.assertEquals("Retrieved account should have name a1", "a1", account.getName());
		Assert.assertEquals("Retrieved account should have a account manager with user name fred", "fred", account.getAccountManager().getUserName());
		
		account.setAmId(2L);
		account.refresh();
		
		Assert.assertEquals("Account Manager should now have user name of fran", "fran", account.getAccountManager().getUserName());
	}
	
	@Ignore // until BO extensions work with JPA
	@Test
	public void testLazyCollectionRefresh() {
		final Long fredManagerId = 1L;
		AccountManager manager = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(AccountManager.class, new Long(fredManagerId));
		
		Assert.assertEquals("Retrieve manager should have a name 'fred'", "fred", manager.getUserName());
		Assert.assertEquals("Manager should have one account", new Integer(1), new Integer(manager.getAccounts().size()));
		
		final String accountNumber = "a2";
		Account account = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Account.class, accountNumber);

		account.setAmId(1L);
		account = (Account) KNSServiceLocator.getBusinessObjectService().save(account);
		
		manager.refreshReferenceObject("accounts");
		Assert.assertEquals("Manager should have one account", new Integer(2), new Integer(manager.getAccounts().size()));
	}
	
	@Test
	public void testEagerRefreshField() {
		final CountyImplId countyId = new CountyImplId("US", "COCONINO", "AZ");
		County county = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(CountyImpl.class, countyId);
		
		final StateImplId arizonaStateId = new StateImplId("US", "AZ");
		final State arizonaState = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(StateImpl.class, arizonaStateId);
		
		Assert.assertEquals("On retrieval from database, state code should be AZ", arizonaState.getPostalStateCode(), county.getState().getPostalStateCode());
		Assert.assertEquals("On retrieval from database, state name should be ARIZONA", arizonaState.getPostalStateName(), county.getState().getPostalStateName());
		
		county.setStateCode("CA");
		county.setCountyCode("VENTURA");
		county.refresh();
		
		final StateImplId californiaStateId = new StateImplId("US", "CA");
		final State californiaState = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(StateImpl.class, californiaStateId);
		
		Assert.assertEquals("Does eager fetching automatically refresh?", californiaState.getPostalStateCode(), county.getState().getPostalStateCode());
		Assert.assertEquals("On refresh, state name should be CALIFORNIA", californiaState.getPostalStateName(), county.getState().getPostalStateName());
	}
}
