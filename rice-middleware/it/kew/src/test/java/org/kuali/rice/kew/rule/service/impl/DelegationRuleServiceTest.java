/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.kew.rule.service.impl;

import org.junit.Test;
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class DelegationRuleServiceTest extends KEWTestCase {


    @Test
    public void testRetrievalOfDelegationRules() throws Exception {
        loadXmlFile("org/kuali/rice/kew/rule/RouteTemplateConfig.xml");
        loadXmlFile("org/kuali/rice/kew/rule/RulesWithoutResponsibilities.xml");
        //Loading Delegation rule test data. The xml file contains two delegation rules
        loadXmlFile("org/kuali/rice/kew/rule/DelegationRules.xml");
        List<RuleDelegationBo> delegationRuleList;

        //There are two delegations rules, both need to be returned
        delegationRuleList = KEWServiceLocator.getRuleDelegationService().search(null, null, null, null, null, null, null, null, null,Boolean.TRUE ,null,null);
        assertNotNull("The returned delegation rule list should not be null", delegationRuleList);

        //Only one delegation rule is of document type RiceDocument.child1
        delegationRuleList = KEWServiceLocator.getRuleDelegationService().search(null,null,"RiceDocument.child1",null,null,null,null,null,null,Boolean.TRUE,null,null);
        assertTrue(delegationRuleList.size() == 1);

        //Only one delegation rule has description "A rule with a group responsibility"
        delegationRuleList = KEWServiceLocator.getRuleDelegationService().search(null,null,null,null,null,"A rule with a group responsibility",null,null,null,Boolean.TRUE,null,null);
        assertTrue(delegationRuleList.size() == 1);

        //Setting Active indicator to false, should not return any values as both the delegation rules are active
        delegationRuleList = KEWServiceLocator.getRuleDelegationService().search(null,null,null,null,null,null,null,null,null,Boolean.FALSE,null,null);
        assertTrue(delegationRuleList.isEmpty());
}

}
