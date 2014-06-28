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
package org.kuali.rice.krad.demo.uif.form;

import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Brian on 6/17/14.
 */
public class KSWorkshopForm extends UifFormBase implements Serializable {
    private String studentId;
    private String studentName;
    private String program;
    private String standing;
    private String credits;
    private String major;
    private String department;

    private String termName = "Fall 2012";
    private String termId = "11111";

    private List<KSWorkshopCourse> registeredCourses = new ArrayList<KSWorkshopCourse>();
    private List<KSWorkshopCourse> waitlistedCourses = new ArrayList<KSWorkshopCourse>();
    private List<KSWorkshopCourse> pendingCourses = new ArrayList<KSWorkshopCourse>();
    private List<KSRegistrationIssue> registrationIssues = new ArrayList<KSRegistrationIssue>();

    private List<KSWorkshopCourse> coursesInProcess = new ArrayList<KSWorkshopCourse>();

    public KSWorkshopForm(){
        KSWorkshopCourse course1 =
                new KSWorkshopCourse("CHEM 237", "1001", "The Chemistry of Stuff", 3, "Regular", new Date());
        List<KSWorkshopActivity> activities1 = new ArrayList<KSWorkshopActivity>();
        activities1.add(new KSWorkshopActivity("Lec", "MWF 01:00pm - 02:30pm", "Steve Capriani", "PTX 2391"));
        activities1.add(new KSWorkshopActivity("Lab", "MWF 02:30pm - 03:30pm", "Steve Capriani", "PTX 2391"));
        course1.setActivities(activities1);

        registeredCourses.add(course1);

        KSWorkshopCourse course2 =
                new KSWorkshopCourse("ENGL 233", "1001", "The World of Shakespeare", 3, "Audit", new Date());
        List<KSWorkshopActivity> activities2 = new ArrayList<KSWorkshopActivity>();
        activities2.add(new KSWorkshopActivity("Lec", "MWF 01:00pm - 02:30pm", "Someone", "PTX 1111"));
        course2.setActivities(activities2);

        registeredCourses.add(course2);

        KSWorkshopCourse course3 =
                new KSWorkshopCourse("ENGL 640", "1001", "Light and Motion", 3, "Pass/Fail", new Date());
        List<KSWorkshopActivity> activities3 = new ArrayList<KSWorkshopActivity>();
        activities3.add(new KSWorkshopActivity("Lec", "MWF 01:00pm - 02:30pm", "Someone", "PTX 1200"));
        course3.setActivities(activities3);

        registeredCourses.add(course3);

        KSWorkshopCourse course4 =
                new KSWorkshopCourse("CHEM 237", "1001", "The Chemistry of Stuff", 3, "Regular", new Date());
        List<KSWorkshopActivity> activities4 = new ArrayList<KSWorkshopActivity>();
        activities4.add(new KSWorkshopActivity("Lec", "MWF 04:00pm - 05:30pm", "Steve Capriani", "PTX 2391"));
        activities4.add(new KSWorkshopActivity("Lab", "MWF 05:30pm - 06:30pm", "Steve Capriani", "PTX 2391"));
        course4.setActivities(activities4);

        waitlistedCourses.add(course4);

        pendingCourses.add(new KSWorkshopCourse());
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

    public List<KSWorkshopCourse> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<KSWorkshopCourse> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public List<KSWorkshopCourse> getWaitlistedCourses() {
        return waitlistedCourses;
    }

    public void setWaitlistedCourses(List<KSWorkshopCourse> waitlistedCourses) {
        this.waitlistedCourses = waitlistedCourses;
    }

    public int getRegisteredCredits() {
        int credits = 0;
        for (KSWorkshopCourse course: registeredCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public int getWaitlistedCredits() {
        int credits = 0;
        for (KSWorkshopCourse course: waitlistedCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public List<KSWorkshopCourse> getPendingCourses() {
        return pendingCourses;
    }

    public void setPendingCourses(List<KSWorkshopCourse> pendingCourses) {
        this.pendingCourses = pendingCourses;
    }

    public List<KSRegistrationIssue> getRegistrationIssues() {
        return registrationIssues;
    }

    public void setRegistrationIssues(List<KSRegistrationIssue> registrationIssues) {
        this.registrationIssues = registrationIssues;
    }

    public List<KSWorkshopCourse> getCoursesInProcess() {
        return coursesInProcess;
    }

    public void setCoursesInProcess(List<KSWorkshopCourse> coursesInProcess) {
        this.coursesInProcess = coursesInProcess;
    }
}
