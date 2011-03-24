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
package org.kuali.rice.edl.impl.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.edl.impl.bo.EDocLiteAssociation;
import org.kuali.rice.edl.impl.bo.EDocLiteDefinition;
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.edl.impl.dao.EDocLiteDAO;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class EDocLiteDAOOjbImpl extends PersistenceBrokerDaoSupport implements EDocLiteDAO {

    public void saveEDocLiteStyle(EDocLiteStyle styleData) {
        this.getPersistenceBrokerTemplate().store(styleData);
    }

    public void saveEDocLiteDefinition(EDocLiteDefinition edocLiteData) {
    	this.getPersistenceBrokerTemplate().store(edocLiteData);
    }

    public void saveEDocLiteAssociation(EDocLiteAssociation assoc) {
    	this.getPersistenceBrokerTemplate().store(assoc);
    }

    public EDocLiteStyle getEDocLiteStyle(String styleName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", styleName);
        criteria.addEqualTo("activeInd", Boolean.TRUE);
        return (EDocLiteStyle) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(EDocLiteStyle.class, criteria));
    }

    public EDocLiteDefinition getEDocLiteDefinition(String defName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", defName);
        criteria.addEqualTo("activeInd", Boolean.TRUE);
        return (EDocLiteDefinition) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(EDocLiteDefinition.class, criteria));
    }

    public EDocLiteAssociation getEDocLiteAssociation(String docTypeName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("edlName", docTypeName);
        criteria.addEqualTo("activeInd", Boolean.TRUE);
        return (EDocLiteAssociation) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(EDocLiteAssociation.class, criteria));
    }

    /**
     * Returns names of all active Styles
     */
    public List<String> getEDocLiteStyleNames() {
        List<EDocLiteStyle> styles = getEDocLiteStyles();
        List<String> styleNames = new ArrayList<String>(styles.size());
        for (EDocLiteStyle style: styles) {
            styleNames.add(style.getName());
        }
        return styleNames;
    }

    /**
     * Returns all active Styles
     */
    public List<EDocLiteStyle> getEDocLiteStyles() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("activeInd", Boolean.TRUE);
        Iterator it = this.getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(EDocLiteStyle.class, criteria));
        List<EDocLiteStyle> styles = new ArrayList<EDocLiteStyle>();
        while (it.hasNext()) {
            styles.add((EDocLiteStyle) it.next());
        }
        return styles;
    }

    public List getEDocLiteDefinitions() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("activeInd", Boolean.TRUE);
        Iterator it = this.getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(EDocLiteDefinition.class, criteria));
        List defs = new ArrayList();
        while (it.hasNext()) {
            defs.add(((EDocLiteDefinition) it.next()).getName());
        }
        return defs;
    }

    public List getEDocLiteAssociations() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("activeInd", Boolean.TRUE);
        Iterator it = this.getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(EDocLiteAssociation.class, criteria));
        List assocs = new ArrayList();
        while (it.hasNext()) {
            assocs.add(it.next());
        }
        return assocs;
    }

	public List search(EDocLiteAssociation edocLite) {
		Criteria crit = new Criteria();
		if (edocLite.getActiveInd() != null) {
			crit.addEqualTo("activeInd", edocLite.getActiveInd());
		}
		if (edocLite.getDefinition() != null) {
			crit.addLike("UPPER(definition)", "%"+edocLite.getDefinition().toUpperCase()+"%");
		}
		if (edocLite.getEdlName() != null) {
			crit.addLike("UPPER(edlName)", "%"+edocLite.getEdlName().toUpperCase()+"%");
		}
		if (edocLite.getStyle() != null) {
			crit.addLike("UPPER(style)", "%"+edocLite.getStyle().toUpperCase()+"%");
		}
		return (List)this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(EDocLiteAssociation.class, crit));
	}

	public EDocLiteAssociation getEDocLiteAssociation(Long associationId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("edocLiteAssocId", associationId);
		return (EDocLiteAssociation)this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(EDocLiteAssociation.class, crit));
	}
}
