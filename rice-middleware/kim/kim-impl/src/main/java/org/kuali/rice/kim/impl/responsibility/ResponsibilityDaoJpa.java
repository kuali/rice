package org.kuali.rice.kim.impl.responsibility;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ResponsibilityDaoJpa implements ResponsibilityDao {

    private EntityManager entityManager;

    @Override
    public List<ResponsibilityBo> findWorkflowResponsibilities(String documentTypeName) {
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
