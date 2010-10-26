/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kew.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This XML Filter is used to eliminate unwanted elements from a pre-1.0.3 Group XML document.  
 * As an example:
 *
 *   -- Example from GroupXmlImportTest.xml as filtered by GroupNamespaceURIEliminationFilterPOC -- 
 *   <members>
 *       <principalName>ewestfal</principalName>
 *       <principalName>rkirkend</principalName>
 *       <principalId>2015</principalId>
 *       <groupName>
 *           <name>TestWorkgroup</name>
 *           <namespace>KR-WKFLW</namespace>
 *        </groupName>
 *   </members>
 *   
 *   -- Resulting transformation --
 *   <members>
 *       <principalName>ewestfal</principalName>
 *       <principalName>rkirkend</principalName>
 *       <principalId>2015</principalId>
 *   </members>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupNamespaceURIEliminationFilterPOC extends XMLFilterImpl {

	// The URI of a Group 1.0.3 schema 
    public static final String GROUP_URI="http://rice.kuali.org/xsd/kim/group";
   
	// The List containing elements which will be eliminated
	private List<String> elementEliminationList;

	// A flag to determine if the current element is in the elimination list
	private boolean eliminatedElement = false;
	
	// The list which helps keep track of where we are in the XML 
	// hierarchy as the stream is being processed
	private List<String> groupXmlStack = new ArrayList<String>();
    
	public GroupNamespaceURIEliminationFilterPOC(){
		super();
	
		// Initialize the element elimination list
		setElementEliminationList();
	}
	
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		// Push the element onto the stack
		if (groupXmlStack.isEmpty()){
			// Push the root element without onto the stack without special formatting
			groupXmlStack.add(localName);
		}
		else {
			// Push a child element by appending localName to the value of the top element in the stack
			groupXmlStack.add(groupXmlStack.get(groupXmlStack.size()-1) + "." + localName);
		}
		
		// Fetch the current element from the top of the stack
		String currentElement = groupXmlStack.get(groupXmlStack.size()-1);
		
		// Eliminate elements of concern:
		if (elementEliminationList.contains(currentElement)) {
			// Flag the current element for elimination
			eliminatedElement = true;
		}
		// Perform normal parsing
		else {
			eliminatedElement = false;
			super.startElement(GROUP_URI, localName, qName, atts);
		}
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
		// Fetch the current element from the top of the stack
		String currentElement = groupXmlStack.get(groupXmlStack.size()-1);
	
		// Eliminate elements of concern:
		if (elementEliminationList.contains(currentElement)) {
			// Flag the current element for elimination
			eliminatedElement = true;
		}
		// Perform normal parsing behavior
		else {
			eliminatedElement = false;
			super.endElement(GROUP_URI, localName, qName);
		}
		
		// Pop the element from the stack if it's not empty
		if (!groupXmlStack.isEmpty()){
			groupXmlStack.remove(currentElement);			
		}
    }
    
    /*
     * Override the characters() method using an empty implementation so that the text
     * inside of each eliminated element is also eliminated. 
     */
    @Override
    public void characters (char[] ch, int start, int length) throws SAXException {	
    	if (!eliminatedElement) {
    		super.characters(ch, start, length);
    	}
    }
	
	/*
	 * Build a List defining which elements we intend to eliminate .
	 * The values in this List are "hierarchically-qualified" representations of the elements of concern.
	 * 
	 * For example, if "group" is a child of "groups", which is in turn a child of the root
	 * element "data", then it is represented as "data.groups.group" in the Map.
	 */
	private void setElementEliminationList() {
		List<String> elementEliminationList = new ArrayList<String>();
		elementEliminationList.add("data");
		elementEliminationList.add("data.groups");
		elementEliminationList.add("data.groups.group.members.groupName");
		elementEliminationList.add("data.groups.group.members.groupName.name");
		elementEliminationList.add("data.groups.group.members.groupName.namespace");
		this.elementEliminationList = elementEliminationList;
	}
}
