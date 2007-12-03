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

import java.io.Serializable;

import org.jdom.Element;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.services.InconsistentDocElementStateException;


/**
 * <p>Title: IDocElemnt </p>
 * <p>Description: Contract for all Element classes functionality </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version revision
 */
public interface IDocElement extends Serializable {
  /**
   * Get a String representation of the XML for this element.
   *
   * @return String of XML
   */
  public Element getXMLContent();

  /**
   * Loads itself from the document represented in the xml Document.  If
   * allowBlank is set to true and the element pertaining to Objects XML Element can't
   * be found nothing happens otherwise an InvalidXmlException is thrown
   *
   * @param element jdom Element holding content
   * @param allowBlank boolean
   * @throws InvalidXmlException
   */
  public void loadFromXMLContent(Element element, boolean allowBlank)
    throws InvalidXmlException, InconsistentDocElementStateException;

  /**
   * validate that the Object is in a correct state to route as part of a complete
   * document.  In this instance is the chart empty.  If so the Element is invalid.
   * If the element is valid return null
   *
   * @return DocElementError representing error(s)
   * @throws ResourceUnavailableException
   */
  public WorkflowServiceErrorImpl validate();

  /**
   * name of the xml element representing the content represented by the object.
   * Everybody got that?
   *
   * @return String name of element
   */
  public String getElementName();

  public boolean isEmpty();
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
