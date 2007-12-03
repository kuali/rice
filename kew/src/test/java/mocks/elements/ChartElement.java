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

import org.jdom.Element;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.services.InconsistentDocElementStateException;
import edu.iu.uis.eden.services.ServiceErrorConstants;

/**
 * <p>Title: ChartElement</p>
 * <p>Description: Represents a chart element both in xml for documents and as a
 * chart entity itself concerning validation. <br><BR>
 *  See IDocElement documentation for
 * further explanation.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version 1.0
 */
public class ChartElement implements IDocElement {
  /**
	 * 
	 */
	private static final long serialVersionUID = -8383050303425266683L;
private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartElement.class);
  private boolean routeControl;
  private String chart;

  public ChartElement() {
    LOG.debug("constructing . . .");
    this.routeControl = true;
  }

  public Element getXMLContent() {
    LOG.debug("getXmlContent");

    if (this.isEmpty()) {
      LOG.debug("chart null or empty returning empty String");

      return null;
    }

    ChartCodeEOK chartEOK = null;

    try {
      chartEOK = new ChartCodeEOK(chart, routeControl);
    } catch (Exception ex) {
      LOG.error("EOK threw exception loading chart values.  ChartElement " + "improperly tested.",
        ex);
      //this should never happen if the class was properly unit tested
    }

    return chartEOK.buildJdom();
  }

  public void loadFromXMLContent(Element element, boolean allowBlank)
    throws InvalidXmlException, InconsistentDocElementStateException {
    LOG.debug("loadFromXMLContent allowBlank = " + allowBlank);

    if (DocElementValidator.returnWithNoWorkDone(this, element, allowBlank)) {
      return;
    }

    ChartCodeEOK chartEOK = null;

    try {
      chartEOK = new ChartCodeEOK(element);
      this.chart = chartEOK.getChart_cd_value();
      this.routeControl = chartEOK.routeControl;
    } catch (Exception ex) {
      LOG.error("Document with invalid XML given ChartElement", ex);
      throw new InvalidXmlException(ex.getMessage());
    }
  }

  public WorkflowServiceErrorImpl validate() {
    LOG.debug("validate");

    if (this.isEmpty()) {
      LOG.debug("invalid");

      return new WorkflowServiceErrorImpl("Chart is null or blank", ServiceErrorConstants.CHART_BLANK);
    }

    LOG.debug("valid returning null");

    return null;
  }

  /**
   * Tell whether the objects value property/properties have values
   *
   * @return true when object is empty
   */
  public boolean isEmpty() {
    LOG.debug("isEmpty()");

    if ((this.chart == null) || this.chart.trim().equals("")) {
      LOG.debug("empty");

      return true;
    }

    LOG.debug("not empty");

    return false;
  }

  public String getElementName() {
    LOG.debug("getElementName");

    return ChartCodeEOK.CHART_CODE_EOK;
  }

  public void setRouteControl(boolean routeControl) {
    LOG.debug("setRouteControl routeControl = " + routeControl);
    this.routeControl = routeControl;
  }

  public boolean isRouteControl() {
    LOG.debug("isRouteControl = " + this.routeControl);

    return this.routeControl;
  }

  /**
   *
   * @return String value of chart
   */
  public String getChart() {
    LOG.debug("getChart = " + this.chart);

    return this.chart;
  }

  /**
   *
   * @param chart String value of chart
   */
  public void setChart(String chart) {
    LOG.debug("setChart chart = " + chart);
    this.chart = chart;
  }
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
