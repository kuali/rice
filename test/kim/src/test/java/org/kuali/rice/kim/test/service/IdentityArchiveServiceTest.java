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

package org.kuali.rice.kim.test.service;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityDefaultInfoCacheImpl;
import org.kuali.rice.kim.service.IdentityArchiveService;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.impl.IdentityArchiveServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kns.service.KNSServiceLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the IdentityArchiveService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Ignore
public class IdentityArchiveServiceTest extends KIMTestCase {

	private IdentityArchiveService identityArchiveService;

	public void setUp() throws Exception {
		super.setUp();
		final Map<String, Object> emptyMap = Collections.emptyMap();
		KNSServiceLocator.getBusinessObjectService().deleteMatching(KimEntityDefaultInfoCacheImpl.class, emptyMap);
		if (null == identityArchiveService) {
			identityArchiveService = KIMServiceLocatorInternal.getIdentityArchiveService();
		}
	}

	/**
	 * This tests
	 * <ol><li>trying to retrieve a non-existant {@link KimEntityDefaultInfo}
	 * <li>saving a {@link KimEntityDefaultInfo} and retrieving it.
	 * </ol>
	 * This test is specific to {@link IdentityArchiveServiceImpl}
	 */
	@Test
	public void testArchiveFlushesWhenQueueIsFull() throws Exception {
		final int maxWriteQueueSize =
			Integer.valueOf(ConfigContext.getCurrentContextConfig().getProperty("kim.identityArchiveServiceImpl.maxWriteQueueSize"));

		List<KimEntityDefaultInfo> added = new ArrayList<KimEntityDefaultInfo>();

		// exceed the max write queue size to initiate a flush
		for (int i=1; i<=maxWriteQueueSize; i++) {
			MinimalKEDIBuilder builder = new MinimalKEDIBuilder("bogusUser" + i);
			builder.setEntityId("bogusUser" + i);
			KimEntityDefaultInfo bogusUserInfo = builder.build();

			KimEntityDefaultInfo retrieved = identityArchiveService.getEntityDefaultInfoFromArchiveByPrincipalId(builder.getPrincipalId());
			assertNull(retrieved);
			retrieved = identityArchiveService.getEntityDefaultInfoFromArchiveByPrincipalName(builder.getPrincipalName());
			assertNull(retrieved);
			retrieved = identityArchiveService.getEntityDefaultInfoFromArchive(builder.getEntityId());
			assertNull(retrieved);

			identityArchiveService.saveDefaultInfoToArchive(bogusUserInfo);
			added.add(bogusUserInfo);
		}

		// give it a second to flush
		log.info("Sleeping, hoping for a flush to occur!");
		Thread.sleep(1000);
		log.info("Done sleeping!");

		// these should have been flushed by now, test retrieval

		for (KimEntityDefaultInfo kedi : added) {
			// retrieve it every way we can
			KimEntityDefaultInfo retrieved = identityArchiveService.getEntityDefaultInfoFromArchiveByPrincipalId(kedi.getPrincipals().get(0).getPrincipalId());
			assertTrue(kedi.getPrincipals().get(0).getPrincipalId().equals(retrieved.getPrincipals().get(0).getPrincipalId()));
			retrieved = identityArchiveService.getEntityDefaultInfoFromArchiveByPrincipalName(kedi.getPrincipals().get(0).getPrincipalName());
			assertTrue(kedi.getPrincipals().get(0).getPrincipalId().equals(retrieved.getPrincipals().get(0).getPrincipalId()));
			retrieved = identityArchiveService.getEntityDefaultInfoFromArchive(kedi.getEntityId());
			assertTrue(kedi.getPrincipals().get(0).getPrincipalId().equals(retrieved.getPrincipals().get(0).getPrincipalId()));
		}
	}

	private static class MinimalKEDIBuilder {
		private String entityId;
		private String principalId;
		private String principalName;
		private Boolean active;

		public MinimalKEDIBuilder(String name) {
			entityId = UUID.randomUUID().toString();
			principalId = principalName = name;
		}

		public KimEntityDefaultInfo build() {
			if (entityId == null) entityId = UUID.randomUUID().toString();
			if (principalId == null) principalId = UUID.randomUUID().toString();
			if (principalName == null) principalName = principalId;
			if (active == null) active = true;

			KimPrincipalInfo principal = new KimPrincipalInfo();
			principal.setActive(active);
			principal.setEntityId(entityId);
			principal.setPrincipalId(principalId);
			principal.setPrincipalName(principalName);

			KimEntityDefaultInfo kedi = new KimEntityDefaultInfo();
			kedi.setPrincipals(Collections.singletonList(principal));
			kedi.setEntityId(entityId);

			return kedi;
		}

		/**
		 * @return the entityId
		 */
		public String getEntityId() {
			return this.entityId;
		}

		/**
		 * @param entityId the entityId to set
		 */
		public void setEntityId(String entityId) {
			this.entityId = entityId;
		}

		/**
		 * @return the principalId
		 */
		public String getPrincipalId() {
			return this.principalId;
		}

		/**
		 * @param principalId the principalId to set
		 */
		public void setPrincipalId(String principalId) {
			this.principalId = principalId;
		}

		/**
		 * @return the principalName
		 */
		public String getPrincipalName() {
			return this.principalName;
		}

		/**
		 * @param principalName the principalName to set
		 */
		public void setPrincipalName(String principalName) {
			this.principalName = principalName;
		}

		/**
		 * @return the active
		 */
		public Boolean getActive() {
			return this.active;
		}

		/**
		 * @param active the active to set
		 */
		public void setActive(Boolean active) {
			this.active = active;
		}


	}

}
