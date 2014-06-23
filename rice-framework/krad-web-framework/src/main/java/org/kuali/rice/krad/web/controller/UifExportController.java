/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.layout.collections.TableExporter;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Controller that handles table export requests.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/export")
public class UifExportController extends UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifExportController.class);

    @Autowired
    protected HttpServletRequest request;

    /**
     * Retrieves the session form for the form key request parameter so we can initialize a form instance of the
     * same type the view was rendered with.
     *
     * {@inheritDoc}
     */
    @Override
    protected UifFormBase createInitialForm() {
        String formKey = request.getParameter(UifParameters.FORM_KEY);
        if (StringUtils.isBlank(formKey)) {
            throw new RuntimeException("Unable to create export form due to misssing form key parameter");
        }

        UifFormBase sessionForm = GlobalVariables.getUifFormManager().getSessionForm(formKey);
        if (sessionForm != null) {
            try {
                return sessionForm.getClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create export form instance from session form", e);
            }
        }

        return null;
    }

    /**
     * Generates exportable table data as CSV based on the rich table selected.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=" + UifConstants.MethodToCallNames.TABLE_CSV,
            produces = {"text/csv"})
    @ResponseBody
    public String tableCsvRetrieval(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        LOG.debug("processing csv table data request");

        return retrieveTableData(form, request, response);
    }

    /**
     * Generates exportable table data in xsl based on the rich table selected.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=" + UifConstants.MethodToCallNames.TABLE_XLS,
            produces = {"application/vnd.ms-excel"})
    @ResponseBody
    public String tableXlsRetrieval(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        LOG.debug("processing xls table data request");

        return retrieveTableData(form, request, response);
    }

    /**
     * Generates exportable table data based on the rich table selected.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=" + UifConstants.MethodToCallNames.TABLE_XML,
            produces = {"application/xml"})
    @ResponseBody
    public String tableXmlRetrieval(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        LOG.debug("processing xml table data request");

        return retrieveTableData(form, request, response);
    }

    /**
     * Handles exporting the dataObject for this Inquiry to XML if it has a custom XML exporter available.
     *
     * @param form   KualiForm
     * @param result  interface that represents binding results
     * @param request the http request that was made
     * @param response the http response object
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=" + UifConstants.MethodToCallNames.INQUIRY_XML,
            produces = {"application/xml"})
    @ResponseBody
    public ModelAndView inquiryXmlRetrieval(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        Object dataObject = inquiryForm.getDataObject();
        applyCustomExport(Collections.singletonList(dataObject), inquiryForm.getDataObjectClassName(),
                KRADConstants.XML_FORMAT, response);

        return null;
    }


    /**
     * Generates exportable table data based on the rich table selected.
     *
     * <p>First the lifecycle process is run to rebuild the collection group, then
     * {@link org.kuali.rice.krad.uif.layout.collections.TableExporter} is invoked to build the export data from
     * the collection.</p>
     */
    protected String retrieveTableData(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        LOG.debug("processing table data request");

        CollectionGroup collectionGroup = (CollectionGroup) ViewLifecycle.performComponentLifecycle(form.getView(),
                form, request, form.getViewPostMetadata(), form.getUpdateComponentId());

        List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(form,
                collectionGroup.getBindingInfo().getBindingPath());


        Class<?> dataObjectClass = collectionGroup.getCollectionObjectClass();
        String formatType = getValidatedFormatType(request.getParameter(UifParameters.FORMAT_TYPE));

        // set update none to prevent the lifecycle from being run after the controller finishes
        form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATENONE.getKey());

        if (applyCustomExport(modelCollection, dataObjectClass.getName(), formatType, response)) {
            return null;
        }

        // generic export
        return TableExporter.buildExportTableData(collectionGroup, form, formatType);
    }

    /**
     * Checks if a custom exporter can be applied
     *
     * @param dataObjectEntry
     * @return boolean value is true if a custom exporter is present
     */
    protected boolean canApplyCustomExport(DataObjectEntry dataObjectEntry) {
        Class<? extends Exporter> exporterClass = dataObjectEntry.getExporterClass();
        return exporterClass != null;
    }

    /**
     * Applies custom export if an exporter class is defined. Will return false if no exporter class defined
     * or if the dataObject collection is empty.
     *
     *
     * @param dataObjectCollection
     * @param dataObjectClassName
     * @param formatType
     *
     * @param response  true if custom exporter applied else return false.
     *
     */
    protected boolean applyCustomExport(List<Object> dataObjectCollection, String dataObjectClassName,
            String formatType, HttpServletResponse response) {

        String contentType = getContentType(formatType);
        setAttachmentResponseHeader(response, "export." + formatType, contentType);


        // check for custom exporter class defined for the data object class
        DataObjectEntry dataObjectEntry =
                KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDataObjectEntry(
                        dataObjectClassName);

        // Return if no dataobject present to export
        if (CollectionUtils.isEmpty(dataObjectCollection)) {
            return false;
        }

        // No custom exporter present
        if (!canApplyCustomExport(dataObjectEntry)) {
            return false;
        }

        try {
            Exporter exporter = dataObjectEntry.getExporterClass().newInstance();

            if (exporter.getSupportedFormats(dataObjectEntry.getDataObjectClass()).contains(formatType)) {
                exporter.export(dataObjectEntry.getDataObjectClass(), dataObjectCollection, formatType, response.getOutputStream());
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoked custom exporter class", e);
        }

        return false;
    }

    /**
     * Creates consistent setup of attachment response header.
     *
     * @param response http response object
     * @param filename name of the return file
     * @param contentType return content type
     */
    protected void setAttachmentResponseHeader(HttpServletResponse response, String filename, String contentType) {
        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    }

    /**
     * Reviews and returns a valid format type, defaults to csv.
     *
     * @param formatType format type to validate
     * @return valid format type
     */
    protected String getValidatedFormatType(String formatType) {
        if (KRADConstants.EXCEL_FORMAT.equals(formatType) || KRADConstants.XML_FORMAT.equals(formatType)) {
            return formatType;
        }

        return KRADConstants.CSV_FORMAT;
    }

    /**
     * Reviews and returns a valid content type, defaults to text/csv.
     *
     * @param formatType format type to return content type for
     * @return valid content type
     */
    protected String getContentType(String formatType) {
        if (KRADConstants.EXCEL_FORMAT.equals(formatType)) {
            return KRADConstants.EXCEL_MIME_TYPE;
        } else if (KRADConstants.XML_FORMAT.equals(formatType)) {
            return KRADConstants.XML_MIME_TYPE;
        } else {
            return KRADConstants.CSV_MIME_TYPE;
        }
    }
}
