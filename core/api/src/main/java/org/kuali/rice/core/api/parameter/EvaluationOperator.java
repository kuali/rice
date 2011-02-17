package org.kuali.rice.core.api.parameter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the possible evaluation operators that can be supported on system parameters.
 */
@XmlType(name = "EvaluationOperatorType")
@XmlEnum(String.class)
public enum EvaluationOperator {

	/**
	 * Indicates that evaluation will determine if the value being tested in present
	 * in the set of vlaues defined on the parameter.  If it is present in this set,
	 * then evaluation will succeed.
	 */
	@XmlEnumValue(value="A") ALLOW("A"),
	
	/**
	 * Indicates that evaluation will determine if the value being tested is absent
	 * from the set of values defined on the parameter.  If it is absent from this
	 * set, then the evaluation will succeed.
	 */
	@XmlEnumValue(value="D") DISALLOW("D");
	
	private final String operatorCode;
	
	EvaluationOperator(final String operatorCode) {
		this.operatorCode = operatorCode;
	}
	
	/**
	 * Returns the operator code for this evaluation operator.
	 * 
	 * @return the operatorCode
	 */
	public String getOperatorCode() {
		return operatorCode;
	}
	
	public static EvaluationOperator fromOperatorCode(String operatorCode) {
		if (operatorCode == null) {
			return null;
		}
		for (EvaluationOperator operator : values()) {
			if (operator.operatorCode.equals(operatorCode)) {
				return operator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the EvaluationOperator with the given code: " + operatorCode);
	}
	
}
