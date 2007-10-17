/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.stats.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.stats.Stats;

/**
 * A Struts ActionForm for the {@link StatsAction}.
 * 
 * @see StatsAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StatsForm extends ActionForm {

	private static final long serialVersionUID = 4587377779133823858L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StatsForm.class);

    public static final String DAY_TIME_UNIT = "DDD";
    public static final String WEEK_TIME_UNIT = "WW";
    public static final String MONTH_TIME_UNIT = "MM";
    public static final String YEAR_TIME_UNIT = "YYYY";

    public static final String DEFAULT_BEGIN_DATE = "01/01/1900";
    public static final String DEFAULT_END_DATE = "01/01/2400";
    public static final String BEG_DAY_TIME = " 00:00";
    public static final String END_DAY_TIME = " 23:59";
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String TIME_FORMAT = " HH:mm";

    private Stats stats;
    private String methodToCall = "";    
    private String avgActionsPerTimeUnit = DAY_TIME_UNIT;

    private String begDate;
    private String endDate;

    private Date beginningDate;
    private Date endingDate;
    
    public StatsForm() {
        stats = new Stats();
    }
        
    public void determineBeginDate() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT);

        beginningDate = null;

        if (getBegDate() == null || getBegDate().trim().equals("")) {
            beginningDate = dateFormat.parse(DEFAULT_BEGIN_DATE + BEG_DAY_TIME);
        } else {
            beginningDate = dateFormat.parse(getBegDate() + BEG_DAY_TIME);
        }

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        begDate = dateFormat.format(beginningDate);
    }

    public void determineEndDate() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT);

        endingDate = null;

        if (getEndDate() == null || getEndDate().trim().equals("")) {
            endingDate = dateFormat.parse(DEFAULT_END_DATE + END_DAY_TIME);
        } else {
            endingDate = dateFormat.parse(getEndDate() + END_DAY_TIME);
        }

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        endDate = dateFormat.format(endingDate);
    }

    public Map makePerUnitOfTimeDropDownMap() {

        Map dropDownMap = new HashMap();
        dropDownMap.put(DAY_TIME_UNIT, EdenConstants.DAILY_UNIT);
        dropDownMap.put(WEEK_TIME_UNIT, EdenConstants.WEEKLY_UNIT);
        dropDownMap.put(MONTH_TIME_UNIT, EdenConstants.MONTHLY_UNIT);
        dropDownMap.put(YEAR_TIME_UNIT, EdenConstants.YEARLY_UNIT);
        return dropDownMap;

    }
   
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        LOG.debug("validate()");

        ActionErrors errors = new ActionErrors();

        this.validateDate(this.getBegDate(), errors, "error.stats.BegDate");
        this.validateDate(this.getEndDate(), errors, "error.stats.EndDate");

        return errors;
    }

    private void validateDate(String date, ActionErrors errors, String key) {

        if (date == null || date.trim().equals(""))
            return;

        try {
            new SimpleDateFormat(DATE_FORMAT).parse(date.trim());
        } catch (ParseException ex) {
            errors.add(Globals.ERROR_KEY, new ActionMessage(key, date));
        }
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
    public String getApprovedLabel() {
        return EdenConstants.ROUTE_HEADER_APPROVED_LABEL;
    }

    public String getCanceledLabel() {
        return EdenConstants.ROUTE_HEADER_CANCEL_LABEL;
    }

    public String getDisapprovedLabel() {
        return EdenConstants.ROUTE_HEADER_DISAPPROVED_LABEL;
    }

    public String getEnrouteLabel() {
        return EdenConstants.ROUTE_HEADER_ENROUTE_LABEL;
    }

    public String getExceptionLabel() {
        return EdenConstants.ROUTE_HEADER_EXCEPTION_LABEL;
    }

    public String getFinalLabel() {
        return EdenConstants.ROUTE_HEADER_FINAL_LABEL;
    }

    public String getInitiatedLabel() {
        return EdenConstants.ROUTE_HEADER_INITIATED_LABEL;
    }

    public String getProcessedLabel() {
        return EdenConstants.ROUTE_HEADER_PROCESSED_LABEL;
    }

    public String getSavedLabel() {
        return EdenConstants.ROUTE_HEADER_SAVED_LABEL;
    }

    public String getAvgActionsPerTimeUnit() {
        return avgActionsPerTimeUnit;
    }

    public void setAvgActionsPerTimeUnit(String string) {
        avgActionsPerTimeUnit = string;
    }

    public String getBegDate() {
        return begDate;
    }

    public void setBegDate(String begDate) {
        this.begDate = begDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public Date getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(Date beginningDate) {
        this.beginningDate = beginningDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public String getDayTimeUnit() {
        return DAY_TIME_UNIT;
    }
    
    public String getMonthTimeUnit() {
        return MONTH_TIME_UNIT;
    }

    public String getWeekTimeUnit() {
        return WEEK_TIME_UNIT;
    }

    public String getYearTimeUnit() {
        return YEAR_TIME_UNIT;
    }

}