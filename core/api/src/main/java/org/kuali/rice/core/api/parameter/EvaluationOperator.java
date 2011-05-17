package org.kuali.rice.core.api.parameter;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.util.jaxb.EnumStringAdapter;

/**
 * Defines the possible evaluation operators that can be supported on system parameters.
 */
@XmlType(name = "EvaluationOperatorType")
@XmlJavaTypeAdapter(EvaluationOperator.Adapter.class)
public enum EvaluationOperator implements Coded {

	/**
	 * Indicates that evaluation will determine if the value being tested in present
	 * in the set of values defined on the parameter.  If it is present in this set,
	 * then evaluation will succeed.
	 */
	ALLOW("A"),
	
	/**
	 * Indicates that evaluation will determine if the value being tested is absent
	 * from the set of values defined on the parameter.  If it is absent from this
	 * set, then the evaluation will succeed.
	 */
	DISALLOW("D");
	
	private final String code;
	
	EvaluationOperator(final String code) {
		this.code = code;
	}
		
	/**
	 * Returns the operator code for this evaluation operator.
	 * 
	 * @return the code
	 */
	@Override
	public String getCode() {
		return code;
	}
	
	public static EvaluationOperator fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (EvaluationOperator operator : values()) {
			if (operator.code.equals(code)) {
				return operator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the EvaluationOperator with the given code: " + code);
	}
	
	static final class Adapter extends EnumStringAdapter<EvaluationOperator> {
		
		protected Class<EvaluationOperator> getEnumClass() {
			return EvaluationOperator.class;
		}
		
	}
	
}
