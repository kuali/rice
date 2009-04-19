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

import javax.xml.namespace.QName;

import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.kuali.rice.kew.batch.KEWXmlDataLoaderLifecycle;
import org.kuali.rice.kim.bo.role.impl.KimPermissionTemplateImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;

/**
 * This is test base that should be used for all KIM unit tests. All non-web unit tests for KIM should extend this base
 * class.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@BaselineMode(Mode.ROLLBACK)
public abstract class KIMTestCase extends BaselineTestCase {

	private static final String KIM_MODULE_NAME = "kim";
	
	public KIMTestCase() {
		super(KIM_MODULE_NAME);
	}
	
	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> suiteLifecycles = super.getSuiteLifecycles();
		suiteLifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:org/kuali/rice/kim/test/DefaultSuiteTestData.xml"));
		return suiteLifecycles;
	}
	
	@Override
	protected Lifecycle getLoadApplicationLifecycle() {
    	SpringResourceLoader springResourceLoader = new SpringResourceLoader(new QName("KIMTestHarnessApplicationResourceLoader"), "classpath:KIMTestHarnessSpringBeans.xml");
    	springResourceLoader.setParentSpringResourceLoader(getTestHarnessSpringResourceLoader());
    	return springResourceLoader;
	}
	
	/**
	 * Override the standard per-test lifecycles to prepend ClearDatabaseLifecycle and ClearCacheLifecycle
	 * @see org.kuali.rice.test.RiceTestCase#getPerTestLifecycles()
	 */
	@Override
	protected List<Lifecycle> getPerTestLifecycles() {
		List<Lifecycle> lifecycles = super.getPerTestLifecycles();
		lifecycles.add(new ClearCacheLifecycle());
		return lifecycles;
	}
	
	public class ClearCacheLifecycle extends BaseLifecycle {
		@Override
		public void stop() throws Exception {
			KIMServiceLocator.getIdentityManagementService().flushAllCaches();
			KIMServiceLocator.getRoleManagementService().flushRoleCaches();
			super.stop();
		}

	}
	
	protected List<String> getPerTestTablesNotToClear() {
		List<String> tablesNotToClear = new ArrayList<String>();
		tablesNotToClear.add("KRIM_.*");
		tablesNotToClear.add("KRNS_.*");
		return tablesNotToClear;
	}


	/**
     * @see org.kuali.rice.test.RiceTestCase#getModuleName()
     */
	@Override
	protected String getModuleName() {
		return KIM_MODULE_NAME;
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
