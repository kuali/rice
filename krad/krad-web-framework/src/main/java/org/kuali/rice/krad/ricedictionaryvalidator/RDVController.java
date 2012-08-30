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
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A combination view controller for the Rice Dictionary Validator that handles both the setup/execution of the
 * validation and the output of the results.
 */
public class RDVController {
    protected static final String endl=System.getProperty("line.separator");

    protected boolean displayWarnings;
    protected boolean displayErrors;
    protected boolean displayXmlPages;
    protected boolean displayErrorMessages;
    protected boolean displayWarningMessages;

    // logger
    private static final Log LOG = LogFactory.getLog(RDVController.class);

    /**
     * Constructor creating a new Rice Dictionary Validator with limited information during output
     * @param displayErrors - True if the Validator should show the number of error during output
     * @param displayWarnings - True if the Validator should show the number of warnings during output
     * @param displayErrorMessages - True if the Validator should show the messages for the error reports
     * @param displayWarningMessages - True if the Validator should show messages involving warnings
     * @param displayXmlPages - True if the Validator should show the list of xml pages for the error reports
     */
    public RDVController(boolean  displayErrors,boolean displayWarnings,boolean displayErrorMessages,boolean
            displayWarningMessages, boolean displayXmlPages){
        LOG.debug("Creating new Rice Dictionary Validator with limited output");
        this.displayErrors=displayErrors;
        this.displayWarnings=displayWarnings;
        this.displayErrorMessages=displayErrorMessages;
        this.displayWarningMessages=displayWarningMessages;
        this.displayXmlPages=displayXmlPages;
    }

    /**
     * Constructor creating a new Rice Dictionary Validator
     */
    public RDVController(){
        LOG.debug("Creating new Rice Dictionary Validator");
        displayErrors=true;
        displayWarnings=true;
        displayErrorMessages=true;
        displayWarningMessages=true;
        displayXmlPages=true;
    }

    /**
     * Sets the displayWarnings
     * @param display
     */
    public void setDisplayWarnings(boolean display){
        displayWarnings=display;
    }

    /**
     * Sets the displayErrors
     * @param display
     */
    public void setDisplayErrors(boolean display){
        displayErrors=display;
    }

    /**
     * Sets the displayXmlPages
     * @param display
     */
    public void setDisplayXmlPages(boolean display){
        displayXmlPages=display;
    }

    /**
     * Sets the displayErrorMessages
     * @param display
     */
    public void setDisplayErrorMessages(boolean display){
        displayErrorMessages=display;
    }

    /**
     * Sets the displayWarningMessages
     * @param display
     */
    public void setDisplayWarningMessages(boolean display){
        displayWarningMessages=display;
    }

    /**
     * Gets the displayWarnings
     * @return displayWarnings
     */
    public boolean isDisplayWarnings(){
        return displayWarnings;
    }

    /**
     * Gets the displayErrors
     * @return displayErros
     */
    public boolean isDisplayErrors(){
        return displayErrors;
    }

    /**
     * Gets the displayXmlPages
     * @return displayXmlPages
     */
    public boolean isDisplayXmlPages(){
        return displayXmlPages;
    }

    /**
     * Gets the displayErrorMessages
     * @return displayErrorMessages
     */
    public boolean isDisplayErrorMessages(){
        return displayErrorMessages;
    }

    /**
     * Gets the displayWarningMessages
     * @return displayWarningMessages
     */
    public boolean isDisplayWarningMessages(){
        return displayWarningMessages;
    }

