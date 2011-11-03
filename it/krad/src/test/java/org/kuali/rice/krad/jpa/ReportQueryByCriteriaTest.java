/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.jpa;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria.QueryByCriteriaType;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.test.KRADTestCase;

import static org.junit.Assert.assertTrue;

/**
 * Junit test cases for org.kuali.rice.core.jpa.criteria.ReportQueryByCriteria
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReportQueryByCriteriaTest extends KRADTestCase {
    
    @Test
    public void testCriteriaToReportQuery_emptySelect() throws Exception {
        Criteria criteria = new Criteria(Account.class.getName(), "a");
        
        String query = criteria.toQuery(QueryByCriteriaType.SELECT, new String[0]);
        assertTrue(StringUtils.equalsIgnoreCase(query, "select a from Account as a"));
    }

    @Test
    public void testCriteriaToReportQuery_singleFieldSelect() throws Exception {
        Criteria criteria = new Criteria(Account.class.getName(), "a");
        
        String[] attr = new String[1];
        attr[0] = "number";
        
        String query = criteria.toQuery(QueryByCriteriaType.SELECT, attr);
        assertTrue(StringUtils.equalsIgnoreCase(query, "select a.number from Account as a"));
    }
    
    @Test
    public void testCriteriaToReportQuery_multipleFieldSelect() throws Exception {
        Criteria criteria = new Criteria(Account.class.getName(), "a");
        
        String[] attr = new String[3];
        attr[0] = "number";
        attr[1] = "name";
        attr[2] = "amId";
        
        String query = criteria.toQuery(QueryByCriteriaType.SELECT, attr);
        assertTrue(StringUtils.equalsIgnoreCase(query, "select a.number, a.name, a.amId from Account as a"));
    }

}
