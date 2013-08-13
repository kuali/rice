/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.scripts.beans

import groovy.util.logging.Log
import org.apache.commons.lang.ClassUtils

/**
 * This class transforms lookup definitions into their uif counterpart as well as
 * any properties and children beans
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class LookupDefinitionBeanTransformer extends SpringBeanTransformer {

    /**
     * Produces Uif-LookupView based on information in LookupDefinition
     *
     * @param beanNode
     */
    def transformLookupDefinitionBean(Node beanNode) {
        removeChildrenBeans(beanNode);
        def lookupDefParentBeanNode = beanNode
        def lookupTitle = lookupDefParentBeanNode.property.find { it.@name == "title" }.@value;

        def objClassName = getObjectClassName(lookupDefParentBeanNode);
        def objName = ClassUtils.getShortClassName(objClassName);
        beanNode.replaceNode {
            addComment(delegate, "Lookup View")
            bean(id: "$objName-LookupView", parent: "Uif-LookupView") {
                property(name: "headerText", value: lookupTitle)
                addViewNameProperty(delegate, lookupTitle)
                property(name: "dataObjectClassName", value: objClassName)
                transformMenubarProperty(delegate, beanNode)
                transformLookupFieldsProperty(delegate, beanNode)
                transformResultFieldsProperty(delegate, beanNode)
            }
        }
    }

    /**
     * Transforms lookup field properties into criteria fields
     *
     * @param builder
     * @param beanNode
     */
    def transformLookupFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["lookupFields": "criteriaFields"], attributeNameAttrCondition, lookupCriteriaFieldBeanTransform);
    }

    /**
     * Transforms result field properties into data fields
     *
     * @param builder
     * @param beanNode
     */
    def transformResultFieldsProperty(NodeBuilder builder, Node beanNode) {
        transformPropertyBeanList(builder, beanNode, ["resultFields": "resultFields"], attributeNameAttrCondition, dataFieldBeanTransform);
    }


    /**
     * Replaces menubar property with uif message
     *
     * @param builder
     * @param node
     * @return
     */
    def transformMenubarProperty(NodeBuilder builder, Node node) {
        def menubarPropNode = node.property.find { it.@name == "menubar" };
        if (node != null) {
            builder.property(name: "page.header.lowerGroup.items") {
                list(merge: "true") {
                    bean(parent: "Uif-Message") {
                        property(name: "messageText", value: "[" + node.@value + "]")
                    }
                }
            }
        }
    }

}
