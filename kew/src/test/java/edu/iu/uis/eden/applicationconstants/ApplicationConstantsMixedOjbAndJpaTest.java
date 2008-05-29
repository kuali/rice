/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
// Created on Dec 14, 2005
package edu.iu.uis.eden.applicationconstants;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.applicationconstants.dao.ApplicationConstantsDAO;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsMixedOjbAndJpaTest extends KEWTestCase {
	
	private ApplicationConstantsDAO jpaDao;
	private ApplicationConstantsDAO ojbDao;
	
	@Override
	protected void setUpTransactionInternal() throws Exception {
		this.jpaDao = (ApplicationConstantsDAO) KEWServiceLocator.getBean("enApplicationConstantsDAO");
		this.ojbDao = (ApplicationConstantsDAO) KEWServiceLocator.getBean("enApplicationConstantsOJBDAO");
		super.setUpTransactionInternal();
	}

	@Test
	public void testRollbackAcrossOjbAndJpa() {		
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			public void doInTransactionWithoutResult(final TransactionStatus status) {
				ApplicationConstant apConOjb = ojbDao.findByName("Feature.CheckRouteLogAuthentication.CheckFuture");
				ApplicationConstant apConJpa = jpaDao.findByName("RouteQueue.maxRetryAttempts");
		
				apConOjb.setApplicationConstantValue("ojb-blah"); // was 'true'
				apConJpa.setApplicationConstantValue("jpa-blah"); // was '0'
		
				jpaDao.saveConstant(apConJpa);		
				ojbDao.saveConstant(apConOjb);

				status.setRollbackOnly();
			}
		});
		
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			public void doInTransactionWithoutResult(final TransactionStatus status) {
				assertEquals("OJB did not rollback", ojbDao.findByName("Feature.CheckRouteLogAuthentication.CheckFuture").getApplicationConstantValue(), "true");
				assertEquals("JPA did not rollback", jpaDao.findByName("RouteQueue.maxRetryAttempts").getApplicationConstantValue(), "0");		
			}
		});
	}

	@Test
	public void testCommitAcrossOjbAndJpa() {		
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			public void doInTransactionWithoutResult(final TransactionStatus status) {
				ApplicationConstant apConOjb = ojbDao.findByName("Feature.CheckRouteLogAuthentication.CheckFuture");
				ApplicationConstant apConJpa = jpaDao.findByName("RouteQueue.maxRetryAttempts");
		
				apConOjb.setApplicationConstantValue("ojb-update"); // was 'true'
				apConJpa.setApplicationConstantValue("jpa-update"); // was '0'
		
				jpaDao.saveConstant(apConJpa);		
				ojbDao.saveConstant(apConOjb);
			}
		});
		
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			public void doInTransactionWithoutResult(final TransactionStatus status) {
				assertEquals("OJB did not commit", ojbDao.findByName("Feature.CheckRouteLogAuthentication.CheckFuture").getApplicationConstantValue(), "ojb-update");
				assertEquals("JPA did not commit", jpaDao.findByName("RouteQueue.maxRetryAttempts").getApplicationConstantValue(), "jpa-update");		
			}
		});
	}

}