package org.kuali.rice.kns.dto;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum DataType {
	STRING, DATE, TRUNCATED_DATE, BOOLEAN, INTEGER, FLOAT, DOUBLE, LONG, COMPLEX
}
