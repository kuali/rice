/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.kuali.workflow.workgroup.BaseWorkgroupExtension;
import org.kuali.workflow.workgroup.BaseWorkgroupExtensionData;
import org.kuali.workflow.workgroup.WorkgroupType;
import org.kuali.workflow.workgroup.WorkgroupTypeAttribute;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.workgroup.BaseWorkgroup;
import edu.iu.uis.eden.workgroup.BaseWorkgroupMember;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Parses {@link Workgroup}s from XML.
 *
 * @see Workgroup
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupXmlHandler implements XmlConstants, WorkgroupXmlConstants {

    private static final Logger LOG = Logger.getLogger(WorkgroupXmlHandler.class);

    private List workgroups = new ArrayList();

    public List parseWorkgroupEntries(InputStream file) throws JDOMException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
        org.w3c.dom.Document w3cDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

        Document document = new DOMBuilder().build(w3cDocument);
        Element root = document.getRootElement();

        Map<String, BaseWorkgroup> workgroupMap = new HashMap<String, BaseWorkgroup>();


        try{
            for (Iterator workgroupsIt = root.getChildren(WORKGROUPS, WORKGROUP_NAMESPACE).iterator(); workgroupsIt.hasNext();) {
                    Element workgroupsElement = (Element) workgroupsIt.next();
	        for (Iterator workgroupIter = workgroupsElement.getChildren(WORKGROUP, WORKGROUP_NAMESPACE).iterator(); workgroupIter.hasNext();) {
	            Element workgroupElement = (Element) workgroupIter.next();

	            String workgroupName = workgroupElement.getChildTextTrim(WORKGROUP_NAME, WORKGROUP_NAMESPACE);
	            if (StringUtils.isEmpty(workgroupName)) {
	            	throw new RuntimeException(WORKGROUP_NAME + " must be non-empty.");
	            }
	            boolean allowOverwrite = false;
	            String allowOverwriteValue = workgroupElement.getAttributeValue("allowOverwrite");
	            if (!StringUtils.isBlank(allowOverwriteValue)) {
	            	allowOverwrite = Boolean.valueOf(allowOverwriteValue);
	            }
	            BaseWorkgroup workgroup = (BaseWorkgroup)getWorkgroupService().getBlankWorkgroup();
	            Workgroup existingWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(workgroupName));
	            if (existingWorkgroup != null) {
	            	if (!allowOverwrite) {
	            		throw new WorkflowRuntimeException("Workgroup '"+workgroupName+"' already exists!  Cannot import over an existing workgroup without the 'allowOverwrite' attribute set.");
	            	}
	            	workgroup.setWorkgroupId(existingWorkgroup.getWorkflowGroupId().getGroupId());
	            	workgroup.setWorkflowGroupId(existingWorkgroup.getWorkflowGroupId());
	            } else {
		            workgroup = (BaseWorkgroup)getWorkgroupService().getBlankWorkgroup();
	            }

	            workgroup.setDocumentId(new Long(-1));
	            workgroup.setActiveInd(Boolean.TRUE);
	            workgroup.setGroupNameId(new GroupNameId(workgroupName));
	            workgroup.setDescription(workgroupElement.getChildTextTrim(DESCRIPTION, WORKGROUP_NAMESPACE));
	            String type = workgroupElement.getChildTextTrim(WORKGROUP_TYPE, WORKGROUP_NAMESPACE);
	            WorkgroupType workgroupType = null;
	            // if there are not using the default workgroup type
	            if (!(StringUtils.isEmpty(type) || type.equals(EdenConstants.LEGACY_DEFAULT_WORKGROUP_TYPE))) {
	            	workgroup.setWorkgroupType(type);
		            workgroupType = KEWServiceLocator.getWorkgroupTypeService().findByName(type);
		            if (workgroupType == null) {
		            	throw new InvalidXmlException("Could not locate Workgroup Type with the given name '" + type + "'");
		            }
	            }
	            workgroup.setCurrentInd(Boolean.TRUE);
                workgroup.setActiveInd(Boolean.TRUE);
	            String activeInd = workgroupElement.getChildTextTrim(ACTIVE_IND, WORKGROUP_NAMESPACE);
                if (activeInd != null) {
                    workgroup.setActiveInd(new Boolean(activeInd));
                }

                Element extsElement = workgroupElement.getChild(EXTENSIONS, WORKGROUP_NAMESPACE);
                if (extsElement != null) {
                	if (workgroupType == null) {
                		throw new InvalidXmlException("Extensions were defined on a workgorup without a proper Workgroup Type defined.");
                	}
                	for (Iterator extsIter = extsElement.getChildren().iterator(); extsIter.hasNext();) {
                		BaseWorkgroupExtension workgroupExtension = new BaseWorkgroupExtension();
                		Element extElement = (Element) extsIter.next();
                		String attributeName = extElement.getAttributeValue(ATTRIBUTE);
                		if (StringUtils.isBlank(attributeName)) {
                			throw new InvalidXmlException("An attribute was not defined on the workgroup extension for workgroup '" + workgroupName + "'");
                		}
                		RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attributeName);
                		if (ruleAttribute == null) {
                			throw new InvalidXmlException("Could not find attribute with the given name '" + attributeName + "'");
                		}
                		boolean attributeFound = false;
                		for (WorkgroupTypeAttribute workgroupTypeAttribute : workgroupType.getActiveAttributes()) {
                			if(workgroupTypeAttribute.getAttribute().getRuleAttributeId().equals(ruleAttribute.getRuleAttributeId())){
                				workgroupExtension.setWorkgroupTypeAttribute(workgroupTypeAttribute);
                                attributeFound = true;
                				break;
                			}
                		}
                        if (!attributeFound) {
                            throw new InvalidXmlException("Attribute '" + attributeName + "' not found on Workgroup Type '" + workgroupType.getName() + "'");
                        }
                		workgroupExtension.setWorkgroup(workgroup);

                		List dataElements = extElement.getChildren(DATA, WORKGROUP_NAMESPACE);
                		if (dataElements == null || dataElements.isEmpty()) {
                			throw new InvalidXmlException("No extension data defined for extension '" + attributeName + "' on workgroup '" + workgroupName +"'");
                		}
                		for (Iterator dataIt = dataElements.iterator(); dataIt.hasNext();) {
							Element dataElement = (Element) dataIt.next();
							String key = dataElement.getAttributeValue(KEY);
							if (StringUtils.isBlank(key)) {
								throw new InvalidXmlException("Found an empty or non-existent key on extension data for workgroup '" + workgroupName + "'");
							}
							String value = dataElement.getValue();
							BaseWorkgroupExtensionData data = new BaseWorkgroupExtensionData();
							data.setKey(key);
							data.setValue(value);
							data.setWorkgroupExtension(workgroupExtension);
							workgroupExtension.getData().add(data);
						}
                		workgroup.getExtensions().add(workgroupExtension);
                	}
                }

	            for(Iterator memberIter = workgroupElement.getChild(MEMBERS, WORKGROUP_NAMESPACE).getChildren().iterator(); memberIter.hasNext(); ){
	                Element memberElement = (Element) memberIter.next();
	                BaseWorkgroupMember member = new BaseWorkgroupMember();
	                WorkflowUser workflowUser = null;
	                Workgroup nestedWorkgroup = null;
	                if(memberElement.getName().equals(WORKFLOW_ID)){
	                    workflowUser = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(memberElement.getText()));
	                } else if (memberElement.getName().equals(AUTHENTICATION_ID)) {
	                    workflowUser = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(memberElement.getText()));
	                } else if (memberElement.getName().equals(UU_ID)) {
	                    workflowUser = KEWServiceLocator.getUserService().getWorkflowUser(new UuId(memberElement.getText()));
	                } else if (memberElement.getName().equals(EMPL_ID)){
	                    workflowUser = KEWServiceLocator.getUserService().getWorkflowUser(new EmplId(memberElement.getText()));
	                } else if (memberElement.getName().equals(WORKGROUP_NAME)) {
	                	if (workgroupMap.containsKey(memberElement.getText())) {
	                		nestedWorkgroup = workgroupMap.get(memberElement.getText());
	                	} else {
	                		nestedWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(memberElement.getText()));
	                	}
	                } else {
	                    LOG.error("Unknown member element: " + memberElement.getName());
                    }
	                if (workflowUser != null) {
	                    member.setWorkflowId(workflowUser.getWorkflowId());
	                    member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
	                } else if (nestedWorkgroup != null) {
	                    member.setWorkflowId(nestedWorkgroup.getWorkflowGroupId().getGroupId().toString());
	                    member.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
	                } else  {
	                    throw new Exception("A workflow user or nested workgroup cannot be found for "+memberElement.getName()+"="+memberElement.getText()+" on Workgroup " + workgroup.getWorkgroupName());
	                }
	                workgroup.getMembers().add(workflowUser);
	                member.setWorkgroup(workgroup);
	                member.setWorkgroupVersionNumber(new Integer(0));
	              	workgroup.getWorkgroupMembers().add(member);
	            }
	            // need to save now in case this group is used as a nested group in any of the others in this xml document
	            LOG.info("Versioning and saving workgroup '" + workgroup.getDisplayName() + "'");
                KEWServiceLocator.getWorkgroupRoutingService().versionAndSave(workgroup);
	            workgroupMap.put(workgroup.getGroupNameId().getNameId(), workgroup);
	            workgroups.add(workgroup);
	        }
            }
        } catch (Exception e) {
          LOG.error("Error parsing file", e);
          throw new RuntimeException("Error parsing workgroup xml data set.", e);
        }

        return workgroups;
    }

    public List getWorkgroups() {
        return workgroups;
    }
    public void setWorkgroups(List workgroups) {
        this.workgroups = workgroups;
    }

    private WorkgroupService getWorkgroupService() {
    	return KEWServiceLocator.getWorkgroupService();
    }


}
