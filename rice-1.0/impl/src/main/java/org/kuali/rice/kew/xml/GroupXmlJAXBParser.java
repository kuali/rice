/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.xml.CoreNamespaceConstants;
import org.kuali.rice.core.xml.dto.DataXmlDto;
import org.kuali.rice.core.xml.schema.RiceSchemaValidationEventCollector;
import org.kuali.rice.core.xml.schema.RiceXmlSchemaFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * Parses groups from XML using JAXB.
 *
 * @see KimGroups
 *
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 *
 */
public class GroupXmlJAXBParser implements XmlConstants {
    private static final Logger LOG = Logger.getLogger(GroupXmlJAXBParser.class);
    private static final String DEFAULT_GROUP_DESCRIPTION = "";
    private static final String DEFAULT_GROUP_SCHEMA_NAME = "Groups-1.0.3.xsd";
    
	public DataXmlDto parse(InputStream in) throws IOException {
        DataXmlDto groupsXmlDto = new DataXmlDto();
		JAXBContext jaxbContext;
		Unmarshaller unmarshaller;

		try {
			jaxbContext = JAXBContext.newInstance(DataXmlDto.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		}catch (Exception ex) {
			throw new RuntimeException("Error creating JAXB unmarshaller", ex);
		}

		if (in == null) {
			LOG.warn("###############################");
			LOG.warn("#");
			LOG.warn("# XML Import input stream not found!");
			LOG.warn("#");
			LOG.warn("###############################");
		} else {
			try {

				groupsXmlDto = unmarshal(unmarshaller, in);

			} catch (Exception ex) {
				LOG.error(ex.getMessage());
				throw new RuntimeException("Error parsing XML input stream", ex);
			}

		}
		return groupsXmlDto;
	}

	/**
	 * 
	 * This method returns a list of xmlfilters.  The order here matters. The fist element gets processed 
	 * first. FIFO.
	 * 
	 * @return
	 */
	public List<XMLFilter> getXMLFilterList(){
	
		List<XMLFilter> lRet = new ArrayList<XMLFilter>();
		
		lRet.add(new DataNamespaceURIFilter());		
		lRet.add(new GroupNamespaceURITransformationFilterPOC());
		lRet.add(new GroupNamespaceURIMemberTransformationFilterPOC());
		lRet.add(new GroupNamespaceURIFilter());
		
		return lRet;
	}
	
	/**
	 * 
	 * This method takes in a list of xml filters and appends them together via
	 * parent child relationships.  The end result is one xml filter that can be applied to the parse.
	 * 
	 * @param filters
	 * @return
	 * @throws Exception
	 */
	public XMLFilter getXMLFilter(List<XMLFilter> filters) throws Exception{
		 SAXParserFactory spf = SAXParserFactory.newInstance();
	     spf.setNamespaceAware(true);
	     XMLFilter previous = null;
	     XMLFilter current = null;
	     
	     for(int i=0; i< filters.size();i++)
	     {	    	 
	    	 if(i==0){
	    		 previous = filters.get(i);
	    		 previous.setParent(spf.newSAXParser().getXMLReader());
	    	 }else{
	    		 current = filters.get(i);
	    		 current.setParent(previous);
	    		 previous = current;
	    	 }
	     }
	     return current;
	}
	
	protected DataXmlDto unmarshal(Unmarshaller unmarshaller, InputStream in) throws Exception {       

        UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
        Schema groupSchema = RiceXmlSchemaFactory.getSchema(DEFAULT_GROUP_SCHEMA_NAME);
        unmarshaller.setSchema(groupSchema);
        ValidationEventCollector vec = new RiceSchemaValidationEventCollector();
        unmarshaller.setEventHandler(vec);        
        
        XMLFilter filter = this.getXMLFilter(this.getXMLFilterList());
        filter.setContentHandler(handler);
       
        filter.parse(new InputSource(in));
        

        return (DataXmlDto)handler.getResult();
   }
	
//    protected GroupXmlDto unmarshalNew(Unmarshaller unmarshaller, InputStream in) throws Exception {
//        SAXParserFactory spf = SAXParserFactory.newInstance();
//        spf.setNamespaceAware(true);
//
//        XMLFilter filter = new TestGroupNamespaceURIFilter();
//        filter.setParent(spf.newSAXParser().getXMLReader());
//
//        unmarshaller.setListener(new Unmarshaller.Listener() {
//			
//			public void afterUnmarshal(Object target, Object parent) {
//				GroupXmlDto gxd = (GroupXmlDto)target;
//				//super.afterUnmarshal(target, parent);
//			}
//		});
//        
//        
//        UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
//        filter.setContentHandler(handler);
//
//        filter.parse(new InputSource(in));
//
//        return (GroupXmlDto)handler.getResult();
//    }
//
//    protected GroupInfo generateGroupInfo(GroupXmlDto groupDto){
//    	IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
//    	
//    	List<String> errors = new ArrayList<String>();
//    	GroupInfo gi = new GroupInfo();
//    	List<KimTypeAttributeInfo> typeAttributes = null;
//    	
//    	if(isBlank(groupDto.getNamespaceCode())){
//    		errors.add("Namespace must have a value.");
//    	}else{
//    		gi.setNamespaceCode(groupDto.getNamespaceCode());
//    	}
//    	
//    	if(groupDto.getKimType() != null){
//    		KimTypeInfo kimTypeInfo = null;
//    		if(!isBlank(groupDto.getKimType().getKimTypeId())){
//    			 kimTypeInfo = KIMServiceLocator.getTypeInfoService().getKimType(groupDto.getKimType().getKimTypeId());
//    		}
//    		if(!isBlank(groupDto.getKimType().getName()) && !isBlank(groupDto.getKimType().getNamespaceCode())){
//    			kimTypeInfo = KIMServiceLocator.getTypeInfoService().getKimTypeByName(groupDto.getKimType().getNamespaceCode(), groupDto.getKimType().getName());
//    		}
//    		
//    		if (kimTypeInfo == null) {
//            	errors.add("Invalid typeId or type name and namespace specified.");
//            } else  {
//            	gi.setKimTypeId(kimTypeInfo.getKimTypeId());
//            	typeAttributes = kimTypeInfo.getAttributeDefinitions();
//            }            
//    	} else{
//    		KimTypeInfo kimTypeDefault = KIMServiceLocator.getTypeInfoService().getKimTypeByName(KimConstants.KIM_TYPE_DEFAULT_NAMESPACE, KimConstants.KIM_TYPE_DEFAULT_NAME);
//            if (kimTypeDefault != null) {
//            	gi.setKimTypeId(kimTypeDefault.getKimTypeId());
//            	typeAttributes = kimTypeDefault.getAttributeDefinitions();
//            } else {
//            	errors.add("Failed to locate the 'Default' group type!  Please ensure that it's in your database.");
//            }
//    	}
//    	
//    	//Active Indicator
//        gi.setActive(groupDto.isActive());
//        
//      //Get list of attribute keys
//        List<String> validAttributeKeys = new ArrayList<String>();
//        for (KimTypeAttributeInfo attribute : typeAttributes) {
//            validAttributeKeys.add(attribute.getAttributeName());
//        }
//        //Group attributes
//        if (groupDto.getAttributes() != null) {
//        	AttributeSet attributeSet = new AttributeSet();
//            for (String key : groupDto.getAttributes().keySet() ) {
//                String value = groupDto.getAttributes().get(key);
//                attributeSet.put(key, value);
//                if (!validAttributeKeys.contains(key)) {
//                    errors.add("Invalid attribute specified.");
//                }
//            }
//            if (attributeSet.size() > 0) {
//                gi.setAttributes(attributeSet);
//            }
//        }
//        
//        // Group Members
//        for(GroupMembershipXmlDto groupMember : groupDto.getMembers()){
//        	
//        	GroupMembershipInfo gmi = new GroupMembershipInfo(groupDto.getGroupId(), null, null, null, null, null);
//        	if(!isBlank(groupMember.getMemberId())){
//        		KimPrincipal principal = identityManagementService.getPrincipal(groupMember.getMemberId());
//        		gmi.setMemberId(principal.getPrincipalId());
//        		gmi.setMemberTypeCode(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
//        	}
//        	if(!isBlank(groupMember.getMemberName())){
//        		KimPrincipal principal = identityManagementService.getPrincipalByPrincipalName(groupMember.getMemberName());
//        		gmi.setMemberId(principal.getPrincipalId());
//        		gmi.setMemberTypeCode(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
//        	}
//        	if(groupMember.getGroupName() != null){
//        		Group group = identityManagementService.getGroupByName(groupMember.getGroupName().getNamespaceCode(),groupMember.getGroupName().getName());
//        		gmi.setMemberId(group.getGroupId());
//        		gmi.setMemberTypeCode(KimGroupMemberTypes.GROUP_MEMBER_TYPE);
//        	}
//        	if(groupMember.getGroupName() != null){
//        		Group group = identityManagementService.getGroupByName(groupMember.getGroupName().getNamespaceCode(),groupMember.getGroupName().getName());
//        		gmi.setMemberId(group.getGroupId());
//        		gmi.setMemberTypeCode(KimGroupMemberTypes.GROUP_MEMBER_TYPE);
//        	}
//        	
//        	/**
//            if (principal != null) {
//                addPrincipalToGroup(groupInfo.getNamespaceCode(), groupInfo.getGroupName(), principal.getPrincipalId());
//            } else {
//                throw new InvalidXmlException("Principal Name "+principalName+" cannot be found.");
//            }
//            **/
//    
//        }
//    	
//    	return gi;
//    }
// 
    private boolean isBlank(Object o){
    	return (o == null || "".equals(o));
    }
        
    
    public class DataNamespaceURIFilter extends XMLFilterImpl {

        public static final String DATA_URI=CoreNamespaceConstants.CORE;
        
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if("data".equals(localName)) {
                uri = DATA_URI;
            }
            
            super.startElement(uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = DATA_URI;
            }
            
            super.endElement(uri, localName, qName);
        }
    }

}
