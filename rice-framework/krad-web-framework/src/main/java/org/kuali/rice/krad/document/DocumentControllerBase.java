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
package org.kuali.rice.krad.document;

import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Base controller class for all KRAD document view screens working with document models.
 *
 * <p>Provides default controller implementations for the standard document actions including: doc handler
 * (retrieve from doc search and action list), save, route (and other KEW actions)</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DocumentControllerBase extends UifControllerBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract DocumentFormBase createInitialForm();

    /**
     * @see DocumentControllerService#docHandler(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=docHandler")
    public ModelAndView docHandler(DocumentFormBase form) throws Exception {
        return getControllerService().docHandler(form);
    }

    /**
     * @see DocumentControllerService#reload(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=reload")
    public ModelAndView reload(DocumentFormBase form) throws Exception {
        return getControllerService().reload(form);
    }

    /**
     * @see DocumentControllerService#recall(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=recall")
    public ModelAndView recall(DocumentFormBase form) {
        return getControllerService().recall(form);
    }

    /**
     * @see DocumentControllerService#save(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @Override
    @RequestMapping(params = "methodToCall=save")
    public ModelAndView save(UifFormBase form) {
        return getControllerService().save((DocumentFormBase) form);
    }

    /**
     * @see DocumentControllerService#save(org.kuali.rice.krad.web.form.DocumentFormBase,
     * org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent)
     */
    protected ModelAndView save(DocumentFormBase form, SaveDocumentEvent saveDocumentEvent) {
        return getControllerService().save(form, saveDocumentEvent);
    }

    /**
     * @see DocumentControllerService#complete(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=complete")
    public ModelAndView complete(DocumentFormBase form) {
        return getControllerService().complete(form);
    }

    /**
     * @see DocumentControllerService#route(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=route")
    public ModelAndView route(DocumentFormBase form) {
        return getControllerService().route(form);
    }

    /**
     * @see DocumentControllerService#blanketApprove(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=blanketApprove")
    public ModelAndView blanketApprove(DocumentFormBase form) {
        return getControllerService().blanketApprove(form);
    }

    /**
     * @see DocumentControllerService#approve(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=approve")
    public ModelAndView approve(DocumentFormBase form) {
        return getControllerService().approve(form);
    }

    /**
     * @see DocumentControllerService#disapprove(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=disapprove")
    public ModelAndView disapprove(DocumentFormBase form) {
        return getControllerService().disapprove(form);
    }

    /**
     * @see DocumentControllerService#fyi(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=fyi")
    public ModelAndView fyi(DocumentFormBase form) {
        return getControllerService().fyi(form);
    }

    /**
     * @see DocumentControllerService#acknowledge(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=acknowledge")
    public ModelAndView acknowledge(DocumentFormBase form) {
        return getControllerService().acknowledge(form);
    }

    /**
     * @see DocumentControllerService#sendAdHocRequests(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=sendAdHocRequests")
    public ModelAndView sendAdHocRequests(DocumentFormBase form) {
        return getControllerService().sendAdHocRequests(form);
    }

    /**
     * @see DocumentControllerService#supervisorFunctions(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=supervisorFunctions")
    public ModelAndView supervisorFunctions(DocumentFormBase form) {
        return getControllerService().supervisorFunctions(form);
    }

    /**
     * @see DocumentControllerService#insertNote(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertNote")
    public ModelAndView insertNote(DocumentFormBase form) {
        return getControllerService().insertNote(form);
    }

    /**
     * @see DocumentControllerService#deleteNote(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deleteNote")
    public ModelAndView deleteNote(DocumentFormBase form) {
        return getControllerService().deleteNote(form);
    }

    /**
     * {@inheritDoc}
     *
     * @see DocumentControllerService#superUserTakeActions(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=superUserTakeActions")
    public ModelAndView superUserTakeActions(DocumentFormBase form) {
        return getControllerService().superUserTakeActions(form);
    }

    /**
     * {@inheritDoc}
     *
     * @see DocumentControllerService#superUserApprove(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=superUserApprove")
    public ModelAndView superUserApprove(DocumentFormBase form) {
        return getControllerService().superUserApprove(form);
    }

    /**
     * {@inheritDoc}
     *
     * @see DocumentControllerService#superUserDisapprove(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=superUserDisapprove")
    public ModelAndView superUserDisapprove(DocumentFormBase form) {
        return getControllerService().superUserDisapprove(form);
    }

    /**
     * @see DocumentControllerService#downloadAttachment(org.kuali.rice.krad.web.form.DocumentFormBase,
     * javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=downloadAttachment")
    public ModelAndView downloadAttachment(DocumentFormBase form, HttpServletResponse response) {
        return getControllerService().downloadAttachment(form, response);
    }

    /**
     * @see DocumentControllerService#cancelAttachment(org.kuali.rice.krad.web.form.DocumentFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=cancelAttachment")
    public ModelAndView cancelAttachment(DocumentFormBase form) {
        return getControllerService().cancelAttachment(form);
    }

    @Override
    protected DocumentControllerService getControllerService() {
        return (DocumentControllerService) super.getControllerService();
    }

    @Override
    @Autowired
    @Qualifier("documentControllerService")
    public void setControllerService(ControllerService controllerService) {
        super.setControllerService(controllerService);
    }

}
