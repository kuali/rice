package org.kuali.rice.krms.api.repository;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum LogicalOperator implements Coded {
	
	AND("&"),
	OR("|");

	private final String code;
	
	private LogicalOperator(String code){
		this.code = code;
	}
	
	@Override
	public String getCode(){
		return code;
	}
	
	public static final Collection<String> OP_CODES =
		Collections.unmodifiableCollection(Arrays.asList(AND.code, OR.code));
		
	public static LogicalOperator fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (LogicalOperator logicalOperator : values()) {
			if (logicalOperator.code.equals(code)) {
				return logicalOperator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the LogicalOperator with the given code: " + code);
	}
	
	@Override
	public String toString(){
		return code;
	}
	
	static final class Adapter extends EnumStringAdapter<LogicalOperator> {
		
		protected Class<LogicalOperator> getEnumClass() {
			return LogicalOperator.class;
		}
		
	}

}
