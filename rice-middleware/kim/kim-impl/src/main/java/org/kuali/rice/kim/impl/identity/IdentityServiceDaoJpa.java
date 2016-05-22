/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.kim.impl.identity;

import com.google.common.collect.Lists;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.impl.identity.name.EntityNameBo;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityServiceDaoJpa implements IdentityServiceDao {

    private static final int PARTITION_SIZE = 500;

    private EntityManager entityManager;

    @Override
    public Map<String, EntityNamePrincipalName> getDefaultNamesByPrincipalIds(List<String> principalIds) {
        Map<String, EntityNamePrincipalName> results = new HashMap<String, EntityNamePrincipalName>();

        // This partitioning is required because EclipseLink does not handle splitting up IN clauses with a large number of values into chunks
        List<List<String>> partitionedPrincipalIds = Lists.partition(principalIds, PARTITION_SIZE);
        for(List<String> partition : partitionedPrincipalIds) {
            List<NameHolder> names = getEntityManager().createNamedQuery("EntityNameBo.findDefaultNamesForPrincipalIds",
                    NameHolder.class).setParameter("principalIds", partition).getResultList();
            for(NameHolder name : names) {
                EntityNamePrincipalName.Builder nameBuilder = EntityNamePrincipalName.Builder.create();
                EntityNameBo entityName = name.getEntityName();
                entityName.setSuppressName(name.isSuppressName());
                nameBuilder.setDefaultName(EntityName.Builder.create(entityName));
                nameBuilder.setPrincipalName(name.getPrincipalName());
                results.put(name.getPrincipalId(), nameBuilder.build());
            }
        }
        return results;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static class NameHolder {

        private EntityNameBo entityName;
        private String principalId;
        private String principalName;
        private boolean suppressName;

        public NameHolder() {}

        public NameHolder(EntityNameBo entityName, String principalId, String principalName, boolean suppressName) {
            this.entityName = entityName;
            this.principalId = principalId;
            this.principalName = principalName;
            this.suppressName = suppressName;
        }

        public EntityNameBo getEntityName() {
            return entityName;
        }

        public void setEntityName(EntityNameBo entityName) {
            this.entityName = entityName;
        }

        public String getPrincipalId() {
            return principalId;
        }

        public void setPrincipalId(String principalId) {
            this.principalId = principalId;
        }

        public String getPrincipalName() {
            return principalName;
        }

        public void setPrincipalName(String principalName) {
            this.principalName = principalName;
        }

        public boolean isSuppressName() {
            return suppressName;
        }

        public void setSuppressName(boolean suppressName) {
            this.suppressName = suppressName;
        }
    }
}
