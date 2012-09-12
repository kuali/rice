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
package org.kuali.rice.krad.datadictionary.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataDictionaryException;
import org.kuali.rice.krad.datadictionary.parse.StringListConverter;
import org.kuali.rice.krad.datadictionary.parse.StringMapConverter;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ComponentBeanPostProcessor;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * A validator for Rice Dictionaries that stores the information found during its validation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RDValidator {

    // logger
    private static final Log LOG = LogFactory.getLog(RDValidator.class);

    private ArrayList<ErrorReport> errorReports;
    private TracerToken tracerTemp;
    private int numberOfErrors;
    private int numberOfWarnings;

    /**
     * Constructor creating an empty validation report
     */
    public RDValidator() {
        numberOfErrors = 0;
        numberOfWarnings = 0;
        errorReports = new ArrayList<ErrorReport>();
    }

    /**
     * Runs the validations on a collection of beans
     *
     * @param beans - Collection of beans being validated
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    private boolean runValidations(KualiDefaultListableBeanFactory beans, boolean failOnWarning) {
        LOG.info("Starting Dictionary Validation");

        /*Map<String, View> uifBeans;

        try {
            uifBeans = beans.getBeansOfType(View.class);
            for (View views : uifBeans.values()) {
                try{
                    if (doValidationOnUIFBean(views)) {
                        TracerToken tracer = tracerTemp.getCopy();
                        tracer.setValidationStage(TracerToken.START_UP);
                        runValidationsOnComponents(views,tracer);
                    }
                } catch (Exception e) {
                    String value[] = {views.getId(),"Exception = "+e.getMessage()};
                    ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error Validating Bean View", "During Validation",value);
                    ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
                    temp.add(error);
                    compileReports(temp);
                }
            }
        }catch (Exception e){
            String value[] = {"Validation set = views","Exception = "+e.getMessage()};
            ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error in Loading Spring Beans", "Before Validation",value);
            ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
            temp.add(error);
            compileReports(temp);
        }*/

        Map<String, DataDictionaryEntry> ddBeans;

        try{
            ddBeans=beans.getBeansOfType(DataDictionaryEntry.class);
            for(DataDictionaryEntry entry : ddBeans.values()){
                try{

                    TracerToken tracer = tracerTemp.getCopy();
                    tracer.setValidationStage(TracerToken.BUILD);
                    compileReports(entry.completeValidation(tracer));

                }catch(Exception e){
                    String value[] = {"Validation set = Data Dictionary Entries","Exception = "+e.getMessage()};
                    ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error in Loading Spring Beans", "During Validation",value);
                    ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
                    temp.add(error);
                    compileReports(temp);
                }
            }
        }catch(Exception e){
            String value[] = {"Validation set = Data Dictionary Entries","Exception = "+e.getMessage()};
            ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error in Loading Spring Beans", "Before Validation",value);
            ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
            temp.add(error);
            compileReports(temp);
        }

        compileFinalReport();

        LOG.info("Completed Dictionary Validation");

        if (numberOfErrors > 0) {
            return false;
        }
        if (failOnWarning) {
            if (numberOfWarnings > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates a UIF Component
     *
     * @param object - The UIF Component to be validated
     * @param failOnWarning - Whether the validation should fail if warnings are found
     * @return Returns true if the validation passes
     */
    public boolean validate(Component object, boolean failOnWarning) {
        LOG.info("Starting Dictionary Validation");

        if (doValidationOnUIFBean(object)) {
            TracerToken tracer = new TracerToken();
            ArrayList<ErrorReport> reports;

            tracer.setValidationStage(TracerToken.BUILD);

            LOG.debug("Validating Component: " + object.getId());
            reports = object.completeValidation(tracer.getCopy());
            compileReports(reports);

            runValidationsOnLifecycle(object, tracer.getCopy());

            runValidationsOnPrototype(object, tracer.getCopy());

        }

        compileFinalReport();

        LOG.info("Completed Dictionary Validation");

        if (numberOfErrors > 0) {
            return false;
        }
        if (failOnWarning) {
            if (numberOfWarnings > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the beans in a collection of xml files
     *
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, boolean failOnWarning) {
        KualiDefaultListableBeanFactory beans = loadBeans(xmlFiles);

        return runValidations(beans, failOnWarning);
    }

    /**
     * Validates a collection of beans
     *
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param loader - The source that was used to load the beans
     * @param beans - Collection of preloaded beans
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String xmlFiles[],ResourceLoader loader,KualiDefaultListableBeanFactory beans, boolean failOnWarning) {
        tracerTemp=new TracerToken(xmlFiles,loader);
        return runValidations(beans, failOnWarning);
    }

    /**
     * Runs the validations on a component
     *
     * @param component - The component being checked
     * @param tracer - The current bean trace for the validation line
     */
    private void runValidationsOnComponents(Component component,TracerToken tracer){

        ArrayList<ErrorReport> reports;

        try{
            ExpressionUtils.populatePropertyExpressionsFromGraph(component, false);
        }catch (Exception e){
            String value[] = {"view = "+component.getId()};
            ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error Validating Bean View", "Loading Expressions",value);
            ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
            temp.add(error);
        }

        LOG.debug("Validating View: " + component.getId());

        try{
            reports = component.completeValidation(tracer.getCopy());
            compileReports(reports);
        }catch (Exception e){
            String value[] = {component.getId()};
            ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error Validating Bean View", "During Data Object Validation",value);
            ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
            temp.add(error);
        }

        try{
            runValidationsOnLifecycle(component, tracer.getCopy());
        }catch (Exception e){
            String value[] = {component.getId(),component.getComponentsForLifecycle().size()+""};
            ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error Validating Bean View", "During Lifecycle Validations",value);
            ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
            temp.add(error);
        }

        try{
            runValidationsOnPrototype(component, tracer.getCopy());
        }catch (Exception e){
            String value[] = {component.getId(),component.getComponentPrototypes().size()+""};
            ErrorReport error = new ErrorReport(ErrorReport.ERROR, "Error Validating Bean View", "During Prototype Validations",value);
            ArrayList<ErrorReport> temp = new ArrayList<ErrorReport>();
            temp.add(error);
        }
    }

    /**
     * Runs the validations on a components lifecycle items
     *
     * @param component - The component whose lifecycle items are being checked
     * @param tracer - The current bean trace for the validation line
     */
    private void runValidationsOnLifecycle(Component component, TracerToken tracer) {
        if (component.getComponentsForLifecycle() != null) {
            if (doValidationOnUIFBean(component)) {
                tracer.addBean(component);
                for (int j = 0; j < component.getComponentsForLifecycle().size(); j++) {
                    ;
                    Component temp = component.getComponentsForLifecycle().get(j);
                    if (temp != null) {
                        if (tracer.getValidationStage() == TracerToken.START_UP) {
                            ExpressionUtils.populatePropertyExpressionsFromGraph(temp, false);
                        }
                        if (temp.isRender()) {
                            compileReports(temp.completeValidation(tracer.getCopy()));
                            runValidationsOnLifecycle(temp, tracer.getCopy());
                        }
                    }
                }
            }
        }
    }

    /**
     * Runs the validations on a components prototypes
     *
     * @param component - The component whose prototypes are being checked
     * @param tracer - The current bean trace for the validation line
     */
    private void runValidationsOnPrototype(Component component, TracerToken tracer) {
        if (component.getComponentPrototypes() != null) {
            if (doValidationOnUIFBean(component)) {
                tracer.addBean(component);
                for (int j = 0; j < component.getComponentPrototypes().size(); j++) {
                    Component temp = component.getComponentPrototypes().get(j);
                    if (temp != null) {
                        if (tracer.getValidationStage() == TracerToken.START_UP) {
                            ExpressionUtils.populatePropertyExpressionsFromGraph(temp, false);
                        }
                        if (temp.isRender()) {
                            compileReports(temp.completeValidation(tracer.getCopy()));
                            runValidationsOnPrototype(temp, tracer.getCopy());
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the component being checked is a default or template component by seeing if its id starts with "uif"
     *
     * @param component - The component being checked
     * @return Returns true if the component is not a default or template
     */
    private boolean doValidationOnUIFBean(Component component) {
        if (component.getId() == null) {
            return true;
        }
        if (component.getId().length() < 3) {
            return true;
        }
        String temp = component.getId().substring(0, 3).toLowerCase();
        if (temp.contains("uif")) {
            return false;
        }
        return true;
    }

    /**
     * Validates an expression string for correct Spring Expression language syntax
     *
     * @param expression - The expression being validated
     * @return Returns true if the expression is of correct SpringEL syntax
     */
    public static boolean validateSpringEL(String expression) {
        if (expression != null) {
            if (expression.compareTo("") != 0) {
                if (expression.length() > 3) {
                    String atSymbol = expression.substring(0, 1);
                    String openBracket = expression.substring(1, 2);
                    String closeBracket = expression.substring(expression.length() - 1, expression.length());

                    if (!atSymbol.contains("@") || !openBracket.contains("{") || !closeBracket.contains("}")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if a property of a Component is being set by expressions
     *
     * @param object - The Component being checked
     * @param property - The property being set
     * @return Returns true if the property is contained in the Components property expressions
     */
    public static boolean checkExpressions(Component object, String property) {
        if (object.getPropertyExpressions().containsKey(property)) {
            return true;
        }
        return false;
    }

    /**
     * Compile a new set of reports into the list of reports already generated for the validation
     */
    private void compileReports(ArrayList<ErrorReport> newReports) {
        errorReports.addAll(newReports);
    }

    /**
     * Compiles general information on the validation from the list of generated error reports
     */
    private void compileFinalReport() {
        for (int i = 0; i < errorReports.size(); i++) {
            if (errorReports.get(i).getErrorStatus() == ErrorReport.ERROR) {
                numberOfErrors++;
            } else if (errorReports.get(i).getErrorStatus() == ErrorReport.WARNING) {
                numberOfWarnings++;
            }
        }
    }

    /**
     * Loads the Spring Beans from a list of xml files
     *
     * @param xmlFiles
     * @return The Spring Bean Factory for the provided list of xml files
     */
    public KualiDefaultListableBeanFactory loadBeans(String[] xmlFiles){

        LOG.info("Starting XML File Load");
        KualiDefaultListableBeanFactory beans = new KualiDefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(beans);

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

        try {
            xmlReader.loadBeanDefinitions(core);
        } catch (Exception e) {
            LOG.error("Error loading bean definitions", e);
            throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
        }

        try {
            xmlReader.loadBeanDefinitions(getResources(test));
        } catch (Exception e) {
            LOG.error("Error loading bean definitions", e);
            throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
        }

        UifBeanFactoryPostProcessor factoryPostProcessor = new UifBeanFactoryPostProcessor();
        factoryPostProcessor.postProcessBeanFactory(beans);

        tracerTemp=new TracerToken(xmlFiles,xmlReader.getResourceLoader());

        LOG.info("Completed XML File Load");

        return beans;
    }

    /**
     * Converts the list of file paths into a list of resources
     *
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

    /**
     * Retrieves the number of errors found in the validation
     *
     * @return The number of errors found in the validation
     */
    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    /**
     * Retrieves the number of warnings found in the validation
     *
     * @return The number of warnings found in the validation
     */
    public int getNumberOfWarnings() {
        return numberOfWarnings;
    }

    /**
     * Retrieves an individual error report for errors found during the validation
     *
     * @param index
     * @return The error report at the provided index
     */
    public ErrorReport getErrorReport(int index) {
        return errorReports.get(index);
    }

    /**
     * Retrieves the number of error reports generated during the validation
     *
     * @return The number of ErrorReports
     */
    public int getErrorReportSize() {
        return errorReports.size();
    }
}
