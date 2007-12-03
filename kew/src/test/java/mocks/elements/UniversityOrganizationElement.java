/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package mocks.elements;

import mocks.MockChartOrgService;

import org.jdom.Element;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.services.InconsistentDocElementStateException;
import edu.iu.uis.eden.services.ServiceErrorConstants;

/**
 * <p>
 * Title: UniversityOrganizationElement
 * </p>
 * <p>
 * Description: Compound Doc Element. Brings ChartElement and OrgCodeElement together into a single route control.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Indiana University
 * </p>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version 1.0
 */
public class UniversityOrganizationElement implements IDocElement {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversityOrganizationElement.class);
    private static final String ELEMENT_NAME = "university_organization_eok";
    private boolean routeControl;
    private ChartElement chartElement;
    private OrgCodeElement orgCodeElement;

    public UniversityOrganizationElement() {
        LOG.debug("constructing . . .");
        this.routeControl = true;
        chartElement = new ChartElement();
        orgCodeElement = new OrgCodeElement();
    }

    public Element getXMLContent() {
        LOG.debug("getXMLContent");

        if (this.isEmpty()) {
            return null;
        }

        Element me = new Element(ELEMENT_NAME);

        if (this.routeControl) {
            me.setAttribute("route-control", "yes");
        }

        if (!this.chartElement.isEmpty()) {
            LOG.debug("inserting chart element");
            me.addContent(chartElement.getXMLContent());
        }

        if (!this.orgCodeElement.isEmpty()) {
            LOG.debug("inserting org element");
            me.addContent(orgCodeElement.getXMLContent());
        }

        LOG.debug("return XMLContent " + me.toString());

        return me;
    }

    public void loadFromXMLContent(Element element, boolean allowBlank) throws InvalidXmlException, InconsistentDocElementStateException {
        LOG.debug("loadFromXMLContent");

        if (DocElementValidator.returnWithNoWorkDone(this, element, allowBlank)) {
            LOG.debug("returning without setting");

            return;
        }

        //it's good load kiddies with their elements
        chartElement.loadFromXMLContent(element.getChild(chartElement.getElementName()), allowBlank);

        orgCodeElement.loadFromXMLContent(element.getChild(orgCodeElement.getElementName()), allowBlank);

        LOG.debug("loaded UniversityOrganizationElement");
    }

    public WorkflowServiceErrorImpl validate() {
        LOG.debug("validate");

        boolean inError = false;

        WorkflowServiceErrorImpl myErrors = new WorkflowServiceErrorImpl("University Organization Children In Error", ServiceErrorConstants.CHILDREN_IN_ERROR);

        WorkflowServiceErrorImpl chartError = chartElement.validate();

        if (chartError != null) {
            LOG.debug("chart is in error");
            inError = true;
            myErrors.addChild(chartError);
        }

        WorkflowServiceErrorImpl orgError = orgCodeElement.validate();

        if (orgError != null) {
            LOG.debug("OrgCode is in error");
            inError = true;
            myErrors.addChild(orgError);
        }

        if (inError) {
            LOG.debug("in error returning a DocElementError");

            return myErrors;
        }

        /*
         * database validation - the chart and org must exist in the IU org table
         */
        if (!this.databaseValidate()) {
            String msg = "Chart " + this.getChart() + " and Org " + this.getOrgCode() + " don't exist";
            LOG.debug(msg);

            return new WorkflowServiceErrorImpl(msg, ServiceErrorConstants.UNIVERSITY_ORGANIZATION_INVALID);
        }

        LOG.debug("valid returning null");

        return null;
    }

    public boolean isEmpty() {
        if (this.chartElement.isEmpty() && this.orgCodeElement.isEmpty()) {
            return true;
        }

        return false;
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public void setRouteControl(boolean routeControl) {
    }

    public boolean isRouteControl() {
        return this.routeControl;
    }

    /**
     * Validates that the chart and org exist within the IU Hierarchy.
     *
     * @return true if the object is valid false if not
     * @throws ResourceUnavailableException
     */
    protected boolean databaseValidate() {
        return new MockChartOrgService().findOrganization(getChart(), getOrgCode()) != null;
    }

    public String getOrgCode() {
        return this.orgCodeElement.getOrgCode();
    }

    public void setOrgCode(String orgCode) {
        this.orgCodeElement.setOrgCode(orgCode);
    }

    public void setChart(String chart) {
        this.chartElement.setChart(chart);
    }

    public String getChart() {
        return this.chartElement.getChart();
    }

    /**
     * validates underlying chart outside of the context of being in a UniversityOrganizationElement
     *
     * @return DocElementError of underlying chart's error(s)
     */
    public WorkflowServiceErrorImpl validateChart() throws ResourceUnavailableException {
        return this.chartElement.validate();
    }

    /**
     * validates underlying OrgCode outside of the context of being in a UniversityOrganizationElement
     *
     * @return DocElementError of underlying orgCode's error(s)
     */
    public WorkflowServiceErrorImpl validateOrgCode() throws ResourceUnavailableException {
        return this.orgCodeElement.validate();
    }
}