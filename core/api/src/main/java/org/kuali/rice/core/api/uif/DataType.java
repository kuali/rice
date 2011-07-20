package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlEnum;
import java.util.Date;

/**
 * A enum that defines the type of an attribute definition.  Some of these enum values may not be supported in certain contexts.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlEnum
public enum DataType {
	STRING(String.class), DATE(Date.class), TRUNCATED_DATE(Date.class), BOOLEAN(Boolean.class), INTEGER(Integer.class), FLOAT(Float.class), DOUBLE(Double.class), LONG(Long.class), COMPLEX(Object.class);
	
	private final Class<?> type;
	
	private DataType(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
}
