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
package edu.iu.uis.eden.batch.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.CompositeXmlDocCollection;
import edu.iu.uis.eden.batch.FileXmlDocCollection;
import edu.iu.uis.eden.batch.XmlDoc;
import edu.iu.uis.eden.batch.XmlDocCollection;
import edu.iu.uis.eden.batch.ZipXmlDocCollection;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.UserLoginFilter;

/**
 * Struts action that accepts uploaded files and feeds them to the XmlIngesterService
 * @see edu.iu.uis.eden.batch.XmlIngesterService
 * @see edu.iu.uis.eden.batch.web.IngesterForm
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IngesterAction extends Action {
    private static final Logger LOG = Logger.getLogger(IngesterAction.class);

    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        LOG.debug(request.getMethod());
        if (!"post".equals(request.getMethod().toLowerCase())) {
            LOG.debug("returning to view");
            return mapping.findForward("view");
        }

        IngesterForm iform = (IngesterForm) form;

        List messages = new ArrayList();
        List tempFiles = new ArrayList();
        try {
            Collection  files = iform.getFiles();
            List collections = new ArrayList(files.size());
            LOG.debug(files);
            LOG.debug("" + files.size());

            Iterator it = files.iterator();
            while (it.hasNext()) {
                FormFile file = (FormFile) it.next();
                if (file.getFileName() == null || file.getFileName().length() == 0) continue;
                if (file.getFileData() == null) {
                    messages.add("File '" + file.getFileName() + "' contained no data");
                    continue;
                }
                LOG.debug("Processing file: " + file.getFileName());
                // ok, we have to copy it to *another* file because Struts doesn't give us a File
                // reference (which itself is not a bad abstraction) and XmlDocs based on ZipFile
                // can't be constructed without a file reference.
                FileOutputStream fos = null;
                File temp = null;
                try {
                    temp = File.createTempFile("ingester", null);
                    tempFiles.add(temp);
                    fos = new FileOutputStream(temp);
                    fos.write(file.getFileData());
                } catch (IOException ioe) {
                    messages.add("Error copying file data for '" + file.getFileName() + "': " + ioe);
                    continue;
                } finally {
                    if (fos != null) try {
                        fos.close();
                    } catch (IOException ioe) {
                        LOG.error("Error closing temp file output stream: " + temp, ioe);
                    }
                }
                if (file.getFileName().toLowerCase().endsWith(".zip")) {
                    try {
                        collections.add(new ZipXmlDocCollection(temp));
                    } catch (IOException ioe) {
                        String message = "Unable to load file: " + file; 
                        LOG.error(message);
                        messages.add(message);
                    }
                } else if (file.getFileName().endsWith(".xml")) {
                    collections.add(new FileXmlDocCollection(temp, file.getFileName()));
                } else {
                    messages.add("Ignoring extraneous file: " + file.getFileName());
                }
            }

            if (collections.size() == 0) {
                String message = "No valid files to ingest"; 
                LOG.debug(message);
                messages.add(message);
            } else {
                // wrap in composite collection to make transactional
                CompositeXmlDocCollection compositeCollection = new CompositeXmlDocCollection(collections);
                int totalProcessed = 0;
                List<XmlDocCollection> c = new ArrayList<XmlDocCollection>(1);
                c.add(compositeCollection);
                try {
                    Collection failed = KEWServiceLocator.getXmlIngesterService().ingest(c, UserLoginFilter.getUserSession(request).getWorkflowUser());
                    boolean txFailed = failed.size() > 0;
                    if (txFailed) {
                        messages.add("Ingestion failed");
                    }
                    it = collections.iterator();
                    while (it.hasNext()) {
                        XmlDocCollection collection = (XmlDocCollection) it.next();
                        List docs = collection.getXmlDocs();
                        Iterator docIt = docs.iterator();
                        while (docIt.hasNext()) {
                            XmlDoc doc = (XmlDoc) docIt.next();
                            if (doc.isProcessed()) {
                                if (!txFailed) {
                                    totalProcessed++;
                                    messages.add("Ingested xml doc: " + doc.getName() + (doc.getProcessingMessage() == null ? "" : "\n" + doc.getProcessingMessage()));
                                } else {
                                    messages.add("Rolled back doc: " + doc.getName() + (doc.getProcessingMessage() == null ? "" : "\n" + doc.getProcessingMessage()));
                                }
                            } else {
                                messages.add("Failed to ingest xml doc: " + doc.getName() + (doc.getProcessingMessage() == null ? "" : "\n" + doc.getProcessingMessage()));
                            }
                        }
                    }
                } catch (Exception e) {
                    String message = "Error during ingest";
                    LOG.error(message, e);
                    messages.add(message + ": " + e  + ":\n" + Utilities.collectStackTrace(e));
                }
                if (totalProcessed == 0) {
                    String message = "No xml docs ingested"; 
                    LOG.debug(message);
                    messages.add(message);
                }
            }
        } finally {
            if (tempFiles.size() > 0) {
                Iterator it = tempFiles.iterator();
                while (it.hasNext()) {
                    File temp = (File) it.next();
                    if (!temp.delete()) {
                        LOG.warn("Error deleting temp file: " + temp);
                    }
                }
            }
        }

        request.setAttribute("messages", messages);
        return mapping.findForward("view");
    }
}