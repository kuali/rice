/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kim.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.web.jetty.JettyServer;
import org.kuali.rice.kew.batch.KEWXmlDataLoaderLifecycle;
import org.kuali.rice.kim.bo.role.impl.KimPermissionTemplateImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.TestUtilities;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;

/**
 * This is test base that should be used for all KIM unit tests. All non-web unit tests for KIM should extend this base
 * class.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class KIMTestCase extends RiceTestCase {

	/**
     * This overridden method is responsible for loading up the kimtestharness from Spring.
     *
     * @see org.kuali.rice.test.RiceTestCase#getSuiteLifecycles()
     */
	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifeCycles = super.getSuiteLifecycles();
		JettyServer server = new JettyServer(9955, "/kim-test", "/../kim/src/test/webapp");
		server.setFailOnContextFailure(true);
		server.setTestMode(true);
        lifeCycles.add(server);
        lifeCycles.add(new InitializeGRL());
		return lifeCycles;
	}
	
	/**
	 * Override the standard per-test lifecycles to prepend ClearDatabaseLifecycle and ClearCacheLifecycle
	 * @see org.kuali.rice.test.RiceTestCase#getPerTestLifecycles()
	 */
	@Override
	protected List<Lifecycle> getPerTestLifecycles() {
		List<Lifecycle> lifecycles = new ArrayList<Lifecycle>();
		lifecycles.add(new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
		lifecycles.add(new ClearCacheLifecycle());
        lifecycles.add(new SQLDataLoaderLifecycle("classpath:org/kuali/rice/kim/test/DefaultTestData.sql", ";"));
		lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:org/kuali/rice/kim/test/DefaultTestData.xml"));
		lifecycles.add(getPerTestDataLoaderLifecycle());
		return lifecycles;
	}

	private class InitializeGRL extends BaseLifecycle {
        @Override
        public void start() throws Exception {
            TestUtilities.addWebappsToContext();
            super.start();
        }

    }
	
	/**
	 * Flushes the KEW cache(s)
	 */
	public class ClearCacheLifecycle extends BaseLifecycle {
		@Override
		public void stop() throws Exception {
			KIMServiceLocator.getIdentityManagementService().flushAllCaches();
			super.stop();
		}

	}
		
	/**
     * Returns the List of tables that should be cleared on every test run.
     */
	@Override
	protected List<String> getTablesToClear() {
		List<String> tablesToClear = new ArrayList<String>();
		tablesToClear.add("KR.*");
		return tablesToClear;
	}

	/**
     * At this time Derby for KIM is not supported.
     *
     * @see org.kuali.rice.test.RiceTestCase#getDerbySQLFileLocation()
     */
	@Override
	protected String getDerbySQLFileLocation() {
		return null;
	}

	/**
     * @see org.kuali.rice.test.RiceTestCase#getModuleName()
     */
	@Override
	protected String getModuleName() {
		return "kim";
	}
	
	protected KimTypeImpl getDefaultKimType() {
		KimTypeImpl type = KIMServiceLocator.getTypeInternalService().getKimTypeByName(KimConstants.KIM_TYPE_DEFAULT_NAMESPACE, KimConstants.KIM_TYPE_DEFAULT_NAME);
		if (type == null) {
			fail("Failed to locate the default Kim Type.");
		}
		return type;
	}
	
	protected KimPermissionTemplateImpl getDefaultPermissionTemplate() {
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		fieldValues.put("namespaceCode", "KUALI");
		fieldValues.put("name", "Default");
		KimPermissionTemplateImpl template = (KimPermissionTemplateImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimPermissionTemplateImpl.class, fieldValues);
		if (template == null) {
			fail("Failed to locate the default Permission Template.");
		}
		return template;
	}
	
	protected String getNewRoleId() {
		return getIdFromSequence("KRIM_ROLE_ID_S");
	}
	
	protected String getNewRoleMemberId() {
		return getIdFromSequence("KRIM_ROLE_MBR_ID_S");
	}
	
	protected String getNewRolePermissionId() {
		return getIdFromSequence("KRIM_ROLE_ID_S");
	}
	
	protected String getIdFromSequence(String sequenceName) {
		Long sequenceId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName);
		return "" + sequenceId;
	}
}
