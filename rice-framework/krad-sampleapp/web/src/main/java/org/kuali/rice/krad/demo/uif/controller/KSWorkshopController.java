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
package org.kuali.rice.krad.demo.uif.controller;

import org.kuali.rice.krad.demo.uif.form.KSRegistrationIssue;
import org.kuali.rice.krad.demo.uif.form.KSWorkshopActivity;
import org.kuali.rice.krad.demo.uif.form.KSWorkshopCourse;
import org.kuali.rice.krad.demo.uif.form.KSWorkshopForm;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Brian on 6/17/14.
 */
@Controller
@RequestMapping(value = "/ksworkshop")
public class KSWorkshopController extends UifControllerBase {

    @Override
    protected KSWorkshopForm createInitialForm() {
        return new KSWorkshopForm();
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getStudentInfo")
    public ModelAndView getStudentInfo(@ModelAttribute("KualiForm") KSWorkshopForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        // Pretend service call to get student and populate
        form.setStudentName("Allison Glass");
        form.setStanding("Junior");
        form.setProgram("Undergrad");
        form.setDepartment("Arts and Humanites");
        form.setMajor("Psychology");
        form.setCredits("68");

        return getModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=register")
    public ModelAndView register(@ModelAttribute("KualiForm") KSWorkshopForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        DialogResponse dialogResponse = form.getDialogResponse("KS-AdminRegistration-RegisterDialogResponse");
        if (dialogResponse == null) {
            for (KSWorkshopCourse course : form.getPendingCourses()) {
                course.setCourseName("Some course name here");
                course.setCredits(3);
                course.setRegDate(new Date());
                course.setRegOptions("reg");
                List<KSWorkshopActivity> activities = new ArrayList<KSWorkshopActivity>();
                activities.add(new KSWorkshopActivity("Lec", "MWF 01:00pm - 02:30pm", "Steve Capriani", "PTX 2391"));
                activities.add(new KSWorkshopActivity("Lab", "MWF 02:30pm - 03:30pm", "Steve Capriani", "PTX 2391"));
                course.setActivities(activities);
            }

            return showDialog("KS-AdminRegistration-RegisterDialogResponse", true, form);
        }

        // continue with registration
        form.getRegisteredCourses().addAll(form.getPendingCourses());
        form.setPendingCourses(new ArrayList<KSWorkshopCourse>());
        form.getPendingCourses().add(new KSWorkshopCourse());

        return getModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=registerConfirm")
    public ModelAndView registerConfirm(@ModelAttribute("KualiForm") KSWorkshopForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        // You would fire off the registration request here, we don't do anything for the prototype
        synchronized (form) {
            form.getCoursesInProcess().addAll(form.getPendingCourses());
            form.setPendingCourses(new ArrayList<KSWorkshopCourse>());
        }
        return getModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=regUpdateQuery")
    @ResponseBody
    public Map performFieldQuery(KSWorkshopForm form) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> updateIds = new ArrayList<String>();

        synchronized (form) {
            Random generator = new Random();

            int i = generator.nextInt(5);
            if (i == 0 && !form.getCoursesInProcess().isEmpty()) {
                // faking a registration complete
                form.getRegisteredCourses().add(form.getCoursesInProcess().get(0));
                form.getCoursesInProcess().remove(0);

                updateIds.add("KS-AdminRegistration-Registered");
            }

            i = generator.nextInt(5);
            if (i == 0 && !form.getCoursesInProcess().isEmpty()) {
                // faking a waitlist complete
                form.getWaitlistedCourses().add(form.getCoursesInProcess().get(0));
                form.getCoursesInProcess().remove(0);

                updateIds.add("KS-AdminRegistration-Waitlist");
            }

            i = generator.nextInt(5);
            if (i == 0 && !form.getCoursesInProcess().isEmpty()) {
                // faking an issue found
                KSRegistrationIssue regIssue = new KSRegistrationIssue();
                regIssue.setCourse(form.getCoursesInProcess().get(0));
                regIssue.setMessage("Something bad happened");
                form.getRegistrationIssues().add(regIssue);
                form.getCoursesInProcess().remove(0);

                updateIds.add("KS-AdminRegistration-Issues");
            }
        }

        if (form.getCoursesInProcess().isEmpty()) {
            result.put("stop", true);
        }

        result.put("updateIds", updateIds);

        return result;
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=allowRegister")
    public ModelAndView allowRegister(@ModelAttribute("KualiForm") KSWorkshopForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        return getModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=denyRegister")
    public ModelAndView denyRegister(@ModelAttribute("KualiForm") KSWorkshopForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        return getModelAndView(form);
    }

}
