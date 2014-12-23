/*
 * Copyright 2006-2014 The Kuali Foundation
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
package edu.sampleu.demo.kitchensink;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.krad.form.UITestForm;
import org.kuali.rice.core.api.util.RiceUtilities;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/uiwebzip")
public class UIWebZipTestController extends UifControllerBase {

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected UITestForm createInitialForm(HttpServletRequest request) {
        return new UITestForm();
    }

    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        UITestForm uiTestForm = (UITestForm) form;
        return super.start(uiTestForm, result, request, response);
    }

    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=downloadMultipleFiles")
    public void downloadMultipleFiles(HttpServletResponse response, HttpServletRequest request) {

        ByteArrayOutputStream textOut = new ByteArrayOutputStream();
        ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
        ByteArrayOutputStream wordOut = new ByteArrayOutputStream();

        try {
            InputStream textInput = RiceUtilities.getResourceAsStream("classpath:edu/sampleu/sampledocs/README.txt");
            InputStream pdfInput = RiceUtilities.getResourceAsStream( "classpath:edu/sampleu/sampledocs/TestDocumentPDF.pdf");
            InputStream wordInput = RiceUtilities.getResourceAsStream( "classpath:edu/sampleu/sampledocs/TestDocumentWord.doc");

            int i;
            while ((i = textInput.read()) > 0) {
                textOut.write(i);
            }

            while ((i = pdfInput.read()) > 0) {
                pdfOut.write(i);
            }

            while ((i = wordInput.read()) > 0) {
                wordOut.write(i);
            }

            Map<String, ByteArrayOutputStream> outputStreamMap = new HashMap<String, ByteArrayOutputStream>();
            outputStreamMap.put("README.txt", textOut);
            outputStreamMap.put("TestDocumentPDF.pdf", pdfOut);
            outputStreamMap.put("TestDocumentWord.doc", wordOut);

            WebUtils.saveMimeZipOutputStreamAsFile(response, "application/zip", outputStreamMap, "testdocs.zip");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
