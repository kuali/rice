/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.workgroup.dao;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.workflow.workgroup.WorkgroupType;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * OJB implementation of the WorkgroupTypeDAO.  Allows for simple access and persistence of
 * WorkgroupTypes.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeDAOOjbImpl extends PersistenceBrokerDaoSupport implements WorkgroupTypeDAO {

	public WorkgroupType findById(Long workgroupTypeId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("workgroupTypeId", workgroupTypeId);
		return (WorkgroupType)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(WorkgroupType.class, crit));
	}

	public WorkgroupType findByName(String name) {
		Criteria crit = new Criteria();
		crit.addEqualTo("name", name);
		return (WorkgroupType)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(WorkgroupType.class, crit));
	}

	public Collection findAll(boolean activeOnly) {
		Criteria crit = new Criteria();
		if (activeOnly) {
			crit.addEqualTo("active", Boolean.TRUE);
		}
		return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(WorkgroupType.class, crit));
	}

	public void save(WorkgroupType workgroupType) {
		getPersistenceBrokerTemplate().store(workgroupType);
	}

	public Collection search(Long id, String name, String label, String description, Boolean active) {
		Criteria crit = new Criteria();
		if (id != null) {
			crit.addEqualTo("workgroupTypeId", id);
		}
		if (!StringUtils.isBlank(name)) {
			crit.addEqualTo("name", name);
		}
		if (!StringUtils.isBlank(label)) {
			crit.addEqualTo("label", label);
		}
		if (!StringUtils.isBlank(description)) {
			crit.addEqualTo("description", description);
		}
		if (active != null) {
			crit.addEqualTo("active", active);
		}
		return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(WorkgroupType.class, crit));
	}

}
