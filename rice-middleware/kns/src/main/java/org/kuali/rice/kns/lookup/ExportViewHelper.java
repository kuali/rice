/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.WriterOutputStream;
import org.displaytag.model.Row;
import org.displaytag.model.TableModel;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.exception.ExportNotSupportedException;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A helper class to be used with the custom ExportView implementations for
 * Display Tag.  Most of the logic for interfacing with the KNS export
 * system is encapsulated in this helper class so it can be shared between
 * the various Display Tag export implementations. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated KNS Struts deprecated, use KRAD and the Spring MVC framework.
 */
@Deprecated
public class ExportViewHelper {

	private BusinessObjectEntry businessObjectEntry;
	private List<BusinessObject> businessObjects;
	
	public ExportViewHelper(TableModel tableModel) {
		this.businessObjectEntry = loadBusinessObjectEntry();
		this.businessObjects = loadBusinessObjects(tableModel);
	}
	
	protected BusinessObjectEntry loadBusinessObjectEntry() {
		KualiForm kualiForm = KNSGlobalVariables.getKualiForm();
		if (kualiForm instanceof LookupForm) {
			LookupForm lookupForm = (LookupForm) kualiForm;
			if (!StringUtils.isBlank(lookupForm.getBusinessObjectClassName())) {
				return KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(lookupForm.getBusinessObjectClassName());
			}
		}
		return null;
	}
	
	protected List<BusinessObject> loadBusinessObjects(TableModel tableModel) {
		List<BusinessObject> businessObjects = new ArrayList<BusinessObject>();
		List<Row> rowList = tableModel.getRowListFull();
		for (Row row : rowList) {
			ResultRow resultRow = (ResultRow)row.getObject();
			if (resultRow.getBusinessObject() != null) {
				businessObjects.add(resultRow.getBusinessObject());
			}
		}
		return businessObjects;
	}
	
	public BusinessObjectEntry getBusinessObjectEntry() {
		return businessObjectEntry;
	}
	
	public List<BusinessObject> getBusinessObjects() {
		return businessObjects;
	}
	
	public boolean attemptCustomExport(OutputStream outputStream, String exportFormat) throws IOException {
		if (getBusinessObjectEntry() != null && getBusinessObjectEntry().getExporterClass() != null) {
			final Exporter exporter;
			try {
				exporter = getBusinessObjectEntry().getExporterClass().newInstance();
			} catch (Exception e) {
				throw new ExportNotSupportedException("Failed to load export class: " + businessObjectEntry.getExporterClass(),e);
			}
			List<String> supportedFormats = exporter.getSupportedFormats(businessObjectEntry.getBusinessObjectClass());
			if (supportedFormats.contains(exportFormat)) {
				exporter.export(businessObjectEntry.getBusinessObjectClass(), getBusinessObjects(), exportFormat, outputStream);
				return true;
			}
		}
		return false;
	}
	
	public boolean attemptCustomExport(Writer writer, String exportFormat) throws IOException {
		return attemptCustomExport(new WriterOutputStream(writer), exportFormat);
	}

    // KULRICE-12281: Turn off the ability to export results from the certain lookups
    public void checkPermission() throws AuthorizationException {
        boolean isAuthorized = false;
        String componentName = businessObjectEntry.getBusinessObjectClass().getName();
        String nameSpaceCode = "KR-NS";
        String templateName =  "Export Records";
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        String principalUserName = GlobalVariables.getUserSession().getPrincipalName();
        Map<String, String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put("componentName", componentName);
        isAuthorized = KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(principalId,nameSpaceCode,templateName,permissionDetails,new HashMap<String,String>());
        if(!isAuthorized){
            throw new AuthorizationException(principalUserName, "Exporting the LookUp Results", componentName);
        }
    }
	
}
