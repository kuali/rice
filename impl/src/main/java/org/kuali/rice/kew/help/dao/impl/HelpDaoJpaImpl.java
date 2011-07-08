/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.help.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.help.dao.HelpDAO;


public class HelpDaoJpaImpl implements HelpDAO {
	
    @PersistenceContext(unitName="kew-unit")
    private EntityManager entityManager;
    
    public void save(HelpEntry helpEntry) {
        if (helpEntry.getHelpId() == null) {
            entityManager.persist(helpEntry);
        } else {
            entityManager.merge(helpEntry);
        }
    }
    
    public void deleteEntry(HelpEntry helpEntry) {
        HelpEntry reattatched = entityManager.merge(helpEntry);
        entityManager.remove(reattatched);
    }
    
    public HelpEntry findById(String helpId) {
   		return (HelpEntry) entityManager.createNamedQuery("HelpEntry.FindById").setParameter("helpId", helpId).getSingleResult();
    }
    
    public List search(HelpEntry helpEntry) {
        Criteria crit = new Criteria("HelpEntry", "he");
       
        if (helpEntry.getHelpId() != null && !StringUtils.equals(helpEntry.getHelpId(),"0")) {
            crit.eq("helpId", helpEntry.getHelpId());
        }

        if (!this.isStringEmpty(helpEntry.getHelpName())) {
            crit.rawJpql("UPPER(he.helpName) like '%" + helpEntry.getHelpName().toUpperCase() + "%'");
        }

        if (!this.isStringEmpty(helpEntry.getHelpText())) {
            crit.rawJpql("UPPER(he.helpText) like '%" + helpEntry.getHelpText().toUpperCase() + "%'");
        }
        
        if (!this.isStringEmpty(helpEntry.getHelpKey())) {
            crit.rawJpql("UPPER(he.helpKey) like '%" + helpEntry.getHelpKey().toUpperCase() + "%'");
        }
        
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }
    
    private boolean isStringEmpty(String string) {
        if ((string == null) || string.trim().equals("")) {
            return true;
        }
        return false;
    }
    
    public HelpEntry findByKey(String helpKey){
   		return (HelpEntry) entityManager.createNamedQuery("HelpEntry.FindByKey").setParameter("helpKey", helpKey).getSingleResult();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
