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
package org.kuali.rice.kew.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.api.services.CoreApiServiceLocator;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kns.bo.Exporter;
import org.kuali.rice.kns.exception.ExportNotSupportedException;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * The DataExporter allows for exporting of KEW BusinessObjects to various
 * supported formats. The current implementation supports only XML export. This
 * process is initiated from the KNS screens (lookups and inquiries) and this
 * implementation leverages the existing XmlExporterService which is part of KEW
 * and which was used to do exports before KEW was converted to use the KNS.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataExporter implements Exporter {

	private List<String> supportedFormats = new ArrayList<String>();

	public DataExporter() {
		supportedFormats.add(KNSConstants.XML_FORMAT);
	}

	/**
	 * Export the given List of BusinessObjects of the specified type to XML.
	 */
	public void export(Class<?> dataObjectClass,
			List<? extends Object> dataObjects, String exportFormat,
			OutputStream outputStream) throws IOException {
		if (!KNSConstants.XML_FORMAT.equals(exportFormat)) {
			throw new ExportNotSupportedException("The given export format of "
					+ exportFormat
					+ " is not supported by the KEW XML Exporter!");
		}
		ExportDataSet dataSet = buildExportDataSet(dataObjectClass, dataObjects);
		outputStream.write(CoreApiServiceLocator.getXmlExporterService()
				.export(dataSet));
		outputStream.flush();
	}

	public List<String> getSupportedFormats(Class<?> dataObjectClass) {
		return supportedFormats;
	}

	/**
	 * Builds the ExportDataSet based on the BusinessObjects passed in.
	 */
	protected ExportDataSet buildExportDataSet(Class<?> dataObjectClass,
			List<? extends Object> dataObjects) {
		KewExportDataSet dataSet = new KewExportDataSet();
		for (BusinessObject businessObject : businessObjects) {
			if (businessObjectClass.equals(RuleAttribute.class)) {
				dataSet.getRuleAttributes().add((RuleAttribute)businessObject);
			} else if (businessObjectClass.equals(RuleTemplate.class)) {
				dataSet.getRuleTemplates().add((RuleTemplate)businessObject);
			} else if (businessObjectClass.equals(DocumentType.class)) {
				dataSet.getDocumentTypes().add((DocumentType)businessObject);
			} else if (businessObjectClass.equals(HelpEntry.class)) {
				dataSet.getHelp().add((HelpEntry)businessObject);
			} else if (businessObjectClass.equals(RuleBaseValues.class)) {
				dataSet.getRules().add((RuleBaseValues)businessObject);
			} else if (businessObjectClass.equals(RuleDelegation.class)) {
				dataSet.getRuleDelegations().add((RuleDelegation)businessObject);
			} else if (businessObjectClass.equals(GroupImpl.class)) {
				dataSet.getGroups().add((GroupImpl)businessObject);
			}    
		}

		ExportDataSet exportDataSet = new ExportDataSet();
		dataSet.populateExportDataSet(exportDataSet);
		return exportDataSet;
	}

}
