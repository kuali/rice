package org.kuali.rice.kew.framework.document.search;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * An enumeration which defines the set of available document search customizations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = "documentSearchCustomization")
@XmlType(name = "DocumentSearchCustomizationType")
@XmlEnum
public enum DocumentSearchCustomization {

    /**
     * Customization of the document search criteria.
     */
    CRITERIA,

    /**
     * Customization of clearing of the document search criteria.
     */
    CLEAR_CRITERIA,

    /**
     * Customization of document search results.
     */
    RESULTS,

    /**
     * Customization of the document search result attribute fields.
     */
    RESULT_SET_FIELDS;

}
