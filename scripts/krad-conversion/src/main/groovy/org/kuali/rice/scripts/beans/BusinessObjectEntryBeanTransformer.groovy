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

/**
 * This class converts business object entries into data object entries
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class BusinessObjectEntryBeanTransformer extends SpringBeanTransformer {

    /**
     * Processes BusinessObjectEntry and can be used on attribute definitions
     *
     * @param beanNode
     * @return
     */
    def transformBusinessObjectEntryBean(Node beanNode) {
        if (beanNode?.@parent == "BusinessObjectEntry") {
            beanNode.@parent = "DataObjectEntry";
        }
        transformControlProperty(beanNode, ddBeanControlMap, replacePropertyDuringConversion);
        transformValidationPatternBeanProperty(beanNode, replacePropertyDuringConversion)
        this.removeProperties(beanNode, ddPropertiesRemoveList);
        this.renameProperties(beanNode, ddPropertiesMap);
        renamePropertyBeans(beanNode, ddPropertiesMap, false);

        return beanNode
    }

    /**
     * Modifies validationPattern into KRAD validCharactersConstraint
     *
     * @param beanNode
     * @param replaceNode - if true, replace existing KNS node. if false, add new KRAD node, keeping KNS node
     * @return
     */
    def transformValidationPatternBeanProperty(Node beanNode, boolean replaceNode) {
        def validationPatternProperty = beanNode?.property?.find { "validationPattern".equals(it.@name) };
        if (validationPatternProperty){
            if (replaceNode){
                // transform the existing validationPattern node into KRAD validCharactersConstraint
                validationPatternProperty.replaceNode {
                    transformValidationPatternProperty(delegate, validationPatternProperty);
                }
            }  else {
                // build a new KRAD validationCharactersConstraint and add it to the parent, keeping the existing KNS validationPattern
                validationPatternProperty.plus {
                    transformValidationPatternProperty(delegate, validationPatternProperty);
                }
            }
        }
    }

    /**
     * Builds a KRAD validCharactersConstraint property node, with corresponding validationPatterConstraint bean.
     * @param builder
     * @param beanNode
     * @return the newly created property node
     */
    def transformValidationPatternProperty(NodeBuilder builder, Node beanNode) {
        def patternBean = beanNode.bean.find { return true; }
        def beanAttributes  = gatherValidationPatternAttributes(patternBean)
        def propertyAttributes = [name: 'validCharactersConstraint']
        if (beanNode.@id) {
            propertyAttributes.put("id", beanNode.@id)
        }
        builder.property(propertyAttributes) {
            beanAttributes.put("parent", validationPatternMap[patternBean.@parent])
            genericBeanTransform(builder, beanAttributes)
        }
    }

}
