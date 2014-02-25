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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CourseSearchResult {

    private String courseOfferingId;
    private String courseOfferingCode;
    private boolean honorsCourse;
    private boolean auditCourse;
    private boolean studentSelectablePassFail;
    private String courseOfferingDesc;
    private String courseOfferingCreditOptionDisplay;
    private String courseOfferingGradingOptionDisplay;

    public CourseSearchResult() {

    }

    /**
     *
     * @return
     */
    public String getCourseOfferingId() {
        return courseOfferingId;
    }

    /**
     *
     * @param courseOfferingId
     */
    public void setCourseOfferingId(String courseOfferingId) {
        this.courseOfferingId = courseOfferingId;
    }

    /**
     *
     * @return
     */
    public String getCourseOfferingCode() {
        return courseOfferingCode;
    }

    /**
     *
     * @param courseOfferingCode
     */
    public void setCourseOfferingCode(String courseOfferingCode) {
        this.courseOfferingCode = courseOfferingCode;
    }

    /**
     *
     * @return
     */
    public boolean getHonorsCourse() {
        return honorsCourse;
    }

    /**
     *
     * @param honorsCourse
     */
    public void setHonorsCourse(boolean honorsCourse) {
        this.honorsCourse = honorsCourse;
    }

    /**
     *
     * @return
     */
    public boolean getAuditCourse() {
        return auditCourse;
    }

    /**
     *
     * @param auditCourse
     */
    public void setAuditCourse(boolean auditCourse) {
        this.auditCourse = auditCourse;
    }

    /**
     *
     * @return
     */
    public boolean getStudentSelectablePassFail() {
        return studentSelectablePassFail;
    }

    /**
     *
     * @param studentSelectablePassFail
     */
    public void setStudentSelectablePassFail(boolean studentSelectablePassFail) {
        this.studentSelectablePassFail = studentSelectablePassFail;
    }

    /**
     *
     * @return
     */
    public String getCourseOfferingDesc() {
        return courseOfferingDesc;
    }

    /**
     *
     * @param courseOfferingDesc
     */
    public void setCourseOfferingDesc(String courseOfferingDesc) {
        this.courseOfferingDesc = courseOfferingDesc;
    }

    /**
     *
     * @return
     */
    public String getCourseOfferingCreditOptionDisplay() {
        return courseOfferingCreditOptionDisplay;
    }

    /**
     *
     * @param courseOfferingCreditOptionDisplay
     */
    public void setCourseOfferingCreditOptionDisplay(String courseOfferingCreditOptionDisplay) {
        this.courseOfferingCreditOptionDisplay = courseOfferingCreditOptionDisplay;
    }

    /**
     *
     * @return
     */
    public String getCourseOfferingGradingOptionDisplay() {
        return courseOfferingGradingOptionDisplay;
    }

    /**
     *
     * @param courseOfferingGradingOptionDisplay
     */
    public void setCourseOfferingGradingOptionDisplay(String courseOfferingGradingOptionDisplay) {
        this.courseOfferingGradingOptionDisplay = courseOfferingGradingOptionDisplay;
    }
}
