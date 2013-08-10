/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kew.useroptions.dao.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.dao.UserOptionsDAO;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class UserOptionsDaoJpaImpl implements UserOptionsDAO {

    private static final String SEQUENCE_NAME = "KREW_ACTN_LIST_OPTN_S";

    @PersistenceContext
    private EntityManager entityManager;
    private DataSource dataSource;

	public Long getNewOptionIdForActionList() {
        return new Long(MaxValueIncrementerFactory.getIncrementer(dataSource, SEQUENCE_NAME).nextLongValue());
    }

    public List findByUserQualified(String principalId, String likeString) {
        return new ArrayList(entityManager.createNamedQuery("UserOptions.FindByUserQualified").setParameter("workflowId", principalId).setParameter("optionId", likeString).getResultList());
    }

    public void deleteByUserQualified(String principalId, String likeString) {
        List<UserOptions> userOptions = (List<UserOptions>) entityManager.createNamedQuery("UserOptions.FindByUserQualified").setParameter("workflowId", principalId).setParameter("optionId", likeString).getResultList();
        for (UserOptions uo : userOptions) {
            entityManager.remove(uo);
        }
    }

    public Collection findByWorkflowUser(String principalId) {
        return entityManager.createNamedQuery("UserOptions.FindByWorkflowId").setParameter("workflowId", principalId).getResultList();
    }

    public void save(UserOptions userOptions) {
        if (userOptions.getOptionId() == null) {
            entityManager.persist(userOptions);
        } else {
            entityManager.merge(userOptions);
        }
    }
    
    public void save(Collection<UserOptions> userOptions) {
    	if (userOptions != null) for (UserOptions option : userOptions) {
			save(option);
		}
    }

    public void deleteUserOptions(UserOptions userOptions) {
        UserOptions reattatched = entityManager.merge(userOptions);
        entityManager.remove(reattatched);
    }

    public UserOptions findByOptionId(String optionId, String principalId) {
        return (UserOptions) entityManager.createNamedQuery("UserOptions.FindByOptionId").setParameter("optionId", optionId).setParameter("workflowId", principalId).getSingleResult();
    }

    public Collection findByOptionValue(String optionId, String optionValue) {
        return entityManager.createNamedQuery("UserOptions.FindByOptionValue").setParameter("optionId", optionId).setParameter("optionValue", optionValue).getResultList();
    }

    @Override
    public List<UserOptions> findEmailUserOptionsByType(String emailSetting) {
        return Lists.newArrayList(Iterables.filter(entityManager.createNamedQuery("UserOptions.FindByOptionValue")
                                                                .setParameter("optionId", KewApiConstants.EMAIL_RMNDR_KEY)
                                                                .setParameter("optionIdLike", "%" + KewApiConstants.DOCUMENT_TYPE_NOTIFICATION_PREFERENCE_SUFFIX)
                                                                .setParameter("optionValue", emailSetting)
                                                                .getResultList(),
                                                   UserOptions.class));
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
