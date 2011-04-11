package org.kuali.rice.kns.datadictionary.validation;

import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;

/**
 * A simple data type enum inherited from the Kuali Student project, that can be used to define a specific data type for a dictionary object 
 * or one of its member attributes. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlEnum
public enum DataType {
	STRING(String.class), DATE(Date.class), TRUNCATED_DATE(Date.class), BOOLEAN(Boolean.class), INTEGER(Integer.class), FLOAT(Float.class), DOUBLE(Double.class), LONG(Long.class), COMPLEX(Object.class);
	
	private Class<?> type;
	
	private DataType(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
}
