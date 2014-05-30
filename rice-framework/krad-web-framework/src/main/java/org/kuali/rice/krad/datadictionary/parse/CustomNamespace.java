/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.parse;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.Map;

/**
 * Handles the registering of custom schema tags with the custom parser for the Spring Xml Authoring System.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomNamespace extends NamespaceHandlerSupport {

    /**
     * Registers the tag name with the custom parser in Spring.
     */
    public void init() {
        Map<String, BeanTagInfo> tags = CustomTagAnnotations.getBeanTags();

        for (String customTag : tags.keySet()) {
            registerBeanDefinitionParser(customTag, new CustomSchemaParser());
        }

        registerBeanDefinitionParser("inc", new CustomSchemaParser());
        registerBeanDefinitionParser("property", new CustomSchemaParser());
        registerBeanDefinitionParser("bean", new CustomSchemaParser());
        registerBeanDefinitionParser("value", new CustomSchemaParser());
    }
}
