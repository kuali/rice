/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.core.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

/**
 * 
 */
public class BatchReport extends PersistableBusinessObjectBase {

    private String scriptName;
    private String storedProcedureName;
    private String reportCode;
    private Integer reportLineNumber;
    private Date reportCreateDate;
    private String reportText;

    /**
     * Default constructor.
     */
    public BatchReport() {

    }

    /**
     * Gets the scriptName attribute.
     * 
     * @return Returns the scriptName
     * 
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Sets the scriptName attribute.
     * 
     * @param scriptName The scriptName to set.
     * 
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }


    /**
     * Gets the storedProcedureName attribute.
     * 
     * @return Returns the storedProcedureName
     * 
     */
    public String getStoredProcedureName() {
        return storedProcedureName;
    }

    /**
     * Sets the storedProcedureName attribute.
     * 
     * @param storedProcedureName The storedProcedureName to set.
     * 
     */
    public void setStoredProcedureName(String storedProcedureName) {
        this.storedProcedureName = storedProcedureName;
    }


    /**
     * Gets the reportCode attribute.
     * 
     * @return Returns the reportCode
     * 
     */
    public String getReportCode() {
        return reportCode;
    }

    /**
     * Sets the reportCode attribute.
     * 
     * @param reportCode The reportCode to set.
     * 
     */
    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }


    /**
     * Gets the reportLineNumber attribute.
     * 
     * @return Returns the reportLineNumber
     * 
     */
    public Integer getReportLineNumber() {
        return reportLineNumber;
    }

    /**
     * Sets the reportLineNumber attribute.
     * 
     * @param reportLineNumber The reportLineNumber to set.
     * 
     */
    public void setReportLineNumber(Integer reportLineNumber) {
        this.reportLineNumber = reportLineNumber;
    }


    /**
     * Gets the reportCreateDate attribute.
     * 
     * @return Returns the reportCreateDate
     * 
     */
    public Date getReportCreateDate() {
        return reportCreateDate;
    }

    /**
     * Sets the reportCreateDate attribute.
     * 
     * @param reportCreateDate The reportCreateDate to set.
     * 
     */
    public void setReportCreateDate(Date reportCreateDate) {
        this.reportCreateDate = reportCreateDate;
    }


    /**
     * Gets the reportText attribute.
     * 
     * @return Returns the reportText
     * 
     */
    public String getReportText() {
        return reportText;
    }

    /**
     * Sets the reportText attribute.
     * 
     * @param reportText The reportText to set.
     * 
     */
    public void setReportText(String reportText) {
        this.reportText = reportText;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("scriptName", this.scriptName);
        m.put("storedProcedureName", this.storedProcedureName);
        m.put("reportCode", this.reportCode);
        if (this.reportLineNumber != null) {
            m.put("reportLineNumber", this.reportLineNumber.toString());
        }
        return m;
    }
}
