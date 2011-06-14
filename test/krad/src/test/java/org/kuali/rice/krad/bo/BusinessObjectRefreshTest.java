/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.krad.bo;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.krad.test.document.bo.AccountManager;
import org.kuali.rice.shareddata.impl.county.CountyBo;
import org.kuali.rice.shareddata.impl.county.CountyId;
import org.kuali.rice.shareddata.impl.state.StateBo;
import org.kuali.rice.shareddata.impl.state.StateId;
import org.kuali.test.KRADTestCase;

/**
 * Tests how refreshing works for Business Objects 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BusinessObjectRefreshTest extends KRADTestCase {

	@Test
	public void testLazyRefreshField() {
		final String accountNumber = "a1";
		Account account = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Account.class, accountNumber);
		
		Assert.assertEquals("Retrieved account should have name a1", "a1", account.getName());
		Assert.assertEquals("Retrieved account should have a account manager with user name fred", "fred", account.getAccountManager().getUserName());
		
		account.setAmId(2L);
		account.refreshReferenceObject("accountManager");
		
		Assert.assertEquals("Account Manager should now have user name of fran", "fran", account.getAccountManager().getUserName());
		
	}
	
	@Test
	public void testLazyRefreshWholeObject() {
		final String accountNumber = "a1";
		Account account = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Account.class, accountNumber);
		
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
		AccountManager manager = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(AccountManager.class, new Long(fredManagerId));
		
		Assert.assertEquals("Retrieve manager should have a name 'fred'", "fred", manager.getUserName());
		Assert.assertEquals("Manager should have one account", new Integer(1), new Integer(manager.getAccounts().size()));
		
		final String accountNumber = "a2";
		Account account = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Account.class, accountNumber);

		account.setAmId(1L);
		account = (Account) KRADServiceLocator.getBusinessObjectService().save(account);
		
		manager.refreshReferenceObject("accounts");
		Assert.assertEquals("Manager should have one account", new Integer(2), new Integer(manager.getAccounts().size()));
	}
	
	@Test
	public void testEagerRefreshField() {
		final CountyId countyId = new CountyId("COCONINO", "US", "AZ");
		CountyBo county = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(CountyBo.class, countyId);
		
		final StateId arizonaStateId = new StateId("US", "AZ");
		final StateBo arizonaState = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(StateBo.class, arizonaStateId);
		
		Assert.assertEquals("On retrieval from database, state code should be AZ", arizonaState.getCode(), county.getState().getCode());
		Assert.assertEquals("On retrieval from database, state name should be ARIZONA", arizonaState.getName(), county.getState().getName());
		
		county.setStateCode("CA");
		county.setCode("VENTURA");
		county.refresh();
		
		final StateId californiaStateId = new StateId("US", "CA");
		final StateBo californiaState = KRADServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(StateBo.class, californiaStateId);
		
		Assert.assertEquals("Does eager fetching automatically refresh?", californiaState.getCode(), county.getState().getCode());
		Assert.assertEquals("On refresh, state name should be CALIFORNIA", californiaState.getName(), county.getState().getName());
	}
}
