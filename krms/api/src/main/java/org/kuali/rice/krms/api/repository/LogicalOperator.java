package org.kuali.rice.krms.api.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "LogicalOperatorType")
@XmlEnum(String.class)
public enum LogicalOperator {
	
	@XmlEnumValue(value="&") AND("&"),
	@XmlEnumValue(value="|") OR("|");

	private final String opCode;
	
	private LogicalOperator(String op){
		this.opCode = op;
	}
	
	public String opCode(){
		return opCode;
	}
	
	public static final Collection<String> OP_CODES =
		Collections.unmodifiableCollection(Arrays.asList(AND.opCode, OR.opCode));
		
	public static LogicalOperator fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (LogicalOperator logicalOperator : values()) {
			if (logicalOperator.opCode.equals(code)) {
				return logicalOperator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the LogicalOperator with the given code: " + code);
	}
	
	@Override
	public String toString(){
		return opCode;
	}
}
