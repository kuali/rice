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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolutionEngine;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.api.repository.PropositionParameter;
import org.kuali.rice.krms.api.repository.PropositionParameterType;
import org.kuali.rice.krms.api.repository.RepositoryDataException;
import org.kuali.rice.krms.api.repository.TermDefinition;
import org.kuali.rice.krms.api.repository.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.TermSpecificationDefinition;
import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.rice.krms.framework.engine.expression.BinaryOperatorExpression;
import org.kuali.rice.krms.framework.engine.expression.BooleanValidatingExpression;
import org.kuali.rice.krms.framework.engine.expression.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.expression.ConstantExpression;
import org.kuali.rice.krms.framework.engine.expression.Expression;
import org.kuali.rice.krms.framework.engine.expression.ExpressionBasedProposition;
import org.kuali.rice.krms.framework.engine.expression.TermExpression;
import org.kuali.rice.krms.framework.type.PropositionTypeService;
import org.kuali.rice.krms.impl.repository.TermRepositoryService;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SimplePropositionTypeService implements PropositionTypeService {

	private TermResolutionEngine termResolutionEngine;
	private TermRepositoryService termRepositoryService;
	
	@Override
	public Proposition loadProposition(PropositionDefinition propositionDefinition) {
		return new ExpressionBasedProposition(translateToExpression(propositionDefinition, termResolutionEngine, termRepositoryService));
	}

	/**
	 * Translates the parameters on the given proposition definition to create an expression for evaluation.
	 * The proposition parameters are defined in a reverse-polish notation so a stack is used for
	 * evaluation purposes.
	 * 
	 * @param propositionDefinition
	 * @return
	 */
	private static Expression<Boolean> translateToExpression(PropositionDefinition propositionDefinition, TermResolutionEngine termResolutionEngine, TermRepositoryService termRepositoryService) {
		LinkedList<Expression<? extends Object>> stack = new LinkedList<Expression<? extends Object>>();
		for (PropositionParameter parameter : propositionDefinition.getParameters()) {
			PropositionParameterType parameterType = PropositionParameterType.fromCode(parameter.getParameterType());
			if (parameterType == PropositionParameterType.CONSTANT) {
				// TODO - need some way to define data type on the prop parameter as well?  Not all constants will actually be String values!!!
				stack.addFirst(new ConstantExpression<String>(parameter.getValue()));
			} else if (parameterType == PropositionParameterType.FUNCTION) {
				String functionId = parameter.getValue();
				
				// TODO need to go out and look up the function for the "function id" which will be stored in the value
				// then figure out how many arguments it has, popping off the stack in order to pass them in
				
				throw new UnsupportedOperationException("TODO - Implement Me!!!");
			} else if (parameterType == PropositionParameterType.OPERATOR) {
				ComparisonOperator operator = ComparisonOperator.fromCode(parameter.getValue());
				if (stack.size() < 2) {
					throw new RepositoryDataException("Failed to initialize expression for comparison operator " + operator + " because a sufficient number of arguments was not available on the stack.  Current contents of stack: " + stack.toString());
				}
				Expression<? extends Object> rhs = stack.removeFirst();
				Expression<? extends Object> lhs = stack.removeFirst();
				stack.addFirst(new BinaryOperatorExpression(operator, lhs, rhs));
			} else if (parameterType == PropositionParameterType.TERM) {
				String termId = parameter.getValue();

				TermDefinition termDefinition = termRepositoryService.getTermById(termId);
				if (termDefinition == null) { throw new RepositoryDataException("unable to load term with id " + termId);}
				Term term = translateTermDefinition(termDefinition);
				
				new TermExpression(term, termResolutionEngine);
			}
		}
		if (stack.size() != 1) {
			throw new RepositoryDataException("Final contents of expression stack are incorrect, there should only be one entry but was " + stack.size() +".  Current contents of stack: " + stack.toString());
		}
		return new BooleanValidatingExpression(stack.removeFirst());
	}
	
	public static Term translateTermDefinition(TermDefinition termDefinition) {
		if (termDefinition == null) {
			throw new RepositoryDataException("Given TermDefinition is null");
		}
		TermSpecificationDefinition termSpecificationDefinition = termDefinition.getSpecification();
		if (termSpecificationDefinition == null) { throw new RepositoryDataException("term with id " + termDefinition.getId() + " has a null specification"); } 
		
		Set<TermParameterDefinition> params = termDefinition.getParameters();
		Map<String,String> paramsMap = new TreeMap<String,String>();
		if (!CollectionUtils.isEmpty(params)) for (TermParameterDefinition param : params) {
			if (StringUtils.isBlank(param.getName())) { 
				throw new RepositoryDataException("TermParameterDefinition.name may not be blank"); 
			}
			paramsMap.put(param.getName(), param.getValue());
		}
		
		return new Term(new TermSpecification(termSpecificationDefinition.getName(), termSpecificationDefinition.getType()), paramsMap);
	}

	public void setTermResolutionEngine(TermResolutionEngine termResolutionEngine) {
		this.termResolutionEngine = termResolutionEngine;
	}
	
	/**
	 * @param termRepositoryService the termRepositoryService to set
	 */
	public void setTermRepositoryService(TermRepositoryService termRepositoryService) {
		this.termRepositoryService = termRepositoryService;
	}
	
}
