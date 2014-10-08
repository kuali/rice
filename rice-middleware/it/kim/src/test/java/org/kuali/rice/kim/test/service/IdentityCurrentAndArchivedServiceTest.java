/*
 * Copyright 2006-2014 The Kuali Foundation
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

import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.impl.identity.EntityDefaultInfoCacheBo;
import org.kuali.rice.kim.impl.identity.IdentityArchiveService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit test for the IdentityCurrentAndArchivedService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class IdentityCurrentAndArchivedServiceTest extends KIMTestCase {

    public static final String KIM_IDENTITY_SERVICE = "kimIdentityService";
    public static final String KIM_IDENTITY_ARCHIVE_SERVICE = "kimIdentityArchiveService";


    private IdentityService identityService;
    private IdentityArchiveService identityArchiveService;

    public static IdentityArchiveService getIdentityArchiveService() {
        return GlobalResourceLoader.getService(KIM_IDENTITY_ARCHIVE_SERVICE);
    }
    public static IdentityService getIdentityService() {
        return GlobalResourceLoader.getService(KIM_IDENTITY_SERVICE);
    }

    public void setUp() throws Exception {
        super.setUp();
        if (null == identityService) {
            identityService = getIdentityService();
        }

        if (null == identityArchiveService) {
            identityArchiveService = getIdentityArchiveService();
        }
    }

    @Test
    public void testGetEntityDefault() throws Exception {

        MinimalKEDIBuilder builder = new MinimalKEDIBuilder("bogususer");
        builder.setEntityId("bogususer");
        EntityDefault bogusUserInfo = builder.build();

        //Create a record in KRIM_ENTITY_CACHE_T
        identityArchiveService.saveEntityDefaultToArchive(bogusUserInfo);
        identityArchiveService.flushToArchive();

        // retrieve using the archiveService to make sure its in the KRIM_ENTITY_CACHE_T
        EntityDefault retrieved = identityArchiveService.getEntityDefaultFromArchiveByPrincipalId(
                builder.getPrincipalId());
        assertNotNull("no value retrieved for principalId: " + retrieved.getPrincipals().get(0).getPrincipalId(), retrieved);

        // retrieve it using  the identity current and archived Service  should not use cache
        retrieved = identityService.getEntityDefaultByPrincipalId("bogususer");
        assertNotNull("no value retrieved for principalId: " + retrieved.getPrincipals().get(0).getPrincipalId(), retrieved);

        // retrieve again - should use cache
        EntityDefault retrieveAgain = identityService.getEntityDefaultByPrincipalId("bogususer");
        assertNotNull("no value retrieved for principalId: " + retrieveAgain.getPrincipals().get(0).getPrincipalId(), retrieveAgain);


    }


    @SuppressWarnings("unused")
    private static class MinimalKEDIBuilder {
        private String entityId;
        private String principalId;
        private String principalName;
        private Boolean active;

        public MinimalKEDIBuilder(String name) {
            entityId = UUID.randomUUID().toString();
            principalId = principalName = name;
        }

        public EntityDefault build() {
            if (entityId == null) entityId = UUID.randomUUID().toString();
            if (principalId == null) principalId = UUID.randomUUID().toString();
            if (principalName == null) principalName = principalId;
            if (active == null) active = true;

            Principal.Builder principal = Principal.Builder.create(principalName);
            principal.setActive(active);
            principal.setEntityId(entityId);
            principal.setPrincipalId(principalId);

            EntityDefault.Builder kedi = EntityDefault.Builder.create();
            kedi.setPrincipals(Collections.singletonList(principal));
            kedi.setEntityId(entityId);

            return kedi.build();
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