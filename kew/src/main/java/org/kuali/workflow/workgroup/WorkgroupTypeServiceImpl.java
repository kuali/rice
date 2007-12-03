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
package org.kuali.workflow.workgroup;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Element;
import org.kuali.workflow.workgroup.dao.WorkgroupTypeDAO;

import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Basic implementation of the WorkgroupTypeService.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeServiceImpl implements WorkgroupTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkgroupTypeServiceImpl.class);

	private static final String XML_FILE_NOT_FOUND = "general.error.filenotfound";
    private static final String XML_PARSE_ERROR = "general.error.parsexml";

	private WorkgroupTypeDAO workgroupTypeDAO;

	public WorkgroupType findById(Long workgroupTypeId) {
		return getWorkgroupTypeDAO().findById(workgroupTypeId);
	}

	public WorkgroupType findByName(String workgroupTypeName) {
		return getWorkgroupTypeDAO().findByName(workgroupTypeName);
	}

	public List<WorkgroupType> findAllActive() {
		Collection workgroupTypes = getWorkgroupTypeDAO().findAll(true);
		return new ArrayList<WorkgroupType>(workgroupTypes);
	}

	public List<WorkgroupType> findAll() {
		Collection workgroupTypes = getWorkgroupTypeDAO().findAll(false);
		return new ArrayList<WorkgroupType>(workgroupTypes);
	}

	public void save(WorkgroupType workgroupType) {
    	getWorkgroupTypeDAO().save(workgroupType);
    }

	public WorkgroupTypeDAO getWorkgroupTypeDAO() {
		return workgroupTypeDAO;
	}

	public void setWorkgroupTypeDAO(WorkgroupTypeDAO workgroupTypeDAO) {
		this.workgroupTypeDAO = workgroupTypeDAO;
	}

	public void loadXml(InputStream inputStream, WorkflowUser user) {
		try {
			WorkgroupTypeXmlParser parser = new WorkgroupTypeXmlParser();
			parser.setWorkgroupTypeService(this);
			List<WorkgroupType> workgroupTypes = parser.parseWorkgroupTypes(inputStream);
			for (WorkgroupType workgroupType : workgroupTypes) {
				save(workgroupType);
			}
		} catch(FileNotFoundException e) {
            throw new WorkflowServiceErrorException("XML file not found", new WorkflowServiceErrorImpl("Rule Attribute XML file not found", XML_FILE_NOT_FOUND) );
    	} catch (Exception e) { //any other exception
            LOG.error("Error loading xml file", e);
            throw new WorkflowServiceErrorException("Error loading xml file", new WorkflowServiceErrorImpl("Error loading xml file", XML_PARSE_ERROR));
        }
	}

	public List<WorkgroupType> search(Long id, String name, String label, String description, Boolean active) {
		Collection results = getWorkgroupTypeDAO().search(id, name, label, description, active);
		return new ArrayList<WorkgroupType>(results);
	}

	public Element export(ExportDataSet dataSet) {
		WorkgroupTypeXmlExporter exporter = new WorkgroupTypeXmlExporter();
        return exporter.export(dataSet);
	}

}
