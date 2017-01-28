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
package org.kuali.rice.krad.labs.fileUploads;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.impex.xml.CompositeXmlDocCollection;
import org.kuali.rice.core.api.impex.xml.FileXmlDocCollection;
import org.kuali.rice.core.api.impex.xml.XmlDoc;
import org.kuali.rice.core.api.impex.xml.XmlDocCollection;
import org.kuali.rice.core.api.impex.xml.ZipXmlDocCollection;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the XML Ingester View
 *
 * <p>
 *     Displays the initial Ingester view page and processes file upload requests.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Controller
@RequestMapping(value = "/ingester")
public class XmlIngesterController extends UifControllerBase {

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected XmlIngesterForm createInitialForm() {
        return new XmlIngesterForm();
    }

    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(UifFormBase form) {
        XmlIngesterForm ingesterForm = (XmlIngesterForm)form;

        return super.start(ingesterForm);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=upload")
    public ModelAndView upload(@ModelAttribute("KualiForm") XmlIngesterForm ingesterForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        List<File> tempFiles = new ArrayList<File>();
        List<XmlDocCollection> collections = copyInputFiles(ingesterForm.getFiles(), tempFiles);
        try {
            if (collections.size() == 0) {
                String message = "No valid files to ingest";
                GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_NO_VALID_FILES);
            } else {
                if (ingestFiles(collections) == 0) {
                    //	                String message = "No xml docs ingested";
                    GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_NO_XMLS);
                }
            }
        } finally {
            if (tempFiles.size() > 0) {
                for (File tempFile : tempFiles)
                {
                    if (!tempFile.delete())
                    {
                        //LOG.warn("Error deleting temp file: " + tempFile);
                    }
                }
            }
        }
        return getModelAndView(ingesterForm);
    }

    /**
     * Copies the MultipartFiles into an XmlDocCollection list
     *
     * <p>
     * Reads each of the input files into temporary files to get File reference needed
     * to create FileXmlDocCollection objects.  Also verifies that only .xml or .zip files are
     * to be processed.
     * </p>
     *
     * @param fileList list of MultipartFiles selected for ingestion
     * @param tempFiles temporary files used to get File reference
     *
     * @return uploaded files in a List of XmlDocCollections
     */
    protected  List<XmlDocCollection> copyInputFiles(List<MultipartFile> fileList, List<File> tempFiles){
        List<XmlDocCollection> collections = new ArrayList<XmlDocCollection>();
        for (MultipartFile file : fileList) {
            if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
                continue;
            }

            // Need to copy into temp file get File reference because XmlDocs based on ZipFile
            // can't be constructed without a file reference.
            FileOutputStream fos = null;
            File temp = null;
            try{
                temp = File.createTempFile("ingester", null);
                tempFiles.add(temp);
                fos = new FileOutputStream(temp);
                fos.write(file.getBytes());
            } catch (IOException ioe) {
                GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID,
                        XmlIngesterConstants.ERROR_INGESTER_COPY_FILE , file.getOriginalFilename(), ExceptionUtils.getFullStackTrace(ioe));
                continue;
            } finally{
                if (fos != null) {
                    try{
                        fos.close();
                    } catch (IOException ioe){
                        //                          LOG.error("Error closing temp file output stream: " + temp, ioe);
                    }
                }
            }

            // only .zip and .xml files will be processed
            if (file.getOriginalFilename().toLowerCase().endsWith(".zip"))
            {
                try {
                    collections.add(new ZipXmlDocCollection(temp));
                } catch (IOException ioe) {
                    GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_LOAD_FILE, file.getOriginalFilename());
                }
            } else if (file.getOriginalFilename().endsWith(".xml")) {
                collections.add(new FileXmlDocCollection(temp, file.getOriginalFilename()));
            } else {
                GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_EXTRANEOUS_FILE, file.getOriginalFilename());
            }
        }

        return collections;
    }

    /**
     * Ingests the list of files into the system
     *
     * @param collections xml documents to be ingested
     * @return the number of files successfully ingested
     */
    protected int ingestFiles(List<XmlDocCollection> collections){
        // wrap in composite collection to make transactional
        CompositeXmlDocCollection compositeCollection = new CompositeXmlDocCollection(collections);
        int totalProcessed = 0;
        List<XmlDocCollection> c = new ArrayList<XmlDocCollection>(1);
        c.add(compositeCollection);
        try {
            // ingest the collection of files
            Collection<XmlDocCollection> failed = CoreApiServiceLocator.getXmlIngesterService().ingest(c, GlobalVariables.getUserSession().getPrincipalId());
            boolean txFailed = failed.size() > 0;
            if (txFailed) {
                GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_FAILED);
            }

            // loop through the results, collecting the error messages for each doc
            collectIngestionMessages(collections, txFailed);
        } catch (Exception e) {
            GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_DURING_INJECT, ExceptionUtils.getFullStackTrace(e));
        }

        return totalProcessed;
    }

    /**
     * loop through the results, returns the number of successfully processed files
     *
     * <p>
     * Also collects the error messages for each doc
     * </p>
     *
     * @param collections the list of processed documents
     * @param txFailed flag whether upload contained errors

     * @return the number of files successfully ingested
     */
    protected int collectIngestionMessages(List<XmlDocCollection> collections, boolean txFailed){
        int totalProcessed = 0;
        for (XmlDocCollection collection1 : collections)
        {
            List<? extends XmlDoc> docs = collection1.getXmlDocs();
            for (XmlDoc doc1 : docs)
            {
                if (doc1.isProcessed())
                {
                    if (!txFailed)
                    {
                        totalProcessed++;
                        GlobalVariables.getMessageMap().putInfoForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.INFO_INGESTER_SUCCESS, doc1.getName(),doc1.getProcessingMessage());
                    } else {
                        GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_ROLLEDBACK, doc1.getName(),doc1.getProcessingMessage());
                    }
                } else
                {GlobalVariables.getMessageMap().putErrorForSectionId(XmlIngesterConstants.INGESTER_SECTION_ID, XmlIngesterConstants.ERROR_INGESTER_FAILED_XML, doc1.getName(),doc1.getProcessingMessage());
                }
            }
        }
        return totalProcessed;
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=close")
    public ModelAndView close(@ModelAttribute("KualiForm") XmlIngesterForm ingesterForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        return null;
    }

}
