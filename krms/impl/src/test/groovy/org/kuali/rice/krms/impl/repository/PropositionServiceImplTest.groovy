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

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kuali.rice.kns.bo.PersistableBusinessObject
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.kuali.rice.krms.api.repository.Proposition
import org.kuali.rice.krms.api.repository.PropositionContract
import org.kuali.rice.krms.api.repository.PropositionParameter
import org.kuali.rice.krms.api.repository.PropositionParameterContract
import org.kuali.rice.krms.framework.engine.ComparisonOperator;

class PropositionServiceImplTest {
    private def MockFor mock
    private final shouldFail = new GroovyTestCase().&shouldFail
	private PropositionServiceImpl pservice;
	
	private static final List <PropositionParameter.Builder> parmList = createPropositionParameters()
	private static final Proposition proposition = createProposition()
	private PropositionBo bo = PropositionBo.from(proposition)
	
    @Before
    void setupBoServiceMockContext() {
        mock = new MockFor(BusinessObjectService.class)
		pservice = new PropositionServiceImpl()
    }

	@Test
	public void test_create_proposition_null_proposition() {
		def boService = mock.proxyDelegateInstance()
		pservice.setBusinessObjectService(boService)
		shouldFail(IllegalArgumentException.class) {
			pservice.createProposition(null)
		}
		mock.verify(boService)
	}

	@Test
	void test_create_proposition_exists() {
		mock.demand.findBySinglePrimaryKey (1..1) { clazz, map -> bo }
		def boService = mock.proxyDelegateInstance()
		pservice.setBusinessObjectService(boService);
		shouldFail(IllegalStateException.class) {
			pservice.createProposition(proposition)
		}
		mock.verify(boService)
	}

    @Test
    void test_create_proposition_success() {
        mock.demand.findBySinglePrimaryKey (1..1) { clazz, map -> null }
        mock.demand.save { PersistableBusinessObject bo -> }
        def boService = mock.proxyDelegateInstance()
        pservice.setBusinessObjectService(boService);
        pservice.createProposition(proposition)
        mock.verify(boService)
    }

	@Test
	public void test_update_proposition_null_proposition() {
		def boService = mock.proxyDelegateInstance()
		pservice.setBusinessObjectService(boService)
		shouldFail(IllegalArgumentException.class) {
			pservice.updateProposition(null)
		}
		mock.verify(boService)
	}

	@Test
    void test_update_proposition_exists() {
        mock.demand.findBySinglePrimaryKey (1..1) { clazz, map -> bo }
        mock.demand.save { PersistableBusinessObject bo -> }
        def boService = mock.proxyDelegateInstance()
        pservice.setBusinessObjectService(boService);
        pservice.updateProposition(proposition)
        mock.verify(boService)
    }

	@Test
    void test_update_proposition_does_not_exist() {
        mock.demand.findBySinglePrimaryKey (1..1) { clazz, map -> null }
        def boService = mock.proxyDelegateInstance()
        pservice.setBusinessObjectService(boService);
		shouldFail(IllegalStateException.class) {
			pservice.updateProposition(proposition)
		}
        mock.verify(boService)
    }

	@Test
	void test_get_proposition_by_id_null_id() {
		def boService = mock.proxyDelegateInstance()
		pservice.setBusinessObjectService(boService);

		shouldFail(IllegalArgumentException.class) {
			pservice.getPropositionById(null)
		}
		mock.verify(boService)
	}

    @Test
    void test_get_proposition_by_id_exists() {
        mock.demand.findBySinglePrimaryKey (1..1) { clazz, map -> bo }
        def boService = mock.proxyDelegateInstance()
        pservice.setBusinessObjectService(boService);
        Assert.assertEquals (proposition, pservice.getPropositionById("2002"))
        mock.verify(boService)
    }

    @Test
    void test_get_proposition_by_id_does_not_exist() {
        mock.demand.findBySinglePrimaryKey (1..1) { clazz, map -> null }
        def boService = mock.proxyDelegateInstance()
        pservice.setBusinessObjectService(boService);
        Assert.assertNull (pservice.getPropositionById("blah"))
        mock.verify(boService)
    }

	private static createProposition() {
		def List<PropositionParameter.Builder> myList = parmList
		return Proposition.Builder.create(new PropositionContract () {
			def String propId = "2002"
			def String description = "Is campus type = Bloomington"
			def String typeId = "1"
			def String propositionTypeCode = "S"
			def List<? extends PropositionParameterContract> parameters = myList
		}).build()
	}
		
	private static createPropositionParameters(){
		List <PropositionParameter.Builder> propParms = new ArrayList <PropositionParameter.Builder> ()
		PropositionParameter.Builder ppBuilder1 = PropositionParameter.Builder.create(new PropositionParameterContract() {
			def String id = "1000"
			def String propId = "2001"
			def String value = "campusCode"
			def String parameterType = "T"
			def Integer sequenceNumber = new Integer("0")
		})
		PropositionParameter.Builder ppBuilder2 = PropositionParameter.Builder.create(new PropositionParameterContract() {
			def String id = "1001"
			def String propId = "2001"
			def String value = "BL"
			def String parameterType = "C"
			def Integer sequenceNumber = new Integer("1")
		})
		PropositionParameter.Builder ppBuilder3 = PropositionParameter.Builder.create(new PropositionParameterContract() {
			def String id = "1003"
			def String propId = "2001"
			def String value = ComparisonOperator.EQUALS
			def String parameterType = "F"
			def Integer sequenceNumber = new Integer("2")
		})
		for ( ppb in [ppBuilder1, ppBuilder2, ppBuilder3]){
			propParms.add (ppb)
		}
		return propParms;
	}
}
