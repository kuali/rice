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
package org.kuali.rice.krad.web.form;


import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Form class for <code>InquiryView</code> screens
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryForm extends UifFormBase {
    private static final long serialVersionUID = 4733144086378429410L;
    private static final Logger LOG = Logger.getLogger(InquiryForm.class);

    private String dataObjectClassName;
    private Object dataObject;

    private boolean canExport;

    public InquiryForm() {
        setViewTypeName(ViewType.INQUIRY);
    }

    /**
     * Examines the BusinessObject's data dictionary entry to determine if it
     * supports XML export or not and set's canExport appropriately.
     *
     * TODO: should be moved to the view and authorization done in presentation controller
     */
    protected void populateExportCapabilities(String boClassName) {
        setCanExport(false);
        DataObjectEntry dataObjectEntry =
                KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDataObjectEntry(boClassName);
        Class<? extends Exporter> exporterClass = dataObjectEntry.getExporterClass();
        if (exporterClass != null) {
            try {
                Exporter exporter = exporterClass.newInstance();
                if (exporter.getSupportedFormats(dataObjectEntry.getDataObjectClass()).contains(KRADConstants.XML_FORMAT)) {
                    setCanExport(true);
                }
            } catch (Exception e) {
                LOG.error("Failed to locate or create exporter class: " + exporterClass, e);
                throw new RuntimeException("Failed to locate or create exporter class: " + exporterClass);
            }
        }
    }

    /**
     * Class name of the data object the inquiry will display
     *
     * <p>
     * Used to set the data object class for the <code>Inquirable</code> which
     * is then used to perform the inquiry query
     * </p>
     *
     * @return String class name
     */
    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for the inquiry data object class name
     *
     * @param dataObjectClassName
     */
    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * Result data object for inquiry that will be display with the view
     *
     * @return Object object instance containing the inquiry data
     */
    public Object getDataObject() {
        return this.dataObject;
    }

    /**
     * Setter for the inquiry data object
     *
     * @param dataObject
     */
    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * <code>Inquirable</code>  instance that will be used to perform
     * the inquiry
     *
     * @return Inquirable instance
     */
    public Inquirable getInquirable() {
        return (Inquirable) getView().getViewHelperService();
    }

    public boolean isCanExport() {
        return this.canExport;
    }

    public void setCanExport(boolean canExport) {
        this.canExport = canExport;
    }
}
