package org.kuali.rice.kew.api.document.search;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines flags that can be used during document search to indicate what relation the route node name being searched on
 * should have to the current route node of the documents being searched.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = "routeNodeLookupLogic")
@XmlType(name = "RouteNodeLookupLogicType")
@XmlEnum
public enum RouteNodeLookupLogic {

    /**
     * Indicates that the document should be exactly at the specified route node in it's route path.
     */
    EXACTLY,

    /**
     * Indicates that the document should currently be somewhere before the specified route node in it's route path.
     */
    BEFORE,

    /**
     * Indicates that the document should currently be somewhere after the specified route node in it's route path.
     */
    AFTER

}
