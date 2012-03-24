/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor;
import org.kuali.test.KRADTestCase;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.*;

import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

/**
 * Unit tests for the UIF Bean Factory Post Processor
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifBeanFactoryPostProcessorTest extends KRADTestCase {
    /**
     * Tests the postProcessBeanFactory method using a bean with simple inheritance
     */
    @Test
    public void testPostProcessBeanFactoryWithSimpleInheritanceSucceeds() throws Exception {
        String firstBeanId = "MyBean";
        String secondBeanId = "YourBean";
        String xmlDocument = createXMLDocument(firstBeanId, secondBeanId);
        KualiDefaultListableBeanFactory ddBeans = new KualiDefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ddBeans);
        xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);

        InputStream inputStream = new ByteArrayInputStream(xmlDocument.getBytes());
        Resource resource = new InputStreamResource(inputStream);
        xmlReader.loadBeanDefinitions(resource);
        inputStream.close();

        UifBeanFactoryPostProcessor factoryPostProcessor = new UifBeanFactoryPostProcessor();
        factoryPostProcessor.postProcessBeanFactory(ddBeans);

        BeanDefinition firstBeanDefinition = ddBeans.getBeanDefinition(firstBeanId);
        Map<String, String> firstBeanPropertyExpressions = (Map<String, String>)firstBeanDefinition.getPropertyValues().getPropertyValue("propertyExpressions").getValue();
        Assert.assertEquals(firstBeanPropertyExpressions.get("suppressActions"), "@{1 eq 1}");

        BeanDefinition secondBeanDefinition = ddBeans.getBeanDefinition(secondBeanId);
        Map<String, String> secondBeanPropertyExpressions = (Map<String, String>)secondBeanDefinition.getPropertyValues().getPropertyValue("propertyExpressions").getValue();
        Assert.assertNull(secondBeanPropertyExpressions.get("suppressActions"));
    }

    /**
     * Creates an XML document in string format
     *
     * @param firstBeanId - the ID of the first bean created in the document
     * @param secondBeanId - the ID of the second bean created in the document
     * @return a string of the XML document
     */
    private String createXMLDocument(String firstBeanId, String secondBeanId) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("beans");
            root.setAttribute("xmlns", "http://www.springframework.org/schema/beans");
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xmlns:p", "http://www.springframework.org/schema/p");
            root.setAttribute("xsi:schemaLocation", "http://www.springframework.org/schema/beans\n"
                    + "                    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd");
            document.appendChild(root);

            Element myBean = CreateMyBean(document, firstBeanId);
            root.appendChild(myBean);

            Element yourBean = CreateYourBean(document, secondBeanId);
            root.appendChild(yourBean);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return stringWriter.toString();
        } catch (Exception e) {
        }
        
        return "";
    }

    /**
     * Creates a simple bean
     *
     * @param document
     * @param id
     * @return
     */
    private Element CreateMyBean(Document document, String id) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", id);
        bean.setAttribute("name", id);
        bean.setAttribute("class", "org.kuali.rice.krad.uif.view.LookupView");

        Element beanChild = document.createElement("property");
        beanChild.setAttribute("name", "suppressActions");
        beanChild.setAttribute("value", "@{1 eq 1}");
        bean.appendChild(beanChild);
        return bean;
    }

    /**
     * Creates a simple bean
     *
     * @param document
     * @param id
     * @return
     */
    private Element CreateYourBean(Document document, String id) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", id);
        bean.setAttribute("name", id);
        bean.setAttribute("parent", "MyBean");

        Element beanChild = document.createElement("property");
        beanChild.setAttribute("name", "suppressActions");
        beanChild.setAttribute("value", "false");
        bean.appendChild(beanChild);
        return bean;
    }
}
