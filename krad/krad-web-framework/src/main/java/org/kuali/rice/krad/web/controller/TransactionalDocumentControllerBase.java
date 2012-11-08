/**
 * Copyright 2005-2012 The Kuali Foundation
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.TransactionDocumentFormBase;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * Controller for <code>TransactionalView</code> screens which operate on
 * <code>TransactionalDocument</code> instances
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class TransactionalDocumentControllerBase extends DocumentControllerBase {
    protected static final Logger LOG = Logger.getLogger(TransactionalDocumentControllerBase.class);

    @Override
    protected abstract TransactionDocumentFormBase createInitialForm(HttpServletRequest request);

    /**
     * Method that will take the current document and call its copy method if Copyable.
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_COPY)
    public ModelAndView copy(@ModelAttribute("KualiForm") TransactionDocumentFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((Copyable) form.getDocument()).toCopy();
        return getUIFModelAndView(form);
    }

}
