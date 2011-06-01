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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolutionEngine;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.RepositoryDataException;
import org.kuali.rice.krms.api.repository.function.FunctionDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionParameterDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionRepositoryService;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.framework.engine.Function;
import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.rice.krms.framework.engine.expression.BinaryOperatorExpression;
import org.kuali.rice.krms.framework.engine.expression.BooleanValidatingExpression;
import org.kuali.rice.krms.framework.engine.expression.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.expression.ConstantExpression;
import org.kuali.rice.krms.framework.engine.expression.Expression;
import org.kuali.rice.krms.framework.engine.expression.ExpressionBasedProposition;
import org.kuali.rice.krms.framework.engine.expression.FunctionExpression;
import org.kuali.rice.krms.framework.engine.expression.TermExpression;
import org.kuali.rice.krms.framework.type.FunctionTypeService;
import org.kuali.rice.krms.framework.type.PropositionTypeService;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.type.KrmsTypeResolver;

/**
 * A default implementation of {@link PropositionTypeService} for propositions
 * which are composed of terms, operators, and functions.  A simple proposition
 * is self-contained and has no compound "sub" propositions.  However, it's
 * behavior is defined by the set of parameters on the {@link PropositionDefinition}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SimplePropositionTypeService implements PropositionTypeService {

	private TermBoService termBoService;
	private FunctionRepositoryService functionRepositoryService;
	private KrmsTypeResolver typeResolver;
	
	@Override
	public Proposition loadProposition(PropositionDefinition propositionDefinition) {
		return new ExpressionBasedProposition(translateToExpression(propositionDefinition));
	}

	/**
	 * Translates the parameters on the given proposition definition to create an expression for evaluation.
	 * The proposition parameters are defined in a reverse-polish notation so a stack is used for
	 * evaluation purposes.
	 * 
	 * @param propositionDefinition the proposition definition to translate
	 * 
	 * @return the translated expression for the given proposition, this
	 * expression, when evaluated, will return a Boolean.
	 */
	protected Expression<Boolean> translateToExpression(PropositionDefinition propositionDefinition) {
		LinkedList<Expression<? extends Object>> stack = new LinkedList<Expression<? extends Object>>();
		for (PropositionParameter parameter : propositionDefinition.getParameters()) {
			PropositionParameterType parameterType = PropositionParameterType.fromCode(parameter.getParameterType());
			if (parameterType == PropositionParameterType.CONSTANT) {
				// TODO - need some way to define data type on the prop parameter as well?  Not all constants will actually be String values!!!
				stack.addFirst(new ConstantExpression<String>(parameter.getValue()));
			} else if (parameterType == PropositionParameterType.FUNCTION) {
				String functionId = parameter.getValue();
				FunctionDefinition functionDefinition = functionRepositoryService.getFunction(functionId);
				if (functionDefinition == null) {
					throw new RepositoryDataException("Unable to locate function with the given id: " + functionId);
				}
				FunctionTypeService functionTypeService = typeResolver.getFunctionTypeService(functionDefinition);
				Function function = functionTypeService.loadFunction(functionDefinition);
				// TODO throw an exception if function is null?
				List<FunctionParameterDefinition> parameters = functionDefinition.getParameters();
				if (stack.size() < parameters.size()) {
					throw new RepositoryDataException("Failed to initialize custom function '" + functionDefinition.getNamespace() + " " + functionDefinition.getName() +
							"'.  There were only " + stack.size() + " values on the stack but function requires at least " + parameters.size());
				}
				List<Expression<? extends Object>> arguments = new ArrayList<Expression<? extends Object>>();
				// work backward through the list to match params to the stack
				for (int index = parameters.size() - 1; index >= 0; index--) {
					FunctionParameterDefinition parameterDefinition = parameters.get(index);
					// TODO need to check types here? expression object probably needs a getType on it so that we can confirm that the types will be compatible?
					Expression<? extends Object> argument = stack.removeFirst();
					arguments.add(argument);
				}
				stack.addFirst(new FunctionExpression(function, arguments));
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

				TermDefinition termDefinition = termBoService.getTermById(termId);
				if (termDefinition == null) { throw new RepositoryDataException("unable to load term with id " + termId);}
				Term term = translateTermDefinition(termDefinition);
				
				stack.addFirst(new TermExpression(term));
			}
		}
		if (stack.size() != 1) {
			throw new RepositoryDataException("Final contents of expression stack are incorrect, there should only be one entry but was " + stack.size() +".  Current contents of stack: " + stack.toString());
		}
		return new BooleanValidatingExpression(stack.removeFirst());
	}
	
	protected Term translateTermDefinition(TermDefinition termDefinition) {
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

	/**
	 * @param termBoService the termBoService to set
	 */
	public void setTermBoService(TermBoService termBoService) {
		this.termBoService = termBoService;
	}

	public void setFunctionRepositoryService(FunctionRepositoryService functionRepositoryService) {
		this.functionRepositoryService = functionRepositoryService;
	}
	
	public void setTypeResolver(KrmsTypeResolver typeResolver) {
		this.typeResolver = typeResolver;
	}
	
}
