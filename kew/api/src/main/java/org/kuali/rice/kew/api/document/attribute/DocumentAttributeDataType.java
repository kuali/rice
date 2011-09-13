package org.kuali.rice.kew.api.document.attribute;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the valid data types for implementations of the {@link DocumentAttributeContract}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = "documentAttributeDataType")
@XmlType(name = "DocumentAttributeDataTypeType")
@XmlEnum
public enum DocumentAttributeDataType {

    /**
     * Indicates a document attribute which holds character data.
     */
    STRING,

    /**
     * Indicates a document attribute which holds date and (optional) time information.
     */
    DATE_TIME,

    /**
     * Indicates a document attribute which holds an integer value.
     */
    INTEGER,

    /**
     * Indicates a document attribute which holds a real number.
     */
    DECIMAL

}
