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
package org.kuali.rice.krad.labs.coursesearch;

import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.net.*;

import javax.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the controller class for course search performance labs.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/coursesearchperformance")
public class CourseSearchController extends UifControllerBase {

    public static final String KUALI_ATP_2012_FALL = "kuali.atp.2012Fall";

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected CourseSearchForm createInitialForm() {
        return new CourseSearchForm();
    }

    /**
     *
     * @param form
     * @return
     */
    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(UifFormBase form) {
        CourseSearchForm perfForm = (CourseSearchForm) form;

        return getModelAndView(perfForm);
    }

    /**
     *  This method takes the user input (int) and generates a collection with that many rows. That collection is saved
     *  to the form object so it can be displayed on the page.
     * @param form    CourseSearchForm
     * @param result
     * @param request
     * @param response
     * @return    ModelAndView
     */
    @RequestMapping(params = "methodToCall=buildcollection")
    public ModelAndView buildCollection(@ModelAttribute("KualiForm") CourseSearchForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        String courseCode = form.getInputOne(); // get input from page
        String termId = KUALI_ATP_2012_FALL;
        String termCode = "201208";
        String temp = "[\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9e89ed85-66ba-4a9c-9765-c36e78929051\",\n"
                + "      \"courseOfferingCode\":\"CHEM105\",\n"
                + "      \"courseOfferingDesc\":\"Fundamental of Organic and Biochemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"25a7e006-e71a-4d5d-806c-04675f365222\",\n"
                + "      \"courseOfferingCode\":\"CHEM131\",\n"
                + "      \"courseOfferingDesc\":\"Chemistry I - Fundamentals of General Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ea618cb3-8583-4833-bbe3-43c7961f70fb\",\n"
                + "      \"courseOfferingCode\":\"CHEM131S\",\n"
                + "      \"courseOfferingDesc\":\"Chemistry I - Fundamentals of General Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ee786c4b-57c9-4237-9519-4bdb961ca5d4\",\n"
                + "      \"courseOfferingCode\":\"CHEM132\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry I Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"56802cf2-5577-4163-9fa7-a356396e8d45\",\n"
                + "      \"courseOfferingCode\":\"CHEM132C\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry I Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"5fc5ff07-eb70-4c0a-b549-124eb879c8cb\",\n"
                + "      \"courseOfferingCode\":\"CHEM132S\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry I Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ced6bd11-bcd2-445f-be16-3a46d8231323\",\n"
                + "      \"courseOfferingCode\":\"CHEM135\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry for Engineers\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"27f703ed-d24c-4580-a401-105b3b1e5825\",\n"
                + "      \"courseOfferingCode\":\"CHEM135U\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry for Engineers\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"e960defa-3c0c-4bf6-bb2f-0ec13d80096d\",\n"
                + "      \"courseOfferingCode\":\"CHEM136\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry Laboratory for Engineers\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"2403ccea-0951-4730-b481-445631cc14a9\",\n"
                + "      \"courseOfferingCode\":\"CHEM146\",\n"
                + "      \"courseOfferingDesc\":\"Principles of General Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"bc3cafeb-5c1a-452c-9ab3-dabb992d1178\",\n"
                + "      \"courseOfferingCode\":\"CHEM147\",\n"
                + "      \"courseOfferingDesc\":\"Principles of Chemistry Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"2d847317-1c21-4c8c-990c-ab56f0897c75\",\n"
                + "      \"courseOfferingCode\":\"CHEM231\",\n"
                + "      \"courseOfferingDesc\":\"Organic Chemistry I\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9c5f138c-951b-4d3a-8cd5-369a23f89caa\",\n"
                + "      \"courseOfferingCode\":\"CHEM232\",\n"
                + "      \"courseOfferingDesc\":\"Organic Chemistry Laboratory I\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ebe93736-4f75-45fc-a313-e375575cdf4a\",\n"
                + "      \"courseOfferingCode\":\"CHEM241\",\n"
                + "      \"courseOfferingDesc\":\"Organic Chemistry II\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9133914d-53a1-49d6-ab4d-cf83a3848262\",\n"
                + "      \"courseOfferingCode\":\"CHEM242\",\n"
                + "      \"courseOfferingDesc\":\"Organic Chemistry Laboratory II\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"759bba9a-0ccf-4ddd-bdb9-8abe46f2b784\",\n"
                + "      \"courseOfferingCode\":\"CHEM247\",\n"
                + "      \"courseOfferingDesc\":\"Principles of Organic Chemistry II\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"4.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"d4208ae9-7a2a-4ad7-b570-7c060aa74d90\",\n"
                + "      \"courseOfferingCode\":\"CHEM271\",\n"
                + "      \"courseOfferingDesc\":\"General Chemistry and Energetics\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"2.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"fdfd8711-e66e-4618-8321-f4bc74062792\",\n"
                + "      \"courseOfferingCode\":\"CHEM272\",\n"
                + "      \"courseOfferingDesc\":\"General Bioanalytical Chemistry Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"2.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"8e7fa3b9-9560-4f2e-88c5-59c29dc9f126\",\n"
                + "      \"courseOfferingCode\":\"CHEM277\",\n"
                + "      \"courseOfferingDesc\":\"Fundamentals of Analytical and Bioanalytical Chemistry Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"3b84eca8-8664-4de1-8703-b1aebeddc1fb\",\n"
                + "      \"courseOfferingCode\":\"CHEM398\",\n"
                + "      \"courseOfferingDesc\":\"Special Projects\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"2.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"075b54a0-133a-4e58-9b89-99cb1498e0f0\",\n"
                + "      \"courseOfferingCode\":\"CHEM399A\",\n"
                + "      \"courseOfferingDesc\":\"Introduction to Chemical Research\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0,1.5,2.0,2.5,3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9a3126b4-bcad-403a-87ba-ddc438141039\",\n"
                + "      \"courseOfferingCode\":\"CHEM399B\",\n"
                + "      \"courseOfferingDesc\":\"Introduction to Chemical Research\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0,1.5,2.0,2.5,3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"26a79829-b42c-441b-b9cf-0d8f7a35629f\",\n"
                + "      \"courseOfferingCode\":\"CHEM399C\",\n"
                + "      \"courseOfferingDesc\":\"Introduction to Chemical Research\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0,1.5,2.0,2.5,3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"84d4cc82-a24f-4775-8374-a5a4ebcab530\",\n"
                + "      \"courseOfferingCode\":\"CHEM399X\",\n"
                + "      \"courseOfferingDesc\":\"Introduction to Chemical Research; Chemistry Instruction\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0,1.5,2.0,2.5,3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"0c5fcfef-11dd-4e89-b9ab-a28b4b2d7fb0\",\n"
                + "      \"courseOfferingCode\":\"CHEM403\",\n"
                + "      \"courseOfferingDesc\":\"Radiochemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"195107f1-0afb-4af3-a78a-d287a57f8a3c\",\n"
                + "      \"courseOfferingCode\":\"CHEM425\",\n"
                + "      \"courseOfferingDesc\":\"Instrumental Methods of Analysis\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"4.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"e67d87d6-8d89-401a-86ac-08ca8bd5195b\",\n"
                + "      \"courseOfferingCode\":\"CHEM441\",\n"
                + "      \"courseOfferingDesc\":\"Advanced Organic Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"6f2cb68a-b09d-4b2b-8459-7fe17de11055\",\n"
                + "      \"courseOfferingCode\":\"CHEM460\",\n"
                + "      \"courseOfferingDesc\":\"Structure Determination Using Spectroscopic Methods\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"abf9196f-a0d2-4558-b47b-308bf0257c35\",\n"
                + "      \"courseOfferingCode\":\"CHEM474\",\n"
                + "      \"courseOfferingDesc\":\"Environmental Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"87521128-54b5-4c97-a756-b1ecd7dcc5f5\",\n"
                + "      \"courseOfferingCode\":\"CHEM481\",\n"
                + "      \"courseOfferingDesc\":\"Physical Chemistry I\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"99f82ba5-ad01-4489-b446-99ea8f62d292\",\n"
                + "      \"courseOfferingCode\":\"CHEM482\",\n"
                + "      \"courseOfferingDesc\":\"Physical Chemistry II\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"7a84cb1d-51ad-44a5-a315-69451dea6154\",\n"
                + "      \"courseOfferingCode\":\"CHEM483\",\n"
                + "      \"courseOfferingDesc\":\"Physical Chemistry Laboratory I\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"2.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9118cf6c-a573-4443-9208-89079ff89d8b\",\n"
                + "      \"courseOfferingCode\":\"CHEM484\",\n"
                + "      \"courseOfferingDesc\":\"Physical Chemistry Laboratory II\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"2.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"d5604c9f-664c-4dc1-94b1-695a821b7c8f\",\n"
                + "      \"courseOfferingCode\":\"CHEM491\",\n"
                + "      \"courseOfferingDesc\":\"Advanced Organic Chemistry Laboratory\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"4.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Pass/Fail Grading\",\n"
                + "      \"studentSelectablePassFail\":true,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9218e1e8-8279-4ea2-9975-9decdf337eca\",\n"
                + "      \"courseOfferingCode\":\"CHEM601\",\n"
                + "      \"courseOfferingDesc\":\"Structure and Bonding of Molecules and Materials\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"eb942c03-0e7e-4bd7-a774-d26cb1c64f25\",\n"
                + "      \"courseOfferingCode\":\"CHEM608K\",\n"
                + "      \"courseOfferingDesc\":\"Selected Topics in Inorganic Chemistry; Chemistry Teaching and Learning in Higher Education\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0,1.5,2.0,2.5,3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"19bb06aa-a076-4937-8fc4-acccf85b9828\",\n"
                + "      \"courseOfferingCode\":\"CHEM611\",\n"
                + "      \"courseOfferingDesc\":\"Professional Skills for New Graduate Students\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"9dd9724e-efd6-4d31-9624-b46450b57027\",\n"
                + "      \"courseOfferingCode\":\"CHEM625\",\n"
                + "      \"courseOfferingDesc\":\"Separation Methods in Quantitative Analysis\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"8938913c-cbd0-4405-97ad-f7e179114b83\",\n"
                + "      \"courseOfferingCode\":\"CHEM626\",\n"
                + "      \"courseOfferingDesc\":\"Metrology for Chemistry and Biochemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"39c50ead-18ef-4909-89dc-3341cf8f6fd5\",\n"
                + "      \"courseOfferingCode\":\"CHEM640\",\n"
                + "      \"courseOfferingDesc\":\"Problems in Organic Reaction Mechanisms\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"bc7d6a5e-b77c-400b-a79b-1e02164b2de8\",\n"
                + "      \"courseOfferingCode\":\"CHEM641\",\n"
                + "      \"courseOfferingDesc\":\"Organic Reaction Mechanisms\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"22f75db7-d9ac-4da7-ba0c-6def7b544718\",\n"
                + "      \"courseOfferingCode\":\"CHEM682\",\n"
                + "      \"courseOfferingDesc\":\"Chemical Kinetics and Dynamics\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"4a6f4368-2a17-4bef-b62c-872fa66f8211\",\n"
                + "      \"courseOfferingCode\":\"CHEM684\",\n"
                + "      \"courseOfferingDesc\":\"Chemical Thermodynamics\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"8ba3008b-cf09-4df3-99fe-403f44be4eed\",\n"
                + "      \"courseOfferingCode\":\"CHEM689\",\n"
                + "      \"courseOfferingDesc\":\"Special Topics in Physical Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"73e0e856-d344-4946-a759-65209dfd6e3b\",\n"
                + "      \"courseOfferingCode\":\"CHEM690\",\n"
                + "      \"courseOfferingDesc\":\"Quantum Chemistry I\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"3.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ce30b18d-5003-430c-9b8d-07d320785f4d\",\n"
                + "      \"courseOfferingCode\":\"CHEM699\",\n"
                + "      \"courseOfferingDesc\":\"Special Problems in Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1 - 6\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"af7586c2-92aa-4da7-81d0-82e757efae99\",\n"
                + "      \"courseOfferingCode\":\"CHEM799\",\n"
                + "      \"courseOfferingDesc\":\"Master's Thesis Research\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1 - 6\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"47b27aeb-3506-4cef-9cab-ce82eb16b98a\",\n"
                + "      \"courseOfferingCode\":\"CHEM889A\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Analytical, Nuclear and Environmental Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"81590cdf-e8bb-42b2-8192-9782ad9ca0e7\",\n"
                + "      \"courseOfferingCode\":\"CHEM889C\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Inorganic\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"2972d4f5-2670-4d69-8bc5-c3d0afb28a4a\",\n"
                + "      \"courseOfferingCode\":\"CHEM889D\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Organic\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"672d59b9-c2ae-4462-82f3-b0ebfc181c59\",\n"
                + "      \"courseOfferingCode\":\"CHEM889E\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Physical Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ee6838b4-40d8-419e-8838-a5286e269dc9\",\n"
                + "      \"courseOfferingCode\":\"CHEM889F\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Chemical Physics\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"fe0b4cbd-0796-4810-88ec-0dc22f570cc6\",\n"
                + "      \"courseOfferingCode\":\"CHEM889G\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Physical Organic\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Allow students to audit\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"ac1a7153-1ceb-483e-bf3c-417494ac4522\",\n"
                + "      \"courseOfferingCode\":\"CHEM889M\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Materials Chemistry\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"019e01b1-695a-4513-92b6-4303af879b61\",\n"
                + "      \"courseOfferingCode\":\"CHEM889P\",\n"
                + "      \"courseOfferingDesc\":\"Seminar; Structure and Reactivity of Biopolymers\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1.0\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":true,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"d0b9cf2f-879d-4bf4-8afd-05531b842219\",\n"
                + "      \"courseOfferingCode\":\"CHEM898\",\n"
                + "      \"courseOfferingDesc\":\"Pre-Candidacy Research\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1 - 8\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Administrative Grade of Satisfactory\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   },\n"
                + "   {\n"
                + "      \"courseOfferingId\":\"a3c5e362-6343-4177-ae39-c2f55b32f56b\",\n"
                + "      \"courseOfferingCode\":\"CHEM899\",\n"
                + "      \"courseOfferingDesc\":\"Doctoral Dissertation Research\",\n"
                + "      \"courseOfferingCreditOptionDisplay\":\"1 - 8\",\n"
                + "      \"courseOfferingGradingOptionDisplay\":\"Letter\",\n"
                + "      \"studentSelectablePassFail\":false,\n"
                + "      \"auditCourse\":false,\n"
                + "      \"honorsCourse\":false\n"
                + "   }\n"
                + "]";

        JsonReader jsonReader = Json.createReader(new StringReader(temp));
        JsonArray jsonArray = jsonReader.readArray();
        List<CourseSearchResult> collectionList = new ArrayList<CourseSearchResult>();

        populateCourseSearchResults(jsonArray, collectionList);
        form.setPerfCollection(collectionList); // add collection to form.

        return getModelAndView(form);
    }

