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
public class KSAdminRegistrationForm extends UifFormBase implements Serializable {
    private String studentId;
    private String studentName;
    private String program;
    private String standing;
    private String credits;
    private String major;
    private String department;

    private String termName = "Fall 2012";
    private String termId = "11111";

    private KSAdminRegistrationCourse pendingDropCourse;

    private int editRegisteredIndex;
    private int editWaitlistedIndex;

    private KSAdminRegistrationCourse tempRegCourseEdit;
    private KSAdminRegistrationCourse tempWaitlistCourseEdit;

    private List<KSAdminRegistrationCourse> registeredCourses = new ArrayList<KSAdminRegistrationCourse>();
    private List<KSAdminRegistrationCourse> waitlistedCourses = new ArrayList<KSAdminRegistrationCourse>();
    private List<KSAdminRegistrationCourse> pendingCourses = new ArrayList<KSAdminRegistrationCourse>();
    private List<KSAdminRegistrationIssue> registrationIssues = new ArrayList<KSAdminRegistrationIssue>();

    private List<KSAdminRegistrationCourse> coursesInProcess = new ArrayList<KSAdminRegistrationCourse>();

    public KSAdminRegistrationForm(){
        editRegisteredIndex = -1;
        editWaitlistedIndex = -1;

        Date regDate = new Date();

        KSAdminRegistrationCourse course1 =
                new KSAdminRegistrationCourse("CHEM 237", "1001", "The Chemistry of Stuff", 3, "reg", regDate);
        List<KSAdminRegistrationActivity> activities1 = new ArrayList<KSAdminRegistrationActivity>();
        activities1.add(new KSAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Steve Capriani", "PTX 2391"));
        activities1.add(new KSAdminRegistrationActivity("Lab", "MWF 02:30pm - 03:30pm", "Steve Capriani", "PTX 2391"));
        course1.setActivities(activities1);
        course1.setSubterm(true);

        registeredCourses.add(course1);

        KSAdminRegistrationCourse course2 =
                new KSAdminRegistrationCourse("ENGL 233", "1001", "The World of Shakespeare", 3, "audit", regDate);
        course2.setEffectiveDate(new Date(regDate.getTime() - 86400000));
        List<KSAdminRegistrationActivity> activities2 = new ArrayList<KSAdminRegistrationActivity>();
        activities2.add(new KSAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Someone", "PTX 1111"));
        course2.setActivities(activities2);

        registeredCourses.add(course2);

        KSAdminRegistrationCourse course3 =
                new KSAdminRegistrationCourse("ENGL 640", "1001", "Light and Motion", 3, "pf", regDate);
        List<KSAdminRegistrationActivity> activities3 = new ArrayList<KSAdminRegistrationActivity>();
        activities3.add(new KSAdminRegistrationActivity("Lec", "MWF 01:00pm - 02:30pm", "Someone", "PTX 1200"));
        course3.setActivities(activities3);

        registeredCourses.add(course3);

        KSAdminRegistrationCourse course4 =
                new KSAdminRegistrationCourse("CHEM 237", "1001", "The Chemistry of Stuff", 3, "reg", new Date());
        List<KSAdminRegistrationActivity> activities4 = new ArrayList<KSAdminRegistrationActivity>();
        activities4.add(new KSAdminRegistrationActivity("Lec", "MWF 04:00pm - 05:30pm", "Steve Capriani", "PTX 2391"));
        activities4.add(new KSAdminRegistrationActivity("Lab", "MWF 05:30pm - 06:30pm", "Steve Capriani", "PTX 2391"));
        course4.setActivities(activities4);

        waitlistedCourses.add(course4);

        pendingCourses.add(new KSAdminRegistrationCourse());
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

    public List<KSAdminRegistrationCourse> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<KSAdminRegistrationCourse> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public List<KSAdminRegistrationCourse> getWaitlistedCourses() {
        return waitlistedCourses;
    }

    public void setWaitlistedCourses(List<KSAdminRegistrationCourse> waitlistedCourses) {
        this.waitlistedCourses = waitlistedCourses;
    }

    public int getRegisteredCredits() {
        int credits = 0;
        for (KSAdminRegistrationCourse course: registeredCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public int getWaitlistedCredits() {
        int credits = 0;
        for (KSAdminRegistrationCourse course: waitlistedCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public List<KSAdminRegistrationCourse> getPendingCourses() {
        return pendingCourses;
    }

    public void setPendingCourses(List<KSAdminRegistrationCourse> pendingCourses) {
        this.pendingCourses = pendingCourses;
    }

    public List<KSAdminRegistrationIssue> getRegistrationIssues() {
        return registrationIssues;
    }

    public void setRegistrationIssues(List<KSAdminRegistrationIssue> registrationIssues) {
        this.registrationIssues = registrationIssues;
    }

    public List<KSAdminRegistrationCourse> getCoursesInProcess() {
        return coursesInProcess;
    }

    public void setCoursesInProcess(List<KSAdminRegistrationCourse> coursesInProcess) {
        this.coursesInProcess = coursesInProcess;
    }

    public KSAdminRegistrationCourse getPendingDropCourse() {
        return pendingDropCourse;
    }

    public void setPendingDropCourse(KSAdminRegistrationCourse pendingDropCourse) {
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

    public KSAdminRegistrationCourse getTempRegCourseEdit() {
        return tempRegCourseEdit;
    }

    public void setTempRegCourseEdit(KSAdminRegistrationCourse tempRegCourseEdit) {
        this.tempRegCourseEdit = tempRegCourseEdit;
    }

    public KSAdminRegistrationCourse getTempWaitlistCourseEdit() {
        return tempWaitlistCourseEdit;
    }

    public void setTempWaitlistCourseEdit(KSAdminRegistrationCourse tempWaitlistCourseEdit) {
        this.tempWaitlistCourseEdit = tempWaitlistCourseEdit;
    }
}
