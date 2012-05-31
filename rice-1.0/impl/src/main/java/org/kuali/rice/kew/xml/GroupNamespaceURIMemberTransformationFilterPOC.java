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

import org.kuali.rice.kim.xml.KimNamespaceConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This XML Filter is used to transform the <member> section generated in GroupNamespaceURIMemberTransformationFilterPOC.
 * such that the transformation from the pre-filtered pre-1.0.3 XML document looks like this:
 * 
 *   -- Example from GroupXmlImportTest.xml as filtered by GroupNamespaceURIMemberTransformationFilterPOC -- 
 *   <members>
 *       <memberId>ewestfal</memberId>
 *       <memberId>rkirkend</memberId>
 *       <memberId>2015</memberId>
 *   </members>
 *   
 *   -- Resulting transformation --
 *   <members>
 *       <member>
 *           <memberId>ewestfal</memberId>
 *   		 <memberTypeCode>P</memberTypeCode>
 *       </member>
 *       <member>
 *           <memberId>rkirkend</memberId>
 *    		<memberTypeCode>P</memberTypeCode>
 *   	 </member>
 *   	<member>
 *   		<memberId>2015</memberId>
 *   		<memberTypeCode>P</memberTypeCode>
 *      </member>
 *   </members>
 * 
 * Note 1: This filter assumes that <memberTypeCode> should be defaulted to "P" for all members. 
 *         
 * Note 2: As is the case with GroupNamespaceURIMemberTransformationFilterPOC, this filter has methods for transforming 
 *         the attributes of elements, but all they do is return "new AttributesImpl()". If it is necessary to transform 
 *         attributes of pre-1.0.3 elements, we can build those methods out. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupNamespaceURIMemberTransformationFilterPOC extends XMLFilterImpl {

	// The URI of a Group 1.0.3 schema     
    public static final String GROUP_URI_OLD = "ns:workflow/Group";
	public static final String GROUP_URI_NEW = KimNamespaceConstants.GROUP;
    
	public GroupNamespaceURIMemberTransformationFilterPOC(){
		super();
	}
	
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	if (localName.equals("memberId") && GROUP_URI_OLD.equals(uri)){
    		super.startElement(GROUP_URI_NEW, "member", "member", new AttributesImpl());
    			super.startElement(GROUP_URI_NEW, localName, qName, atts);
    	}
    	else {
    		// Pass all elements through as they are
    		super.startElement(uri, localName, qName, atts);
    	}
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
		// Append a <memberTypeCode> element to each <memberId> element
		if (localName.equals("memberId") && GROUP_URI_OLD.equals(uri)){
				super.endElement(GROUP_URI_NEW, "memberId", "memberId");
				super.startElement(GROUP_URI_NEW, "memberTypeCode", "memberTypeCode", new AttributesImpl());
		            String memberTypeCode = "P";
		            characters(memberTypeCode.toCharArray(), 0, 1);
		        super.endElement(GROUP_URI_NEW, "memberTypeCode", "memberTypeCode");
	        super.endElement(GROUP_URI_NEW, "member", "member");
		}
		else {
			// Pass other elements through as they are
			super.endElement(uri, localName, qName);
		}
    }
}
