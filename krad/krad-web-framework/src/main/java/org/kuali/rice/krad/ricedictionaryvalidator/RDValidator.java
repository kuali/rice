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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.Configurable;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * A validator for Rice Dictionaries that stores the information found during its validation.
 */
public class RDValidator {

    private ArrayList<ErrorReport> errorReports;
    private int numberOfErrors;
    private int numberOfWarnings;

    // logger
    private static final Log LOG = LogFactory.getLog(RDValidator.class);

    /**
     * Constructor creating an empty validation report
     */
    public RDValidator(){
        numberOfErrors=0;
        numberOfWarnings=0;
        errorReports=new ArrayList<ErrorReport>();
    }

    /**
     * Retrieves the number of errors found in the validation
     * @return The number of errors found in the validation
     */
    public int getNumberOfErrors(){
        return numberOfErrors;
    }

    /**
     * Retrieves the number of warnings found in the validation
     * @return
     */
    public int getNumberOfWarnings(){
        return numberOfWarnings;
    }

    /**
     * Retrieves an individual error report for errors found during the validation
     * @param index
     * @return The error report at the provided index
     */
    public ErrorReport getErrorReport(int index){
        return errorReports.get(index);
    }

    /**
     * Retrieves the number of error reports generated during the validation
     * @return
     */
    public int getErrorReportSize(){
        return errorReports.size();
    }

    /**
     * Validates the beans in a collection of xml files
     * @param xmlFiles - Collection of xml files to load beans from
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, boolean failOnWarning){
        BeanLoader loader=new BeanLoader();
        KualiDefaultListableBeanFactory beans=loader.loadBeans(xmlFiles);

        return runValidations(xmlFiles,beans,failOnWarning);
    }

    /**
     * Validates a collection of beans
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param beans - Collection of preloaded beans
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, KualiDefaultListableBeanFactory beans, boolean failOnWarning){
        return runValidations(xmlFiles,beans,failOnWarning);
    }

    /**
     * Runs the validations on a collection of beans
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param beans - Collection of beans being validated
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    private boolean runValidations(String[] xmlFiles, KualiDefaultListableBeanFactory beans, boolean failOnWarning){

        try{
            Map<String,View> uifBeans = beans.getBeansOfType(View.class);

            LOG.info("Starting Dictionary Validation");

            for ( View views : uifBeans.values() ) {
                if(doValidationOnUIFBean(views)){
                    TracerToken tracer=new TracerToken();
                    XmlBeanParser parserTools=new XmlBeanParser();
                    ArrayList<ErrorReport> reports;

                    LOG.debug("Validating View: "+views.getId());
                    reports=views.completeValidation(tracer.getCopy(),parserTools);
                    compileReports(reports);

                    LOG.debug("Validating Lifecycle Components");
                    runValidationsOnLifecycle(views,tracer.getCopy(),parserTools);


                    LOG.debug("Validating Prototypes");
                    runValidationsOnPrototype(views,tracer.getCopy(),parserTools);

                }
            }
        }catch (Exception e){
            ErrorReport error =new ErrorReport(ErrorReport.ERROR,"Error in Spring Loading","Before Validation");
            ArrayList<ErrorReport> temp =  new ArrayList<ErrorReport>();
            temp.add(error);
            compileReports(temp);
        }


        compileFinalReport();

        LOG.info("Completed Dictionary Validation");

        if(numberOfErrors>0)return false;
        if(failOnWarning)
            if(numberOfWarnings>0)return false;


        return true;
    }

    /**
     * Compile a new set of reports into the list of reports already generated for the validation
     */
    private void compileReports(ArrayList<ErrorReport> newReports){
            errorReports.addAll(newReports);
    }

    /**
     * Compiles general information on the validation from the list of generated error reports
     */
    private void compileFinalReport(){
        for(int i=0;i<errorReports.size();i++){
            if(errorReports.get(i).getErrorStatus()== ErrorReport.ERROR)numberOfErrors++;
            else if(errorReports.get(i).getErrorStatus()== ErrorReport.WARNING)numberOfWarnings++;
        }
    }

    private void runValidationsOnLifecycle(Component component, TracerToken tracer, XmlBeanParser parser){
        if(component.getComponentsForLifecycle()!=null){
            if(doValidationOnUIFBean(component)){
                tracer.addBean(component);
                for(int j=0;j<component.getComponentsForLifecycle().size();j++){;
                    Component temp=component.getComponentsForLifecycle().get(j);
                    if(temp!=null){
                        compileReports(temp.completeValidation(tracer.getCopy(),parser));
                        runValidationsOnLifecycle(temp,tracer.getCopy(),parser);
                    }
                }
            }
        }
    }
    private void runValidationsOnPrototype(Component component, TracerToken tracer, XmlBeanParser parser){
        if(component.getComponentPrototypes()!=null){
            if(doValidationOnUIFBean(component)){
                tracer.addBean(component);
                for(int j=0;j<component.getComponentPrototypes().size();j++){;
                    Component temp=component.getComponentPrototypes().get(j);
                    if(temp!=null){
                        compileReports(temp.completeValidation(tracer.getCopy(),parser));
                        runValidationsOnPrototype(temp,tracer.getCopy(),parser);
                    }
                }
            }
        }
    }

    private boolean doValidationOnUIFBean(Component component){
        if(component.getId()==null)return true;
        if(component.getId().length()<3)return true;
        String temp=component.getId().substring(0,3).toLowerCase();
        if(temp.contains("uif")){
            return false;
        }
        return true;
    }
    public static boolean validateSpringEL(String expression){
        if(expression!=null){
            if(expression.compareTo("")!=0){
                if(expression.length()>3){
                    String atSymbol = expression.substring(0,1);
                    String openBracket = expression.substring(1,2);
                    String closeBracket = expression.substring(expression.length()-1,expression.length());

                    if(!atSymbol.contains("@") || !openBracket.contains("{") || !closeBracket.contains("}")){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public static boolean checkExpressions(Configurable object){
        if(object.getExpressionGraph()!=null)
            if(object.getExpressionGraph().size()>0)return true;
        if(object.getPropertyExpressions()!=null)
            if(object.getPropertyExpressions().size()>0)return true;
        if(object.getRefreshExpressionGraph()!=null)
            if(object.getRefreshExpressionGraph().size()>0)return true;
        return false;
    }
}