    /**
     *
     * @param jsonArray
     * @param collectionList
     * @throws JsonException
     */
    public void populateCourseSearchResults(JsonArray jsonArray, List<CourseSearchResult> collectionList)throws JsonException {
        int j = jsonArray.size();
        int i;
        for(i=0; i<j; i++) {
            CourseSearchResult courseSearchResult = new CourseSearchResult();
            JsonObject jsonObject = (JsonObject)jsonArray.get(i);
            courseSearchResult.setCourseOfferingId(jsonObject.getString("courseOfferingId"));
            courseSearchResult.setCourseOfferingCode(jsonObject.getString("courseOfferingCode"));
            courseSearchResult.setHonorsCourse(jsonObject.getBoolean("honorsCourse"));
            courseSearchResult.setAuditCourse(jsonObject.getBoolean("auditCourse"));
            courseSearchResult.setStudentSelectablePassFail(jsonObject.getBoolean("studentSelectablePassFail"));
            courseSearchResult.setCourseOfferingDesc(jsonObject.getString("courseOfferingDesc"));
            courseSearchResult.setCourseOfferingCreditOptionDisplay(jsonObject.getString(
                    "courseOfferingCreditOptionDisplay"));
            courseSearchResult.setCourseOfferingGradingOptionDisplay(jsonObject.getString(
                    "courseOfferingGradingOptionDisplay"));
            collectionList.add(courseSearchResult);
        }


    }

    /**
     *
     * @param urlToRead
     * @return
     */
    public String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
