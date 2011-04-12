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
		
	@Override
	public String toString(){
		return opCode;
	}
}
