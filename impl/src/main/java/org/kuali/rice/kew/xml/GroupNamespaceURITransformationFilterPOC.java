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

import org.kuali.rice.core.util.KeyValue;
import org.xml.sax.SAXException;

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
public class GroupNamespaceURITransformationFilterPOC extends AbstractTransformationFilter {

	// The URI of a Group 1.0.0 schema 
    public static final String GROUP_URI="ns:workflow/Group";    
    
	// The Map containing element transformation values
	private Map<String,String> elementTransformationMap;
    
	public GroupNamespaceURITransformationFilterPOC(){
		super();
		
		// Initialize the element transformation map
		setElementTransformationMap();		
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


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#getElementTransformationList()
	 */	
	public List<KeyValue> getElementTransformationList() {
		List<KeyValue> rList = new ArrayList<KeyValue>();

		rList.add(new KeyValue("group.name", GROUP_URI));
		rList.add(new KeyValue("group.namespace", GROUP_URI));
		rList.add(new KeyValue("group.description", GROUP_URI));
		rList.add(new KeyValue("group.members.principalName", GROUP_URI));
		rList.add(new KeyValue("group.members.principalId", GROUP_URI));		

		return rList;
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#getStartingElementPath()
	 */	
	public String getStartingElementPath() {
		
		return "data.groups";
	}

	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#transformStartElement(org.kuali.rice.kew.xml.AbstractTransformationFilter.CurrentElement)
	 */
	public CurrentElement transformStartElement(CurrentElement currentElement) throws SAXException{
		String transformedLocalName = elementTransformationMap.get(this.getTrimmedCurrentElementKey(currentElement.getNameKey()));
		String transformedQualifiedName = transformedLocalName;
		
		return new CurrentElement(currentElement.getNameKey(),currentElement.getUri(), transformedLocalName, transformedQualifiedName, currentElement.getAttributes());
	}
	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#transformEndElement(org.kuali.rice.kew.xml.AbstractTransformationFilter.CurrentElement)
	 */
	public CurrentElement transformEndElement(CurrentElement currentElement) throws SAXException {
		String transformedLocalName = elementTransformationMap.get(this.getTrimmedCurrentElementKey(currentElement.getNameKey()));
		String transformedQualifiedName = transformedLocalName;
								
		return new CurrentElement(currentElement.getNameKey(),currentElement.getUri(), transformedLocalName, transformedQualifiedName);
	}
	

	

}