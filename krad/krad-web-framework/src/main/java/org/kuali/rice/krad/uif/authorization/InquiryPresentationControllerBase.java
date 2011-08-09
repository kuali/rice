/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.authorization;

import java.util.Set;

import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.view.InquiryView;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryPresentationControllerBase extends PresentationControllerBase {

    /**
     * Prepares a list of action flags applicable for a inquiry
     *
     * @see org.kuali.rice.krad.uif.authorization.PresentationControllerBase#getActionFlags(org.kuali.rice.krad.web.spring.form.UifFormBase)
     */
    @Override
    public Set<String> getActionFlags(UifFormBase model) {
        Set<String> actionFlags = super.getActionFlags(model);

        if (isExportSupported((InquiryView) model.getView())) {
            actionFlags.add(KRADConstants.KUALI_ACTION_CAN_EXPORT);
        }

        return actionFlags;
    }

    /**
     * Examines the dataobjects's data dictionary entry to determine if it supports XML export or not
     *
     * @return boolean true if it supports export, false if not
     */
    protected boolean isExportSupported(InquiryView view) {
        DataObjectEntry dataObjectEntry = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary()
                .getDataObjectEntry(view.getDataObjectClassName().getName());

        Class<? extends Exporter> exporterClass = dataObjectEntry.getExporterClass();
        if (exporterClass != null) {
            try {
                Exporter exporter = exporterClass.newInstance();
                if (exporter.getSupportedFormats(dataObjectEntry.getDataObjectClass())
                        .contains(KRADConstants.XML_FORMAT)) {
                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to locate or create exporter class: " + exporterClass);
            }
        }

        return false;
    }
}
