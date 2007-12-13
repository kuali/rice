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
package edu.iu.uis.eden.xml.export;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.kuali.workflow.attribute.Extension;
import org.kuali.workflow.attribute.ExtensionData;

import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.xml.WorkgroupXmlConstants;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports {@link Workgroup}s to XML.
 *
 * @see Workgroup
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupXmlExporter implements XmlExporter, XmlConstants, WorkgroupXmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    private ExportRenderer renderer = new ExportRenderer(WORKGROUP_NAMESPACE);

    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getWorkgroups().isEmpty()) {
            Element rootElement = renderer.renderElement(null, WORKGROUPS);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, WORKGROUP_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getWorkgroups().iterator(); iterator.hasNext();) {
                Workgroup workgroup = (Workgroup)iterator.next();
                exportWorkgroup(rootElement, workgroup);
            }
            return rootElement;
        }
        return null;
    }

    private void exportWorkgroup(Element parent, Workgroup workgroup) {
        Element workgroupElement = renderer.renderElement(parent, WORKGROUP);
        renderer.renderTextElement(workgroupElement, WORKGROUP_NAME, workgroup.getGroupNameId().getNameId());
        renderer.renderTextElement(workgroupElement, DESCRIPTION, workgroup.getDescription());
        if (!StringUtils.isBlank(workgroup.getWorkgroupType())) {
        	renderer.renderTextElement(workgroupElement, WORKGROUP_TYPE, workgroup.getWorkgroupType());
        }
        if (!workgroup.getActiveInd().booleanValue()) {
            renderer.renderBooleanElement(workgroupElement, ACTIVE_IND, workgroup.getActiveInd(), true);
        }
        exportExtensions(workgroupElement, workgroup.getExtensions());
        exportMembers(workgroupElement, workgroup.getMembers());
    }

    private void exportExtensions(Element parent, List<Extension> extensions) {
    	if (extensions != null && !extensions.isEmpty()) {
    		Element extensionsElement = renderer.renderElement(parent, EXTENSIONS);
    		for (Extension extension : extensions) {
    			Element extensionElement = renderer.renderElement(extensionsElement, EXTENSION);
    			extensionElement.setAttribute(ATTRIBUTE, extension.getAttributeName());
    	    	        if (extension.getData() != null && !extension.getData().isEmpty()) {
    			for (ExtensionData extensionData : extension.getData()) {
    			    Element dataElement = renderer.renderElement(extensionElement, DATA);
    			    if (!Utilities.isEmpty(extensionData.getValue())) {
    			        dataElement.setText(extensionData.getValue());
    			    }
//    				Element dataElement = renderer.renderTextElement(extensionElement, DATA, extensionData.getValue());
    				dataElement.setAttribute(KEY, extensionData.getKey());
    			}
    	    	        }
    		}
    	}
    }

    private void exportMembers(Element parent, List<Recipient> members) {
        if (!members.isEmpty()) {
            Element membersElement = renderer.renderElement(parent, MEMBERS);
            for (Recipient member : members) {
            	if (member instanceof WorkflowUser) {
            		WorkflowUser user = (WorkflowUser) member;
            		renderer.renderTextElement(membersElement, AUTHENTICATION_ID, user.getAuthenticationUserId().getId());
            	} else if (member instanceof Workgroup) {
            		Workgroup workgroup = (Workgroup) member;
            		renderer.renderTextElement(membersElement, WORKGROUP_NAME, workgroup.getGroupNameId().getNameId());
            	}
            }
        }
    }

}
