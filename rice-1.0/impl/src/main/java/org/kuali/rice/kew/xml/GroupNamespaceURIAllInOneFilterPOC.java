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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This XML Filter is used to upgrade pre-1.0.3 Group XML document to 1.0.3 document.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupNamespaceURIAllInOneFilterPOC extends XMLFilterImpl {

	// The URI of a Group 1.0.3 schema 
    public static final String GROUP_URI="http://rice.kuali.org/xsd/kim/group";
    
	// The Map containing element transformation values
	private Map<String,String> elementTransformationMap;

	// The List containing elements which will be eliminated
	private List<String> elementEliminationList;

	// A flag to determine if the current element is in the elimination list
	private boolean eliminatedElement = false;
	
	// The List containing elements with attributes for transformation
	private List<String> elementAttributeTransformationList;
	
	// The list which helps keep track of where we are in the XML 
	// hierarchy as the stream is being processed
	private List<String> groupXmlStack = new ArrayList<String>();
    
	public GroupNamespaceURIAllInOneFilterPOC(){
		super();
		// Initialize the element transformation map
		setElementTransformationMap();
		// Initialize the element elimination list
		setElementEliminationList();
		// Initialize the element attribute transformation list
		setElementAttributeTransformationList();
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
			// Transform <principalName> and <principalId> sections
			if (currentElement.equals("data.groups.group.members.principalName") || currentElement.equals("data.groups.group.members.principalId")){
				super.startElement(GROUP_URI, "member", "member", new AttributesImpl());
			}
			                                                                                          
			// Transform elements of concern
			if (elementTransformationMap.containsKey(currentElement)){
				String transformedLocalName = elementTransformationMap.get(currentElement);
				String transformedQualifiedName = transformedLocalName;
				super.startElement(GROUP_URI, transformedLocalName, transformedQualifiedName, getTransformedAttributes(currentElement, atts));
			}
			else {
				// Pass other elements through as they are
				super.startElement(GROUP_URI, localName, qName, atts);
			}
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
			// Transform elements of concern
			if (elementTransformationMap.containsKey(currentElement)){
				String transformedLocalName = elementTransformationMap.get(currentElement);
				String transformedQualifiedName = transformedLocalName;
				super.endElement(GROUP_URI, transformedLocalName, transformedQualifiedName);
			}
			else {
				// Pass other elements through as they are
				super.endElement(GROUP_URI, localName, qName);
			}
		
			// Transform <principalName> and <principalId> sections
			if (currentElement.equals("data.groups.group.members.principalName") || currentElement.equals("data.groups.group.members.principalId")){
			    super.startElement(GROUP_URI, "memberTypeCode", "memberTypeCode", new AttributesImpl());
		            String memberTypeCode = "P";
		            characters(memberTypeCode.toCharArray(), 0, 1);
		            super.endElement(GROUP_URI, "memberTypeCode", "memberTypeCode");
				super.endElement(GROUP_URI, "member", "member");
			}
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
	 * Build a Map that maps elements we intend to transform to their corresponding transformed value.
	 * The keys in this Map are "hierarchically-qualified" representations of the elements of concern.
	 * 
	 * For example, if "group" is a child of "groups", which is in turn a child of the root
	 * element "data", then it is represented as "data.groups.group" in the Map.
	 */
	private void setElementTransformationMap(){
		Map<String,String> elementTransformationMap = new HashMap<String,String>();
		elementTransformationMap.put("data.groups.group", "group");
		elementTransformationMap.put("data.groups.group.name", "groupName");
		elementTransformationMap.put("data.groups.group.description", "groupDescription");
		elementTransformationMap.put("data.groups.group.namespace", "namespaceCode");
		elementTransformationMap.put("data.groups.group.members.principalName", "memberId");
		elementTransformationMap.put("data.groups.group.members.principalId", "memberId");
		this.elementTransformationMap = elementTransformationMap;
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
	
	/*
	 * Placeholder: Build a List defining which elements have attributes that we intend to transform
	 */
	private void setElementAttributeTransformationList() {
		List<String> elementAttributeTransformationList = new ArrayList<String>();
		this.elementAttributeTransformationList = elementAttributeTransformationList;
	}
	
	/*
	 * Placeholder method adding support for transforming an element's attributes
	 */
	private Attributes getTransformedAttributes(String stackRepresentedElement, Attributes attributes){
		// If the element is found in the Element Attribute Transformation List, transform its appropriate attributes
		if (elementAttributeTransformationList.contains(stackRepresentedElement)){
			// Just a placeholder, remember?
			return new AttributesImpl();
		}
		else {
			// Otherwise, return a "hollow" Attributes object
			return new AttributesImpl();
		}		
	}
}
