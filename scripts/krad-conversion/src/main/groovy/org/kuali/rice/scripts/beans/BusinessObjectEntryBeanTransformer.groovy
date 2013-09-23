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
        transformValidationPatternProperty(beanNode, replacePropertyDuringConversion)
        this.removeProperties(beanNode, ddPropertiesRemoveList);
        this.renameProperties(beanNode, ddPropertiesMap);
        renamePropertyBeans(beanNode, ddPropertiesMap, false);

        return beanNode
    }

    /**
     * Modifies control and controlField elements into Uif Control elements
     *
     * @param beanNode
     * @param renamedControlBeans
     * @param replace
     * @return
     */
    def transformControlProperty(def beanNode, Map<String, String> renamedControlBeans, boolean replace) {
        def controlProperty = beanNode?.property?.find { "control".equals(it.@name) };
        def controlFieldProperty = beanNode?.property?.find { "controlField".equals(it.@name) };
        if (controlProperty) {
            def controlDefBean = controlProperty.bean.find { it.@parent?.endsWith("Definition") };
            def controlDefParent = controlDefBean.@parent;
            if (controlFieldProperty && replace) {
                this.removeProperties(beanNode, ["control"]);
            } else if (renamedControlBeans.get(controlDefParent) != null) {
                if (replace){
                    controlProperty.replaceNode {
                        property(name: "controlField") {
                            transformControlDefinitionBean(delegate, controlDefBean, renamedControlBeans)
                        }
                    }
                }else {
                    controlProperty.plus {
                        property(name: "controlField") {
                            transformControlDefinitionBean(delegate, controlDefBean, renamedControlBeans)
                        }
                    }
                }

                if ("Uif-VerticalRadioControl".equals(renamedControlBeans.get(controlDefParent)) ||
                        "Uif-DropdownControl".equals(renamedControlBeans.get(controlDefParent))) {
                    def attributes = genericGatherAttributes(controlDefBean, ["*valuesFinderClass": "p:optionsFinder"]);
                    controlProperty.plus {
                        property(name: "optionsFinder") {
                            bean(class: attributes.get("p:optionsFinder").value)
                        }
                    }
                }
            }
        }
    }

    /**
     * Used for transforming control definitions into control field properties
     *
     * @param builder
     * @param controlDefBean
     * @param controlDefReplacements
     * @return
     */
    def transformControlDefinitionBean(NodeBuilder builder, Node controlDefBean, Map<String, String> controlDefReplacements) {
        String controlDefParent = controlDefBean.@parent.toString()
        def controlDefReplacementBean = controlDefReplacements[controlDefParent]
        def attributes = gatherControlAttributes (controlDefBean)
        if ("Uif-DropdownControl".equals(controlDefReplacementBean)) {
            attributes.putAll(genericGatherAttributes(controlDefBean, ["*includeKeyInLabel": "p:includeKeyInLabel"]))
            attributes.put("parent", "Uif-DropdownControl")
        } else if ("Uif-VerticalRadioControl".equals(controlDefReplacementBean)) {
            attributes.putAll(genericGatherAttributes(controlDefBean, ["*includeKeyInLabel": "p:includeKeyInLabel"]))
            attributes.put("parent", "Uif-VerticalRadioControl")
        } else if ("Uif-TextAreaControl".equals(controlDefReplacementBean)) {
            attributes.putAll(genericGatherAttributes(controlDefBean, ["*rows": "p:rows", "*cols": "p:cols"]))
            attributes.put("parent", "Uif-TextAreaControl")
        } else if ("Uif-LinkField".equals(controlDefReplacementBean)) {
            attributes.putAll(genericGatherAttributes(controlDefBean, ["*target": "p:target", "*hrefText": "p:linkText", "*styleClass":"p:fieldLabel.cssClasses"]))
            attributes.put("parent", "Uif-LinkField")
            attributes.put("href", "@{#propertyName}")
        } else if ("Uif-CurrencyTextControl".equals(controlDefReplacementBean)) {
            attributes.putAll(genericGatherAttributes(controlDefBean, ["*formattedMaxLength": "p:maxLength", "*size": "p:size"]))
            attributes.put("parent", "Uif-CurrencyTextControl")
        } else if (controlDefReplacementBean != null) {
            attributes.put("parent", controlDefReplacements[controlDefParent])
        }else {
            attributes.put("parent", "Uif-" + controlDefParent.replace("Definition", ""))
        }
        genericBeanTransform(builder, attributes)
    }

    /**
     * Modifies KNS validationPattern property into KRAD validCharactersConstraint
     *
     * @param beanNode
     * @param replace - boolean.  if true, replace existing KNS node. if false, add new KRAD node, keeping KNS node
     * @return KRAD validCharacterConstraint property element with appropriate child validationPatternConstraint bean.
     */
    def transformValidationPatternProperty(Node beanNode, boolean replace) {
        def validationPatternProperty = beanNode?.property?.find { "validationPattern".equals(it.@name) };
        if (validationPatternProperty){
            if (replace){
                // transform the existing validationPattern node into KRAD validCharactersConstraint
                validationPatternProperty.replaceNode {
                    buildValidationPatternProperty(delegate, validationPatternProperty);
                }
            }  else {
                // build a new KRAD validationCharactersConstraint and add it to the parent, keeping the existing KNS validationPattern
                validationPatternProperty.plus {
                    buildValidationPatternProperty(delegate, validationPatternProperty);
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
    def buildValidationPatternProperty(NodeBuilder builder, Node beanNode) {
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
