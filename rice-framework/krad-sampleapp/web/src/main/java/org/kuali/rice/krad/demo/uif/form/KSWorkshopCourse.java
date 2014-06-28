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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Brian on 6/18/14.
 */
public class KSWorkshopCourse implements Serializable{

    private static final long serialVersionUID = 5236548204817229477L;
    private String course;
    private String section;
    private String courseName;
    private Integer credits;
    private String regOptions;
    private Date regDate;
    private List<KSWorkshopActivity> activities;

    public KSWorkshopCourse(){}

    public KSWorkshopCourse(String course, String section, String courseName, Integer credits, String regOptions,
            Date regDate) {
        this.course = course;
        this.section = section;
        this.courseName = courseName;
        this.credits = credits;
        this.regOptions = regOptions;
        this.regDate = regDate;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getCredits() {
        return credits;
    }

    public String getRegOptions() {
        return regOptions;
    }

    public void setRegOptions(String regOptions) {
        this.regOptions = regOptions;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public List<KSWorkshopActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<KSWorkshopActivity> activities) {
        this.activities = activities;
    }

    public List<String> getActivityTypes(){
        ArrayList<String> list = new ArrayList<String>();
        for (KSWorkshopActivity activity: activities) {
            list.add(activity.getType());
        }

        return list;
    }

    public List<String> getActivityDateTimes(){
        ArrayList<String> list = new ArrayList<String>();
        for (KSWorkshopActivity activity: activities) {
            list.add(activity.getDateTime());
        }

        return list;
    }

    public List<String> getActivityInstructors(){
        ArrayList<String> list = new ArrayList<String>();
        for (KSWorkshopActivity activity: activities) {
            list.add(activity.getInstructor());
        }

        return list;
    }

    public List<String> getActivityRooms(){
        ArrayList<String> list = new ArrayList<String>();
        for (KSWorkshopActivity activity: activities) {
            list.add(activity.getRoom());
        }

        return list;
    }
}
