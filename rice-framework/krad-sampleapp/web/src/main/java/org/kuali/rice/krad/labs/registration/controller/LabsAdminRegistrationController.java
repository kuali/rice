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
package org.kuali.rice.krad.labs.registration.controller;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.io.SerializationUtils;
import org.kuali.rice.krad.labs.registration.form.LabsAdminRegistrationActivity;
import org.kuali.rice.krad.labs.registration.form.LabsAdminRegistrationCourse;
import org.kuali.rice.krad.labs.registration.form.LabsAdminRegistrationForm;
import org.kuali.rice.krad.labs.registration.form.LabsAdminRegistrationIssue;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.kuali.rice.krad.web.form.UifFormBase;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class for KS Admin Registration Lab prototype
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/ksworkshop")
public class LabsAdminRegistrationController extends UifControllerBase {
    private static String REG_COLL_ID = "KS-AdminRegistration-Registered";
    private static String WAITLIST_COLL_ID = "KS-AdminRegistration-Waitlist";

    @Override
    protected LabsAdminRegistrationForm createInitialForm() {
        return new LabsAdminRegistrationForm();
    }

    /**
     * @see org.kuali.rice.krad.web.service.RefreshControllerService#refresh(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @Override
    @RequestMapping(params = "methodToCall=refresh")
    public ModelAndView refresh(UifFormBase form) {
        cancelEdits((LabsAdminRegistrationForm) form, form.getUpdateComponentId());
        return getRefreshControllerService().refresh(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getStudentInfo")
    public ModelAndView getStudentInfo(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
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
    public ModelAndView register(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        DialogResponse dialogResponse = form.getDialogResponse("KS-AdminRegistration-RegisterDialogResponse");
        if (dialogResponse == null) {
            for (LabsAdminRegistrationCourse course : form.getPendingCourses()) {
                course.setCourseName("Some course name here");
                course.setCredits(3);
                course.setRegDate(new Date());
                course.setRegOptions("reg");
                course.setEffectiveDate(new Date());
                List<LabsAdminRegistrationActivity> activities = new ArrayList<LabsAdminRegistrationActivity>();
                activities.add(new LabsAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Steve Capriani", "PTX 2391"));
                activities.add(new LabsAdminRegistrationActivity("Lab", "MWF 02:30pm - 03:30pm", "Steve Capriani", "PTX 2391"));
                course.setActivities(activities);
            }

            return showDialog("KS-AdminRegistration-RegisterDialogResponse", true, form);
        }

        // continue with registration
        form.getCoursesInProcess().addAll(form.getPendingCourses());
        form.setPendingCourses(new ArrayList<LabsAdminRegistrationCourse>());
        form.getPendingCourses().add(new LabsAdminRegistrationCourse());

        return getModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=dropCourse")
    public ModelAndView dropCourse(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String selectedCollectionPath = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection path was not set for collection action");
        }

        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);

        String selectedLine = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
        int selectedLineIndex = -1;
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        }

        DialogResponse dialogResponse = form.getDialogResponse("KS-AdminRegistration-DropRegisteredDialog");

        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(form, selectedCollectionPath);
        Object item = ((List) collection).get(selectedLineIndex);

        if (dialogResponse == null) {
            // Create temp object with the info we need about the course
            LabsAdminRegistrationCourse pendingDropCourse = new LabsAdminRegistrationCourse();
            pendingDropCourse.setCode(((LabsAdminRegistrationCourse) item).getCode());
            pendingDropCourse.setSection(((LabsAdminRegistrationCourse) item).getSection());
            pendingDropCourse.setDropDate(new Date());
            form.setPendingDropCourse(pendingDropCourse);

            return showDialog("KS-AdminRegistration-DropRegisteredDialog", true, form);
        }

        // TODO you would do the actual drop call here
        ((LabsAdminRegistrationCourse)item).setDropDate(form.getPendingDropCourse().getDropDate());

        cancelEdits(form, selectedCollectionId);

        return deleteLine(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeWaitlistCourse")
    public ModelAndView removeWaitlistCourse(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);
        cancelEdits(form, selectedCollectionId);

        return deleteLine(form);
    }

    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=regUpdateQuery")
    @ResponseBody
    public Map regUpdateQuery(LabsAdminRegistrationForm form) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> updateIds = new ArrayList<String>();

        if (form.getCoursesInProcess().isEmpty()) {
            result.put("stop", true);
            return result;
        }

        synchronized (form) {
            Random generator = new Random();

            int i = generator.nextInt(5);
            if (i == 0 && !form.getCoursesInProcess().isEmpty()) {
                // faking a registration complete
                form.getRegisteredCourses().add(form.getCoursesInProcess().get(0));
                form.getCoursesInProcess().remove(0);

                updateIds.add(REG_COLL_ID);
            }

            i = generator.nextInt(5);
            if (i == 0 && !form.getCoursesInProcess().isEmpty()) {
                // faking a waitlist complete
                form.getWaitlistedCourses().add(form.getCoursesInProcess().get(0));
                form.getCoursesInProcess().remove(0);

                updateIds.add(WAITLIST_COLL_ID);
            }

            i = generator.nextInt(5);
            if (i == 0 && !form.getCoursesInProcess().isEmpty()) {
                // faking an issue found
                LabsAdminRegistrationIssue regIssue = new LabsAdminRegistrationIssue();
                regIssue.setCourse(form.getCoursesInProcess().get(0));
                regIssue.getMessages().add("Some Problem");
                regIssue.getMessages().add("Some Other Problem");
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

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=editCourse")
    public ModelAndView editCourse(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String selectedCollectionPath = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection path was not set for collection action");
        }

        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);

        String selectedLine = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
        int selectedLineIndex = -1;
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        }

        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(form, selectedCollectionPath);
        Object item = ((List) collection).get(selectedLineIndex);

        cancelEdits(form, selectedCollectionId);

        // TODO May want to write your own copy/clone method or alternatively re-retrieve value from db on cancel
        LabsAdminRegistrationCourse
                tempCourse = (LabsAdminRegistrationCourse) (SerializationUtils.clone((LabsAdminRegistrationCourse) item));

        if (selectedCollectionId.equals(REG_COLL_ID)) {
            form.setEditRegisteredIndex(selectedLineIndex);
            form.setTempRegCourseEdit(tempCourse);
        } else if (selectedCollectionId.equals(WAITLIST_COLL_ID)) {
            form.setEditWaitlistedIndex(selectedLineIndex);
            form.setTempWaitlistCourseEdit(tempCourse);
        }

        return getRefreshControllerService().refresh(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveEdit")
    public ModelAndView saveEdit(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);
        if (selectedCollectionId.equals(REG_COLL_ID)) {
            form.setEditRegisteredIndex(-1);
            form.setTempRegCourseEdit(null);
        } else if (selectedCollectionId.equals(WAITLIST_COLL_ID)) {
            form.setEditWaitlistedIndex(-1);
            form.setTempWaitlistCourseEdit(null);
        }

        // TODO perform actual save on item in the backend

        return refresh(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=cancelEdit")
    public ModelAndView cancelEdit(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);
        cancelEdits(form, selectedCollectionId);

        return refresh(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=allowCourse")
    public ModelAndView allowCourse(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String selectedCollectionPath = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection path was not set for collection action");
        }

        String selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);

        String selectedLine = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
        int selectedLineIndex = -1;
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        }

        // TODO would Force registration here
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(form, selectedCollectionPath);
        Object item = ((List) collection).get(selectedLineIndex);
        form.getRegisteredCourses().add(((LabsAdminRegistrationIssue) item).getCourse());
        ((List) collection).remove(selectedLineIndex);

        return getModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=denyCourse")
    public ModelAndView denyCourse(@ModelAttribute("KualiForm") LabsAdminRegistrationForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        // TODO would deny registration request here
        return deleteLine(form);
    }

    private void cancelEdits(LabsAdminRegistrationForm form, String collectionId) {
        if (collectionId == null) {
            return;
        }

        // Cancel other edit if one is open
        if (form.getEditRegisteredIndex() > -1 && collectionId.equals(REG_COLL_ID)) {
            Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(form, "registeredCourses");
            // TODO using temp here but could retrieve original from db
            ((List) collection).set(form.getEditRegisteredIndex(), form.getTempRegCourseEdit());
            form.setEditRegisteredIndex(-1);
        } else if (form.getEditWaitlistedIndex() > -1 && collectionId.equals(WAITLIST_COLL_ID)) {
            Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(form, "waitlistedCourses");
            // TODO using temp here but could retrieve original from db
            ((List) collection).set(form.getEditWaitlistedIndex(), form.getTempWaitlistCourseEdit());
            form.setEditRegisteredIndex(-1);
        }
    }
}
