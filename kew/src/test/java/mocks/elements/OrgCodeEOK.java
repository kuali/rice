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


/**
 * OrgCDEOK is the key class for retrieving|building the org_cd element in an xml document
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: UIS - Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version 1.0
 */
public class OrgCodeEOK {
  public final static String ORG_CODE_EOK = "university_organization_eok";
  public final static String ORG_CD_TAG = "org_cd";
  public final static String VALUE = "value";
  private final String ROUTE_CONTROL_ATTR = "route-control";
  private final String ROUTE_CONTROL_TRUE = "yes";

  /** Indicates if the key is being used as a route control - true by default*/
  private boolean routeControl = true;

  //default of org_cd value
  public String orgCd = "";

  //default chart_of_accounts_eok object
  public ChartCodeEOK charteok;

  //constructors
  //create an org_cd_eok with org_cd and route-control,
  //and a charteok
  public OrgCodeEOK(String orgCd, ChartCodeEOK charteok, boolean isRouteControl)
    throws Exception {
    if ((orgCd == null) || (charteok == null) || orgCd.equals("")) {
      throw new Exception("Empty values for keys not allowed");
    }

    this.orgCd = orgCd;
    this.routeControl = isRouteControl;
    this.charteok = charteok;
  }

  public OrgCodeEOK(String orgCd, String chartCd, boolean isRouteControl)
    throws Exception {
    this(orgCd, new ChartCodeEOK(chartCd, isRouteControl), isRouteControl);
  }

  /**
   * Construct an OrgCDEOK object from a JDOM element
   * @param keyElement a JDOM element
   */
  public OrgCodeEOK(Element rootElement) throws Exception {
    // check if root element is valid
    if (!rootElement.getName().equals(OrgCodeEOK.ORG_CODE_EOK)) {
      throw new Exception("Invalid OrgCodeEOK element " + rootElement.getName());
    }

    // set the route-control for this eok
    String routeCtrlVal = rootElement.getAttributeValue(this.ROUTE_CONTROL_ATTR);

    if ((routeCtrlVal != null) && routeCtrlVal.trim().equalsIgnoreCase(this.ROUTE_CONTROL_TRUE)) {
      this.routeControl = true;
    } else {
      this.routeControl = false;
    }

    //get children elements
    // org_cd tag
    Element orgCd = rootElement.getChild(OrgCodeEOK.ORG_CD_TAG);

    if (orgCd == null) {
      throw new Exception("Missing <org_cd> tag");
    }

    String orgVal = orgCd.getAttributeValue(VALUE);

    if ((orgVal == null) || orgVal.trim().equals("")) {
      throw new Exception("Invalid org_cd element -- missing value for org_cd");
    }

    this.orgCd = orgVal;

    this.charteok = new ChartCodeEOK(rootElement.getChild(ChartCodeEOK.CHART_CODE_EOK));
  }

  /**
   * Construct the JDOM Element for this key
   * @return Element the JDOM element for this OrgCodeEOK
   */
  public Element buildJdom() {
    //build root element
    Element keyJDOM = new Element(ORG_CODE_EOK);

    //set route-control for root element
    if (this.routeControl) {
      keyJDOM.setAttribute(this.ROUTE_CONTROL_ATTR, ROUTE_CONTROL_TRUE);
    }

    //build the org_cd tag
    Element org_cd = new Element(ORG_CD_TAG);
    org_cd.setAttribute(VALUE, this.orgCd);
    keyJDOM.addContent(org_cd);
    keyJDOM.addContent(charteok.buildJdom());

    return keyJDOM;
  }

  public String getSerializedForm() {
    XMLOutputter out = new XMLOutputter();

    return out.outputString(buildJdom());
  }

  //getters
  public String getOrg_cd_value() {
    return orgCd;
  }

  public ChartCodeEOK getCharteok() {
    return charteok;
  }

  public boolean isRouteControl() {
    return this.routeControl;
  }

  public String getChartCdValue() {
    return this.charteok.getChart_cd_value();
  }

  /**
   * Create a printable representation of this object.  Do not count
   * on the format of this string being the same in future versions!
   *
   * @return a string representation of this object
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("[fin_coa_cd=");
    sb.append(charteok.getChart_cd_value());
    sb.append(",org_cd=");
    sb.append(orgCd);
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
