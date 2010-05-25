/*
 * Copyright 2007 The Kuali Foundation
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
 * This XML Filter is used to rename elements of concern in a pre-1.0.3 Group XML document
 * to their 1.0.3-compliant names. As an example:
 *
 *   -- Example from GroupXmlImportTest.xml as filtered by GroupNamespaceURIEliminationFilterPOC -- 
 *   <members>
 *       <principalName>ewestfal</principalName>
 *       <principalName>rkirkend</principalName>
 *       <principalId>2015</principalId>
 *   </members>
 *   
 *   -- Resulting transformation --
 *   <members>
 *       <memberId>ewestfal</memberId>
 *       <memberId>rkirkend</memberId>
 *       <memberId>2015</memberId>
 *   </members>
 *   
 * Note: This filter has methods for transforming the attributes of elements, but all they
 *       do is return "new AttributesImpl()". If it is necessary to transform attributes of
 *       pre-1.0.3 elements, we can build those methods out.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupNamespaceURITransformationFilterPOC extends XMLFilterImpl {

	// The URI of a Group 1.0.3 schema 
    public static final String GROUP_URI="http://rice.kuali.org/xsd/kim/group";
    
	// The Map containing element transformation values
	private Map<String,String> elementTransformationMap;
	
	// The List containing elements with attributes for transformation
	private List<String> elementAttributeTransformationList;
	
	// The list which helps keep track of where we are in the XML 
	// hierarchy as the stream is being processed
	private List<String> groupXmlStack = new ArrayList<String>();
    
	public GroupNamespaceURITransformationFilterPOC(){
		super();
		
		// Initialize the element transformation map
		setElementTransformationMap();

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
		
		// Transform elements of concern:
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

    public void endElement(String uri, String localName, String qName) throws SAXException {
		// Fetch the current element from the top of the stack
		String currentElement = groupXmlStack.get(groupXmlStack.size()-1);
	
		if (elementTransformationMap.containsKey(currentElement)){
			String transformedLocalName = elementTransformationMap.get(currentElement);
			String transformedQualifiedName = transformedLocalName;
			super.endElement(GROUP_URI, transformedLocalName, transformedQualifiedName);
		}
		else {
			// Pass other elements through as they are
			super.endElement(GROUP_URI, localName, qName);
		}
		
		// Pop the element from the stack if it's not empty
		if (!groupXmlStack.isEmpty()){
			groupXmlStack.remove(currentElement);			
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
		elementTransformationMap.put("group.name", "groupName");
		elementTransformationMap.put("group.namespace", "namespaceCode");
		elementTransformationMap.put("group.description", "groupDescription");
		elementTransformationMap.put("group.members.principalName", "memberId");
		elementTransformationMap.put("group.members.principalId", "memberId");
		this.elementTransformationMap = elementTransformationMap;
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