/*
 * Copyright 2005-2007 The Kuali Foundation
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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.help.dao.HelpDAO;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;



public class HelpDAOOjbImpl extends PersistenceBrokerDaoSupport implements HelpDAO {
	
    public void save(HelpEntry helpEntry){
        this.getPersistenceBrokerTemplate().store(helpEntry);
    }
    
    public void deleteEntry(HelpEntry helpEntry) {
    	this.getPersistenceBrokerTemplate().delete(helpEntry);
    }
    
    public HelpEntry findById(Long helpId){
        Criteria crit = new Criteria();
        crit.addEqualTo("helpId", helpId);
		return (HelpEntry) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(HelpEntry.class, crit)); 
    }
    
    public List search(HelpEntry helpEntry){
        Criteria crit = new Criteria();

        if (helpEntry.getHelpId() != null && helpEntry.getHelpId().longValue() != 0) {
            crit.addEqualTo("helpId", helpEntry.getHelpId());
        }

        if (!this.isStringEmpty(helpEntry.getHelpName())) {
            crit.addLike("UPPER(helpName)", "%" + helpEntry.getHelpName().toUpperCase() + "%");
        }

        if (!this.isStringEmpty(helpEntry.getHelpText())) {
            crit.addLike("UPPER(helpText)", "%" + helpEntry.getHelpText().toUpperCase() + "%");
        }
        
        if (!this.isStringEmpty(helpEntry.getHelpKey())) {
            crit.addLike("UPPER(helpKey)", "%" + helpEntry.getHelpKey().toUpperCase() + "%"); 	
        }
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(HelpEntry.class, crit));
    }
    
    private boolean isStringEmpty(String string) {
        if ((string == null) || string.trim().equals("")) {
            return true;
        }

        return false;
    }
    
    public HelpEntry findByKey(String helpKey){
        Criteria crit = new Criteria();
        crit.addEqualTo("helpKey", helpKey);
		return  (HelpEntry) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(HelpEntry.class, crit)); 
    }
}
