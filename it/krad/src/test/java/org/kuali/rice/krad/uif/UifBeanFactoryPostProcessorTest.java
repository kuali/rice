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
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.test.KRADTestCase;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
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
     * Tests the postProcessBeanFactory method using beans with simple inheritance
     */
    @Test
    public void testPostProcessBeanFactoryWithSimpleInheritanceSucceeds() throws Exception {
        String firstBeanId = "MyBean";
        String secondBeanId = "YourBean";
        String xmlDocument = createSimpleXMLDocument(firstBeanId, secondBeanId);
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
        Assert.assertEquals(firstBeanPropertyExpressions.get("labelRendered"), "@{1 eq 1}");

        BeanDefinition secondBeanDefinition = ddBeans.getBeanDefinition(secondBeanId);
        Map<String, String> secondBeanPropertyExpressions = (Map<String, String>)secondBeanDefinition.getPropertyValues().getPropertyValue("propertyExpressions").getValue();
        Assert.assertNull(secondBeanPropertyExpressions.get("labelRendered"));
    }

    /**
     * Tests the postProcessBeanFactory method using beans with inheritance and nested properties
     */
    @Test
    public void testPostProcessBeanFactoryWithSimpleNestingSucceeds() throws Exception {
        String firstBeanId = "MyBean";
        String secondBeanId = "HisBean";
        String thirdBeanId = "HerBean";
        String xmlDocument = createComplexXMLDocument(firstBeanId, secondBeanId, thirdBeanId);
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
        Assert.assertEquals(firstBeanPropertyExpressions.get("labelRendered"), "@{1 eq 1}");

        BeanDefinition thirdBeanDefinition = ddBeans.getBeanDefinition(thirdBeanId);

        if (thirdBeanDefinition.getPropertyValues().getPropertyValue("propertyExpressions") != null) {
            Map<String, String> thirdBeanPropertyExpressions = (Map<String, String>)thirdBeanDefinition.getPropertyValues().getPropertyValue("propertyExpressions").getValue();
            Assert.assertNull(thirdBeanPropertyExpressions.get("labelRendered"));
        }
    }

    /**
     * Tests the postProcessBeanFactory method using beans with inheritance and nested properties
     * This test should not run successfully until we implement a fix for KULRICE-6845
     */
    @Ignore
    @Test
    public void testPostProcessBeanFactoryWithPeopleFlowSucceeds() throws Exception {
        String xmlDocument = createPeopleFlowXMLDocument();
        KualiDefaultListableBeanFactory ddBeans = new KualiDefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ddBeans);
        xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);

        InputStream inputStream = new ByteArrayInputStream(xmlDocument.getBytes());
        Resource resource = new InputStreamResource(inputStream);
        xmlReader.loadBeanDefinitions(resource);
        inputStream.close();

        UifBeanFactoryPostProcessor factoryPostProcessor = new UifBeanFactoryPostProcessor();
        factoryPostProcessor.postProcessBeanFactory(ddBeans);

        BeanDefinition peopleFlowBeanDefinition = ddBeans.getBeanDefinition("PeopleFlow-InquiryView");
        ManagedList list = (ManagedList)peopleFlowBeanDefinition.getPropertyValues().getPropertyValue("Items").getValue();
        ManagedList items = (ManagedList)((BeanDefinitionHolder)list.get(0)).getBeanDefinition().getPropertyValues().getPropertyValue("items").getValue();

        Assert.assertNotNull(((BeanDefinitionHolder) items.get(0)).getBeanDefinition().getPropertyValues().getPropertyValue("inquiry"));

        Inquiry inquiry = (Inquiry)((BeanDefinitionHolder) items.get(0)).getBeanDefinition().getPropertyValues().getPropertyValue("inquiry.render").getValue();
        Assert.assertFalse(inquiry.isRender());
    }

    /**
     * Creates an XML document in string format
     *
     * @param firstBeanId
     * @param secondBeanId
     * @param thirdBeanId
     * @return
     */
    private String createComplexXMLDocument(String firstBeanId, String secondBeanId, String thirdBeanId) {
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

            Element hisBean = CreateHisBean(document, secondBeanId);
            root.appendChild(hisBean);

            Element herBean = CreateHerBean(document, thirdBeanId);
            root.appendChild(herBean);

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
     * Creates an XML document in string format
     *
     * @param firstBeanId - the ID of the first bean created in the document
     * @param secondBeanId - the ID of the second bean created in the document
     * @return a string of the XML document
     */
    private String createSimpleXMLDocument(String firstBeanId, String secondBeanId) {
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
     * Creates an XML document in string format
     *
     * @return
     */
    private String createPeopleFlowXMLDocument() {
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

            Element inquiryViewParentBean = CreateInquiryViewParentBean(document);
            root.appendChild(inquiryViewParentBean);

            Element inquiryViewBean = CreateInquiryViewBean(document);
            root.appendChild(inquiryViewBean);

            Element componentBaseParentBean = CreateComponentBaseParentBean(document);
            root.appendChild(componentBaseParentBean);

            Element componentBaseBean = CreateComponentBaseBean(document);
            root.appendChild(componentBaseBean);

            Element groupBaseParentBean = CreateGroupBaseParentBean(document);
            root.appendChild(groupBaseParentBean);

            Element groupBaseBean = CreateGroupBaseBean(document);
            root.appendChild(groupBaseBean);

            Element gridGroupParentBean = CreateGridGroupParentBean(document);
            root.appendChild(gridGroupParentBean);

            Element gridGroupBean = CreateGridGroupBean(document);
            root.appendChild(gridGroupBean);

            Element gridSectionParentBean = CreateGridSectionParentBean(document);
            root.appendChild(gridSectionParentBean);

            Element gridSectionBean = CreateGridSectionBean(document);
            root.appendChild(gridSectionBean);

            Element disclosureGridSectionParentBean = CreateDisclosureGridSectionParentBean(document);
            root.appendChild(disclosureGridSectionParentBean);

            Element disclosureGridSectionBean = CreateDisclosureGridSectionBean(document);
            root.appendChild(disclosureGridSectionBean);

            Element fieldBaseParentBean = CreateFieldBaseParentBean(document);
            root.appendChild(fieldBaseParentBean);

            Element fieldBaseBean = CreateFieldBaseBean(document);
            root.appendChild(fieldBaseBean);

            Element fieldBaseWithLabelParentBean = CreateFieldBaseWithLabelParentBean(document);
            root.appendChild(fieldBaseWithLabelParentBean);

            Element fieldBaseWithLabelBean = CreateFieldBaseWithLabelBean(document);
            root.appendChild(fieldBaseWithLabelBean);

            Element dataFieldParentBean = CreateDataFieldParentBean(document);
            root.appendChild(dataFieldParentBean);

            Element dataFieldBean = CreateDataFieldBean(document);
            root.appendChild(dataFieldBean);

            Element peopleFlowInquiryBean = CreatePeopleFlowInquiryViewBean(document);
            root.appendChild(peopleFlowInquiryBean);

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
        bean.setAttribute("class", "org.kuali.rice.krad.uif.field.FieldBase");

        Element beanChild = document.createElement("property");
        beanChild.setAttribute("name", "labelRendered");
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
        beanChild.setAttribute("name", "labelRendered");
        beanChild.setAttribute("value", "false");
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
    private Element CreateHisBean(Document document, String id) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", id);
        bean.setAttribute("name", id);
        bean.setAttribute("class", "org.kuali.rice.krad.uif.view.LookupView");

        Element beanChild = document.createElement("property");
        beanChild.setAttribute("name", "resultsActionsField");

        Element beanPropertyBean = document.createElement("bean");
        beanPropertyBean.setAttribute("parent", "MyBean");
        beanChild.appendChild(beanPropertyBean);
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
    private Element CreateHerBean(Document document, String id) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", id);
        bean.setAttribute("name", id);
        bean.setAttribute("parent", "HisBean");

        Element beanChild = document.createElement("property");
        beanChild.setAttribute("name", "resultsActionsField.labelRendered");
        beanChild.setAttribute("value", "false");
        bean.appendChild(beanChild);
        return bean;
    }

    /**
     * Creates a simple bean
     *
     * @param document
     * @return
     */
    private Element CreateInquiryViewParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-InquiryView-parentBean");
        bean.setAttribute("class", "org.kuali.rice.krad.uif.view.InquiryView");
        return bean;
    }

    /**
     * Creates a simple bean
     *
     * @param document
     * @return
     */
    private Element CreateInquiryViewBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-InquiryView");
        bean.setAttribute("parent", "Uif-InquiryView-parentBean");
        return bean;
    }

    /**
     * Creates a group base bean
     *
     * @param document
     * @return
     */
    private Element CreateGroupBaseBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-GroupBase");
        bean.setAttribute("parent", "Uif-GroupBase-parentBean");
        return bean;
    }

    /**
     * Creates a group base parent bean
     *
     * @param document
     * @return
     */
    private Element CreateGroupBaseParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-GroupBase-parentBean");
        bean.setAttribute("class", "org.kuali.rice.krad.uif.container.Group");
        bean.setAttribute("parent", "Uif-ComponentBase");
        return bean;
    }

    /**
     * Creates a component base bean
     *
     * @param document
     * @return
     */
    private Element CreateComponentBaseBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-ComponentBase");
        bean.setAttribute("parent", "Uif-ComponentBase-parentBean");
        return bean;
    }

    /**
     * Creates a component base parent bean
     *
     * @param document
     * @return
     */
    private Element CreateComponentBaseParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-ComponentBase-parentBean");
        bean.setAttribute("class", "org.kuali.rice.krad.uif.component.ComponentBase");
        return bean;
    }

    /**
     * Creates a disclosure grid section bean
     *
     * @param document
     * @return
     */
    private Element CreateDisclosureGridSectionBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-Disclosure-GridSection");
        bean.setAttribute("parent", "Uif-Disclosure-GridSection-parentBean");
        return bean;
    }

    /**
     * Creates a disclosure grid section parent bean
     *
     * @param document
     * @return
     */
    private Element CreateDisclosureGridSectionParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-Disclosure-GridSection-parentBean");
        bean.setAttribute("parent", "Uif-GridSection");
        return bean;
    }

    /**
     * Creates a grid section bean
     *
     * @param document
     * @return
     */
    private Element CreateGridSectionBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-GridSection");
        bean.setAttribute("parent", "Uif-GridSection-parentBean");
        return bean;
    }

    /**
     * Creates a grid section parent bean
     *
     * @param document
     * @return
     */
    private Element CreateGridSectionParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-GridSection-parentBean");
        bean.setAttribute("parent", "Uif-GridGroup");
        return bean;
    }

    /**
     * Creates a grid group bean
     *
     * @param document
     * @return
     */
    private Element CreateGridGroupBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-GridGroup");
        bean.setAttribute("parent", "Uif-GridGroup-parentBean");
        return bean;
    }

    /**
     * Creates a grid group parent bean
     *
     * @param document
     * @return
     */
    private Element CreateGridGroupParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-GridGroup-parentBean");
        bean.setAttribute("parent", "Uif-GroupBase");
        return bean;
    }

    /**
     * Creates a field base parent bean
     *
     * @param document
     * @return
     */
    private Element CreateFieldBaseParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-FieldBase-parentBean");
        bean.setAttribute("class", "org.kuali.rice.krad.uif.field.FieldBase");
        bean.setAttribute("parent", "Uif-ComponentBase");
        return bean;
    }

    /**
     * Creates a field base bean
     *
     * @param document
     * @return
     */
    private Element CreateFieldBaseBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-FieldBase");
        bean.setAttribute("parent", "Uif-FieldBase-parentBean");
        return bean;
    }

    /**
     * Creates a field base with label parent bean
     *
     * @param document
     * @return
     */
    private Element CreateFieldBaseWithLabelParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-FieldBase-withLabel-parentBean");
        bean.setAttribute("parent", "Uif-FieldBase");
        return bean;
    }

    /**
     * Creates a field base with label bean
     *
     * @param document
     * @return
     */
    private Element CreateFieldBaseWithLabelBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-FieldBase-withLabel");
        bean.setAttribute("parent", "Uif-FieldBase-withLabel-parentBean");
        return bean;
    }

    /**
     * Creates a data field parent bean
     *
     * @param document
     * @return
     */
    private Element CreateDataFieldParentBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-DataField-parentBean");
        bean.setAttribute("class", "org.kuali.rice.krad.uif.field.DataField");
        bean.setAttribute("parent", "Uif-FieldBase-withLabel");
        return bean;
    }

    /**
     * Creates a data field bean
     *
     * @param document
     * @return
     */
    private Element CreateDataFieldBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "Uif-DataField");
        bean.setAttribute("parent", "Uif-DataField-parentBean");
        return bean;
    }

    /**
     * Creates a people flow inquiry view bean
     *
     * @param document
     * @return
     */
    private Element CreatePeopleFlowInquiryViewBean(Document document) {
        Element bean = document.createElement("bean");
        bean.setAttribute("id", "PeopleFlow-InquiryView");
        bean.setAttribute("parent", "Uif-InquiryView");

        Element itemsProperty = document.createElement("property");
        itemsProperty.setAttribute("name", "Items");

        Element listElement = document.createElement("list");

        Element gridBean = document.createElement("bean");
        gridBean.setAttribute("parent", "Uif-Disclosure-GridSection");

        Element gridItemsProperty = document.createElement("property");
        gridItemsProperty.setAttribute("name", "items");

        Element gridItemsList = document.createElement("list");

        Element nameDataField = document.createElement("bean");
        nameDataField.setAttribute("parent", "Uif-DataField");

        Element nameDataFieldProperty = document.createElement("property");
        nameDataFieldProperty.setAttribute("name", "propertyName");
        nameDataFieldProperty.setAttribute("value", "name");

        Element inquiryRenderProperty = document.createElement("property");
        inquiryRenderProperty.setAttribute("name", "inquiry.render");
        inquiryRenderProperty.setAttribute("value", "false");

        nameDataField.appendChild(nameDataFieldProperty);
        nameDataField.appendChild(inquiryRenderProperty);
        gridItemsList.appendChild(nameDataField);
        gridItemsProperty.appendChild(gridItemsList);
        gridBean.appendChild(gridItemsProperty);
        listElement.appendChild(gridBean);
        itemsProperty.appendChild(listElement);
        bean.appendChild(itemsProperty);
        return bean;
    }
}
