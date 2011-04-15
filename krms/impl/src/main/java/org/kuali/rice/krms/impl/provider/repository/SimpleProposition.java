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
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.IncompatibleTypeException;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolutionEngine;
import org.kuali.rice.krms.api.engine.TermResolutionException;
import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.framework.engine.Function;
import org.kuali.rice.krms.framework.engine.Proposition;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class SimpleProposition implements Proposition {
	
	private final PropositionDefinition propositionDefinition;
	private final Expression<Boolean> expression;
	private final TermResolutionEngine termResolutionEngine;
	
	SimpleProposition(PropositionDefinition propositionDefinition, TermResolutionEngine termResolutionEngine) {
		this.propositionDefinition = propositionDefinition;
		this.expression = translateToExpression(propositionDefinition);
		this.termResolutionEngine = termResolutionEngine;
	}
	
	private static Expression<Boolean> translateToExpression(PropositionDefinition propositionDefinition) {
		// TODO
		return null;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		return expression.invoke(environment).booleanValue();
	}

	abstract class Expression<T> {
		abstract T invoke(ExecutionEnvironment environment);
	}
	
	final class BooleanValidatingExpression extends Expression<Boolean> {
		private final Expression<? extends Object> expression;
		BooleanValidatingExpression(Expression<? extends Object> expression) {
			this.expression = expression;
		}
		Boolean invoke(ExecutionEnvironment environment) {
			Object result = expression.invoke(environment);
			if (result instanceof Boolean) {
				return (Boolean)result;
			}
			throw new IncompatibleTypeException("Type mismatch when executing simple proposition with id: " + propositionDefinition.getPropId(), result, Boolean.class);
		}
	}
	
	final class BinaryOperator extends Expression<Boolean> {
		
		private final ComparisonOperator operator;
		private final Expression<Object> lhs;
		private final Expression<Object> rhs;
		
		BinaryOperator(ComparisonOperator operator, Expression<Object> lhs, Expression<Object> rhs) {
			this.operator = operator;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		Boolean invoke(ExecutionEnvironment environment) {
			Object lhsValue = lhs.invoke(environment);
			Object rhsValue = rhs.invoke(environment);
			return operator.compare(lhsValue, rhsValue);
		}
		
	}
	
	final class FunctionExpression extends Expression<Object> {
		
		Function function;
		List<Expression<? extends Object>> arguments;
		
		FunctionExpression(Function function, List<Expression<? extends Object>> arguments) {
			this.function = function;
			this.arguments = arguments;
		}
		
		@Override
		Object invoke(ExecutionEnvironment environment) {
			List<Object> argumentValues = new ArrayList<Object>(arguments.size());
			for (Expression<? extends Object> argument : arguments) {
				argumentValues.add(argument.invoke(environment));
			}
			return function.invoke(argumentValues);
		}		
		
	}
	
	final class Constant<T> extends Expression<T> {
		
		private final T value;
		
		Constant(T value) {
			this.value = value;
		}
		
		@Override
		T invoke(ExecutionEnvironment environment) {
			return value;
		}
		
	}
	
	final class DataTerm extends Expression<Object> {
		
		private final Term term;
		
		DataTerm(Term term) {
			this.term = term;
		}
		
		@Override
		Object invoke(ExecutionEnvironment environment) {
			try {
				return termResolutionEngine.resolveTerm(term);
			} catch (TermResolutionException e) {
				// TODO - term resolution exception needs to become runtime
				throw new RuntimeException(e);
			}
		}	
		
	}
	
	// TODO this is temporary until peter gets the data model hooked up to the term service
	final class TermResolverParmaeter {
		
	}

}
