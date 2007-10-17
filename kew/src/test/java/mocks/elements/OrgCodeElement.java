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
 * <p>Title: OrgCodeElement </p>
 * <p>Description: Business respresentation of a OrgCode both in XML and \
 * business rules.  <br><BR>
 *  See IDocElement documentation for
 * further explanation.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version 1.0
 */
public class OrgCodeElement implements IDocElement {
  /**
	 *
	 */
	private static final long serialVersionUID = -6506350696429732753L;
private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrgCodeElement.class);
  private static final String ATTRIBUTE_NAME = "value";
  private boolean routeControl;
  private String orgCode;

  public OrgCodeElement() {
    LOG.debug("constructing . . .");
    this.routeControl = false;
  }

  public Element getXMLContent() {
    LOG.debug("getXMLContent");

    //no eok representation of this fine grain of an element so if org is null
    // or empty return an empty string
    if (this.isEmpty()) {
      LOG.debug("empty returning empty String");

      return null;
    }

    //make the element
    Element me = new Element(OrgCodeEOK.ORG_CD_TAG);
    me.setAttribute(ATTRIBUTE_NAME, orgCode);

    LOG.debug("returning XMLContent = " + me.toString());

    return me;
  }

  public void loadFromXMLContent(Element element, boolean allowBlank)
    throws InvalidXmlException, InconsistentDocElementStateException {
    LOG.debug("loadFromXMLContent allowBlank = " + allowBlank);

    if (DocElementValidator.returnWithNoWorkDone(this, element, allowBlank)) {
      return;
    }

    this.orgCode = element.getAttributeValue(ATTRIBUTE_NAME);
    LOG.debug("element found setting value");
  }

  public WorkflowServiceErrorImpl validate() {
    LOG.debug("validate");

    if (this.isEmpty()) {
      LOG.debug("invalid return DocElementError");

      return new WorkflowServiceErrorImpl("Org Code blank", ServiceErrorConstants.ORG_BLANK);
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

    if ((this.orgCode == null) || this.orgCode.trim().equals("")) {
      LOG.debug("empty");

      return true;
    }

    LOG.debug("not empty");

    return false;
  }

  /**
   *
   * @param orgCode An Org Code
   */
  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }

  /**
   *
   * @return String or an Org Code
   */
  public String getOrgCode() {
    return this.orgCode;
  }

  public String getElementName() {
    return OrgCodeEOK.ORG_CD_TAG;
  }

  public void setRouteControl(boolean routeControl) {
    this.routeControl = false;
  }

  public boolean isRouteControl() {
    return this.routeControl;
  }
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
