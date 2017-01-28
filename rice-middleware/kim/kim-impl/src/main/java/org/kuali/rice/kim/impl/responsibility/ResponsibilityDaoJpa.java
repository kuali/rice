/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.impl.responsibility;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ResponsibilityDaoJpa implements ResponsibilityDao {

    private EntityManager entityManager;

    @Override
    public List<ResponsibilityBo> findWorkflowResponsibilities(String documentTypeName) {
        // query the responsibility ids from the responsibility attributes table first, then load the responsibilities
        // one by one. This avoids a bug where querying for responsibilities by attribute value (doucment type in this
        // case) would return a responsibility with only a single attribute, not 4 as expected for responsibilities.
        List<String> responsibilityIds = getEntityManager().createNamedQuery("Responsibility.workflowResponsibilities", String.class).
                setParameter("documentTypeName", documentTypeName).getResultList();
        List<ResponsibilityBo> responsibilities = new ArrayList<>();
        for (String responsibilityId : responsibilityIds) {
            responsibilities.add(entityManager.find(ResponsibilityBo.class, responsibilityId));
        }
        return responsibilities;
    }

    @Override
    public List<ResponsibilityBo> findWorkflowExceptionResponsibilities(String documentTypeName) {
        // query the responsibility ids from the responsibility attributes table first, then load the responsibilities
        // one by one. This avoids a bug where querying for responsibilities by attribute value (doucment type in this
        // case) would return a responsibility with only a single attribute, not 4 as expected for responsibilities.
        List<String> responsibilityIds = getEntityManager().createNamedQuery("Responsibility.workflowExceptionResponsibilities", String.class).
                setParameter("documentTypeName", documentTypeName).getResultList();
        List<ResponsibilityBo> responsibilities = new ArrayList<>();
        for (String responsibilityId : responsibilityIds) {
            responsibilities.add(entityManager.find(ResponsibilityBo.class, responsibilityId));
        }
        return responsibilities;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
