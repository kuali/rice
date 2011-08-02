package org.kuali.rice.kew.api.document.attribute;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * TODO ...
 */
@XmlRootElement(name = "documentAttributeDataType")
@XmlType(name = "DocumentAttributeDataTypeType")
@XmlEnum
public enum DocumentAttributeDataType {

    STRING, DATE_TIME, INTEGER, DECIMAL

}
