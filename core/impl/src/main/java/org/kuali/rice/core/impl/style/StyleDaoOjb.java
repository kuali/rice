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
package org.kuali.rice.core.impl.style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class StyleDaoOjb extends PersistenceBrokerDaoSupport implements StyleDao {

    public void saveStyle(StyleBo styleData) {
        this.getPersistenceBrokerTemplate().store(styleData);
    }

    public StyleBo getStyle(String styleName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", styleName);
        criteria.addEqualTo("active", Boolean.TRUE);
        return (StyleBo) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(StyleBo.class, criteria));
    }

    /**
     * Returns names of all active Styles
     */
    public List<String> getStyleNames() {
        List<StyleBo> styles = getStyles();
        List<String> styleNames = new ArrayList<String>(styles.size());
        for (StyleBo style: styles) {
            styleNames.add(style.getName());
        }
        return styleNames;
    }

    /**
     * Returns all active Styles
     */
    public List<StyleBo> getStyles() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("active", Boolean.TRUE);
        Iterator it = this.getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(StyleBo.class, criteria));
        List<StyleBo> styles = new ArrayList<StyleBo>();
        while (it.hasNext()) {
            styles.add((StyleBo) it.next());
        }
        return styles;
    }

}
