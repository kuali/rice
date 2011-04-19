/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.impl.repository

import java.util.List;

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kuali.rice.kns.bo.PersistableBusinessObject
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterContract;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;

class RuleRepositoryServiceImplTest {
    private def MockFor mock
    private final shouldFail = new GroovyTestCase().&shouldFail
	private RuleRepositoryServiceImpl pservice;

	

	@BeforeClass
	static void createSampleBOs() {
	}
	
	@BeforeClass
	static void createSampleParameters() {
	}


    @Before
    void setupBoServiceMockContext() {
        mock = new MockFor(BusinessObjectService.class)
		pservice = new PropositionRepositoryServiceImpl()
    }

//
// RuleRepositoryService Tests
//			
	
	// Test RuleRepository Service.getAgendaTree()
	@Test
	public void test_get_agend_tree_single_node_tree() {
		def boService = mock.proxyDelegateInstance()
		pservice.setBusinessObjectService(boService)

				mock.verify(boService)
	}

}
