package org.kuali.rice.kew.api.document.lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines flags that can be used during document lookup to indicate what relation
 * the route node name being searched on should have to the current route node of
 * the documents being searched.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = "routeNodeLookupLogic")
@XmlType(name = "RouteNodeLookupLogicType")
@XmlEnum
public enum RouteNodeLookupLogic {

    EXACTLY, BEFORE, AFTER

}