    /**
     * Validates a collection of Spring Beans with no output
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param beans - Collection of preloaded beans
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, KualiDefaultListableBeanFactory beans, boolean failOnWarning){
        LOG.debug("Validating without output");
        RDValidator validator = new RDValidator();

        boolean passed=validator.validate(xmlFiles,beans,failOnWarning);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with output going to a file
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param beans - Collection of preloaded beans
     * @param outputFile - The file location to save the output to
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, KualiDefaultListableBeanFactory beans, String outputFile, boolean
            failOnWarning){
        RDValidator validator = new RDValidator();
        LOG.debug("Validating with file output to "+outputFile);

        boolean passed=validator.validate(xmlFiles,beans,failOnWarning);

        writeToFile(outputFile,validator,passed);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with output going to a print stream
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param beans - Collection of preloaded beans
     * @param stream - The PrintStream the output is sent to
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, KualiDefaultListableBeanFactory beans, PrintStream stream, boolean
            failOnWarning){
        RDValidator validator = new RDValidator();
        LOG.debug("Validating with Print Stream output");

        boolean passed=validator.validate(xmlFiles,beans, failOnWarning);

        writeToStream(stream,validator,passed);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with output going to Log4j
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param beans - Collection of preloaded beans
     * @param log - The Log4j logger the output is sent to
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, KualiDefaultListableBeanFactory beans, Log log, boolean failOnWarning){
        RDValidator validator = new RDValidator();
        LOG.debug("Validating with Log4j output");

        boolean passed=validator.validate(xmlFiles,beans, failOnWarning);

        writeToLog(log,validator,passed);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with no output
     * @param xmlFiles - The collection of xml files used to load the beans
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, boolean failOnWarning){
        LOG.debug("Validating without output");
        RDValidator validator = new RDValidator();

        boolean passed=validator.validate(xmlFiles,failOnWarning);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with output going to a file
     * @param xmlFiles - The collection of xml files used to load the beans
     * @param outputFile - The file location to save the output to
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, String outputFile, boolean failOnWarning){
        RDValidator validator = new RDValidator();
        LOG.debug("Validating with file output to "+outputFile);

        boolean passed=validator.validate(xmlFiles,failOnWarning);

        writeToFile(outputFile,validator,passed);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with output going to a print stream
     * @param xmlFiles - The collection of xml files used to load the beans
     * @param stream - The PrintStream the output is sent to
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, PrintStream stream, boolean failOnWarning){
        RDValidator validator = new RDValidator();
        LOG.debug("Validating with Print Stream output");

        boolean passed=validator.validate(xmlFiles, failOnWarning);

        writeToStream(stream,validator,passed);

        return passed;
    }

    /**
     * Validates a collection of Spring Beans with output going to Log4j
     * @param xmlFiles - The collection of xml files used to load the provided beans
     * @param log - The Log4j logger the output is sent to
     * @param failOnWarning - Whether detecting a warning should cause the validation to fail
     * @return Returns true if the beans past validation
     */
    public boolean validate(String[] xmlFiles, Log log, boolean failOnWarning){
        RDValidator validator = new RDValidator();
        LOG.debug("Validating with Log4j output");

        boolean passed=validator.validate(xmlFiles, failOnWarning);

       writeToLog(log,validator,passed);

        return passed;
    }

    /**
     * Writes the results of the validation to an output file
     * @param path - The path to the file to write results to
     * @param validator - The filled validator
     * @param passed - Whether the validation passed or not
     */
    protected void writeToFile(String path, RDValidator validator, boolean passed){
        try{
            BufferedWriter fout = new BufferedWriter(new FileWriter(path));

            fout.write("Validation Results" + endl);
            fout.write("Passed: " + passed + endl);
            if(displayErrors) fout.write("Number of Errors: " + validator.getNumberOfErrors() + endl);
            if(displayWarnings) fout.write("Number of Warnings: " + validator.getNumberOfWarnings() + endl);

            if(displayErrorMessages){
                for(int i=0;i<validator.getErrorReportSize();i++){
                    fout.write(endl);
                    if(displayWarningMessages) fout.write(validator.getErrorReport(i).errorMessage());
                    else if(validator.getErrorReport(i).getErrorStatus()== ErrorReport.ERROR) fout.write(validator.getErrorReport(i).errorMessage());

                    if(displayXmlPages) fout.write(validator.getErrorReport(i).errorPageList());
                }
            }

            fout.close();
        }catch (IOException e){
            LOG.warn("Exception when writing file", e);
        }
    }

    /**
     * Writes the results of the validation to an output file
     * @param stream - The PrintStream the output is sent to
     * @param validator - The filled validator
     * @param passed - Whether the validation passed or not
     */
    protected void writeToStream(PrintStream stream, RDValidator validator, boolean passed){
        stream.println("Validation Results");
        stream.println("Passed: "+passed);
        if(displayErrors) stream.println("Number of Errors: "+validator.getNumberOfErrors());
        if(displayWarnings) stream.println("Number of Warnings: "+validator.getNumberOfWarnings());

        if(displayErrorMessages){
            for(int i=0;i<validator.getErrorReportSize();i++){
                stream.println();
                if(displayWarningMessages) stream.println(validator.getErrorReport(i).errorMessage());
                else if(validator.getErrorReport(i).getErrorStatus()== ErrorReport.ERROR)stream.println(validator.getErrorReport(i).errorMessage());

                if(displayXmlPages) stream.println(validator.getErrorReport(i).errorPageList());
            }
        }
    }

    /**
     * Writes the results of the validation to an output file
     * @param log - The Log4j logger the output is sent to
     * @param validator - The filled validator
     * @param passed - Whether the validation passed or not
     */
    protected void writeToLog(Log log, RDValidator validator, boolean passed){
        log.info("Passed: "+passed);
        if(displayErrors) log.info("Number of Errors: "+validator.getNumberOfErrors());
        if(displayWarnings) log.info("Number of Warnings: "+validator.getNumberOfWarnings());

        if(displayErrorMessages){
            for(int i=0;i<validator.getErrorReportSize();i++){
                if(validator.getErrorReport(i).getErrorStatus()== ErrorReport.ERROR){
                    if(displayXmlPages) log.error(validator.getErrorReport(i).errorMessage()+validator.getErrorReport(i).errorPageList());
                    else log.error(validator.getErrorReport(i).errorMessage());

                } else{
                    if(displayWarningMessages) {
                        if(displayXmlPages) log.warn(validator.getErrorReport(i).errorMessage()+validator.getErrorReport(i).errorPageList());
                        else log.warn(validator.getErrorReport(i).errorMessage());
                    }
                }


            }
        }
    }

}
