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
package org.kuali.rice.kim.test.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionDetailsInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AuthorizationServiceImplTest extends RiceTestCase {

	private PermissionService permissionService;
	private RoleService roleService;

	private String contextName = "/knstest";

	private String relativeWebappRoot = "/../web/src/main/webapp";

	private String testConfigFilename = "classpath:META-INF/kim-test-config.xml";

	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifecycles = super.getSuiteLifecycles();
		lifecycles.add(new Lifecycle() {
			boolean started = false;

			public boolean isStarted() {
				return this.started;
			}

			public void start() throws Exception {
				System.setProperty(KEWConstants.BOOTSTRAP_SPRING_FILE, "SampleAppBeans-test.xml");
				ConfigFactoryBean.CONFIG_OVERRIDE_LOCATION = testConfigFilename;
				//new SQLDataLoaderLifecycle(sqlFilename, sqlDelimiter).start();
				new JettyServerLifecycle(HtmlUnitUtil.getPort(), contextName, relativeWebappRoot).start();
				//new KEWXmlDataLoaderLifecycle(xmlFilename).start();
				System.getProperties().remove(KEWConstants.BOOTSTRAP_SPRING_FILE);
				this.started = true;
			}

			public void stop() throws Exception {
				this.started = false;
			}

		});
		return lifecycles;
	}

	@Override
	protected String getModuleName() {
		return "kim";
	}

	@Override
	protected List<Lifecycle> getDefaultSuiteLifecycles() {
		List<Lifecycle> lifecycles = getInitialLifecycles();
		return lifecycles;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		permissionService = (PermissionService)GlobalResourceLoader.getService(new QName("KIM", "kimPermissionService"));
		roleService = (RoleService)GlobalResourceLoader.getService(new QName("KIM", "kimRoleService"));
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testRoleMembership() {
		KimRoleInfo role = roleService.getRole( "r2" );
		assertNotNull( "r2 must exist", role );
		ArrayList<String> roleList = new ArrayList<String>( 1 );
		roleList.add( "r2" );
		Collection<RoleMembershipInfo> principalIds = roleService.getRoleMembers( roleList, null );
		System.out.println( "r2: " + principalIds );
		assertNotNull( "returned list may not be null", principalIds );
		assertFalse( "list must not be empty", principalIds.isEmpty() );
		assertTrue( "p3 must belong to role", principalIds.contains("p3") );
		assertTrue( "p2 must belong to role (assigned via group)", principalIds.contains("p2") );
		//assertEquals("list must be unique (no duplicates)", new HashSet<RoleMembershipInfo>( principalIds ).size(), principalIds.size() );
		assertTrue( "p1 must belong to r2 (via r1)", principalIds.contains("p1") );
		
		role = roleService.getRole( "r1" );
		assertNotNull( "r1 must exist", role );
		roleList.clear();
		roleList.add( "r1" );
		principalIds = roleService.getRoleMembers( roleList, null );
		assertNotNull( "returned list may not be null", principalIds );
		System.out.println( "r1: " + principalIds );
		assertTrue( "p1 must belong to r1 (directly)", principalIds.contains("p1") );
		assertFalse( "p3 must not belong to r1 (higher role)", principalIds.contains("p3") );
		assertFalse( "p2 must not belong to r1 (higher role)", principalIds.contains("p2") );
	}
	
	@Test
	public void testRoleImplication() {
		List<String> impliedRoles = roleService.getImpliedRoleIds("r1");
		System.out.println( impliedRoles );
		assertNotNull( "implied role list may not be null", impliedRoles );
		assertFalse( "implied roles list must not be empty", impliedRoles.isEmpty() );
		assertTrue( "list must contain the source role", impliedRoles.contains("r1") );
		assertTrue( "list must contain r2", impliedRoles.contains("r2") );
		
		impliedRoles = roleService.getImpliedRoleIds("r2");
		assertTrue( "list must contain the source role", impliedRoles.contains("r2") );
		assertFalse( "list must not contain r1", impliedRoles.contains("r1") );
	}
	
//	@Test
//	public void testGetPermissionsForRole() {
//		List<PermissionDetailInfo> perms = authorizationService.getPermissionsForRole( "r1" );
//		System.out.println( "r1: " + perms );
//		assertTrue( "r1 must have perm1 (direct)", hasPermission( perms, "perm1" ) );
//		assertTrue( "r1 must have perm2 (direct)", hasPermission( perms, "perm2" ) );
//		assertTrue( "r1 must have perm3 (via r2)", hasPermission( perms, "perm3" ) );
//		perms = authorizationService.getPermissionsForRole( "r2" );
//		System.out.println( "r2: " + perms );
//		assertTrue( "r2 must have perm3 (direct)", hasPermission( perms, "perm3" ) );
//		assertFalse( "r2 must not have perm1", hasPermission( perms, "perm1" ) );
//		assertFalse( "r2 must not have perm2", hasPermission( perms, "perm2" ) );
//	}
	
	@Test
	public void testHasPermission() {
		
		assertTrue( "p1 must have perm1 (via r1)", permissionService.hasPermission( "KR-NS", "p1", "perm1", null ) );		
		assertTrue( "p1 must have perm2 (via r1)", permissionService.hasPermission( "KR-NS", "p1", "perm2", null ) );
		assertTrue( "p1 must have perm3 (via r2)", permissionService.hasPermission( "KR-NS", "p1", "perm3", null ) );
		assertTrue( "p3 must have perm3 (via r2)", permissionService.hasPermission( "KR-NS", "p3", "perm3", null ) );
		assertFalse( "p3 must not have perm1", permissionService.hasPermission( "KR-NS", "p3", "perm1", null ) );
		assertFalse( "p3 must not have perm2", permissionService.hasPermission( "KR-NS", "p3", "perm2", null ) );
	}
	
	protected boolean hasPermission( List<PermissionDetailsInfo> perms, String permissionId ) {
		for ( PermissionDetailsInfo perm : perms ) {
			if ( perm.getPermissionId().equals( permissionId ) ) {
				return true;
			}
		}
		return false;
	}
	// test that only active roles/permissions are used
	// test that only roles attached to active groups are returned
	// check that implied/implying lists are correct
	// check qualification matching
	// need hierarchical test for qualification matching
	// check namespace filters
	
	// non-qualified role/permission checks
	// qualified role/permission checks
	// add type services in test spring startup? - how in rice?
	
}
