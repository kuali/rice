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
import org.jdom.output.XMLOutputter;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;


/**
 * ChartCodeEOK is the key class for retrieving|building fin_coa_cd element to|from an xml document
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: UIS - Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version 1.0
 */
public class ChartCodeEOK {
  public final static String CHART_CODE_EOK = "financial_chart_of_accounts_eok";
  public final static String CHART_CD_TAG = "fin_coa_cd";
  public final static String VALUE = "value";
  private final static String ROUTE_CONTROL_ATTR = "route-control";
  private final static String ROUTE_CONTROL_TRUE = "yes";

  /* default route control*/
  public boolean routeControl = true;

  //default fin_coa_cd value attribute
  public String chart_cd_value = "";

  //constructors
  public ChartCodeEOK(String chartCode, boolean routeControl)
    throws Exception {
    if ((chartCode == null) || chartCode.equals("")) {
      throw new Exception("Empty chartcode not allowed");
    }

    this.chart_cd_value = chartCode;
    this.routeControl = routeControl;
  }

  /**
   * create a ChartCodeEOK object from a JDOM element
   * @param keyElement a JDOM element
   */
  public ChartCodeEOK(Element rootElement) throws Exception {
    // check if the element is a valid one for us
    if ((rootElement == null) || !rootElement.getName().equals(CHART_CODE_EOK)) {
      throw new WorkflowRuntimeException("Invalid ChartCodeEOK element " + rootElement.getName());
    }

    String routeCtrlVal = rootElement.getAttributeValue(ROUTE_CONTROL_ATTR);

    if ((routeCtrlVal != null) && routeCtrlVal.trim().equals(ROUTE_CONTROL_TRUE)) {
      routeControl = true;
    } else {
      routeControl = false;
    }

    Element fin_coa_cd = rootElement.getChild(CHART_CD_TAG);

    if (fin_coa_cd == null) {
      throw new WorkflowRuntimeException("<fin_coa_cd> tag missing. ");
    }

    String fin_coa_cd_value = fin_coa_cd.getAttributeValue(VALUE);

    if ((fin_coa_cd_value == null) || fin_coa_cd_value.trim().equals("")) {
      throw new WorkflowRuntimeException("<fin_coa_cd> value missing");
    }

    chart_cd_value = fin_coa_cd_value.trim();
  }

  /**
   * Returns the doc type key in the XML format.
   * @return String representing XML for key.
   */
  public String getSerializedForm() {
    XMLOutputter out = new XMLOutputter();

    return out.outputString(buildJdom());
  }

  /**
   * Construct the JDOM Element for this key
   * @return Element the JDOM element for this ChartCodeEOK object
   */
  public Element buildJdom() {
    Element root = new Element(CHART_CODE_EOK);

    if (this.routeControl) {
      root.setAttribute(ROUTE_CONTROL_ATTR, ROUTE_CONTROL_TRUE);
    }

    Element fin_coa_cd = new Element(CHART_CD_TAG);
    fin_coa_cd.setAttribute(VALUE, this.chart_cd_value);

    root.addContent(fin_coa_cd);

    return root;
  }

  //getters and setters
  public String getChart_cd_value() {
    return chart_cd_value;
  }

  public boolean isRouteControl() {
    return routeControl;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("[fin_coa_cd=");
    sb.append(chart_cd_value);
    sb.append("]");

    return sb.toString();
  }
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
