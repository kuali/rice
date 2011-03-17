/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kns.test.document.bo.Account;
import org.kuali.rice.kns.test.document.bo.AccountManager;
import org.kuali.rice.kns.uif.service.impl.ExpressionEvaluatorServiceImpl;
import org.kuali.rice.test.BaseRiceTestCase;

/**
 * Test cases for the <code>ExpressionEvaluatorService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExpressionEvaluatorServiceTest extends BaseRiceTestCase {

	private ExpressionEvaluatorService expressionEvaluatorService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		expressionEvaluatorService = new ExpressionEvaluatorServiceImpl();
	}

	/**
	 * Test basic expressions (without variables) against the root context
	 */
	@Test
	public final void testDefaultContextExpressions() {
		Account account = getTestAccount();

		Map<String, Object> evaluationParameters = new HashMap<String, Object>();

		String expression = "Name '@{name}'";
		String evaluation = expressionEvaluatorService.evaluateExpressionTemplate(account, evaluationParameters,
				expression);
		assertEquals("Invalid evaluation of simple property expression", "Name 'Test Account'", evaluation);

		expression = "Account @{number} has name @{name}";
		evaluation = expressionEvaluatorService.evaluateExpressionTemplate(account, evaluationParameters, expression);
		assertEquals("Invalid evaluation of simple property expression", "Account 1 has name Test Account", evaluation);

		expression = "Account manager id @{accountManager.amId}";
		evaluation = expressionEvaluatorService.evaluateExpressionTemplate(account, evaluationParameters, expression);
		assertEquals("Invalid evaluation of nested property expression", "Account manager id 3", evaluation);

		expression = "Account manager account @{accountManager.accounts[0].number}";
		evaluation = expressionEvaluatorService.evaluateExpressionTemplate(account, evaluationParameters, expression);
		assertEquals("Invalid evaluation of nested list property expression", "Account manager account 1", evaluation);
	}

	protected Account getTestAccount() {
		Account account = new Account();

		account.setNumber("1");
		account.setName("Test Account");
		account.setAmId(new Long(3));

		AccountManager accountManager = new AccountManager();
		accountManager.setAmId(new Long(3));
		accountManager.setUserName("Joe Account");

		List<Account> managerAccounts = new ArrayList<Account>();
		managerAccounts.add(account);
		managerAccounts.add(account);

		accountManager.setAccounts(managerAccounts);
		account.setAccountManager(accountManager);

		return account;
	}

}
