package org.kuali.rice.krms.api.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum LogicalOperator {
	
	AND("&"),
	OR("|");

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
