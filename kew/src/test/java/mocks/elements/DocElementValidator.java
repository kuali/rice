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

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.services.InconsistentDocElementStateException;


/**
 * <p>Title: DocElementValidator </p>
 * <p>Description: Used to do simple validation on objects implementing
 * IDocElement interface.  To be used as a composite preventing the need
 * for repeated validation to be used in a superclass or individual implementations.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version 1.0
 */
public class DocElementValidator {
  private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocElementValidator.class);

  /**
   * Checks if loadFromXMLContent methods on standard IDocElement implementations
   * should return with no work done.  Also throws common exceptions.
   *
   * @param docElement the doc element to be loaded
   * @param element the element with the content containing the object's value(s)
   * @param allowBlank flags whether the object should throw an exception or
   *      continue running if the element passed in is blank
   * @return boolean notifying the calling IDocElement to return with no work done.
   *      This would be allowBlanks is true and element is blank (null)
   * @throws InvalidXmlException throws if the passed in element is not the correct
   *      element the object loads from
   * @throws InconsistentDocElementStateException throws if blanks should not be
   *      allowed and the element passed in is blank (null)
   */
  public static boolean returnWithNoWorkDone(IDocElement docElement, Element element,
    boolean allowBlank) throws InvalidXmlException, InconsistentDocElementStateException {
    LOG.debug("returnWithNoWorkDone");

    return isElementPresent(docElement.getElementName(), element, allowBlank);
  }

  /**
   * Checks if loadFromXMLContent methods on standard IDocElement implementations
   * should return with no work done.  Also throws common exceptions.
   *
   * @param docElement the doc element to be loaded
   * @param element the element with the content containing the object's value(s)
   * @param allowBlank flags whether the object should throw an exception or
   *      continue running if the element passed in is blank
   * @param elementName name the element containing the IDocElement values should
   *      have
   * @return boolean notifying the calling IDocElement to return with no work done.
   *      This would be allowBlanks is true and element is blank (null)
   * @throws InvalidXmlException throws if the passed in element is not the correct
   *      element the object loads from
   * @throws InconsistentDocElementStateException throws if blanks should not be
   *      allowed and the element passed in is blank (null)
   */
  public static boolean isElementPresent(String elementName, Element element, boolean allowBlank)
    throws InvalidXmlException, InconsistentDocElementStateException {
    LOG.debug("returnWithNoWorkDone elementName = " + elementName);

    if (element == null) {
      LOG.debug("Element is null.");

      if (allowBlank) {
        LOG.debug("Allowing blank returning true.");

        return true;
      } else {
        LOG.debug("Not allowing blank throwing InconsistentDocElementStateException");
        throw new InconsistentDocElementStateException("Null Element passed " +
          "in and allowBlank set false");
      }
    }

    if (!elementName.equals(element.getName())) {
      LOG.debug("Element name and class' element name are inconsistent " +
        "throwing InvalidXmlException");
      throw new InvalidXmlException("Element is of the wrong type");
    }

    LOG.debug("everything good, returning false");

    return false;
  }
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
