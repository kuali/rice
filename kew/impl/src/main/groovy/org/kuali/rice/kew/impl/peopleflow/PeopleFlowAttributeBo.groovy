package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.kew.impl.type.KewAttributeDefinitionBo
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

/**
 * Created by IntelliJ IDEA.
 * User: gilesp
 * Date: 8/3/11
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: implement contract interface
class PeopleFlowAttributeBo extends PersistableBusinessObjectBase {

    def String id
	def String attributeDefinitionId
	def String value
    def String peopleFlowId

	def KewAttributeDefinitionBo attributeDefinition
}
