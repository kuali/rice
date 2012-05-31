/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package mocks;

import java.io.Serializable;

public class MockOrganization implements Serializable {

	private static final long serialVersionUID = 295309153521813643L;

	private String finCoaCd;
    private String orgCd;
    private String reportsToChart;
    private String reportsToOrg;

    public MockOrganization(String finCoaCd, String orgCd, String reportsToChart, String reportsToOrg) {
    	this.finCoaCd = finCoaCd;
    	this.orgCd = orgCd;
    	this.reportsToChart = reportsToChart;
    	this.reportsToOrg = reportsToOrg;
    }

    public String getFinCoaCd() {
        return finCoaCd;
    }
    public void setFinCoaCd(String chart) {
        this.finCoaCd = chart;
    }
    public String getOrgCd() {
        return orgCd;
    }
    public void setOrgCd(String org) {
        this.orgCd = org;
    }
    public String getReportsToChart() {
        return reportsToChart;
    }
    public void setReportsToChart(String reportsToChart) {
        this.reportsToChart = reportsToChart;
    }
    public String getReportsToOrg() {
        return reportsToOrg;
    }
    public void setReportsToOrg(String reportsToOrg) {
        this.reportsToOrg = reportsToOrg;
    }
    public boolean hasParent() {
        return !(getFinCoaCd().equals(getReportsToChart()) && getOrgCd().equals(getReportsToOrg()));
    }
}
