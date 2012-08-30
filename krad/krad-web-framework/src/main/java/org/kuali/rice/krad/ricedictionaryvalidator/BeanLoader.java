/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.ricedictionaryvalidator;

import org.kuali.rice.krad.datadictionary.DataDictionaryException;
import org.kuali.rice.krad.datadictionary.parse.StringListConverter;
import org.kuali.rice.krad.datadictionary.parse.StringMapConverter;
import org.kuali.rice.krad.uif.util.ComponentBeanPostProcessor;
import org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;

/**
 * Handles the loading of a collection of Spring Beans in the same format as the KRAD Framework
 * from a list of xml files.
 */
public class BeanLoader {
    private KualiDefaultListableBeanFactory beans;
    private XmlBeanDefinitionReader xmlReader;

    // logger
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BeanLoader.class);

    /**
     * Constructor creating a new loader
     */
    public BeanLoader(){

    }

    /**
     * Loads the Spring Beans from a list of xml files
     * @param xmlFiles
     * @return The Spring Bean Factory for the provided list of xml files
     */
    public KualiDefaultListableBeanFactory loadBeans(String[] xmlFiles){

        LOG.info("Starting XML File Load");
        beans = new KualiDefaultListableBeanFactory();
        xmlReader = new XmlBeanDefinitionReader(beans);

        configureFactory();


        ArrayList<String> coreFiles=new ArrayList<String>();
        ArrayList<String> testFiles = new ArrayList<String>();

        for(int i=0;i<xmlFiles.length;i++){
            if(xmlFiles[i].contains("classpath")){
                coreFiles.add(xmlFiles[i]);
            }else{
                testFiles.add(xmlFiles[i]);
            }
        }
        String core[]=new String[coreFiles.size()];
        coreFiles.toArray(core);

        String test[]=new String[testFiles.size()];
        testFiles.toArray(test);

        loadCoreBeans(core);
        loadTestBeans(test);

        ProcessBeans();

        LOG.info("Completed XML File Load");

        return beans;
    }

    /**
     * Configures the Bean Factory to the same settings as the KRAD Framework
     */
    private void configureFactory(){
        try {
            BeanPostProcessor idPostProcessor = ComponentBeanPostProcessor.class.newInstance();
            beans.addBeanPostProcessor(idPostProcessor);
            beans.setBeanExpressionResolver(new StandardBeanExpressionResolver());
            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new StringMapConverter());
            conversionService.addConverter(new StringListConverter());
            beans.setConversionService(conversionService);
        } catch (Exception e1) {
            LOG.error("Cannot create component decorator post processor: " + e1.getMessage(), e1);
            throw new RuntimeException("Cannot create component decorator post processor: " + e1.getMessage(), e1);
        }
    }

    /**
     * Process the beans in the Bean Factroy
     */
    private void ProcessBeans(){
        UifBeanFactoryPostProcessor factoryPostProcessor = new UifBeanFactoryPostProcessor();
        factoryPostProcessor.postProcessBeanFactory(beans);
    }

    /**
     * Converts the list of file paths into a list of resources
     * @param files The list of file paths for conversion
     * @return A list of resources created from the file paths
     */
    private Resource[] getResources(String files[]){
        Resource resources[] = new Resource[files.length];
        for(int i=0;i<files.length;i++){
            resources[0]=new FileSystemResource(files[i]);
        }

        return resources;
    }

    private void loadTestBeans(String files[]){
        try {
            xmlReader.loadBeanDefinitions(getResources(files));
        } catch (Exception e) {
            LOG.error("Error loading bean definitions", e);
            throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
        }
    }

    private void loadCoreBeans(String files[]){
        try {
            xmlReader.loadBeanDefinitions(files);
        } catch (Exception e) {
            LOG.error("Error loading bean definitions", e);
            throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
        }
    }

}
