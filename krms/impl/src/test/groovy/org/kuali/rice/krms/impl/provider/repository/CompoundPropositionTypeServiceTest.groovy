/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.provider.repository;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.kuali.rice.krms.api.repository.PropositionDefinition
import org.kuali.rice.krms.api.repository.PropositionDefinitionContract
import org.kuali.rice.krms.api.repository.PropositionParameter
import org.kuali.rice.krms.api.repository.PropositionParameterContract
import org.kuali.rice.krms.api.repository.PropositionParameterType
import org.kuali.rice.krms.api.repository.PropositionType

import groovy.mock.interceptor.MockFor

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class CompoundPropositionTypeServiceTest {

	CompoundPropositionTypeService service;
	MockFor mockTranslator;
		
	@Before
	void setUp() {
		service = new CompoundPropositionTypeService();
		mockTranslator = new MockFor(RepositoryToEngineTranslator);
		service.setTranslator(mockTranslator.proxyDelegateInstance());
	}
	
	private void verifyMocks() {
		mockTranslator.verify(service.translator)
	}
	
	private static final String ID = "1";
	private static final String DESCRIPTION = "description"
	private static final String TYPE_ID = "1";
	private static final String PROPOSITION_TYPE_CODE = PropositionType.SIMPLE.getCode();
	
	private static PropositionDefinition createPropositionDefinition() {
		return PropositionDefinition.Builder.create(new PropositionDefinitionContract() {
			String propId = CompoundPropositionTypeServiceTest.ID;
			String description = CompoundPropositionTypeServiceTest.DESCRIPTION;
			String typeId = CompoundPropositionTypeServiceTest.TYPE_ID;
			String propositionTypeCode = CompoundPropositionTypeServiceTest.PROPOSITION_TYPE_CODE;
			String compoundOpCode = null;
			List parameters = [
				PropositionParameter.Builder.create(new PropositionParameterContract() {
					String id = "2";
					String propId = "1";
					String value = "abc";
					String parameterType = PropositionParameterType.CONSTANT.getCode();
					Integer sequenceNumber = 1;
					
				}).build(),
				PropositionParameter.Builder.create(new PropositionParameterContract() {
					String id = "3";
					String propId = "1";
					String value = "abc";
					String parameterType = PropositionParameterType.CONSTANT.getCode();
					Integer sequenceNumber = 2;
				}).build(),
				PropositionParameter.Builder.create(new PropositionParameterContract() {
					String id = "4";
					String propId = "1";
					String value = "=";
					String parameterType = PropositionParameterType.OPERATOR.getCode();
					Integer sequenceNumber = 3;
				}).build()
			];
			List compoundComponents = [];
		}).build();
	}
	
	/**
	 * Test method for {@link org.kuali.rice.krms.impl.provider.repository.CompoundPropositionTypeService#loadProposition(org.kuali.rice.krms.api.repository.PropositionDefinition)}.
	 */
	@Test
	public void testLoadProposition() {
		PropositionDefinition propositionDefintion = createPropositionDefinition();
		
		// TODO write the actual test!
		
	}

}
