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
package org.kuali.rice.krad.labs.registration.form;

import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for KS Admin Registration Lab prototype
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsAdminRegistrationForm extends UifFormBase implements Serializable {
    private String studentId;
    private String studentName;
    private String program;
    private String standing;
    private String credits;
    private String major;
    private String department;

    private String termName = "Fall 2012";
    private String termId = "11111";

    private LabsAdminRegistrationCourse pendingDropCourse;

    private int editRegisteredIndex;
    private int editWaitlistedIndex;

    private LabsAdminRegistrationCourse tempRegCourseEdit;
    private LabsAdminRegistrationCourse tempWaitlistCourseEdit;

    private List<LabsAdminRegistrationCourse> registeredCourses = new ArrayList<LabsAdminRegistrationCourse>();
    private List<LabsAdminRegistrationCourse> waitlistedCourses = new ArrayList<LabsAdminRegistrationCourse>();
    private List<LabsAdminRegistrationCourse> pendingCourses = new ArrayList<LabsAdminRegistrationCourse>();
    private List<LabsAdminRegistrationIssue> registrationIssues = new ArrayList<LabsAdminRegistrationIssue>();

    private List<LabsAdminRegistrationCourse> coursesInProcess = new ArrayList<LabsAdminRegistrationCourse>();

    public LabsAdminRegistrationForm(){
        editRegisteredIndex = -1;
        editWaitlistedIndex = -1;

        Date regDate = new Date();

        LabsAdminRegistrationCourse course1 =
                new LabsAdminRegistrationCourse("CHEM 237", "1001", "The Chemistry of Stuff", 3, "reg", regDate);
        List<LabsAdminRegistrationActivity> activities1 = new ArrayList<LabsAdminRegistrationActivity>();
        activities1.add(new LabsAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Steve Capriani", "PTX 2391"));
        activities1.add(new LabsAdminRegistrationActivity("Lab", "MWF 02:30pm - 03:30pm", "Steve Capriani", "PTX 2391"));
        course1.setActivities(activities1);
        course1.setSubterm(true);

        registeredCourses.add(course1);

        LabsAdminRegistrationCourse course2 =
                new LabsAdminRegistrationCourse("ENGL 233", "1001", "The World of Shakespeare", 3, "audit", regDate);
        course2.setEffectiveDate(new Date(regDate.getTime() - 86400000));
        List<LabsAdminRegistrationActivity> activities2 = new ArrayList<LabsAdminRegistrationActivity>();
        activities2.add(new LabsAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Someone", "PTX 1111"));
        course2.setActivities(activities2);

        registeredCourses.add(course2);

        LabsAdminRegistrationCourse course3 =
                new LabsAdminRegistrationCourse("ENGL 640", "1001", "Light and Motion", 3, "pf", regDate);
        List<LabsAdminRegistrationActivity> activities3 = new ArrayList<LabsAdminRegistrationActivity>();
        activities3.add(new LabsAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Someone", "PTX 1200"));
        course3.setActivities(activities3);

        registeredCourses.add(course3);

        LabsAdminRegistrationCourse course4 =
                new LabsAdminRegistrationCourse("CHEM 237", "1001", "The Chemistry of Stuff", 3, "reg", new Date());
        List<LabsAdminRegistrationActivity> activities4 = new ArrayList<LabsAdminRegistrationActivity>();
        activities4.add(new LabsAdminRegistrationActivity("Lec", "MWF 04:00pm - 05:30pm", "Steve Capriani", "PTX 2391"));
        activities4.add(new LabsAdminRegistrationActivity("Lab", "MWF 05:30pm - 06:30pm", "Steve Capriani", "PTX 2391"));
        course4.setActivities(activities4);

        waitlistedCourses.add(course4);

        pendingCourses.add(new LabsAdminRegistrationCourse());
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getStanding() {
        return standing;
    }

    public void setStanding(String standing) {
        this.standing = standing;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public List<LabsAdminRegistrationCourse> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<LabsAdminRegistrationCourse> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public List<LabsAdminRegistrationCourse> getWaitlistedCourses() {
        return waitlistedCourses;
    }

    public void setWaitlistedCourses(List<LabsAdminRegistrationCourse> waitlistedCourses) {
        this.waitlistedCourses = waitlistedCourses;
    }

    public int getRegisteredCredits() {
        int credits = 0;
        for (LabsAdminRegistrationCourse course: registeredCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public int getWaitlistedCredits() {
        int credits = 0;
        for (LabsAdminRegistrationCourse course: waitlistedCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public List<LabsAdminRegistrationCourse> getPendingCourses() {
        return pendingCourses;
    }

    public void setPendingCourses(List<LabsAdminRegistrationCourse> pendingCourses) {
        this.pendingCourses = pendingCourses;
    }

    public List<LabsAdminRegistrationIssue> getRegistrationIssues() {
        return registrationIssues;
    }

    public void setRegistrationIssues(List<LabsAdminRegistrationIssue> registrationIssues) {
        this.registrationIssues = registrationIssues;
    }

    public List<LabsAdminRegistrationCourse> getCoursesInProcess() {
        return coursesInProcess;
    }

    public void setCoursesInProcess(List<LabsAdminRegistrationCourse> coursesInProcess) {
        this.coursesInProcess = coursesInProcess;
    }

    public LabsAdminRegistrationCourse getPendingDropCourse() {
        return pendingDropCourse;
    }

    public void setPendingDropCourse(LabsAdminRegistrationCourse pendingDropCourse) {
        this.pendingDropCourse = pendingDropCourse;
    }

    public int getEditRegisteredIndex() {
        return editRegisteredIndex;
    }

    public void setEditRegisteredIndex(int editRegisteredIndex) {
        this.editRegisteredIndex = editRegisteredIndex;
    }

    public int getEditWaitlistedIndex() {
        return editWaitlistedIndex;
    }

    public void setEditWaitlistedIndex(int editWaitlistedIndex) {
        this.editWaitlistedIndex = editWaitlistedIndex;
    }

    public LabsAdminRegistrationCourse getTempRegCourseEdit() {
        return tempRegCourseEdit;
    }

    public void setTempRegCourseEdit(LabsAdminRegistrationCourse tempRegCourseEdit) {
        this.tempRegCourseEdit = tempRegCourseEdit;
    }

    public LabsAdminRegistrationCourse getTempWaitlistCourseEdit() {
        return tempWaitlistCourseEdit;
    }

    public void setTempWaitlistCourseEdit(LabsAdminRegistrationCourse tempWaitlistCourseEdit) {
        this.tempWaitlistCourseEdit = tempWaitlistCourseEdit;
    }
}
