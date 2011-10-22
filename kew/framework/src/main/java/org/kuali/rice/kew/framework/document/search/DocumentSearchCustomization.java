package org.kuali.rice.kew.framework.document.search;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * An enumeration which defines the set of available document lookup customizations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = "documentLookupCustomization")
@XmlType(name = "DocumentLookupCustomizationType")
@XmlEnum
public enum DocumentSearchCustomization {

    /**
     * Customization of the document lookup criteria.
     */
    CRITERIA,

    /**
     * Customization of clearing of the document lookup criteria.
     */
    CLEAR_CRITERIA,

    /**
     * Customization of document lookup results.
     */
    RESULTS,

    /**
     * Customization of the document lookup result attribute fields.
     */
    RESULT_SET_FIELDS;

}
