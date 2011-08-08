package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krad.bo.MutableInactivatable

/**
 * Created by IntelliJ IDEA.
 * User: gilesp
 * Date: 8/3/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: implement contract interface
class PeopleFlowBo extends PersistableBusinessObjectBase implements MutableInactivatable {

    def String id
    def String name
    def String namespace
    def String typeId
    def String description
    def boolean active

    def List<PeopleFlowAttributeBo> attributes;
    def List<PeopleFlowMemberBo> members;
}
