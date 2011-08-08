package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

/**
 * Created by IntelliJ IDEA.
 * User: gilesp
 * Date: 8/3/11
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: implement contract interface
class PeopleFlowMemberBo extends PersistableBusinessObjectBase {
    def String id
    def String peopleFlowId
    def String memberTypeCode
    def String memberId
    def Integer priority
    def String delegateFromId
}
